/*
-- TBD --
#თუ მომხმარებელმა დააჭირა გაუქმებას პროგრამა დაიხუროს
#შეერთების კნოპკა სპინერში სახელების მაგივრად მოწყობილობებით შეუერთდეს

#მესიჯი გაიაზროს და გადასცეს შესაბამის ფანჯარას თუ ღიაა
#კოსმეტიკური რემონტი, თუ რამე - გავაშაო ნაცრისფერი ეკრანი - ცეკვის მუსიკა
#siashi UUIDebit gafiltruli marto robotebi

#Command ხმის ბრძანებები
#nastroikebshi gadamrtvelebs da daxurvas vibracia
*/

package me.rocket_scientist.quadruped;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ImageButton;
import android.os.Vibrator;
import android.view.View.OnTouchListener;
import android.bluetooth.BluetoothDevice;
import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ConnectActivity extends FragmentActivity {
    //Global to project
    public static BluetoothSerial bluetoothSerial;
    Intent JoystickIntent;
    //Global to page
    Spinner Spinner_device_list;
    ImageButton Button_Connect;
    Vibrator vibrator;
    public static ConnectActivity FConnectActivity;

    public byte[] StringToByte(String s){
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2){
            data[i/2] = (byte)((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        FConnectActivity = this;

        //Exit function
        if(getIntent().getBooleanExtra("Exit me", false)){
            finish();
        }

        JoystickIntent = new Intent(ConnectActivity.this, JoystickActivity.class);
        //Initialize Global to page
        currentBtaddr = new ArrayList<String>();
        Spinner_device_list = (Spinner) findViewById(R.id.spinner_device_list);
        Spinner_device_list.setEnabled(false);
        Spinner_device_list.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(40);
                }
                return false;
            }
        });
        Button_Connect = (ImageButton) findViewById(R.id.button_connect);
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        Button_Connect.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Button_Connect.setEnabled(false);
                Spinner_device_list.setEnabled(false);
                BluetoothConnect();
            }
        });
        Button_Connect.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(40);
                }
                return false;
            }
        });
        //Initialize Local to page
        ImageButton Button_Exit = (ImageButton) findViewById(R.id.button_exit);
        Button_Exit.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                bluetoothSerial.stop();
                bluetoothSerial.bluetoothDisble();
                finish();
            }
        });
        Button_Exit.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(40);
                }
                return false;
            }
        });
        final ImageButton Button_Copyright = (ImageButton) findViewById(R.id.button_copyright);
        Button_Copyright.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                //Open Copyright URL here
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://rocket-scientist.me/pages/quadruped/copyright.html"));
                startActivity(browserIntent);
            }
        });
        Button_Copyright.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(40);
                }
                return false;
            }
        });
        final ImageButton Button_Manual = (ImageButton) findViewById(R.id.button_manual);
        Button_Manual.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                //Open Help URL here
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://rocket-scientist.me/pages/quadruped/manual.html"));
                startActivity(browserIntent);
            }
        });
        Button_Manual.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(40);
                }
                return false;
            }
        });
        if((ContextCompat.checkSelfPermission(FConnectActivity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)||(ContextCompat.checkSelfPermission(FConnectActivity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(FConnectActivity, new String[]{Manifest.permission.BLUETOOTH}, BTPERMISSION);
            ActivityCompat.requestPermissions(FConnectActivity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, BTPERMISSION);
            PQeue = CRRUN;
        }
        else{
            createRun();
        }
    }
    protected void createRun(){
        //Initialize Global to project
        bluetoothSerial = new BluetoothSerial(this, new BluetoothSerialListener() {
            @Override
            public void onBluetoothNotSupported() {
                bluetooth_disconnect();
            }

            @Override
            public void onBluetoothDisabled() {
                bluetooth_disconnect();
            }

            @Override
            public void onBluetoothDeviceDisconnected() {
                bluetooth_disconnect();
            }

            @Override
            public void onConnectingBluetoothDevice() {
            }

            @Override
            public void onBluetoothDeviceConnected(String name, String address) {
                startActivity(JoystickIntent);
                //STOP AND DESTROY TIMEOUT TIMER HERE
            }

            @Override
            public void onBluetoothSerialRead(String message) {
                if (message.charAt(0) == '#') {
                    if (message.charAt(1) == 'C') {
                        if(SettingsActivity.RSettingsActivity){
                            SettingsActivity.calibresp = message;
                            SettingsActivity.parseval();
                        }
                    }
                }
            }

            @Override
            public void onBluetoothSerialWrite(String message) {
            }
        });
        //Start
        bluetoothSerial.start();
        bluetoothSerial.setup();
        bluetooth_disconnect();
    }

    public static List<String> currentBtaddr;
    public static String btcode;

    public void BluetoothConnect() {
        if(bluetooth_enable(CONNQ) == true){
            for (BluetoothDevice fdev : bluetoothSerial.getPairedDevices()) {
                //if (fdev.getName().equals(Spinner_device_list.getSelectedItem().toString())) {
                if (fdev.getAddress().equals(currentBtaddr.get(Spinner_device_list.getSelectedItemPosition()))){
                    btcode = fdev.getAddress();
                    bluetoothSerial.connect(fdev);
                }
            }
        }
    }

    static final public int BTPERMISSION = 123;
    static final public int CONNQ = 1;
    static final public int DCONNQ = 2;
    static final public int UNPAIRQ = 3;
    static final public int CRRUN = 4;
    static public int PQeue = 0;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case BTPERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bluetoothSerial.bluetoothEnable();
                    if(PQeue == CONNQ){
                        PQeue = 0;
                        //Shevaertot aq
                        BluetoothConnect();
                    }else if(PQeue == DCONNQ){
                        PQeue = 0;
                        //Gamovaertot aq
                        bluetooth_disconnect();
                    }else if(PQeue == UNPAIRQ){
                        PQeue = 0;
                        //Anpear aq
                        unpairBtDev(btcode);
                    }else if(PQeue == CRRUN){
                        PQeue = 0;
                        //onCreate aq
                        createRun();
                    }

                }else{
                    Intent intent = new Intent(this, ConnectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Exit me", true);
                    startActivity(intent);
                    finish();
                }
            }
        }
        return;
    }

    static public boolean bluetooth_enable(int processaftergrant){
        boolean res = true;
        if(bluetoothSerial.isBluetoothEnabled() == false) {
			if((ContextCompat.checkSelfPermission(FConnectActivity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)||(ContextCompat.checkSelfPermission(FConnectActivity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)){
				ActivityCompat.requestPermissions(FConnectActivity, new String[]{Manifest.permission.BLUETOOTH}, BTPERMISSION);
                ActivityCompat.requestPermissions(FConnectActivity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, BTPERMISSION);
                PQeue = processaftergrant;
                res = false;
            }
            else{
                bluetoothSerial.bluetoothEnable();
            }
        }
        return res;
    }
    public void bluetooth_disconnect() {
        //Disconnect bluetooth
        if(bluetooth_enable(DCONNQ) == true){
            bluetoothSerial.stop();
            //Close all activities if open
            if (SettingsActivity.RSettingsActivity) {
                SettingsActivity.FSettingsActivity.finish();
            }
            if (JoystickActivity.RJoystickActivity) {
                JoystickActivity.FJoystickActivity.finish();
            }
            //Device list spinner action
            List<String> device_names_as_List = new ArrayList<String>();
            currentBtaddr.clear();
            for (BluetoothDevice d : bluetoothSerial.getPairedDevices()) {
                ParcelUuid[] uuids = d.getUuids();
                if(uuids != null){
                    for(ParcelUuid uuid : uuids){
                        if(uuid.getUuid().toString().contains("00001101-0000-1000-8000-00805f9b34fb")){
                            device_names_as_List.add(d.getName());
                            currentBtaddr.add(d.getAddress());
                        }
                    }
                }
            }
            String[] device_names_label_loading = {"No paired robots found"};
            String[] device_names_as_string = device_names_as_List.toArray(new String[device_names_as_List.size()]);
            if (device_names_as_List.size() != 0) {
                ArrayAdapter<String> pairedListAdapter = new ArrayAdapter<String>(this, R.layout.connect_spinner_item, device_names_as_string);
                pairedListAdapter.setDropDownViewResource(R.layout.connect_spinner_dropdown_item);
                Spinner_device_list.setAdapter(pairedListAdapter);
                Spinner_device_list.setEnabled(true);
                Button_Connect.setEnabled(true);
            } else {
                ArrayAdapter<String> pairedListAdapter = new ArrayAdapter<String>(this, R.layout.connect_spinner_item, device_names_label_loading);
                pairedListAdapter.setDropDownViewResource(R.layout.connect_spinner_dropdown_item);
                Spinner_device_list.setAdapter(pairedListAdapter);
                Spinner_device_list.setEnabled(false);
                Button_Connect.setEnabled(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetooth_disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /*public boolean unpairBtDev(BluetoothDevice device) throws Exception{
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean retval = (Boolean)removeBondMethod.invoke(device);
        return retval.booleanValue();
    }*/
    static public void unpairBtDev(String bcode){
        if(bluetooth_enable(UNPAIRQ) == true){
            for (BluetoothDevice device : bluetoothSerial.getPairedDevices()) {
                if (device.getAddress().equals(bcode)) {
                    try{
                        Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                        //while(device.getBondState() != BluetoothDevice.BOND_NONE);
                    } catch(Exception e){
                        //Log.e(TAG, e.getMessage());
                    }
                }
            }
        }
    }
}
