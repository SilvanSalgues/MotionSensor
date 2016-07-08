package org.salguesmines_ales.silvan.motionsensor;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class DisplayResults extends AppCompatActivity {

    private List<Results> mListResults = new ArrayList<>();
    private  Results mResults = new Results();
    private JSONSerializer mSerializer;
    private LineChart mLineChart;
    private int iteration;
    private ArrayList<Entry> xVal = new ArrayList<>();
    private ArrayList<Entry> yVal = new ArrayList<>();
    private ArrayList<Entry> zVal = new ArrayList<>();
    private static ProgressDialog mProgressDialog;
    private static Context mContext;
    private ErrorStatus status;

    /*private ArrayList<Double> xVal = new ArrayList<>();
    private ArrayList<Double> yVal = new ArrayList<>();
    private ArrayList<Double> zVal = new ArrayList<>();*/

    public static final int MSG_ERR = 0, MSG_CNF = 1, MSG_IND = 2;

    public static final String TAG = "ProgressBarActivity";

    enum ErrorStatus { NO_ERROR }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        mSerializer = new JSONSerializer("MotionSensor.json", DisplayResults.this.getApplicationContext());
        try {
            mListResults = mSerializer.load();
        } catch (Exception e){
            Log.e("Error loading notes: ", "", e);
        }
        mLineChart = (LineChart) findViewById(R.id.chart);
        calculation();
    }

    public void calculation() {
        mProgressDialog = ProgressDialog.show(this, "Please wait", "Long operation starts...", true);
        iteration = mListResults.size()-1;

        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Treatment of data...";
                msg = mHandler.obtainMessage(MSG_IND, progressBarData);
                mHandler.sendMessage(msg);
                status = treatmentData();

                if (ErrorStatus.NO_ERROR != status) {
                    Log.e(TAG, "error while parsing the file status:" + status);
                    msg = mHandler.obtainMessage(MSG_ERR, "error while parsing the file status:" + status);
                    mHandler.sendMessage(msg);
                } else {
                    progressBarData = "Preparation for displaying...";
                    msg = mHandler.obtainMessage(MSG_IND, progressBarData);
                    mHandler.sendMessage(msg);

                    status = dataToChart();

                    if (ErrorStatus.NO_ERROR != status) {
                        Log.e(TAG, "error while computing the path status:" + status);
                        msg = mHandler.obtainMessage(MSG_ERR, "error while computing the path status:" + status);
                        mHandler.sendMessage(msg);
                    } else {
                        msg = mHandler.obtainMessage(MSG_CNF, "Parsing and computing ended successfully !");
                        mHandler.sendMessage(msg);
                    }
                }
            }
        })).start();

        // displaying results
        mLineChart.invalidate();
    }

    protected ErrorStatus treatmentData() {

        mResults = mListResults.get(0);
        Float initTime = (float) mResults.getmTime();

        for (int i = 0; i <= iteration; i++){
            mResults = mListResults.get(i);
            /*xVal.add(mResults.getmLinearAccelerationX());
            yVal.add(mResults.getmLinearAccelerationY());
            zVal.add(mResults.getmLinearAccelerationZ());*/

            Float tempX = (float) mResults.getmLinearAccelerationX();
            Float tempY = (float) mResults.getmLinearAccelerationY();
            Float tempZ = (float) mResults.getmLinearAccelerationZ();
            Float tempT = (mResults.getmTime() - initTime);
            Entry entryX = new Entry(i, tempX);
            xVal.add(entryX);
            Entry entryY = new Entry(i, tempY);
            yVal.add(entryY);
            Entry entryZ = new Entry(i, tempZ);
            zVal.add(entryZ);
        }

        return ErrorStatus.NO_ERROR;
    }

    protected ErrorStatus dataToChart() {

        LineDataSet setX= new LineDataSet(xVal, "X");
        setX.setAxisDependency(YAxis.AxisDependency.LEFT);
        setX.setDrawCircles(false);
        setX.setColor(Color.rgb(255, 0, 0));

        LineDataSet setY = new LineDataSet(yVal, "Y");
        setY.setAxisDependency(YAxis.AxisDependency.LEFT);
        setY.setDrawCircles(false);
        setY.setColor(Color.rgb(0, 255, 0));

        LineDataSet setZ = new LineDataSet(zVal, "Z");
        setZ.setAxisDependency(YAxis.AxisDependency.LEFT);
        setZ.setDrawCircles(false);
        setZ.setColor(Color.rgb(0, 0, 255));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setX);
        dataSets.add(setY);
        dataSets.add(setZ);
        LineData data = new LineData(dataSets);
        mLineChart.setData(data);

        Legend lg = mLineChart.getLegend();
        lg.setEnabled(true);
        lg.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        lg.setCustom(new int[]{Color.RED, Color.GREEN, Color.BLUE}, new String[]{"Acc.Lin.X", "Acc.Lin.Y", "Acc.Lin.Z"});
        mLineChart.notifyDataSetChanged();

        return ErrorStatus.NO_ERROR;
    }

    final static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            String text2display = null;
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_ERR:
                    text2display = (String) msg.obj;
                    Toast.makeText(mContext, "Error: " + text2display, Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_CNF:
                    text2display = (String) msg.obj;
                    Toast.makeText(mContext, "Info: " + text2display, Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                default: // should never happen
                    break;
            }
        }
    };

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}
