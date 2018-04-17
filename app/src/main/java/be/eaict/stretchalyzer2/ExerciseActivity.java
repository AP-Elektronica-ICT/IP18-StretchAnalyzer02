package be.eaict.stretchalyzer2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Cé on 4/11/2018.
 */

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener {

    //declaration sensor
    Sensor accSensor;

    //declatration test textvieuws mag weg nadien !!!
    TextView yValue;

    // graph declarations
    private LineGraphSeries<DataPoint> series;
    private int mSec;
    private double angle;

    //oncreate method
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        createAccelerometer();
        createGraphview();
    }

    //method accelerometer
    public void createAccelerometer(){
        //permission
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // accelerometer waarde geven
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // eventlistener
        sensorManager.registerListener(ExerciseActivity.this, accSensor,SensorManager.SENSOR_DELAY_NORMAL);

        //test textvieuws mag weg nadien
        yValue = (TextView) findViewById(R.id.yValue);
    }

    //method graphview
    private void createGraphview() {
        // graphvieuw aanmaken
        GraphView graph = (GraphView) findViewById(R.id.graph2);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);

        //vertical axsis title
        graph.getGridLabelRenderer().setVerticalAxisTitle("Angle");
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLUE);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(40);

        //layout grafiek
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
        graph.getGridLabelRenderer().setHighlightZeroLines(true);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getViewport().setBackgroundColor(Color.WHITE);

        //miliseconds zichtbaar
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);

        // vieuwport waarde tussen 180 en - 180 y-as
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-180);
        graph.getViewport().setMaxY(180);

        // vieuwport waarde tussen 0 en maxvalue array (ms) x-as
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(6000);


        //layout data
        series.setTitle("Stretching");
        series.setColor(Color.RED);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(6);
        series.setThickness(4);

    }


    //onResume nodig voor live data
    @Override
    protected void onResume() {
        super.onResume();
        // simulate real time met thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                //(12000 komt van 10 minuten * 60 seconden * 20(1 seconde om de 50 miliseconden)
                for (int i = 0; i < 12000; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addDatapoints();
                        }
                    });

                    // sleep om de livedata te vertragen tot op ingegeven waarde
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // errors
                    }
                }
            }
        }).start();
    }


    //datapoints toevoegen aan runnable
    private void addDatapoints() {
        //(12000 komt van 10 minuten * 60 seconden * 20(om de 50 miliseconden)
        series.appendData(new DataPoint(mSec+=50, angle), true, 12000);
    }



    //sensorEventlistener override method
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //sensorEventlistener override method
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //formule angle acc (werkt niet)
       angle =  Math.asin(sensorEvent.values[1]/9.81)/Math.PI*180;


       //test textvieuws mag weg nadien
        yValue.setText("Angle :" + angle);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //terug knop
    public void onClickToHome(View view) {
        onBackPressed();
    }
}