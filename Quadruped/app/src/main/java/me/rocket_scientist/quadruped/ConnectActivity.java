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


    //CRYPT
    static char[] modulusTx = {
            0xa5, 0x08, 0x16, 0x4b, 0x42, 0xb6, 0x73, 0x84, 0xc1, 0xeb, 0x9f, 0x93, 0xaa, 0x42,
            0xd4, 0xff, 0xee, 0x75, 0xfa, 0x90, 0x7b, 0x62, 0xce, 0x39, 0xb9, 0x2e, 0x44, 0x9a, 0x54,
            0xd1, 0x53, 0xb5, 0xe6, 0x3f, 0xf4, 0x58, 0xd1, 0x23, 0x23, 0xa7, 0x0f, 0x73, 0x81, 0xef,
            0xd4, 0x90, 0xae, 0xae, 0x67, 0x77, 0xd1, 0xa5, 0x89, 0xc0, 0x46, 0x3b, 0xab, 0x26, 0x42,
            0xf5, 0xb9, 0x3d, 0xb2, 0xc5, 0xe5, 0x66, 0x04, 0x7e, 0xa4, 0x5f, 0xec, 0xeb, 0xce, 0xda,
            0x63, 0x08, 0x59, 0xf7, 0x6d, 0xf9, 0x8b, 0xab, 0xcf, 0x30, 0x1c, 0x24, 0x74, 0xb5, 0xc2,
            0x6f, 0x7e, 0x3b, 0x8b, 0x55, 0x85, 0xcd, 0x74, 0xf6, 0xa3, 0x32, 0x7c, 0x3f, 0x80, 0xd0,
            0xef, 0xda, 0x78, 0x37, 0x8d, 0x72, 0x87, 0xbc, 0xf0, 0x03, 0xf9, 0xae, 0xb6, 0xde, 0xae,
            0xf8, 0x5e, 0xd5, 0xb9, 0xd8, 0xbd, 0x29, 0xf7, 0x8d
    };
    static char[] exponentPrivateTx = {
            0x96, 0x50, 0x23, 0x5e, 0x31, 0x49, 0x55, 0x36, 0x5a, 0x1a, 0x6e, 0xa2, 0x64, 0xb5,
            0xb2, 0xa3, 0xd9, 0x62, 0x83, 0xf2, 0x0d, 0x30, 0x15, 0x7d, 0xe5, 0xf9, 0x0a, 0x6b, 0x37,
            0x4d, 0xe1, 0xdd, 0x0f, 0xf6, 0xb8, 0x6c, 0x1d, 0x30, 0xd0, 0x4c, 0x5d, 0x7a, 0x6b, 0xa3,
            0x32, 0x0d, 0x05, 0xcd, 0xbc, 0x58, 0x1a, 0x0f, 0x98, 0xc4, 0x14, 0x34, 0x4c, 0xd2, 0xf0,
            0xb2, 0x37, 0x22, 0xc7, 0x2c, 0x9a, 0x32, 0xbb, 0xd5, 0x99, 0x28, 0xe6, 0xa1, 0xe9, 0xbf,
            0xc0, 0x2e, 0x5b, 0x99, 0x17, 0x72, 0x69, 0x92, 0x81, 0x25, 0x79, 0x1b, 0x29, 0x69, 0x2c,
            0x24, 0x31, 0x6b, 0x2c, 0x35, 0xdc, 0xa0, 0x8e, 0xcf, 0x6c, 0xf3, 0x5a, 0xbb, 0x9a, 0xbf,
            0x32, 0xec, 0x22, 0x9b, 0x38, 0x0e, 0xc3, 0xbb, 0x40, 0x96, 0xc5, 0x59, 0x4e, 0x1f, 0x36,
            0x93, 0x7c, 0xb7, 0x50, 0xba, 0xb6, 0x00, 0xdf, 0x01
    };
    static char[] exponentPublicTx = {
            0x01, 0x00, 0x01
    };
    static char[] modulusRx = {
            0xaa, 0x33, 0x5f, 0x48, 0x88, 0xb0, 0x71, 0xcb, 0x4d, 0xc7, 0x1e, 0x28, 0x5c, 0x62,
            0x74, 0x03, 0x76, 0xdf, 0x67, 0xff, 0x64, 0xce, 0x24, 0xe4, 0x5c, 0x89, 0xaa, 0x4d, 0xbb,
            0x72, 0xa6, 0x7d, 0xfc, 0x4e, 0x49, 0x0e, 0xbc, 0x27, 0x45, 0x3b, 0x75, 0x0e, 0x9a, 0x6a,
            0x62, 0xd5, 0x8a, 0x06, 0xa9, 0xf8, 0xff, 0x7d, 0x07, 0xa4, 0xba, 0xd6, 0x6b, 0x50, 0x09,
            0x87, 0x40, 0xf7, 0xb9, 0x2d, 0xaf, 0xe3, 0x7b, 0x06, 0x8b, 0x09, 0x01, 0xf5, 0x00, 0xeb,
            0x86, 0xd9, 0xdb, 0x2e, 0x2a, 0x4b, 0x70, 0x28, 0xed, 0x41, 0x90, 0x43, 0x75, 0x61, 0x5f,
            0x86, 0x8b, 0xcb, 0x50, 0x8a, 0x0d, 0xf3, 0x67, 0xb1, 0xf6, 0x6e, 0x11, 0x46, 0x28, 0x67,
            0xe3, 0x33, 0x6b, 0x1c, 0x8a, 0x55, 0xa7, 0x3c, 0x44, 0x21, 0x87, 0xce, 0x42, 0xe1, 0x78,
            0xa2, 0x29, 0x23, 0x88, 0xac, 0x94, 0xde, 0x3e, 0x13
    };
    static char[] exponentPublicRx = {
            0x01, 0x00, 0x01
    };
    PublicKey publicKeyTx;
    PrivateKey privateKeyTx;
    PublicKey publicKeyRx;
    byte[] randomarrINT = new byte[86]; //ამაში ვწერ დაგენერირებულ შემთხვევით რიცხვს
    String randomarrTXT; //ამაში ვწერ იგივე რიცხვს ტექსტად
    String randomarrTxtEnc; //ამაში ვწერ შემთხვევით რიცხვებს ჯერ დაშიფრულს და მერე ტექსტად ქცეულს (არ ვიცი რად გვინდა)
/*
ვშიფრავ publicKeyRx
ვაგზავნი
გაშიფრავს თავისი პრივატე კლუჩით, დაშიფრავს publicKeyTx
მიგზავნის
გავშიფრავ privateKeyTx,
შევადარებ თუ ისაა რაც გავაგზავნე
*/

    public static BigInteger IntArrToBigIntegerArr(char[] t){
        ByteBuffer b = ByteBuffer.allocate(t.length);
        int i = 0;
        char tmp = 0;
        while(i != t.length){
            if(i+1 == t.length){
                b.put((byte)t[i]);
                i+=1;
            }else {
                b.putChar((char) ((t[i] << 8) | (t[i + 1])));
                i+=2;
            }
        }
        return new BigInteger(1, b.array());
    }
    private static PrivateKey getPrivateKeyFromBinary(char[] modulus, char[] privateExponent) {
        try{
            BigInteger privateExponentInt = IntArrToBigIntegerArr(privateExponent);
            BigInteger keyInt = IntArrToBigIntegerArr(modulus);
            RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(keyInt, privateExponentInt);
            KeyFactory factory = KeyFactory .getInstance("RSA");
            return factory.generatePrivate(privateKeySpec);//KeyFactory.getInstance("RSA").generatePrivate(new RSAPrivateKeySpec(cm, ce));
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private static PublicKey getPublicKeyFromBinary(char[] modulus, char[] publicExponent) {
        try{
            BigInteger publicExponentInt = IntArrToBigIntegerArr(publicExponent);
            BigInteger keyInt = IntArrToBigIntegerArr(modulus);
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(keyInt, publicExponentInt);
            KeyFactory factory = KeyFactory .getInstance("RSA");
            return factory.generatePublic(publicKeySpec);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public byte[] RSAEncrypt(byte plain[], PublicKey key)throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plain);
    }
    public byte[] RSADecrypt(final byte[] encryptedBytes, PrivateKey key)throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedBytes);
    }
    public byte[] StringToByte(String s){
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2){
            data[i/2] = (byte)((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public void RSAInit(){
        publicKeyTx = getPublicKeyFromBinary(modulusTx,exponentPublicTx);
        privateKeyTx = getPrivateKeyFromBinary(modulusTx,exponentPrivateTx);
        publicKeyRx = getPublicKeyFromBinary(modulusRx,exponentPublicRx);
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
    public void SendHandshake(){
        RSAInit();
        //Generate Random number
        int i = 0;
        while(i != 86){
            randomarrINT[i] = (byte)(new Random().nextInt(255) + 1);
            i++;
        }
        //Also fill as HEX text
        randomarrTXT = bytesToHex(randomarrINT);
        //Encrypt INT with publicKeyRx
        try{
            randomarrTxtEnc = bytesToHex(RSAEncrypt(randomarrINT, publicKeyRx));
        }catch(Exception e){
            e.printStackTrace();
        }
        //Send
        //bluetoothSerial.write("@".concat(String.format("%04d", randomarrTxtEnc.length())).concat(randomarrTxtEnc));//4simbolo teqstis sigrdze
        bluetoothSerial.write("@".concat(randomarrTxtEnc));
    }
    //

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
                SendHandshake();
            }

            @Override
            public void onBluetoothSerialRead(String message) {
                if(message.charAt(0) == '@'){
                    //Decrypt and compare here
                    if(message.length() >= 258){
                        StringBuilder b = new StringBuilder(message);
                        b.deleteCharAt(0);
                        b.deleteCharAt(256);
                        try{
                            byte[] retval = RSADecrypt(StringToByte(b.toString()), privateKeyTx);
                            if(Arrays.equals(retval, randomarrINT)){
                                startActivity(JoystickIntent);
                                //STOP AND DESTROY TIMEOUT TIMER HERE
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }else if (message.charAt(0) == '#') {
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
