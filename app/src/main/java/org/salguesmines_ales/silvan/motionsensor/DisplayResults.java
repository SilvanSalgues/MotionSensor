package org.salguesmines_ales.silvan.motionsensor;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DisplayResults extends AppCompatActivity {

    private List<Results> mListResults = new ArrayList<>();
    private Results results = new Results();
    private JSONSerializer mSerializer;

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
            mListResults = new ArrayList<Results>();
            Log.e("Error loading notes: ", "", e);
        }
    }


}
