package com.example.distressapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class myBackgroundProcess extends BroadcastReceiver implements SensorEventListener   {
    //BroadcastReciever listens to any action you put into the background process
    private SensorManager sensorManager;

    private Sensor accelerometerSensor;

    private boolean isAccelerometerSensorAvailable, itISNotFirstTime = false;
    private boolean isRegisteredListener = false;

    private float currentX, currentY, currentZ, lastX, lastY, lastZ, xDifference, yDifference, zDifference;

    private float shakeThreshold = 5f;// this is a float number meaning 5x, 0y, 0z

    private Vibrator vibrator;
    Context context;

    // Listens to the command inside it. initializes shaking and sensor
    @Override
    public void onReceive(Context contexts, Intent intent) {

        context = contexts;//gets context of the app and passes it globally

        onSetShakingInitialization();
        sensorManager.registerListener(this, accelerometerSensor, sensorManager.SENSOR_DELAY_NORMAL);

       /* if(isAccelerometerSensorAvailable){
            if(!isRegisteredListener){

                isRegisteredListener = true;
            }

        } else {
            onSetShakingInitialization();
            sensorManager.registerListener(this, accelerometerSensor, sensorManager.SENSOR_DELAY_NORMAL);
        }*/
    }


    public  void onSetShakingInitialization(){
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable = true;
        } else{
            Toast.makeText(context, "Accelerometer sensor is not available",Toast.LENGTH_LONG).show();
            isAccelerometerSensorAvailable = false;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if(itISNotFirstTime){
            xDifference = Math.abs(lastX - currentX);

            yDifference = Math.abs(lastY - currentY);

            zDifference = Math.abs(lastZ - currentZ);

            // to detect if phone is shaking
            if((xDifference > shakeThreshold && yDifference > shakeThreshold) ||
                    (xDifference > shakeThreshold && zDifference > shakeThreshold) ||
                    (yDifference > shakeThreshold && zDifference > shakeThreshold) ){

                //to make it vibrate
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else{
                    vibrator.vibrate(500);
                }
                /* Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);*/

                // to bring the application up
                Intent intent = new Intent("android.intent.category.LAUNCHER");
                intent.setClassName("com.example.distressapplication", "com.example.distressapplication.MainActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }


        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        itISNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
