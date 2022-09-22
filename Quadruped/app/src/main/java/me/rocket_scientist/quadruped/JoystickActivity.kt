package me.rocket_scientist.quadruped

import android.Manifest.permission.RECORD_AUDIO
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.rocket_scientist.rsuielements.RsVertSlider
import java.util.*
import kotlin.concurrent.schedule


class JoystickActivity : AppCompatActivity() {
    //Commands from external activity
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when(intent?.action){
                "JoystickSettingsActivityEXIT" -> finish()
                "JoystickActivitySETTINGS" -> {
                    timer_sett.cancel()
                    button_settings.isEnabled = true
                }
            }
        }
    }
    //Tilt angle
    private var tiltangle: Double = 0.0
    //Speed
    private var speed = 1
    //Command sending timer
    private var timer_cmd = Timer()
    //Settings responce timeout timer
    private var timer_sett = Timer()
    //Voice commands
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    //UI
    private lateinit var image_tilt: ImageView
    private lateinit var button_swim_d1: ImageButton
    private lateinit var button_workout_d2: ImageButton
    private lateinit var button_hello_d3: ImageButton
    private lateinit var button_speed: ImageButton
    private lateinit var button_forward: ImageButton
    private lateinit var button_backward: ImageButton
    private lateinit var button_turn_left: ImageButton
    private lateinit var button_turn_right: ImageButton
    private lateinit var button_left: ImageButton
    private lateinit var button_right: ImageButton
    private lateinit var button_cmd: ImageButton
    private lateinit var button_disconnect: ImageButton
    private lateinit var button_settings: ImageButton
    private lateinit var slider_acc: RsVertSlider
    private lateinit var text_cmd: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)

        //External commands
        registerReceiver(broadCastReceiver, IntentFilter("JoystickSettingsActivityEXIT"))
        registerReceiver(broadCastReceiver, IntentFilter("JoystickActivitySETTINGS"))

        //UI
        button_swim_d1 = findViewById(R.id.ImageButton_SwimD1)
        button_workout_d2 = findViewById(R.id.ImageButton_WorkoutD2)
        button_hello_d3 = findViewById(R.id.ImageButton_HelloD3)
        button_speed = findViewById(R.id.ImageButton_Speed)
        button_forward = findViewById(R.id.ImageButton_Up)
        button_backward = findViewById(R.id.ImageButton_Down)
        button_turn_left = findViewById(R.id.ImageButton_TurnLeft)
        button_turn_right = findViewById(R.id.ImageButton_TurnRight)
        button_left = findViewById(R.id.ImageButton_Left)
        button_right = findViewById(R.id.ImageButton_Right)
        button_cmd = findViewById(R.id.ImageButton_Cmd)
        button_disconnect = findViewById(R.id.ImageButton_Disconnect)
        button_settings = findViewById(R.id.ImageButton_Settings)
        image_tilt = findViewById(R.id.ImageView_Tilt)
        slider_acc = findViewById(R.id.RsVertSlider_Accelerator)
        text_cmd = findViewById(R.id.TextView_Cmd)

        //Enable button clicks
        button_swim_d1.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        MainActivity.btth_w.sendString("#D1")
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        button_workout_d2.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        MainActivity.btth_w.sendString("#D2")
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        button_hello_d3.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        MainActivity.btth_w.sendString("#D3")
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        button_speed.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    if(speed < 3){
                        speed++
                    }else{
                        speed = 1
                    }
                    when(speed){
                        1 -> button_speed.setImageResource(R.drawable.ja_speed_1)
                        2 -> button_speed.setImageResource(R.drawable.ja_speed_2)
                        3 -> button_speed.setImageResource(R.drawable.ja_speed_3)
                    }
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        button_forward.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        when(speed){
                            1 -> MainActivity.btth_w.sendString("#1-1")
                            2 -> MainActivity.btth_w.sendString("#1-2")
                            3 -> MainActivity.btth_w.sendString("#1-3")
                        }
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        button_backward.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        when(speed){
                            1 -> MainActivity.btth_w.sendString("#2-1")
                            2 -> MainActivity.btth_w.sendString("#2-2")
                            3 -> MainActivity.btth_w.sendString("#2-3")
                        }
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        button_turn_right.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        when(speed){
                            1 -> MainActivity.btth_w.sendString("#3-1")
                            2 -> MainActivity.btth_w.sendString("#3-2")
                            3 -> MainActivity.btth_w.sendString("#3-3")
                        }
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        button_turn_left.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        when(speed){
                            1 -> MainActivity.btth_w.sendString("#4-1")
                            2 -> MainActivity.btth_w.sendString("#4-2")
                            3 -> MainActivity.btth_w.sendString("#4-3")
                        }
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        button_left.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        when(speed){
                            1 -> MainActivity.btth_w.sendString("#5-1")
                            2 -> MainActivity.btth_w.sendString("#5-2")
                            3 -> MainActivity.btth_w.sendString("#5-3")
                        }
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        button_right.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        when(speed){
                            1 -> MainActivity.btth_w.sendString("#6-1")
                            2 -> MainActivity.btth_w.sendString("#6-2")
                            3 -> MainActivity.btth_w.sendString("#6-3")
                        }
                    }
                }
                MotionEvent.ACTION_UP -> timer_cmd.cancel()
            }
            v?.onTouchEvent(event) ?: true
        }

        //button_cmd.setOnTouchListener(btnsOnTouch)
        button_disconnect.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                }
            }
            v?.onTouchEvent(event) ?: true
        }
        button_disconnect.setOnClickListener{
            setResult(Activity.RESULT_OK)
            finish()
        }

        button_settings.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                }
            }
            v?.onTouchEvent(event) ?: true
        }
        button_settings.setOnClickListener{
            button_settings.isEnabled = false
            timer_sett.cancel()
            timer_sett = Timer()
            timer_sett.schedule(MainActivity.cmd_timeout_ms) {
                setResult(Activity.RESULT_OK)
                finish()
            }
            MainActivity.btth_w.sendString("#F")
        }

        //Tilt sensor
        val sensorListener: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(mSensorEvent: SensorEvent) {
                val X_Axis = mSensorEvent.values[0]
                val Y_Axis = mSensorEvent.values[1]
                var angle = Math.atan2(X_Axis.toDouble(), Y_Axis.toDouble()) / (Math.PI / 180)
                if (angle < 0) {
                    angle = if (angle > -90) {
                        0.0
                    } else {
                        180.0
                    }
                }
                tiltangle = angle
                if (angle >= 90) {
                    image_tilt.rotation = angle.toFloat() - 90
                } else if (angle < 90) {
                    image_tilt.rotation = 360 - (90 - angle.toFloat())
                } else if (angle == 90.0) {
                    image_tilt.rotation = 0f
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
        }

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            100000
        )

        //Accelerator
        //!TBD

        //Voice commands
        /*speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        speechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
        val listener = SpeechRecognitionListener()
        speechRecognizer!!.setRecognitionListener(listener)

        button_cmd.setOnTouchListener(object : View.OnTouchListener() {
            fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> if (ContextCompat.checkSelfPermission(
                            JoystickActivity,
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            JoystickActivity,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            MICPERMISSION
                        )
                    } else {
                        MainActivity.vibrate(this)
                        mSpeechRecognizer.cancel()
                        mSpeechRecognizer.stopListening()
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                        command_down = 1
                    }
                    MotionEvent.ACTION_UP -> if (ContextCompat.checkSelfPermission(
                            FJoystickActivity,
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            FJoystickActivity,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            124
                        )
                    } else {
                        command_down = 0
                        mSpeechRecognizer.cancel()
                        mSpeechRecognizer.stopListening()
                        text_cmd.text = ""
                        if (cHandler != null) {
                            cHandler.removeCallbacks(cAction)
                            cHandler = null
                        }
                    }
                }
                return false
            }
        })*/
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
    }

    //Voice commands
/*    var commandtmp = 0

    protected class SpeechRecognitionListener : RecognitionListener {
        override fun onBeginningOfSpeech() {
            //DebugTxt.setText("Begining");
        }

        override fun onBufferReceived(buffer: ByteArray) {
            //DebugTxt.setText("Buffer received");
        }

        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            if (command_down === 1) {
                if (error == SpeechRecognizer.ERROR_NETWORK || error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT || error == SpeechRecognizer.ERROR_SERVER) {
                    //Network Error
                    mSpeechRecognizer.cancel()
                    mSpeechRecognizer.stopListening()
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                } else if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                    //DebugTxt.setText("ERR SOFTW");
                    mSpeechRecognizer.cancel()
                    mSpeechRecognizer.stopListening()
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                    //ERROR Software
                } else if (error == SpeechRecognizer.ERROR_CLIENT) {
                    //TBD!!!
                    //DebugTxt.setText("ERR CLIENT");
                    //ERROR Client
                } else if (error == SpeechRecognizer.ERROR_AUDIO) {
                    //DebugTxt.setText("ERR AUDIO");
                    //ERROR No Audio
                } else if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                    val intent = Intent(this@JoystickActivity, ConnectActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("Exit me", true)
                    startActivity(intent)
                    finish()
                    //DebugTxt.setText("ERR PERM");
                    //ERROR No permission to access microphone
                } else if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    //Error NO MATCH, SPEECH TIMEOUT
                    mSpeechRecognizer.cancel()
                    mSpeechRecognizer.stopListening()
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                }
            }
            //DebugTxt.setText("Erorlar");
        }

        override fun onEvent(eventType: Int, params: Bundle) {}
        override fun onPartialResults(partialResults: Bundle) {
            /*ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String ttext = "";
            int msize = matches.size();
            int i = 0;
            while(i != msize){
                ttext = ttext.concat(matches.get(i));
                ttext = ttext.concat(" ");
                i++;
            }
            DebugTxt.setText(ttext);*/
            //DebugTxt.setText("Partial Results");
        }

        override fun onReadyForSpeech(params: Bundle) {
            //DebugTxt.setText("Ready for speech");
        }

        private fun SendCode(commandId: Int) {
            if (commandId == 2 || commandId == 3 || commandId == 4) {
                bluetooth_send("#1" + "-" + java.lang.String.format("%01d", Speed))
            } else if (commandId == 5) {
                bluetooth_send("#2" + "-" + java.lang.String.format("%01d", Speed))
            } else if (commandId == 6) {
                bluetooth_send("#3" + "-" + java.lang.String.format("%01d", Speed))
            } else if (commandId == 7) {
                bluetooth_send("#4" + "-" + java.lang.String.format("%01d", Speed))
            } else if (commandId == 8) {
                bluetooth_send("#5" + "-" + java.lang.String.format("%01d", Speed))
            } else if (commandId == 9) {
                bluetooth_send("#6" + "-" + java.lang.String.format("%01d", Speed))
            } else if (commandId == 10) {
                bluetooth_send("#D1")
            } else if (commandId == 11) {
                bluetooth_send("#D2")
            } else if (commandId == 12) {
                bluetooth_send("#D3")
            }
        }

        private val vCsize = 17
        private val vCommands = arrayOf(
            "stop", "robot stop",
            "forward", "go", "robot go",
            "back",
            "right",
            "left",
            "slide left",
            "slide right",
            "swim",
            "workout",
            "hello",
            "faster", "go faster",
            "slower", "slow down"
        )

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            //Parse HERE, matches.get(0)
            var resTx = matches!![0]
            if (resTx.length > 32) {
                resTx = resTx.substring(0, 32)
            }
            resTx =
                resTx.substring(0, 1).uppercase(Locale.getDefault()) + resTx.substring(1) + "..."
            var i = 0
            while (i != vCsize) {
                if (matches[0].equals(vCommands[i])) {
                    setCmdText(true, resTx)
                    //Run command HERE
                    if (i == 0 || i == 1) {
                        commandtmp = i
                        if (cHandler != null) {
                            cHandler.removeCallbacks(cAction)
                            cHandler = null
                        }
                    } else if (i == 13 || i == 14) {
                        if (Speed < 3) {
                            Speed++
                        }
                        if (Speed === 1) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_1)
                        } else if (Speed === 2) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_2)
                        } else if (Speed === 3) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_3)
                        }
                    } else if (i == 15 || i == 16) {
                        if (Speed > 1) {
                            Speed--
                        }
                        if (Speed === 1) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_1)
                        } else if (Speed === 2) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_2)
                        } else if (Speed === 3) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_3)
                        }
                    } else {
                        commandtmp = i
                        if (cHandler != null) {
                            cHandler.removeCallbacks(cAction)
                            cHandler = null
                        }
                        cHandler = Handler()
                        cAction = object : Runnable {
                            override fun run() {
                                SendCode(commandtmp)
                                cHandler.postDelayed(this, 40)
                            }
                        }
                        cHandler.postDelayed(cAction, 40)
                    }
                    i = vCsize - 1
                } else if (i + 1 == vCsize) {
                    setCmdText(false, resTx)
                }
                i++
            }

            //Restart
            if (command_down === 1) {
                mSpeechRecognizer.cancel()
                mSpeechRecognizer.stopListening()
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
            }
        }

        override fun onRmsChanged(rmsdB: Float) {}
    }
*/
    private fun setCmdText(color: Boolean, txt: String) {
        if (color) {
            text_cmd.setTextColor(Color.parseColor("#ffffff"))
            text_cmd.setShadowLayer(12f, 0f, 0f, Color.parseColor("#01b7fa"))
        } else {
            text_cmd.setTextColor(Color.parseColor("#ffaaaa"))
            text_cmd.setShadowLayer(12f, 0f, 0f, Color.parseColor("#ff1d1d"))
        }
        text_cmd.setText(txt)
    }
}