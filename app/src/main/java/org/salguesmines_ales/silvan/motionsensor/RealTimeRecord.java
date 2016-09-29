package org.salguesmines_ales.silvan.motionsensor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class RealTimeRecord extends AppCompatActivity implements SensorEventListener, OnChartValueSelectedListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double[] gravity = new double[3];
    private double[] linearAcceleration = new double[3];
    private Switch recording;
    private Button clearVal;
    private LineChart mLineChart;
    private Thread thread;
    private long initTime;
    private float curTime;
    private ILineDataSet xDataSet;
    private LineData data;
    public int itt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mLineChart = (LineChart) findViewById(R.id.chart1);
        mLineChart.setOnChartValueSelectedListener(this);
        mLineChart.setDescription("record rt");
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setPinchZoom(true);

        /*Legend lg = mLineChart.getLegend();
        lg.setEnabled(true);
        lg.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        lg.setCustom(new int[]{Color.RED, Color.GREEN, Color.BLUE}, new String[]{"Acc.Lin.X", "Acc.Lin.Y", "Acc.Lin.Z"});*/

        mLineChart.notifyDataSetChanged();

        recording = (Switch) findViewById(R.id.recording);
        recording.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initTime = System.currentTimeMillis();
                addValues();
                /*rtRecord();*/
            }
        });

        clearVal = (Button) findViewById(R.id.clearVal);
        clearVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.clearValues();
                Toast.makeText(RealTimeRecord.this, "Chart cleared!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LineDataSet createSetX(){

        LineDataSet set = new LineDataSet(null, "X");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawCircles(false);
        set.setColor(Color.rgb(255, 0, 0));
        return set;
    }

    private void addValues() {

        mSensorManager.registerListener(RealTimeRecord.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                data = mLineChart.getData();

                if (data != null) {

                    xDataSet = data.getDataSetByIndex(0);

                    if (xDataSet == null) {
                        xDataSet = createSetX();
                        data.addDataSet(xDataSet);
                        itt =0;
                    }
                }
                data.addEntry(new Entry(curTime, (float) linearAcceleration[0]),itt);

                data.notifyDataChanged();
                mLineChart.notifyDataSetChanged();
                mLineChart.setVisibleXRangeMaximum(120);
                mLineChart.moveViewToX(data.getEntryCount());
                itt++;
                curTime = (float)(System.currentTimeMillis()-initTime);

                mLineChart.invalidate();
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    runOnUiThread(runnable);
                }
                mSensorManager.unregisterListener(RealTimeRecord.this, mSensor);
                recording.setChecked(false);
            }
        });
        thread.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mSensor);
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}
