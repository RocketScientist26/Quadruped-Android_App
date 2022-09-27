
/*
	Commands
	Detect robot:
		#?
			#R
	Move:
		#1-3
			#[COMMAND ID (FROM 1 TO 6)]-[SPEED VALUE (FROM 1 TO 3)]
	Kick:
		#L00-00
			#L[KICK DIRECTION (FROM 0 TO ANIM_DATA_KICK_DIRECTION_MAX)]-[KICK STRENGTH (FROM 0 TO ANIM_DATA_KICK_STRENGTH_MAX)]
	Driving:
		#K000-00
			#K[DRIVING DIRECTION (FROM 0 TO ANIM_DATA_DRIVE_DIRECTION_MAX)]-[DRIVING SPEED (FROM 0 TO ANIM_DATA_DRIVE_STRENGHT_MAX)]
	Special tricks:
		#D1
			#D[TRICK ID (FROM 1 to 3)]
	LED Enable/disable:
		#E0
			#E[LED SHOULD BE ENABLED OR DISABLED (1 OR 0)]
	Settings page opened:
		#F
			Robot goes into calibration pose.
			#C-0-0-0-0-0-0-0-01234Quadruped 1\n
	Calibration value changed for servo:
		#I+91
			#I[CALIBRATION VALUE FROM -9 TO +9][SERVO NUMBER FROM 1 TO 8]
	Request to store settings in flash:
		#G
			We just store current settings to flash
	Settings page closed:
		#H
			Robot should return in normal stand pose
	Bluetooth name and password change requested:
		#JQuadruped 1234
			#J[NAME][PASSWORD]
*/

package me.rocket_scientist.quadruped

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import me.rocket_scientist.rsbt.RsBtConnMngrThread
import me.rocket_scientist.rsbt.RsBtDevMngr
import me.rocket_scientist.rsbt.RsBtReadThread
import me.rocket_scientist.rsbt.RsBtWriteThread
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    //Global
    companion object{
        //State machine for external StoreToFlash command
        var flash_store_rq = false
        //Command responce timeout
        const val cmd_timeout_ms: Long = 500
        //Command sending interval
        const val cmd_interval: Long = 100

        lateinit var btth_w: RsBtWriteThread

        fun vibrate(context: Context){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(40)
            }
        }

    }

    //Other pages
    private lateinit var SettingsIntent: Intent
    private var SettingsIntentCloseEvent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode == Activity.RESULT_CANCELED){
            disconnect()
        }
    }
    private lateinit var JoystickIntent: Intent
    private var JoystickIntentCloseEvent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode == Activity.RESULT_OK){
            disconnect()
        }
    }
    //UI
    private lateinit var spinner_devices: Spinner
    private lateinit var button_connect: ImageButton
    private lateinit var button_proj_page: ImageButton
    private lateinit var button_exit: ImageButton
    //State machine
    private var conn_disconn_rq = false
    private var unpair_rq = false
    private lateinit var c_device: BluetoothDevice
    //Command timeout timer
    private var timer = Timer()

    //Bluetooth write thread
    private var btWriteHandler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            RsBtWriteThread.STAT.MESSAGE_WRITTEN.ordinal -> {
                val data = msg.obj.toString()
                ////If BT Password and name change requested
                if(
                    (data.elementAt(0) == '#')&&
                    (data.elementAt(1) == 'J')&&
                    (data.length == 16)
                ){
                    //Disconnect BT
                    conn_disconn_rq = true
                    uiEnableConditional()
                    btconmngr.disconnect()
                    //Unpair current device
                    unpair_rq = true
                    //Close both activities
                    sendBroadcast(Intent("JoystickSettingsActivityEXIT"))
                }else if(
                    flash_store_rq &&
                    (data.elementAt(0) == '#')&&
                    (data.elementAt(1) == 'I')&&
                    (data.length == 5)
                ){
                    flash_store_rq = false
                    btth_w.sendString("#G")
                }
            }
            RsBtWriteThread.STAT.MESSAGE_SCHEDULED.ordinal -> {
                //!UNUSED
            }
            RsBtWriteThread.STAT.STREAM_BUSY.ordinal -> {
                disconnect()
            }
            RsBtWriteThread.STAT.WRITING_MESSAGE_FAILED.ordinal -> {
                disconnect()
            }
        }

        false
    }

    //Bluetooth read thread
    private lateinit var btth_r: RsBtReadThread
    private var btReadHandler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            RsBtReadThread.STAT.MESSAGE_READ_SUCCESSFULLY.ordinal -> {
                val data = msg.obj.toString()
                if(data == "#R"){
                    timer.cancel()
                    JoystickIntentCloseEvent.launch(JoystickIntent)
                }else if(data.length == 34){
                    if(
                        (data.elementAt(0) == '#') &&
                        (data.elementAt(1) == 'C') &&
                        ((data.elementAt(2) == '-') || (data.elementAt(2) == '+')) &&
                        ((data.elementAt(4) == '-') || (data.elementAt(4) == '+')) &&
                        ((data.elementAt(6) == '-') || (data.elementAt(6) == '+')) &&
                        ((data.elementAt(8) == '-') || (data.elementAt(8) == '+')) &&
                        ((data.elementAt(10) == '-') || (data.elementAt(10) == '+')) &&
                        ((data.elementAt(12) == '-') || (data.elementAt(12) == '+')) &&
                        ((data.elementAt(14) == '-') || (data.elementAt(14) == '+')) &&
                        ((data.elementAt(16) == '-') || (data.elementAt(16) == '+')) &&
                        ((data.elementAt(3).digitToInt() >= 0) && (data.elementAt(3).digitToInt() <= 9)) &&
                        ((data.elementAt(5).digitToInt() >= 0) && (data.elementAt(5).digitToInt() <= 9)) &&
                        ((data.elementAt(7).digitToInt() >= 0) && (data.elementAt(7).digitToInt() <= 9)) &&
                        ((data.elementAt(9).digitToInt() >= 0) && (data.elementAt(9).digitToInt() <= 9)) &&
                        ((data.elementAt(11).digitToInt() >= 0) && (data.elementAt(11).digitToInt() <= 9)) &&
                        ((data.elementAt(13).digitToInt() >= 0) && (data.elementAt(13).digitToInt() <= 9)) &&
                        ((data.elementAt(15).digitToInt() >= 0) && (data.elementAt(15).digitToInt() <= 9)) &&
                        ((data.elementAt(17).digitToInt() >= 0) && (data.elementAt(17).digitToInt() <= 9)) &&
                        ((data.elementAt(32) == '0') || (data.elementAt(32) == '1')) &&
                        (data.elementAt(33) == '\n')
                    ){
                        sendBroadcast(Intent("JoystickActivitySETTINGS"))
                        SettingsIntent.putExtra("CONFIG", data)
                        SettingsIntentCloseEvent.launch(SettingsIntent)
                    }
                }
            }
            RsBtReadThread.STAT.NO_INPUT_STREAM.ordinal -> {
                disconnect()
            }
        }

        false
    }

    //Bluetooth connection manager
    private lateinit var btconmngr: RsBtConnMngrThread
    private var btConnHandler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            RsBtConnMngrThread.STAT.CONNECTED.ordinal -> {
                c_device = btconmngr.currentDevice()
                btth_r = RsBtReadThread(btReadHandler, btconmngr.socket)
                /*
                     After receiving every chunk of data, thread runs
                     timer with "rx_timeout_ms" milliseconds timeout.
                     If no more data will be received for this time,
                     "packet" reception will be completed.
                */
                btth_r.rx_timeout_ms = 80
                btth_r.start()
                btth_w = RsBtWriteThread(btWriteHandler, btconmngr.socket)
                btth_w.start()

                conn_disconn_rq = false
                uiEnableConditional()

                timer = Timer()
                timer.schedule(cmd_timeout_ms) {
                    disconnect()
                }
                btth_w.sendString("#?")
            }
            RsBtConnMngrThread.STAT.DISCONNECTED.ordinal -> {
                conn_disconn_rq = false
                uiEnableConditional()
                if(unpair_rq){
                    unpair_rq = false
                    if(::c_device.isInitialized){
                        btdevmngr.unpair(c_device)
                    }
                }
            }
            RsBtConnMngrThread.STAT.SOCKET_CLOSE_FAIL.ordinal -> {
                btdevmngr.exit()
            }
        }

        false
    }

    //Bluetooth Device Manager
    private var devices: Set<BluetoothDevice?>? = null
    private var btdevmngr = object : RsBtDevMngr(this) {
        override fun rsBtMngrMessage(message: Int) {
            when (message) {
                STAT.TURNED_ON.ordinal -> {
                    devices = getDevices()
                    updateDevices()
                }
                STAT.TURNED_OFF.ordinal -> {
                    devices = null
                    updateDevices()
                }
                STAT.DEVICES_UPDATED.ordinal -> {
                    devices = getDevices()
                    updateDevices()
                }
            }
        }
    }
    private var devices_names = mutableListOf<String>()

    //UI
    private fun updateDevices(){
        var i = 0
        if(devices != null){
            devices_names = mutableListOf()
            @SuppressLint("MissingPermission")
            while(i != devices!!.count()){
                devices_names.add(devices!!.elementAt(i)?.name.toString())
                i++
            }
        }else{
            devices_names.clear()
        }

        if(devices_names.isEmpty()){
            devices_names.add(resources.getString(R.string.spinner_no_devices))
        }
        val aa = ArrayAdapter(this, R.layout.ma_spnr_item, devices_names)
        aa.setDropDownViewResource(R.layout.ma_spnr_dropdown_item)
        spinner_devices.adapter = aa
        uiEnableConditional()
    }
    private fun uiEnableConditional(){
        runOnUiThread {
            var isconnected = false
            if (::btconmngr.isInitialized) {
                if (btconmngr.isConnected()) {
                    isconnected = true
                }
            }

            if (conn_disconn_rq) {
                spinner_devices.isEnabled = false
                button_connect.isEnabled = false
            } else {
                if (isconnected) {
                    button_connect.isEnabled = false
                    spinner_devices.isEnabled = false
                } else {
                    if (devices != null) {
                        if (devices!!.isNotEmpty()) {
                            spinner_devices.isEnabled = true
                            button_connect.isEnabled = true
                        } else {
                            spinner_devices.isEnabled = false
                            button_connect.isEnabled = false
                        }
                    } else {
                        spinner_devices.isEnabled = false
                        button_connect.isEnabled = false
                    }
                }
            }
        }
    }
    private fun connectClicked() {
        conn_disconn_rq = true
        if(::btconmngr.isInitialized){
            if(btconmngr.isConnected()){
                uiEnableConditional()
                btconmngr.disconnect()
                return
            }
        }
        btconmngr = RsBtConnMngrThread(btConnHandler, devices!!.elementAt(spinner_devices.selectedItemPosition)!!, this)
        btconmngr.start()

        uiEnableConditional()
    }

    //ETC
    private fun disconnect(){
        if (::btconmngr.isInitialized) {
            if (btconmngr.isConnected()) {
                conn_disconn_rq = true
                uiEnableConditional()
                btconmngr.disconnect()
            }else{
                conn_disconn_rq = false
                uiEnableConditional()
            }
        }else{
            conn_disconn_rq = false
            uiEnableConditional()
        }

        //Close other pages
        sendBroadcast(Intent("JoystickSettingsActivityEXIT"))
    }

    //App
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //UI
        spinner_devices = findViewById(R.id.Spinner_Devices)
        button_connect = findViewById(R.id.ImageButton_Connect)
        button_proj_page = findViewById(R.id.ImageButton_ProjPage)
        button_exit = findViewById(R.id.ImageButton_Exit)

        //Button vibrations
        val btnsOnTouch = View.OnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> vibrate(this)
            }
            v?.onTouchEvent(event) ?: true
        }
        spinner_devices.setOnTouchListener(btnsOnTouch)
        button_connect.setOnTouchListener(btnsOnTouch)
        button_proj_page.setOnTouchListener(btnsOnTouch)
        button_exit.setOnTouchListener(btnsOnTouch)

        //Button click actions
        button_connect.setOnClickListener{
            connectClicked()
        }
        button_proj_page.setOnClickListener{
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://rocket-scientist.me/quadruped.html")
            )
            startActivity(browserIntent)
        }
        button_exit.setOnClickListener {
            btdevmngr.exit()
        }

        //Other pages
        JoystickIntent = Intent(this@MainActivity, JoystickActivity::class.java)
        SettingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)

        //App
        updateDevices()
        btdevmngr.init()
    }
    override fun onDestroy() {
        super.onDestroy()
        btdevmngr.destroy()
    }
}