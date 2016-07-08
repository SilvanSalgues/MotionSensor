package org.salguesmines_ales.silvan.motionsensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class RealTimeRecord extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double[] gravity = new double[3];
    private double[] linearAcceleration = new double[3];
    private ArrayList<Long> arrayTime =new ArrayList<>();
    private ArrayList<Entry> xVal = new ArrayList<>();
    private ArrayList<Entry> yVal = new ArrayList<>();
    private ArrayList<Entry> zVal = new ArrayList<>();
    private Entry linAccX;
    private Entry linAccY;
    private Entry linAccZ;
    private long curTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mSensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            double mAlpha = 0.8;
            linAccX = new Entry();
            linAccY = new Entry();
            linAccZ = new Entry();

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = mAlpha * gravity[0] + (1 - mAlpha) * event.values[0];
            gravity[1] = mAlpha * gravity[1] + (1 - mAlpha) * event.values[1];
            gravity[2] = mAlpha * gravity[2] + (1 - mAlpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linearAcceleration[0] = event.values[0] - gravity[0];
            linearAcceleration[1] = event.values[1] - gravity[1];
            linearAcceleration[2] = event.values[2] - gravity[2];

            curTime = System.currentTimeMillis();

            linAccX.setY((float) linearAcceleration[0]);
            linAccY.setY((float) linearAcceleration[1]);
            linAccZ.setY((float) linearAcceleration[2]);

            xVal.add(linAccX);
            yVal.add(linAccY);
            zVal.add(linAccZ);

            /*System.out.println(mListResults);*/

            Log.e("results ", "X = " + Double.toString(tempResults.getmLinearAccelerationX()) + "  "
                    + "Y = " + Double.toString(tempResults.getmLinearAccelerationY()) + "  "
                    + "Z = " + Double.toString(tempResults.getmLinearAccelerationZ()) + "  "
                    + "T = " + Long.toString(tempResults.getmTime()));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
