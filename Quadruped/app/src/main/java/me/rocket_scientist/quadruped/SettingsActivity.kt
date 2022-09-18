package me.rocket_scientist.quadruped

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.rocket_scientist.rsuielements.RsVertSlider
import java.util.*

class SettingsActivity : AppCompatActivity() {
    //Commands from external activity
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when(intent?.action){
                "JoystickSettingsActivityEXIT" -> finish()
            }
        }
    }

    //Settings data
    private var m1 = 0
    private var m2 = 0
    private var m3 = 0
    private var m4 = 0
    private var m5 = 0
    private var m6 = 0
    private var m7 = 0
    private var m8 = 0
    private var led = 0

    //State machine
    private var cm = 4

    //UI
    private lateinit var slider: RsVertSlider
    private lateinit var button_inc: ImageButton
    private lateinit var button_dec: ImageButton
    private lateinit var button_m1: ImageButton
    private lateinit var button_m2: ImageButton
    private lateinit var button_m3: ImageButton
    private lateinit var button_m4: ImageButton
    private lateinit var button_m5: ImageButton
    private lateinit var button_m6: ImageButton
    private lateinit var button_m7: ImageButton
    private lateinit var button_m8: ImageButton
    private lateinit var text_m1: TextView
    private lateinit var text_m2: TextView
    private lateinit var text_m3: TextView
    private lateinit var text_m4: TextView
    private lateinit var text_m5: TextView
    private lateinit var text_m6: TextView
    private lateinit var text_m7: TextView
    private lateinit var text_m8: TextView
    private lateinit var button_close: ImageButton
    private lateinit var edit_text_name: EditText
    private lateinit var edit_text_pwd: EditText
    private lateinit var button_save: ImageButton
    private lateinit var button_led: ImageButton

    private fun vibrate() {
        MainActivity.vibrate(this)
    }
    private fun btnSelM() {
        button_m1.setImageResource(R.drawable.sa_sw_unsel)
        button_m2.setImageResource(R.drawable.sa_sw_unsel)
        button_m3.setImageResource(R.drawable.sa_sw_unsel)
        button_m4.setImageResource(R.drawable.sa_sw_unsel)
        button_m5.setImageResource(R.drawable.sa_sw_unsel)
        button_m6.setImageResource(R.drawable.sa_sw_unsel)
        button_m7.setImageResource(R.drawable.sa_sw_unsel)
        button_m8.setImageResource(R.drawable.sa_sw_unsel)
        when(cm){
            1 -> button_m1.setImageResource(R.drawable.sa_sw_sel)
            2 -> button_m2.setImageResource(R.drawable.sa_sw_sel)
            3 -> button_m3.setImageResource(R.drawable.sa_sw_sel)
            4 -> button_m4.setImageResource(R.drawable.sa_sw_sel)
            5 -> button_m5.setImageResource(R.drawable.sa_sw_sel)
            6 -> button_m6.setImageResource(R.drawable.sa_sw_sel)
            7 -> button_m7.setImageResource(R.drawable.sa_sw_sel)
            8 -> button_m8.setImageResource(R.drawable.sa_sw_sel)
        }
    }
    private fun btnSendM(){
        when(cm){
            1 -> {
                text_m1.text = m1.toString()
                if(m1 < 0){
                    MainActivity.btth_w.sendString("#I" + m1.toString() + "1")
                }else{
                    MainActivity.btth_w.sendString("#I" + "+" + m1.toString() + "1")
                }
            }
            2 -> {
                text_m2.text = m2.toString()
                if(m2 < 0){
                    MainActivity.btth_w.sendString("#I" + m2.toString() + "2")
                }else{
                    MainActivity.btth_w.sendString("#I" + "+" + m2.toString() + "2")
                }
            }
            3 -> {
                text_m3.text = m3.toString()
                if(m3 < 0){
                    MainActivity.btth_w.sendString("#I" + m3.toString() + "3")
                }else{
                    MainActivity.btth_w.sendString("#I" + "+" + m3.toString() + "3")
                }
            }
            4 -> {
                text_m4.text = m4.toString()
                if(m4 < 0){
                    MainActivity.btth_w.sendString("#I" + m4.toString() + "4")
                }else{
                    MainActivity.btth_w.sendString("#I" + "+" + m4.toString() + "4")
                }
            }
            5 -> {
                text_m5.text = m5.toString()
                if(m5 < 0){
                    MainActivity.btth_w.sendString("#I" + m5.toString() + "5")
                }else{
                    MainActivity.btth_w.sendString("#I" + "+" + m5.toString() + "5")
                }
            }
            6 -> {
                text_m6.text = m6.toString()
                if(m6 < 0){
                    MainActivity.btth_w.sendString("#I" + m6.toString() + "6")
                }else{
                    MainActivity.btth_w.sendString("#I" + "+" + m6.toString() + "6")
                }
            }
            7 -> {
                text_m7.text = m7.toString()
                if(m7 < 0){
                    MainActivity.btth_w.sendString("#I" + m7.toString() + "7")
                }else{
                    MainActivity.btth_w.sendString("#I" + "+" + m7.toString() + "7")
                }
            }
            8 -> {
                text_m8.text = m8.toString()
                if(m8 < 0){
                    MainActivity.btth_w.sendString("#I" + m8.toString() + "8")
                }else{
                    MainActivity.btth_w.sendString("#I" + "+" + m8.toString() + "8")
                }
            }
        }
    }
    private var timer = Timer()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //External exit command
        registerReceiver(broadCastReceiver, IntentFilter("JoystickSettingsActivityEXIT"))

        slider = findViewById(R.id.RsVertSlider_Slider)
        button_inc = findViewById(R.id.ImageButton_Inc)
        button_dec = findViewById(R.id.ImageButton_Dec)
        button_m1 = findViewById(R.id.ImageButton_M1)
        button_m2 = findViewById(R.id.ImageButton_M2)
        button_m3 = findViewById(R.id.ImageButton_M3)
        button_m4 = findViewById(R.id.ImageButton_M4)
        button_m5 = findViewById(R.id.ImageButton_M5)
        button_m6 = findViewById(R.id.ImageButton_M6)
        button_m7 = findViewById(R.id.ImageButton_M7)
        button_m8 = findViewById(R.id.ImageButton_M8)
        text_m1 = findViewById(R.id.TextView_MVal1)
        text_m2 = findViewById(R.id.TextView_MVal2)
        text_m3 = findViewById(R.id.TextView_MVal3)
        text_m4 = findViewById(R.id.TextView_MVal4)
        text_m5 = findViewById(R.id.TextView_MVal5)
        text_m6 = findViewById(R.id.TextView_MVal6)
        text_m7 = findViewById(R.id.TextView_MVal7)
        text_m8 = findViewById(R.id.TextView_MVal8)
        button_close = findViewById(R.id.ImageButton_Close)
        edit_text_name = findViewById(R.id.EditText_Name)
        edit_text_pwd = findViewById(R.id.EditText_Pwd)
        button_save = findViewById(R.id.ImageButton_Save)
        button_led = findViewById(R.id.ImageButton_Led)

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private var level = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, userAction: Boolean) {
                level = progress - 9
                when(cm){
                    1 -> {
                        m1 = level
                        text_m1.text = level.toString()
                    }
                    2 -> {
                        m2 = level
                        text_m2.text = level.toString()
                    }
                    3 -> {
                        m3 = level
                        text_m3.text = level.toString()
                    }
                    4 -> {
                        m4 = level
                        text_m4.text = level.toString()
                    }
                    5 -> {
                        m5 = level
                        text_m5.text = level.toString()
                    }
                    6 -> {
                        m6 = level
                        text_m6.text = level.toString()
                    }
                    7 -> {
                        m7 = level
                        text_m7.text = level.toString()
                    }
                    8 -> {
                        m8 = level
                        text_m8.text = level.toString()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                vibrate()
                timer.cancel()
                timer = Timer()
                timer.schedule(MainActivity.cmd_interval, MainActivity.cmd_interval){
                    if(level < 0){
                        MainActivity.btth_w.sendString("#I$level$cm")
                    }else{
                        MainActivity.btth_w.sendString("#I+$level$cm")
                    }
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                when(cm){
                    1 -> slider.progress = m1 + 9
                    2 -> slider.progress = m2 + 9
                    3 -> slider.progress = m3 + 9
                    4 -> slider.progress = m4 + 9
                    5 -> slider.progress = m5 + 9
                    6 -> slider.progress = m6 + 9
                    7 -> slider.progress = m7 + 9
                    8 -> slider.progress = m8 + 9
                }
                if (mHandler != null) {
                    mHandler!!.removeCallbacks(mAction)
                    mHandler = null
                }
                MainActivity.btth_w.sendString("#G")
            }
        })


        val touchVibr = View.OnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    vibrate()
                }
            }
            v?.onTouchEvent(event) ?: true
        }
        button_inc.setOnTouchListener(touchVibr)
        button_dec.setOnTouchListener(touchVibr)
        button_m1.setOnTouchListener(touchVibr)
        button_m2.setOnTouchListener(touchVibr)
        button_m3.setOnTouchListener(touchVibr)
        button_m4.setOnTouchListener(touchVibr)
        button_m5.setOnTouchListener(touchVibr)
        button_m6.setOnTouchListener(touchVibr)
        button_m7.setOnTouchListener(touchVibr)
        button_m8.setOnTouchListener(touchVibr)
        button_close.setOnTouchListener(touchVibr)
        button_save.setOnTouchListener(touchVibr)
        button_led.setOnTouchListener(touchVibr)

        button_inc.setOnClickListener{
            when(cm){
                1 -> { if(m1 < 9){ m1++ } }
                2 -> { if(m2 < 9){ m2++ } }
                3 -> { if(m3 < 9){ m3++ } }
                4 -> { if(m4 < 9){ m4++ } }
                5 -> { if(m5 < 9){ m5++ } }
                6 -> { if(m6 < 9){ m6++ } }
                7 -> { if(m7 < 9){ m7++ } }
                8 -> { if(m8 < 9){ m8++ } }
            }
            MainActivity.flash_store_rq = true
            btnSendM()
        }
        button_dec.setOnClickListener{
            when(cm){
                1 -> { if(m1 > -9){ m1-- } }
                2 -> { if(m2 > -9){ m2-- } }
                3 -> { if(m3 > -9){ m3-- } }
                4 -> { if(m4 > -9){ m4-- } }
                5 -> { if(m5 > -9){ m5-- } }
                6 -> { if(m6 > -9){ m6-- } }
                7 -> { if(m7 > -9){ m7-- } }
                8 -> { if(m8 > -9){ m8-- } }
            }
            MainActivity.flash_store_rq = true
            btnSendM()
        }
        button_m1.setOnClickListener{
            cm = 1
            btnSelM()
        }
        button_m2.setOnClickListener{
            cm = 2
            btnSelM()
        }
        button_m3.setOnClickListener{
            cm = 3
            btnSelM()
        }
        button_m4.setOnClickListener{
            cm = 4
            btnSelM()
        }
        button_m5.setOnClickListener{
            cm = 5
            btnSelM()
        }
        button_m6.setOnClickListener{
            cm = 6
            btnSelM()
        }
        button_m7.setOnClickListener{
            cm = 7
            btnSelM()
        }
        button_m8.setOnClickListener{
            cm = 8
            btnSelM()
        }
        button_close.setOnClickListener{
            MainActivity.btth_w.sendString("#H")
            finish()
        }
        button_led.setOnClickListener{
            led = 1 - led
            MainActivity.btth_w.sendString("#E$led")
            if(led == 1){
                button_led.setImageResource(R.drawable.sa_led_checked)
            }else{
                button_led.setImageResource(R.drawable.sa_led_unchecked)
            }
        }
        button_save.setOnClickListener{
            //Disable UI
            button_inc.isEnabled = false
            button_dec.isEnabled = false
            button_m1.isEnabled = false
            button_m2.isEnabled = false
            button_m3.isEnabled = false
            button_m4.isEnabled = false
            button_m5.isEnabled = false
            button_m6.isEnabled = false
            button_m7.isEnabled = false
            button_m8.isEnabled = false
            button_close.isEnabled = false
            button_save.isEnabled = false
            button_led.isEnabled = false
            //Send name/password update command
            var name = edit_text_name.text.toString()
            while(name.length < 10){
                name += " "
            }
            var pwd = edit_text_pwd.text.toString()
            while(pwd.length < 4){
                pwd += " "
            }
            MainActivity.btth_w.sendString("#J$name$pwd")
        }

        //Parse settings
        if(intent.getStringExtra("CONFIG")?.length == 34){
            val data = intent.getStringExtra("CONFIG").toString()
            //Calibration data
            var i = 0
            var res = 0
            while (i < 16) {
                res = data.elementAt(i + 3).code - 48
                if (data.elementAt(i + 2) == '-') {
                    res -= 2 * res
                }
                if (i == 0) {
                    m1 = res
                    text_m1.text = m1.toString()
                } else if (i == 2) {
                    m2 = res
                    text_m2.text = m2.toString()
                } else if (i == 4) {
                    m3 = res
                    text_m3.text = m3.toString()
                } else if (i == 6) {
                    m4 = res
                    text_m4.text = m4.toString()
                } else if (i == 8) {
                    m5 = res
                    text_m5.text = m5.toString()
                } else if (i == 10) {
                    m6 = res
                    text_m6.text = m6.toString()
                } else if (i == 12) {
                    m7 = res
                    text_m7.text = m7.toString()
                } else if (i == 14) {
                    m8 = res
                    text_m8.text = m8.toString()
                }
                i += 2
            }

            //Password
            i = data.indexOf(" ", 18)
            if((i < 18) || (i > 21)){
                edit_text_pwd.setText(data.substring(18,22))
            } else {
                edit_text_pwd.setText(data.substring(18,i))
            }
            //Name
            i = data.indexOf(" ", 22)
            if((i < 22) || (i > 31)){
                edit_text_name.setText(data.substring(22,32))
            }
            else{
                edit_text_name.setText(data.substring(22,i))
            }
            //LED
            if(data.elementAt(32) == '1'){
                button_led.setImageResource(R.drawable.sa_led_checked)
                led = 1
            }
            else{
                button_led.setImageResource(R.drawable.sa_led_unchecked)
                led = 0
            }
        }else{
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onBackPressed() {
        MainActivity.btth_w.sendString("#H")
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
    }
}