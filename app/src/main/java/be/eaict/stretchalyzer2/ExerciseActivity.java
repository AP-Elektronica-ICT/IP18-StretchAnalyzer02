package be.eaict.stretchalyzer2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import be.eaict.stretchalyzer2.DOM.FBRepository;
import be.eaict.stretchalyzer2.DOM.GlobalData;
import be.eaict.stretchalyzer2.DOM.fxDatapoint;

/**
 * Created by Cé on 4/11/2018.
 */

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener {

    //declaration sensor
    Sensor accSensor;

    // graph declarations
    private LineGraphSeries<DataPoint> series;
    private int mSec;
    private double angle;

    //timer
    private TextView countdown;
    private CountDownTimer timer;

    private long mTimeLeftInMillis = GlobalData.startTime;

    //database declarations
    DatabaseReference databaseFXDatapoint;
    List<Double> angles = new ArrayList<>();
    FBRepository fbrepo = new FBRepository();

    //oncreate method
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_exercise );
        this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        createAccelerometer();
        createGraphview();
        changePics();
        counter();

        databaseFXDatapoint = fbrepo.instantiate();

    }

    //timer
    public void counter(){
        countdown = findViewById(R.id.countdown);
        timer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis= millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                if(GlobalData.Sensor){
                    fbrepo.SaveToDatabase( mSec, angles );
                }
                finish();
            }
        }.start();
    }
    //update timer text
    public void updateCountDownText(){
        int minutes =(int) (mTimeLeftInMillis/1000) /60;
        int seconds = (int) (mTimeLeftInMillis/1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        countdown.setText(timeLeftFormatted);
    }

    //method accelerometer
    public void createAccelerometer() {
        //permission
        SensorManager sensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        // accelerometer waarde geven
        accSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        // eventlistener
        sensorManager.registerListener( ExerciseActivity.this, accSensor, SensorManager.SENSOR_DELAY_NORMAL );

    }

    //veranderen van kleur aan de hand van settings met globale variable
    private void changePics(){
        ImageView bol1, bol2;
        bol1 = findViewById( R.id.btnbol1 );
        bol2 = findViewById( R.id.btnbol2 );

        if (GlobalData.Sensor) {
            bol1.setImageResource(R.drawable.groen);
            bol2.setImageResource(R.drawable.rood);
        }else {
            bol1.setImageResource(R.drawable.rood);
            bol2.setImageResource( R.drawable.groen);}
    }

    //method graphview
    private void createGraphview() {
        // graphvieuw aanmaken
        GraphView graph = (GraphView) findViewById( R.id.graph2 );
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries( series );

        //vertical axsis title
        graph.getGridLabelRenderer().setVerticalAxisTitle( "Angle" );
        graph.getGridLabelRenderer().setVerticalAxisTitleColor( Color.BLUE );
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize( 40 );

        //layout grafiek
        graph.getGridLabelRenderer().setGridColor( Color.BLACK );
        graph.getGridLabelRenderer().setHighlightZeroLines( true );
        graph.getGridLabelRenderer().setVerticalLabelsColor( Color.BLACK );
        graph.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.HORIZONTAL );
        graph.getViewport().setBackgroundColor( Color.WHITE );

        //miliseconds zichtbaar
        graph.getGridLabelRenderer().setHorizontalLabelsVisible( true );

        // vieuwport waarde instellen
        graph.getViewport().setYAxisBoundsManual( true );
        graph.getViewport().setMinY(-180);
        graph.getViewport().setMaxY(150 );

        // vieuwport waarde tussen 0 en maxvalue array (ms) x-as
        graph.getViewport().setXAxisBoundsManual( true );
        graph.getViewport().setMinX( -2500 );
        graph.getViewport().setMaxX( 2500 );


        //layout data
        series.setTitle( "Stretching" );
        series.setColor( Color.RED );
        series.setDrawDataPoints( true );
        series.setDataPointsRadius( 6 );
        series.setThickness( 4 );

    }

    //onResume nodig voor live data
    @Override
    protected void onResume() {
        super.onResume();

        if (GlobalData.Sensor) {


            // simulate real time met thread
            new Thread( new Runnable() {

                @Override
                public void run() {
                    //(12000 komt van 10 minuten * 60 seconden * 20(1 seconde om de 50miliseconden)
                    for (int i = 0; i < 12000; i++) {
                        runOnUiThread( new Runnable() {

                            @Override
                            public void run() {
                                addDatapoints();
                            }
                        } );

                        // sleep om de livedata te vertragen tot op ingegeven waarde
                        try {
                            Thread.sleep( 50);
                        } catch (InterruptedException e) {
                            // errors
                        }
                    }
                }
            } ).start();

        }
    }

    //datapoints toevoegen aan runnable
    private void addDatapoints() {

        //(12000 komt van 10 minuten * 60 seconden * 20(om de 50 miliseconden)
        series.appendData( new DataPoint( mSec += 50, angle ), true, 12000 );

        if (Double.isNaN( angle )) {
            angle = 0;
        }
        angles.add( angle );

    }

    //sensorEventlistener override method
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //sensorEventlistener override method
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //formule angle acc (werkt niet)
        angle = Math.asin( sensorEvent.values[1] / 9.81 ) / Math.PI * 180;
    }

    @Override
    public void onBackPressed() {

    }

    //terug knop
    public void onClickToHome(View view) {
        if(GlobalData.Sensor){
            fbrepo.SaveToDatabase( mSec, angles );
        }
        super.onBackPressed();
    }


}