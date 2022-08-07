/*
                 |   Press   |   Cycle   |   Release    |
LeftJoystick        #L00-00     #L00-00
Speed             Changes value "Speed" from 1 to 3
Up                    #1-3         #1-3
Down                  #2-3         #2-3
TurnRight             #3-3         #3-3
TurnLeft              #4-3         #4-3
Left                  #5-3         #5-3
Right                 #6-3         #6-3
Accelerate          #K000-00     #K000-00
F1                    #F1           #F1
F2                    #F2           #F2
F3                    #F3           #F3
D1                    #D1           #D1
D2                    #D2           #D2
D3                    #D3           #D3
Settings        Launch Settings intent
Editor          Launch Editor intent
Command         Voice command
Disconnect      Finish activity
*/

package me.rocket_scientist.quadruped;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import io.github.controlwear.virtual.joystick.android.JoystickView;

import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.view.MotionEvent;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import java.util.ArrayList;
import java.util.Locale;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class JoystickActivity extends FragmentActivity {
    public static int command_down = 0;
    public static VerticalSeekbarClass AccelerateSeekbar;
    private Handler cHandler;
    Runnable cAction;
    ImageButton Button_Speed;
    //  #0 Common
    TextView DebugTxt;
    Vibrator vibrator;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
//  #1 LeftJoystick
    int LJStrengthTxt = 0;
    int LJAngleTxt = 0;
//  #2 Three speed buttons and switch
    int Speed = 3;
//  #3 Accelerate slider and gyroscope
    ImageView ImageGravity;
    int acclevel = 0;
    double tiltangle;
//  #5 Settings and editor
    Intent SettingsIntent;
    Intent EditorIntent;
    public static JoystickActivity FJoystickActivity;
    public static boolean RJoystickActivity = false;
    @Override public void onStart(){
        super.onStart();
        RJoystickActivity = true;
    }
    @Override public void onStop(){
        super.onStop();
        RJoystickActivity = false;
    }
    static final public int MICPERMISSION = 124;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MICPERMISSION: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    /*Intent intent = new Intent(this, ConnectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Exit me", true);
                    startActivity(intent);
                    finish();*/
                }
            }
        }
        return;
    }
    @Override public void onResume(){
        super.onResume();
        final ViewTreeObserver observer = AccelerateSeekbar.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressLint("NewApi")
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {

                        // Ensure you call it only once :
                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            AccelerateSeekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        else {
                            AccelerateSeekbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        Drawable d = getResources().getDrawable(R.drawable.joystick_acc_button_rel);
                        AccelerateSeekbar.setThumb(resizeTh(d,AccelerateSeekbar.getWidth()));
                        AccelerateSeekbar.setProgressAndThumb(0);
                    }
                });
        if(!ConnectActivity.bluetoothSerial.isConnected()){
            finish();
        }
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        FJoystickActivity = this;
        RJoystickActivity = true;
/// #0 Common
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        DebugTxt = (TextView) findViewById(R.id.debugTextView);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

/// #1 LeftJoystick
        final JoystickView joystickL = (JoystickView) findViewById(R.id.joystickViewL);
        joystickL.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                int tmpangle = 0;
                if(angle == 0){
                    tmpangle = 360;
                }
                else{
                    tmpangle = angle;
                }
                tmpangle = 360 - tmpangle;
                if(tmpangle > 90){
                    tmpangle = 90;
                }else if(tmpangle < 1){
                    tmpangle = 0;
                }
                int tmpstrength = strength;
                if(tmpstrength > 80){
                    tmpstrength = 80;
                }else if(tmpstrength < 1 ){
                    tmpstrength = 0;
                }
                //DebugTxt.setText(("#L").concat(String.format("%03d", tmpstrength)).concat("-").concat(String.format("%03d", tmpangle)));
                LJStrengthTxt = tmpstrength;
                LJAngleTxt = tmpangle;
            }
        });
        joystickL.setOnTouchListener(new JoystickView.OnTouchListener() {
            private Handler mHandler;
            @Override
            public boolean onTouch(View view, MotionEvent event ) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    joystickL.setButtonDrawable(getResources().getDrawable(R.drawable.joystick_button_pressed));
                    vibrator.vibrate(40);
                    if (mHandler != null) return true;
                    mHandler = new Handler();
                    mHandler.postDelayed(mAction, 40);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    joystickL.setButtonDrawable(getResources().getDrawable(R.drawable.joystick_button));
                    if (mHandler == null) return true;
                    mHandler.removeCallbacks(mAction);
                    mHandler = null;
                }
                return false;
            }
            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send(("#L").concat(String.format("%03d", LJAngleTxt)).concat("-").concat(String.format("%02d", LJStrengthTxt)));
                    mHandler.postDelayed(this, 40);
                }
            };
        });
/// ## Command button
        final ImageButton Button_Command = (ImageButton) findViewById(R.id.command);
        Button_Command.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(ContextCompat.checkSelfPermission(FJoystickActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(FJoystickActivity, new String[]{Manifest.permission.RECORD_AUDIO}, MICPERMISSION);
                        }
                        else{
                            vibrator.vibrate(40);
                            mSpeechRecognizer.cancel();
                            mSpeechRecognizer.stopListening();
                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                            command_down = 1;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(ContextCompat.checkSelfPermission(FJoystickActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(FJoystickActivity, new String[]{Manifest.permission.RECORD_AUDIO}, 124);
                        }
                        else{
                            command_down = 0;
                            mSpeechRecognizer.cancel();
                            mSpeechRecognizer.stopListening();
                            clearDebugTxt();
                            if(cHandler != null){
                                cHandler.removeCallbacks(cAction);
                                cHandler = null;
                            }
                        }
                        break;
                }
                return false;
            }
        });

/// #2 Three speed buttons
        Button_Speed = (ImageButton) findViewById(R.id.speed);
        Button_Speed.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        //Button_Speed.setImageResource(R.drawable.joystick_speed_prs);
                        if(Speed < 3){
                            Speed++;
                        }
                        else{
                            Speed = 1;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(Speed == 1){
                            Button_Speed.setImageResource(R.drawable.joystick_speed_1);
                        }
                        else if(Speed == 2){
                            Button_Speed.setImageResource(R.drawable.joystick_speed_2);
                        }
                        else if(Speed == 3){
                            Button_Speed.setImageResource(R.drawable.joystick_speed_3);
                        }
                        break;
                }
                return false;
            }
        });
        ImageButton Button_Up = (ImageButton) findViewById(R.id.button_up);
        Button_Up.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send(("#1").concat("-").concat(String.format("%01d", Speed)));
                    mHandler.postDelayed(this, 40);
                }
            };

        });
        ImageButton Button_Down = (ImageButton) findViewById(R.id.button_down);
        Button_Down.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send(("#2").concat("-").concat(String.format("%01d", Speed)));
                    mHandler.postDelayed(this, 40);
                }
            };

        });
        ImageButton Button_Turn_Right = (ImageButton) findViewById(R.id.button_turn_right);
        Button_Turn_Right.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send(("#3").concat("-").concat(String.format("%01d", Speed)));
                    mHandler.postDelayed(this, 40);
                }
            };

        });
        ImageButton Button_Turn_Left = (ImageButton) findViewById(R.id.button_turn_left);
        Button_Turn_Left.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send(("#4").concat("-").concat(String.format("%01d", Speed)));
                    mHandler.postDelayed(this, 40);
                }
            };

        });
        ImageButton Button_Left = (ImageButton) findViewById(R.id.button_left);
        Button_Left.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send(("#5").concat("-").concat(String.format("%01d", Speed)));
                    mHandler.postDelayed(this, 40);
                }
            };

        });
        ImageButton Button_Right = (ImageButton) findViewById(R.id.button_right);
        Button_Right.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send(("#6").concat("-").concat(String.format("%01d", Speed)));
                    mHandler.postDelayed(this, 40);
                }
            };

        });

/// #1 Accelerate slider and gyroscope
        ImageGravity = (ImageView) findViewById(R.id.imageGravity);
        AccelerateSeekbar=(VerticalSeekbarClass)findViewById(R.id.accelerate_seekbar);
        AccelerateSeekbar.setOnSeekBarChangeListener(new AppCompatSeekBar.OnSeekBarChangeListener() {
            private Handler mHandler;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userAction) {
                acclevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Drawable d = getResources().getDrawable(R.drawable.joystick_acc_button_prs);
                AccelerateSeekbar.setThumb(resizeTh(d,AccelerateSeekbar.getWidth()));
                vibrator.vibrate(40);
                if (mHandler != null) return;
                mHandler = new Handler();
                mHandler.postDelayed(mAction, 40);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Drawable d = getResources().getDrawable(R.drawable.joystick_acc_button_rel);
                AccelerateSeekbar.setThumb(resizeTh(d,AccelerateSeekbar.getWidth()));
                AccelerateSeekbar.setProgressAndThumb(0);
                //DebugTxt.setText("#RELEASE");
                if (mHandler == null) return;
                mHandler.removeCallbacks(mAction);
                mHandler = null;
            }
            Runnable mAction = new Runnable() {
                @Override public void run() {
                    //DebugTxt.setText(("#K").concat(String.format("%03d", (int)tiltangle)));
                    //DebugTxt.setText(("#K").concat(String.format("%03d", (int)tiltangle)).concat("-").concat(String.format("%02d", (int)acclevel)));//tiltangle
                    bluetooth_send(("#K").concat(String.format("%03d", (int)tiltangle)).concat("-").concat(String.format("%02d", (int)acclevel)));//tiltangle
					//DebugTxt.setText(("#K").concat(String.format("%03d", (int)tiltangle)).concat("-").concat(String.format("%02d", (int)acclevel)));
                    mHandler.postDelayed(this, 40);//40
                }
            };

        });
        final ViewTreeObserver observer = AccelerateSeekbar.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressLint("NewApi")
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {

                        // Ensure you call it only once :
                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            AccelerateSeekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        else {
                            AccelerateSeekbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        Drawable d = getResources().getDrawable(R.drawable.joystick_acc_button_rel);
                        AccelerateSeekbar.setThumb(resizeTh(d,AccelerateSeekbar.getWidth()));
                        AccelerateSeekbar.setProgressAndThumb(0);
                    }
                });
        final SensorEventListener mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent mSensorEvent) {
                float X_Axis = mSensorEvent.values[0];
                float Y_Axis = mSensorEvent.values[1];
                double angle = Math.atan2(X_Axis, Y_Axis)/(Math.PI/180);
                if(angle < 0){
                    if(angle > -90){
                        angle = 0;
                    }
                    else{
                        angle = 180;
                    }
                }
                //DebugTxt.setText(String.valueOf((int)angle));
                tiltangle = angle;
                if(angle >= 90){
                    ImageGravity.setRotation((float)angle - 90);
                }
                else if(angle < 90){
                    ImageGravity.setRotation(360 - (90 -(float)angle));
                }
                else if(angle == 90){
                    ImageGravity.setRotation(0);
                }
            }
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //TBD
            }
        };
        SensorManager mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),100000);
/// #4 Static switch buttons
        ImageButton Button_D1 = (ImageButton) findViewById(R.id.d1);
        Button_D1.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send("#D1");
                    mHandler.postDelayed(this, 40);
                }
            };

        });
        ImageButton Button_D2 = (ImageButton) findViewById(R.id.d2);
        Button_D2.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send("#D2");
                    mHandler.postDelayed(this, 40);
                }
            };

        });
        ImageButton Button_D3 = (ImageButton) findViewById(R.id.d3);
        Button_D3.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(40);
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 40);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    bluetooth_send("#D3");
                    mHandler.postDelayed(this, 40);
                }
            };

        });
/// #5 Settings and Editor
        SettingsIntent = new Intent(JoystickActivity.this, SettingsActivity.class);
        ImageButton Button_Settings = (ImageButton) findViewById(R.id.settings);
        Button_Settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(SettingsIntent);
            }
        });
        ImageButton ButtonDisconnect = (ImageButton) findViewById(R.id.button_disconnect);
        ButtonDisconnect.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                finish();
            }
        });
    }

//// #0 Common functions
    public void bluetooth_send(String msg){
        ConnectActivity.bluetoothSerial.write(msg);
    }

    static int commandtmp;
    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            //DebugTxt.setText("Begining");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {
            //DebugTxt.setText("Buffer received");
        }

        @Override
        public void onEndOfSpeech()
        {

        }

        @Override
        public void onError(int error)
        {
            if(command_down == 1){
                if((error == SpeechRecognizer.ERROR_NETWORK)||(error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT)||(error == SpeechRecognizer.ERROR_SERVER)){
                    //Network Error
                    mSpeechRecognizer.cancel();
                    mSpeechRecognizer.stopListening();
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }else if(error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY){
                    //DebugTxt.setText("ERR SOFTW");
                    mSpeechRecognizer.cancel();
                    mSpeechRecognizer.stopListening();
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    //ERROR Software
                }else if(error == SpeechRecognizer.ERROR_CLIENT){
                    //TBD!!!
                    //DebugTxt.setText("ERR CLIENT");
                    //ERROR Client
                }else if(error == SpeechRecognizer.ERROR_AUDIO){
                    //DebugTxt.setText("ERR AUDIO");
                    //ERROR No Audio
                }else if(error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS){
                    Intent intent = new Intent(JoystickActivity.this, ConnectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Exit me", true);
                    startActivity(intent);
                    finish();
                    //DebugTxt.setText("ERR PERM");
                    //ERROR No permission to access microphone
                }else if((error == SpeechRecognizer.ERROR_NO_MATCH)||(error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)){
                    //Error NO MATCH, SPEECH TIMEOUT
                    mSpeechRecognizer.cancel();
                    mSpeechRecognizer.stopListening();
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }
            }
            //DebugTxt.setText("Erorlar");
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {
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

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            //DebugTxt.setText("Ready for speech");
        }

        private void SendCode(int commandId){
            if((commandId == 2)||(commandId == 3)||(commandId == 4)){
                bluetooth_send(("#1").concat("-").concat(String.format("%01d", Speed)));
            }else if(commandId == 5){
                bluetooth_send(("#2").concat("-").concat(String.format("%01d", Speed)));
            }else if(commandId == 6){
                bluetooth_send(("#3").concat("-").concat(String.format("%01d", Speed)));
            }else if(commandId == 7){
                bluetooth_send(("#4").concat("-").concat(String.format("%01d", Speed)));
            }else if(commandId == 8){
                bluetooth_send(("#5").concat("-").concat(String.format("%01d", Speed)));
            }else if(commandId == 9){
                bluetooth_send(("#6").concat("-").concat(String.format("%01d", Speed)));
            }else if(commandId == 10){
                bluetooth_send("#D1");
            }else if(commandId == 11){
                bluetooth_send("#D2");
            }else if(commandId == 12){
                bluetooth_send("#D3");
            }
        }

        private int vCsize = 17;
        private String[] vCommands = {
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
        };

        @Override
        public void onResults(Bundle results)
        {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //Parse HERE, matches.get(0)
            String resTx = matches.get(0);
            if(resTx.length() > 32){
                resTx = resTx.substring(0,32);
            }
            resTx = (resTx.substring(0,1).toUpperCase() + resTx.substring(1)).concat("...");
            int i = 0;
            while(i != vCsize){
                if(matches.get(0).equals(vCommands[i])) {
                    setDebugTxt(true, resTx);
                    //Run command HERE
                    if ((i == 0) || (i == 1)) {
                        commandtmp = i;
                        if (cHandler != null) {
                            cHandler.removeCallbacks(cAction);
                            cHandler = null;
                        }
                    } else if ((i == 13) || (i == 14)) {
                        if (Speed < 3) {
                            Speed++;
                        }
                        if (Speed == 1) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_1);
                        } else if (Speed == 2) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_2);
                        } else if (Speed == 3) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_3);
                        }
                    } else if ((i == 15) || (i == 16)) {
                        if (Speed > 1) {
                            Speed--;
                        }
                        if (Speed == 1) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_1);
                        } else if (Speed == 2) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_2);
                        } else if (Speed == 3) {
                            Button_Speed.setImageResource(R.drawable.joystick_speed_3);
                        }
                    }else{
                        commandtmp = i;
                        if(cHandler != null) {
                            cHandler.removeCallbacks(cAction);
                            cHandler = null;
                        }
                        cHandler = new Handler();
                        cAction = new Runnable(){
                            @Override
                            public void run() {
                                SendCode(commandtmp);
                                cHandler.postDelayed(this, 40);
                            }
                        };
                        cHandler.postDelayed(cAction, 40);
                    }
                    i = vCsize-1;
                }
                else if((i+1) == vCsize){
                    setDebugTxt(false, resTx);
                }
                i++;
            }

            //Restart
            if((command_down == 1)){
                mSpeechRecognizer.cancel();
                mSpeechRecognizer.stopListening();
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }
    private void clearDebugTxt(){
        DebugTxt.setText("");
    }
    private void setDebugTxt(boolean color, String txt){
        if(color){
            DebugTxt.setTextColor(Color.parseColor("#ffffff"));
            DebugTxt.setShadowLayer(12,0,0,Color.parseColor("#01b7fa"));
        }else{
            DebugTxt.setTextColor(Color.parseColor("#ffaaaa"));
            DebugTxt.setShadowLayer(12,0,0,Color.parseColor("#ff1d1d"));
        }
        DebugTxt.setText(txt);
    }

    private Drawable resizeTh(Drawable image, int newwidth) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        int newheight = (int)(((float)newwidth/(float)b.getWidth())*(float)b.getHeight());
        float dividefactor = (float)newheight/(float)newwidth;
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, (int)((float)newwidth/dividefactor), (int)((float)newheight/dividefactor), false);
        AccelerateSeekbar.wh = (int)((float)newwidth/dividefactor);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}

