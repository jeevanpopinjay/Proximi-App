package com.android.project.proximi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;

public class AndroidProximitySensorActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback  {
    /** Called when the activity is first created. */

    TextView ProximitySensor, ProximityMax, ProximityReading;

    SensorManager mySensorManager;
    Sensor myProximitySensor;
    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;
    SurfaceView cameraView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.content_android_proximity_sensor);

        ProximitySensor = (TextView)findViewById(R.id.proximitySensor);

        ProximityMax = (TextView)findViewById(R.id.proximityMax);
        ProximityReading = (TextView)findViewById(R.id.proximityReading);

        mySensorManager = (SensorManager)getSystemService(
                Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);

        if (myProximitySensor == null){
            ProximitySensor.setText("No Proximity Sensor!");
        }else{
            ProximitySensor.setText(myProximitySensor.getName());
            ProximityMax.setText("Maximum Range:" + String.valueOf(myProximitySensor.getMaximumRange()));
            mySensorManager.registerListener(proximitySensorEventListener,
                    myProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);


        }

    }

    public void startCamera(){
       /* requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);*/

        recorder = new MediaRecorder();
        initRecorder();
        setContentView(R.layout.content_android_proximity_sensor);

        cameraView = (SurfaceView) findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);


        Thread thread=  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(5000);
                        /*prepareRecorder();
                        recording = true;
                        recorder.start(); */

                        // onClick(cameraView);

                    }
                }
                catch(InterruptedException ex){
                }
                /*prepareRecorder();
                cameraView.callOnClick();*/

                // TODO
            }
        };

        thread.start();
        //prepareRecorder();

    }
    private void initRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        CamcorderProfile cpHigh = CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);
        recorder.setOutputFile("/sdcard/videocapture_morning15.mp4");
        recorder.setMaxDuration(50000); // 50 seconds
        recorder.setMaxFileSize(500000000); // Approximately 5 megabytes
    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());

        try {
            recorder.prepare();
            cameraView.callOnClick();
            cameraView.setClickable(false);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    public void onClick(View v) {
        if (recording) {
            recorder.stop();
            recording = false;

            // Let's initRecorder so we can record again
            initRecorder();
            prepareRecorder();
        } else {
            recording = true;
            recorder.start();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        //finish();
    }

    SensorEventListener proximitySensorEventListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            int a = 0;
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                ProximityReading.setText("Proximity Sensor Reading:"
                        + String.valueOf(event.values[0]));
                /*ProximityReading.setText("Proximity Sensor Reading2:"
                        + String.valueOf(event.values[1]));*/

            }
            if (String.valueOf(event.values[0]) == String.valueOf(event.values[1])) {
                a++;
                try {

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String messageToSend = "Alert Intrusion Detected";
                String number = "+16156354834";

               // SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);
                if(a==1) {
                    startCamera();
                }

            }
        }
       /* MediaPlayer
        mp = MediaPlayer.create(AndroidProximitySensorActivity.this, R.raw.)*/


    };
}