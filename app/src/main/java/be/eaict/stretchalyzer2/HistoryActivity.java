package be.eaict.stretchalyzer2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    //Graph declaration
    LineGraphSeries<DataPoint> series;
    private String numberMs;
    private String numberAngle;
    private ArrayList<String> angle = new ArrayList<>();
    private ArrayList<String> mSec = new ArrayList<>();
    private double x,y;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        createGraph();
    }
    //Graph Method
    public void createGraph(){

        //data uitlezen uit text files (graph)
        try {
            InputStream streamMs = getAssets().open("ms.txt");
            InputStream streamAngle = getAssets().open("angle.txt");

            BufferedReader readerMs= new BufferedReader(new InputStreamReader(streamMs));
            BufferedReader readerAngle = new BufferedReader(new InputStreamReader(streamAngle));

            //lijn per lijn nakijken en in array plaatsen
            while((numberMs = readerMs.readLine()) != null)
                mSec.add(numberMs);
            while((numberAngle= readerAngle.readLine()) != null)
                angle.add(numberAngle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //graphvieuw aanmaken
        GraphView graph =  findViewById(R.id.graph3);

        //data aan graph toevoegen
        series = new LineGraphSeries<DataPoint>();
        for (int i = 0; i<mSec.size(); i++){
            x = Double.parseDouble(mSec.get(i));
            y = Double.parseDouble(angle.get(i));
            series.appendData(new DataPoint(x,y),true,mSec.size());
        }

        // data toevoegen aan graph
        graph.addSeries(series);

        //vertical axsis title
        graph.getGridLabelRenderer().setVerticalAxisTitle("Angle");
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLUE);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(40);

        //horizontal axsis title
        graph.getGridLabelRenderer().setHorizontalAxisTitle("mSec");
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLUE);
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(40);

        //layout grafiek
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
        graph.getGridLabelRenderer().setHighlightZeroLines(true);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getViewport().setBackgroundColor(Color.WHITE);

        //miliseconds onzichtbaar
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        // vieuwport waarde tussen 180 en - 180 y-as
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-180);
        graph.getViewport().setMaxY(180);

        // vieuwport waarde tussen 0 en maxvalue array (ms) x-as
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(Double.parseDouble(Collections.max(mSec)));

        //scaling en scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        //title grafiek
        graph.setTitle("24 Hour Activity Feed");
        graph.setTitleTextSize(50);
        graph.setTitleColor(Color.BLACK);

        //layout data
        series.setColor(Color.RED);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(6);
        series.setThickness(4);
    }
}
