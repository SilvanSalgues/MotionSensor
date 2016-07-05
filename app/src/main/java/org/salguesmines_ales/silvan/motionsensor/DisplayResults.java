package org.salguesmines_ales.silvan.motionsensor;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.List;

public class DisplayResults extends AppCompatActivity {

    private List<Results> mListResults = new ArrayList<>();
    private Results mResults;
    private JSONSerializer mSerializer;
    private LineChart lineChart;
    private int iteration;
    private ArrayList<Double> xVal, yVal, zVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSerializer = new JSONSerializer("MotionSensor.json", DisplayResults.this.getApplicationContext());
        try {
            mListResults = mSerializer.load();
        } catch (Exception e){
            Log.e("Error loading notes: ", "", e);
        }

        calculation();

        lineChart = (LineChart) findViewById(R.id.chart);
    }

    public void calculation() {
        iteration = mListResults.size();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i <= iteration; i++){
                try {
                    mResults = mListResults.get(i);
                    xVal.add(mResults.getmLinearAccelerationX());
                    yVal.add(mResults.getmLinearAccelerationY());
                    zVal.add(mResults.getmLinearAccelerationZ());
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
        new Thread(runnable).start();
    }
}
