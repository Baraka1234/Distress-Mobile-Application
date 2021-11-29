package com.example.distressapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    String myLongitude, myLatitude, phoneNumber;

    private LatLng latLng;
    //Declaring variables
    Button sendCoord, stopRec, recListen,stopPlay, regEmNumber;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    Random random;
    MediaPlayer mediaPlayer;

    final int REQUEST_PERMISSION_CODE = 1000;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_CODE_PERMISSIONS = 2;

    GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSIONS);


            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
            }


            if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("im here");


        regEmNumber = (Button)findViewById(R.id.regEmNumber);
        regEmNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity(v);
            }
        });

        random = new Random();
        stopRec = (Button)findViewById(R.id.stopRec) ;
        recListen = (Button)findViewById(R.id.recListen);
        sendCoord = (Button)findViewById(R.id.sendCoord);
        stopPlay = (Button) findViewById(R.id.stopPlay);


        setupMediaRecorder();
       getLocation();



        System.out.print(" IM Over here2");
        sendCoord.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if(checkPermissionFromDevice()){

                }

                getUser_number();

                String message = "This is to inform you that is in distress and needs help. His/Her current location is: https://www.google.com/maps/@" + myLatitude +"," +myLongitude+",6z";
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);


                System.out.println("----------- audion directory ---- ::: " + pathSave);

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    recListen.setEnabled(false);
                    stopPlay.setEnabled(false);
                    stopRec.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Recording ...", Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    e.printStackTrace();
                }


            }
        });

        System.out.print("Over here 3");

        stopRec.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                stopRec.setEnabled(false);
                recListen.setEnabled(true);
                sendCoord.setEnabled(true);
                stopPlay.setEnabled(false);

                Toast.makeText(MainActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();
            }
        });

        recListen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                stopPlay.setEnabled(true);
                stopRec.setEnabled(false);
                sendCoord.setEnabled(false);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave);
                    mediaPlayer.prepare();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Recording Playing", Toast.LENGTH_LONG).show();
            }
        });

        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRec.setEnabled(false);
                sendCoord.setEnabled(true);
                stopPlay.setEnabled(false);
                recListen.setEnabled(true);

                if(mediaPlayer != null){
                    //mediaPlayer.stop();
                    mediaPlayer.release();
                    setupMediaRecorder();
                }
            }
        });
    }

    public void getLocation(){


        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            myLatitude = String.valueOf(latitude);
            myLongitude = String.valueOf(longitude);
            sendCoord.setEnabled(true);

        }else{

            gps.showSettingsAlert();
        }
    }

    private void setupMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        pathSave = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator
                + UUID.randomUUID().toString()+"_audio_record.3gp";
        System.out.println("---------- File Path ----- ::: " + pathSave);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermissions() {

        ActivityCompat.requestPermissions(this, new String[]{
                WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();}
                else
                    Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show();
            }
            break;
        }

    }

    private void getUser_number(){
        DBHelper db = new DBHelper(this);

        Cursor  res = db.getdata();
        System.out.println(" Error here");
       if(res.moveToFirst()){
           phoneNumber =  res.getString(3);
       }
        System.out.println(" Error here");
       /* while(res.moveToNext()){
            if (res.getCount() > 0){
                phoneNumber = res.getString(4);
            }
        }*/

    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result= ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result== PackageManager.PERMISSION_GRANTED &&
                record_audio_result ==PackageManager.PERMISSION_GRANTED;
    }

    public void openRegisterActivity(View r){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public  void  startBackGroundProcessButtonClick(View view){
        Intent intent = new Intent(this, MyService.class);
        //intent.putExtra(  "Inputextra")

        startService(intent);

        finish();

    }

    @Override
    protected void onDestroy() {
        //startBackGroundProcessButtonClick();
        super.onDestroy();
    }

    @Override
    protected void onStop() {

        //startBackGroundProcessButtonClick();
        super.onStop();
    }
}