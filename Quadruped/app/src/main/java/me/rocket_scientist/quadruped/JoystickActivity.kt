package me.rocket_scientist.quadruped

import android.Manifest.permission.RECORD_AUDIO
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
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.rocket_scientist.rsuielements.RsAngleStick
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
    //speed
    private var speed = 1
    //Command sending timer
    private var timer_cmd = Timer()
    //Settings responce timeout timer
    private var timer_sett = Timer()
    //Voice commands
    private lateinit var speech_recognizer: SpeechRecognizer
    private lateinit var speech_recognizer_intent: Intent
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
    private lateinit var anglestick_left: RsAngleStick
    //Private
    private var tiltangle: Double = 0.0
    private var acc_level = 0
    private var stickangle: Int = 0
    private var stickstrength: Int = 0
    private val cntxt = this

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
        anglestick_left = findViewById(R.id.RsAngleStick_Left)

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
        slider_acc.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private var sbtimer = Timer()

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, userAction: Boolean) {
                acc_level = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                MainActivity.vibrate(cntxt)
                sbtimer.cancel()
                sbtimer = Timer()
                sbtimer.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                    MainActivity.btth_w.sendString("#K" + String.format("%03d", tiltangle.toInt()) + "-" + String.format("%02d", acc_level))
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                sbtimer.cancel()
                acc_level = 0
                slider_acc.progress = acc_level
            }

        })

        //AngleStick
        anglestick_left.setOnMoveListener(object : RsAngleStick.OnMoveListener {

            override fun onMove(angle: Int, strength: Int) {
                stickstrength = strength
                stickangle = angle
            }

        })

        anglestick_left.setOnTouchListener(object : View.OnTouchListener {
            private var sbtimer = Timer()

            override fun onTouch(view: View?, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    MainActivity.vibrate(cntxt)
                    sbtimer.cancel()
                    sbtimer = Timer()
                    sbtimer.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                        MainActivity.btth_w.sendString("#L" + String.format("%02d", stickangle) + "-" + String.format("%02d", stickstrength))
                    }
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    sbtimer.cancel()
                }
                return false
            }

        })

        //Voice commands
        speech_recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speech_recognizer_intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speech_recognizer_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        speech_recognizer_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
        val speech_listener = SpeechRecognitionListener()
        speech_recognizer.setRecognitionListener(speech_listener)

        button_cmd.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(view: View?, event: MotionEvent): Boolean {

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (ContextCompat.checkSelfPermission(cntxt, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(cntxt, arrayOf(RECORD_AUDIO), 124 )
                        } else {
                            MainActivity.vibrate(cntxt)
                            speech_recognizer.cancel()
                            speech_recognizer.stopListening()
                            speech_recognizer.startListening(speech_recognizer_intent)
                        }
                    }
                    MotionEvent.ACTION_UP -> if (ContextCompat.checkSelfPermission(cntxt, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(cntxt, arrayOf(RECORD_AUDIO), 124)
                    } else {
                        speech_recognizer.cancel()
                        speech_recognizer.stopListening()
                        setCmdText(true, "")
                        speech_listener.sptimer.cancel()
                    }
                }

                return false
            }

        })

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
    inner class SpeechRecognitionListener : RecognitionListener {
        private var current_cmd: Int = 0
        var sptimer = Timer()

        override fun onError(error: Int) {
            if (button_cmd.isPressed) {
                if (error == SpeechRecognizer.ERROR_NETWORK || error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT || error == SpeechRecognizer.ERROR_SERVER) {
                    speech_recognizer.cancel()
                    speech_recognizer.stopListening()
                    speech_recognizer.startListening(speech_recognizer_intent)
                } else if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                    speech_recognizer.cancel()
                    speech_recognizer.stopListening()
                    speech_recognizer.startListening(speech_recognizer_intent)
                } else if (error == SpeechRecognizer.ERROR_CLIENT) {
                    //!UNUSED
                } else if (error == SpeechRecognizer.ERROR_AUDIO) {
                    //!UNUSED
                } else if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                    //!UNUSED
                } else if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    speech_recognizer.cancel()
                    speech_recognizer.stopListening()
                    speech_recognizer.startListening(speech_recognizer_intent)
                }
            }
        }

        private fun SendCmd(commandId: Int) {
            if (commandId == 2 || commandId == 3 || commandId == 4) {
                MainActivity.btth_w.sendString("#1" + "-" + String.format("%01d", speed))
            } else if (commandId == 5) {
                MainActivity.btth_w.sendString("#2" + "-" + String.format("%01d", speed))
            } else if (commandId == 6) {
                MainActivity.btth_w.sendString("#3" + "-" + String.format("%01d", speed))
            } else if (commandId == 7) {
                MainActivity.btth_w.sendString("#4" + "-" + String.format("%01d", speed))
            } else if (commandId == 8) {
                MainActivity.btth_w.sendString("#5" + "-" + String.format("%01d", speed))
            } else if (commandId == 9) {
                MainActivity.btth_w.sendString("#6" + "-" + String.format("%01d", speed))
            } else if (commandId == 10) {
                MainActivity.btth_w.sendString("#D1")
            } else if (commandId == 11) {
                MainActivity.btth_w.sendString("#D2")
            } else if (commandId == 12) {
                MainActivity.btth_w.sendString("#D3")
            }
        }

        private val commands_count = 17
        private val commands = arrayOf(
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
            var result = matches!![0]
            if (result.length > 32) {
                result = result.substring(0, 32)
            }
            result = result.substring(0, 1).uppercase(Locale.getDefault()) + result.substring(1) + "..."
            var i = 0
            while (i != commands_count) {
                if (matches[0].equals(commands[i])) {
                    setCmdText(true, result)
                    //Run command HERE
                    if (i == 0 || i == 1) {
                        current_cmd = i
                        sptimer.cancel()
                    } else if (i == 13 || i == 14) {
                        if (speed < 3) {
                            speed++
                        }
                        if (speed == 1) {
                            button_speed.setImageResource(R.drawable.ja_speed_1)
                        } else if (speed == 2) {
                            button_speed.setImageResource(R.drawable.ja_speed_2)
                        } else if (speed == 3) {
                            button_speed.setImageResource(R.drawable.ja_speed_3)
                        }
                    } else if (i == 15 || i == 16) {
                        if (speed > 1) {
                            speed--
                        }
                        if (speed == 1) {
                            button_speed.setImageResource(R.drawable.ja_speed_1)
                        } else if (speed == 2) {
                            button_speed.setImageResource(R.drawable.ja_speed_2)
                        } else if (speed == 3) {
                            button_speed.setImageResource(R.drawable.ja_speed_3)
                        }
                    } else {
                        current_cmd = i
                        sptimer.cancel()
                        sptimer = Timer()
                        sptimer.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval) {
                            SendCmd(current_cmd)
                        }
                    }
                    i = commands_count - 1
                } else if (i + 1 == commands_count) {
                    setCmdText(false, result)
                }
                i++
            }

            //Restart
            if (button_cmd.isPressed) {
                speech_recognizer.cancel()
                speech_recognizer.stopListening()
                speech_recognizer.startListening(speech_recognizer_intent)
            }
        }

        override fun onPartialResults(p0: Bundle?) {}
        override fun onEvent(p0: Int, p1: Bundle?) {}
        override fun onReadyForSpeech(p0: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(p0: ByteArray?) {}
        override fun onEndOfSpeech() {}
    }

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