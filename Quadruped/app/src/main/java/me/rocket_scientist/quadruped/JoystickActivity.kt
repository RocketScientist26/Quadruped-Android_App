package me.rocket_scientist.quadruped

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import me.rocket_scientist.rsbt.RsBtDevMngr
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
    //Command sending interval
    private val cmd_interval: Long = 100;
    //Speed
    private var speed = 1
    //Command sending timer
    private var timer_cmd = Timer()
    //Settings responce timeout timer
    private var timer_sett = Timer()
    //UI
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

        //Enable button clicks
        button_swim_d1.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    MainActivity.vibrate(this)
                    timer_cmd = Timer()
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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
                    timer_cmd.schedule(cmd_interval, cmd_interval) {
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

    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
    }
}