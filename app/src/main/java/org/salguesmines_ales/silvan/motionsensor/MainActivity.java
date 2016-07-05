package org.salguesmines_ales.silvan.motionsensor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long lastUpdate = 0;
    private double[] gravity = new double[3];
    private double[] linearAcceleration = new double[3];
    private List<Results> mListResults = new ArrayList<>();
    private Results tempResults = new Results();
    private Switch switch1;
    private long curTime = 0;
    private TextView textView;
    private TextView textView2;
    private ProgressBar progressBar;
    private Button initialize;
    private ImageView imageView;
    private JSONSerializer mSerializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        switch1 = (Switch) findViewById(R.id.switch1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imageView);
        initialize = (Button) findViewById(R.id.initialize);

        textView2.setText("No Record");
        progressBar.setProgress(0);

        mSerializer = new JSONSerializer("MotionSensor.json", MainActivity.this.getApplicationContext());
        try {
            mListResults = mSerializer.load();
        } catch (Exception e){
            Log.e("Error loading notes: ", "", e);
        }

        if (mListResults.isEmpty()) {
            imageView.setColorFilter(Color.argb(255, 230, 230, 230));
            initialize.setClickable(false);
        } else {
            imageView.setColorFilter(Color.argb(255, 255, 64, 129));
            initialize.setClickable(true);
        }

        switch1.setChecked(false);
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch1.setChecked(true);
                switch1.setClickable(false);
                initialize.setClickable(false);
                textView.setText("Recording");
                record();
                progress();
            }
        });

        initialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListResults.isEmpty()){
                    Toast.makeText(MainActivity.this, "No data recorded", Toast.LENGTH_SHORT).show();
                }else {
                    mListResults.clear();
                    initialize.setClickable(true);
                    imageView.setColorFilter(Color.argb(255, 230, 230, 230));
                    progressBar.setProgress(0);
                }
            }
        });
    }

    public void progress() {
        // do something long
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    final int value = i;
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(value);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    private void record(){

        mSensorManager.registerListener(MainActivity.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Handler recordHandler = new Handler();
        recordHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSensorManager.unregisterListener(MainActivity.this, mSensor);
                switch1.setChecked(false);
                switch1.setClickable(true);
                initialize.setClickable(true);
                textView.setText("No Action");
                textView2.setText("One record");
                initialize.setVisibility(View.VISIBLE);
                imageView.setColorFilter(Color.argb(255, 255, 64, 129));
            }
        }, 10000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (mSensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            double mAlpha = 0.8;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = mAlpha * gravity[0] + (1 - mAlpha) * event.values[0];
            gravity[1] = mAlpha * gravity[1] + (1 - mAlpha) * event.values[1];
            gravity[2] = mAlpha * gravity[2] + (1 - mAlpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linearAcceleration[0] = event.values[0] - gravity[0];
            linearAcceleration[1] = event.values[1] - gravity[1];
            linearAcceleration[2] = event.values[2] - gravity[2];

            lastUpdate = curTime - lastUpdate;
            curTime = System.currentTimeMillis();

            tempResults.setmTime(lastUpdate);
            tempResults.setmLinearAccelerationX(linearAcceleration[0]);
            tempResults.setmLinearAccelerationY(linearAcceleration[1]);
            tempResults.setmLinearAccelerationZ(linearAcceleration[2]);
            mListResults.add(tempResults);

            lastUpdate = curTime;

            /*Log.e("results ", "X = " + Float.toString(linearAcceleration[0]) + "  "
                    + "Y = " + Float.toString(linearAcceleration[1])+ "  "
                    + "Z = " + Float.toString(linearAcceleration[2]));*/
        }
    }

    public void displayResults(View view){
        if (mListResults.isEmpty()){
            Toast.makeText(MainActivity.this, "No data recorded", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "Make something soon", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DisplayResults.class);
            startActivity(intent);
        }
    }

    public void saveNotes(){
        mSerializer = new JSONSerializer("MotionSensor.json", MainActivity.this.getApplicationContext());
        try{
            mSerializer.save(mListResults);
        }catch(Exception e){
            Log.e("Error Saving Notes","", e);
        }
    }

    @Override
    protected void onPause()
    {
        // unregister the sensor
        mSensorManager.unregisterListener(this, mSensor);
        super.onPause();
        saveNotes();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
