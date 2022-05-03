/*
                    |   Press   |   Cycle   |   Release    |
CalibrateSlider        #C+1-0      #C+1-0

#SO gaxsna
#SC daxurva
#SD+11 gradusis sworeba
#SW daxsomeba gradusebis
#C-0-0-0-0-0-0-0-01234Quadruped 0

gamoertebis an #SC shemtxvevashi amowmebs fexit da dgoms asworebs
*/


package me.rocket_scientist.quadruped;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import me.rocket_scientist.quadruped.VerticalSeekbarClass;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends FragmentActivity {
//  #0 Common
    Vibrator vibrator;
    public static SettingsActivity FSettingsActivity;
    public static boolean RSettingsActivity = false;
    public int stoprun = 0;
    @Override public void onStop(){
        super.onStop();
        bluetooth_send("#SC");
        stoprun = 1;
        RSettingsActivity = false;
    }
    @Override public void onResume(){
        super.onResume();
        if(!ConnectActivity.bluetoothSerial.isConnected()){
            finish();
        }else if(stoprun == 1){
            stoprun = 0;
            bluetooth_send("#SO");
        }
    }
    static int ledsel = 1;
    static EditText Password;
    static EditText Name;
    static ImageButton Save;
    static ImageButton ButtonInc;
    static ImageButton ButtonDec;
    public static String calibresp = "#C-0-0-0-0-0-0-0-01234Quadruped 0";
    static ImageButton Led;
    public static void parseval(){
        int i = 0;
        int res = 0;
        while(i < 16){
            res = (int)calibresp.charAt(i+3)-48;
            if(calibresp.charAt(i+2) == '-'){
                res -= 2*res;
            }
            if(i == 0){
                mv1 = res;
            }
            else if(i == 2){
                mv2 = res;
            }
            else if(i == 4){
                mv3 = res;
            }
            else if(i == 6){
                mv4 = res;
            }
            else if(i == 8){
                mv5 = res;
            }
            else if(i == 10){
                mv6 = res;
            }
            else if(i == 12){
                mv7 = res;
            }
            else if(i == 14){
                mv8 = res;
            }
            i+=2;
        }
        Caliblevel = mv4;
        CalibMSel = 4;
        CalibrateSeekbar.setProgressAndThumb(Caliblevel+9);
        //Set progress in acompaning TextView
        Mv1.setText(String.valueOf(mv1));
        Mv2.setText(String.valueOf(mv2));
        Mv3.setText(String.valueOf(mv3));
        Mv4.setText(String.valueOf(mv4));
        Mv5.setText(String.valueOf(mv5));
        Mv6.setText(String.valueOf(mv6));
        Mv7.setText(String.valueOf(mv7));
        Mv8.setText(String.valueOf(mv8));

        //
        i = calibresp.indexOf(" ", 18);
        if((i < 18) || (i > 21)){
            Password.setText(calibresp.substring(18,22));
        }
        else{
            Password.setText(calibresp.substring(18,i));
        }
        i = calibresp.indexOf(" ", 22);
        if((i < 22) || (i > 31)){
            Name.setText(calibresp.substring(22,32));
        }
        else{
            Name.setText(calibresp.substring(22,i));
        }
        if(calibresp.charAt(32) == '1'){
            Led.setImageResource(R.drawable.settings_led_sel);
            ledsel = 1;
        }
        else{
            Led.setImageResource(R.drawable.settings_led_unsel);
            ledsel = 0;
        }
        enable_actions(true);
    }

//  #1 CalibrateSlider
    static VerticalSeekbarClass  CalibrateSeekbar;
    static int Caliblevel = 0;
    static int CalibMSel = 1;
    static int mv1 = 0;
    static int mv2 = 0;
    static int mv3 = 0;
    static int mv4 = 0;
    static int mv5 = 0;
    static int mv6 = 0;
    static int mv7 = 0;
    static int mv8 = 0;
    static TextView Mv1;
    static TextView Mv2;
    static TextView Mv3;
    static TextView Mv4;
    static TextView Mv5;
    static TextView Mv6;
    static TextView Mv7;
    static TextView Mv8;
//  #2 One to eight switch buttons0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
/// #0 Common
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        Password = (EditText) findViewById(R.id.password);
        Name = (EditText) findViewById(R.id.name);
/// #1 Slider
        Mv1 = (TextView) findViewById(R.id.mVal1);
        Mv2 = (TextView) findViewById(R.id.mVal2);
        Mv3 = (TextView) findViewById(R.id.mVal3);
        Mv4 = (TextView) findViewById(R.id.mVal4);
        Mv5 = (TextView) findViewById(R.id.mVal5);
        Mv6 = (TextView) findViewById(R.id.mVal6);
        Mv7 = (TextView) findViewById(R.id.mVal7);
        Mv8 = (TextView) findViewById(R.id.mVal8);
        CalibrateSeekbar = (VerticalSeekbarClass)findViewById(R.id.calibrate_seekbar);
        CalibrateSeekbar.setOnSeekBarChangeListener(new AppCompatSeekBar.OnSeekBarChangeListener() {
            private Handler mHandler;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userAction) {
                Caliblevel = progress - 9;
                //Set progress in acompaning TextView
                if(CalibMSel == 1){
                    Mv1.setText(String.valueOf(Caliblevel));
                    mv1 = Caliblevel;
                }
                else if(CalibMSel == 2){
                    Mv2.setText(String.valueOf(Caliblevel));
                    mv2 = Caliblevel;
                }
                else if(CalibMSel == 3){
                    Mv3.setText(String.valueOf(Caliblevel));
                    mv3 = Caliblevel;
                }
                else if(CalibMSel == 4){
                    Mv4.setText(String.valueOf(Caliblevel));
                    mv4 = Caliblevel;
                }
                else if(CalibMSel == 5){
                    Mv5.setText(String.valueOf(Caliblevel));
                    mv5 = Caliblevel;
                }
                else if(CalibMSel == 6){
                    Mv6.setText(String.valueOf(Caliblevel));
                    mv6 = Caliblevel;
                }
                else if(CalibMSel == 7){
                    Mv7.setText(String.valueOf(Caliblevel));
                    mv7 = Caliblevel;
                }
                else if(CalibMSel == 8){
                    Mv8.setText(String.valueOf(Caliblevel));
                    mv8 = Caliblevel;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Drawable d = getResources().getDrawable(R.drawable.settings_slider_prs);
                CalibrateSeekbar.setThumb(resizeTh(d,CalibrateSeekbar.getWidth()));
                vibrator.vibrate(40);
                if (mHandler != null) return;
                mHandler = new Handler();
                mHandler.postDelayed(mAction, 40);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Drawable d = getResources().getDrawable(R.drawable.settings_slider_rel);
                CalibrateSeekbar.setThumb(resizeTh(d,CalibrateSeekbar.getWidth()));
                CalibrateSeekbar.setProgressAndThumb(CalibrateSeekbar.getProgress());
                if (mHandler != null){
                    mHandler.removeCallbacks(mAction);
                    mHandler = null;
                }
                bluetooth_send("#SW");
            }
            Runnable mAction = new Runnable() {
                @Override public void run() {
                if(Caliblevel < 0){
                    bluetooth_send(("#SD-").concat(String.format("%01d", (int)(Caliblevel-(2*Caliblevel)))).concat(String.format("%01d", (int)CalibMSel)));//#SD+91
                }
                else if(Caliblevel > 0){
                    bluetooth_send(("#SD+").concat(String.format("%01d", (int)Caliblevel)).concat(String.format("%01d", (int)CalibMSel)));//#SD+91
                }
                else{
                    bluetooth_send(("#SD+0").concat(String.format("%01d", (int)CalibMSel)));//#SD+01
                }
                mHandler.postDelayed(this, 40);
                }
            };
        });
        final ViewTreeObserver observer = CalibrateSeekbar.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressLint("NewApi")
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {

                        // Ensure you call it only once :
                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            CalibrateSeekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        else {
                            CalibrateSeekbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        Drawable d = getResources().getDrawable(R.drawable.settings_slider_rel);
                        CalibrateSeekbar.setThumb(resizeTh(d,CalibrateSeekbar.getWidth()));
                    }
                });
/// #2 Switch buttons
        final ImageButton M1 = (ImageButton) findViewById(R.id.m1);
        final ImageButton M2 = (ImageButton) findViewById(R.id.m2);
        final ImageButton M3 = (ImageButton) findViewById(R.id.m3);
        final ImageButton M4 = (ImageButton) findViewById(R.id.m4);
        final ImageButton M5 = (ImageButton) findViewById(R.id.m5);
        final ImageButton M6 = (ImageButton) findViewById(R.id.m6);
        final ImageButton M7 = (ImageButton) findViewById(R.id.m7);
        final ImageButton M8 = (ImageButton) findViewById(R.id.m8);
        M1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                M1.setImageResource(R.drawable.settings_switch_sel);
                M2.setImageResource(R.drawable.settings_switch_unsel);
                M3.setImageResource(R.drawable.settings_switch_unsel);
                M4.setImageResource(R.drawable.settings_switch_unsel);
                M5.setImageResource(R.drawable.settings_switch_unsel);
                M6.setImageResource(R.drawable.settings_switch_unsel);
                M7.setImageResource(R.drawable.settings_switch_unsel);
                M8.setImageResource(R.drawable.settings_switch_unsel);
                CalibMSel = 1;
                CalibrateSeekbar.setProgressAndThumb(mv1+9);
            }
        });
        M2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                M1.setImageResource(R.drawable.settings_switch_unsel);
                M2.setImageResource(R.drawable.settings_switch_sel);
                M3.setImageResource(R.drawable.settings_switch_unsel);
                M4.setImageResource(R.drawable.settings_switch_unsel);
                M5.setImageResource(R.drawable.settings_switch_unsel);
                M6.setImageResource(R.drawable.settings_switch_unsel);
                M7.setImageResource(R.drawable.settings_switch_unsel);
                M8.setImageResource(R.drawable.settings_switch_unsel);
                CalibMSel = 2;
                CalibrateSeekbar.setProgressAndThumb(mv2+9);
            }
        });
        M3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                M1.setImageResource(R.drawable.settings_switch_unsel);
                M2.setImageResource(R.drawable.settings_switch_unsel);
                M3.setImageResource(R.drawable.settings_switch_sel);
                M4.setImageResource(R.drawable.settings_switch_unsel);
                M5.setImageResource(R.drawable.settings_switch_unsel);
                M6.setImageResource(R.drawable.settings_switch_unsel);
                M7.setImageResource(R.drawable.settings_switch_unsel);
                M8.setImageResource(R.drawable.settings_switch_unsel);
                CalibMSel = 3;
                CalibrateSeekbar.setProgressAndThumb(mv3+9);
            }
        });
        M4.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                M1.setImageResource(R.drawable.settings_switch_unsel);
                M2.setImageResource(R.drawable.settings_switch_unsel);
                M3.setImageResource(R.drawable.settings_switch_unsel);
                M4.setImageResource(R.drawable.settings_switch_sel);
                M5.setImageResource(R.drawable.settings_switch_unsel);
                M6.setImageResource(R.drawable.settings_switch_unsel);
                M7.setImageResource(R.drawable.settings_switch_unsel);
                M8.setImageResource(R.drawable.settings_switch_unsel);
                CalibMSel = 4;
                CalibrateSeekbar.setProgressAndThumb(mv4+9);
            }
        });
        M5.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                M1.setImageResource(R.drawable.settings_switch_unsel);
                M2.setImageResource(R.drawable.settings_switch_unsel);
                M3.setImageResource(R.drawable.settings_switch_unsel);
                M4.setImageResource(R.drawable.settings_switch_unsel);
                M5.setImageResource(R.drawable.settings_switch_sel);
                M6.setImageResource(R.drawable.settings_switch_unsel);
                M7.setImageResource(R.drawable.settings_switch_unsel);
                M8.setImageResource(R.drawable.settings_switch_unsel);
                CalibMSel = 5;
                CalibrateSeekbar.setProgressAndThumb(mv5+9);
            }
        });
        M6.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                M1.setImageResource(R.drawable.settings_switch_unsel);
                M2.setImageResource(R.drawable.settings_switch_unsel);
                M3.setImageResource(R.drawable.settings_switch_unsel);
                M4.setImageResource(R.drawable.settings_switch_unsel);
                M5.setImageResource(R.drawable.settings_switch_unsel);
                M6.setImageResource(R.drawable.settings_switch_sel);
                M7.setImageResource(R.drawable.settings_switch_unsel);
                M8.setImageResource(R.drawable.settings_switch_unsel);
                CalibMSel = 6;
                CalibrateSeekbar.setProgressAndThumb(mv6+9);
            }
        });
        M7.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                M1.setImageResource(R.drawable.settings_switch_unsel);
                M2.setImageResource(R.drawable.settings_switch_unsel);
                M3.setImageResource(R.drawable.settings_switch_unsel);
                M4.setImageResource(R.drawable.settings_switch_unsel);
                M5.setImageResource(R.drawable.settings_switch_unsel);
                M6.setImageResource(R.drawable.settings_switch_unsel);
                M7.setImageResource(R.drawable.settings_switch_sel);
                M8.setImageResource(R.drawable.settings_switch_unsel);
                CalibMSel = 7;
                CalibrateSeekbar.setProgressAndThumb(mv7+9);
            }
        });
        M8.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                M1.setImageResource(R.drawable.settings_switch_unsel);
                M2.setImageResource(R.drawable.settings_switch_unsel);
                M3.setImageResource(R.drawable.settings_switch_unsel);
                M4.setImageResource(R.drawable.settings_switch_unsel);
                M5.setImageResource(R.drawable.settings_switch_unsel);
                M6.setImageResource(R.drawable.settings_switch_unsel);
                M7.setImageResource(R.drawable.settings_switch_unsel);
                M8.setImageResource(R.drawable.settings_switch_sel);
                CalibMSel = 8;
                CalibrateSeekbar.setProgressAndThumb(mv8+9);
            }
        });
        ButtonInc = (ImageButton) findViewById(R.id.inc);
        ButtonInc.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                vibrator.vibrate(40);
                if(Caliblevel < 9){
                    Caliblevel++;
                    CalibrateSeekbar.setProgressAndThumb(Caliblevel+9);
                    if(CalibMSel == 1){
                        Mv1.setText(String.valueOf(Caliblevel));
                        mv1 = Caliblevel;
                    }
                    else if(CalibMSel == 2){
                        Mv2.setText(String.valueOf(Caliblevel));
                        mv2 = Caliblevel;
                    }
                    else if(CalibMSel == 3){
                        Mv3.setText(String.valueOf(Caliblevel));
                        mv3 = Caliblevel;
                    }
                    else if(CalibMSel == 4){
                        Mv4.setText(String.valueOf(Caliblevel));
                        mv4 = Caliblevel;
                    }
                    else if(CalibMSel == 5){
                        Mv5.setText(String.valueOf(Caliblevel));
                        mv5 = Caliblevel;
                    }
                    else if(CalibMSel == 6){
                        Mv6.setText(String.valueOf(Caliblevel));
                        mv6 = Caliblevel;
                    }
                    else if(CalibMSel == 7){
                        Mv7.setText(String.valueOf(Caliblevel));
                        mv7 = Caliblevel;
                    }
                    else if(CalibMSel == 8){
                        Mv8.setText(String.valueOf(Caliblevel));
                        mv8 = Caliblevel;
                    }
                }
                if(Caliblevel < 0){
                    bluetooth_send(("#SD-").concat(String.format("%01d", (int)(Caliblevel-(2*Caliblevel)))).concat(String.format("%01d", (int)CalibMSel)));//#SD+91
                }
                else if(Caliblevel > 0){
                    bluetooth_send(("#SD+").concat(String.format("%01d", (int)Caliblevel)).concat(String.format("%01d", (int)CalibMSel)));//#SD+91
                }
                else{
                    bluetooth_send(("#SD+0").concat(String.format("%01d", (int)CalibMSel)));//#SD+01
                }
                bluetooth_send("#SW");
            }
        });

        ButtonDec = (ImageButton) findViewById(R.id.dec);
        ButtonDec.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                vibrator.vibrate(40);
                if(Caliblevel > -9){
                    Caliblevel--;
                    CalibrateSeekbar.setProgressAndThumb(Caliblevel+9);
                    if(CalibMSel == 1){
                        Mv1.setText(String.valueOf(Caliblevel));
                        mv1 = Caliblevel;
                    }
                    else if(CalibMSel == 2){
                        Mv2.setText(String.valueOf(Caliblevel));
                        mv2 = Caliblevel;
                    }
                    else if(CalibMSel == 3){
                        Mv3.setText(String.valueOf(Caliblevel));
                        mv3 = Caliblevel;
                    }
                    else if(CalibMSel == 4){
                        Mv4.setText(String.valueOf(Caliblevel));
                        mv4 = Caliblevel;
                    }
                    else if(CalibMSel == 5){
                        Mv5.setText(String.valueOf(Caliblevel));
                        mv5 = Caliblevel;
                    }
                    else if(CalibMSel == 6){
                        Mv6.setText(String.valueOf(Caliblevel));
                        mv6 = Caliblevel;
                    }
                    else if(CalibMSel == 7){
                        Mv7.setText(String.valueOf(Caliblevel));
                        mv7 = Caliblevel;
                    }
                    else if(CalibMSel == 8){
                        Mv8.setText(String.valueOf(Caliblevel));
                        mv8 = Caliblevel;
                    }
                }
                if(Caliblevel < 0){
                    bluetooth_send(("#SD-").concat(String.format("%01d", (int)(Caliblevel-(2*Caliblevel)))).concat(String.format("%01d", (int)CalibMSel)));//#SD+91
                }
                else if(Caliblevel > 0){
                    bluetooth_send(("#SD+").concat(String.format("%01d", (int)Caliblevel)).concat(String.format("%01d", (int)CalibMSel)));//#SD+91
                }
                else{
                    bluetooth_send(("#SD+0").concat(String.format("%01d", (int)CalibMSel)));//#SD+01
                }
                bluetooth_send("#SW");
            }
        });
///  #3 Close button
        final ImageButton Close = (ImageButton) findViewById(R.id.close);
        Close.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                //bluetooth_send("#SC");
                finish();
            }
        });
///  #4 Save and unpair button
        Save = (ImageButton) findViewById(R.id.save);
        Save.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View view, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(40);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    String nName = Name.getText().toString();
                    int i = nName.length();
                    if(nName.length() < 10){
                        while(i < 10){
                            nName = nName.concat(" ");
                            i++;
                        }
                    }
                    String nPassword = Password.getText().toString();
                    i = 0;
                    if(nPassword.length() < 4){
                        while(i < 4){
                            nPassword = nPassword.concat(" ");
                            i++;
                        }
                    }
                    bluetooth_send(("#SS").concat(nName).concat(nPassword));
                    enable_actions(false);
                    Close.setEnabled(false);
                    ConnectActivity.unpairBtDev(ConnectActivity.btcode);
                }
                return false;
            }
        });
        Led = (ImageButton) findViewById(R.id.led);
        Led.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View view, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(40);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    ledsel = 1 - ledsel;
                    if(ledsel == 1){
                        bluetooth_send("#E1");
                        Led.setImageResource(R.drawable.settings_led_sel);
                    }
                    else{
                        bluetooth_send("#E0");
                        Led.setImageResource(R.drawable.settings_led_unsel);
                    }
                }
                return false;
            }
        });
///  Request levels, name and password
        RSettingsActivity = true;
        enable_actions(false);
        bluetooth_send("#SO");
    }

//// #0 Common functions
    static void enable_actions(boolean enable){
        ButtonInc.setEnabled(enable);
        ButtonDec.setEnabled(enable);
        Save.setEnabled(enable);
        Password.setEnabled(enable);
        Name.setEnabled(enable);
        CalibrateSeekbar.setEnabled(enable);
        Led.setEnabled(enable);
    }
    public void bluetooth_send(String msg){
        ConnectActivity.bluetoothSerial.write(msg);
    }
    private Drawable resizeTh(Drawable image, int newwidth) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        int newheight = (int)(((float)newwidth/(float)b.getWidth())*(float)b.getHeight());
        float dividefactor = (float)newheight/(float)newwidth;
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, (int)((float)newwidth/dividefactor), (int)((float)newheight/dividefactor), false);
        CalibrateSeekbar.wh = (int)((float)newwidth/dividefactor);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}
