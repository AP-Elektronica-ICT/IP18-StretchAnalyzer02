package be.eaict.stretchalyzer2;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import be.eaict.stretchalyzer2.DOM.FBRepository;
import be.eaict.stretchalyzer2.DOM.GlobalData;
import be.eaict.stretchalyzer2.DOM.fxDatapoint;

public class HomeActivity extends AppCompatActivity {


    //Graph declaration
    LineGraphSeries<DataPoint> series;
    private String numberMs;
    private String numberAngle;
    private ArrayList<String> angle = new ArrayList<>();
    private ArrayList<String> mSec = new ArrayList<>();
    private double x, y;

    //  private final String DEVICE_NAME="MyBTBee";
    private final String DEVICE_ADDRESS = "20:13:10:15:33:66";
    private final UUID PORT_UUID = UUID.fromString( "00001101-0000-1000-8000-00805f9b34fb" );//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    TextView textView;
    boolean deviceConnected = false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    private String data;
    private int mInterval = 100;
    private Handler mHandler;
    private Context ctx;
    private TextToSpeech tts;
    private Button btnStart;
    private String text;
    private List<fxDatapoint> datapoints = new ArrayList<>();
    FBRepository fbrepo = new FBRepository();
    DatabaseReference databaseFXDatapoint;
    final List<fxDatapoint> datapointList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        databaseFXDatapoint = fbrepo.instantiate();
        readData();
        createGraph();
        startRepeatingTask();
        ctx = this.getApplicationContext();
        btnStart = findViewById( R.id.btnStart );
        text = "Start exercising!";


        tts = new TextToSpeech( this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                createTts( status );

            }
        } );

        btnStart.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak( text );
                openExerciseActivity();

            }
        } );

    }

    //Text-to-Speech
    private void createTts(int status) {
        Locale locale = new Locale( "en", "IN" );
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage( locale );
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e( "TTS", "This Language is not supported" );
            }

        } else {
            Log.e( "TTS", "Initilization Failed!" );
        }
    }

    private void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak( text, TextToSpeech.QUEUE_FLUSH, null, null );
        } else {
            tts.speak( text, TextToSpeech.QUEUE_FLUSH, null );
        }
    }

    //Graph Method
    public void createGraph() {

        //data uitlezen uit text files (graph)
        try {
            InputStream streamMs = getAssets().open( "ms.txt" );
            InputStream streamAngle = getAssets().open( "angle.txt" );

            BufferedReader readerMs = new BufferedReader( new InputStreamReader( streamMs ) );
            BufferedReader readerAngle = new BufferedReader( new InputStreamReader( streamAngle ) );

            //lijn per lijn nakijken en in array plaatsen
            while ((numberMs = readerMs.readLine()) != null)
                mSec.add( numberMs );
            while ((numberAngle = readerAngle.readLine()) != null)
                angle.add( numberAngle );
        } catch (IOException e) {
            e.printStackTrace();
        }

        //graphvieuw aanmaken
        GraphView graph = findViewById( R.id.graph );


//data aan graph toevoegen

        series = new LineGraphSeries<DataPoint>();

        Date currentDate, previousDate = null, newestDate = null, usedDate = null;
        SimpleDateFormat df = new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" );

        for (fxDatapoint punt : datapointList) {
            try {
                currentDate = df.parse( punt.getDatum() );
                if (previousDate == null) {
                    newestDate = currentDate;
                    previousDate = currentDate;
                } else {
                    if (currentDate.after( previousDate )) {
                        newestDate = currentDate;
                        previousDate = currentDate;
                    }else if(currentDate.before( previousDate )){

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        for (fxDatapoint punt : datapointList) {
            try{
                usedDate = df.parse( punt.getDatum() );
            }
            catch (ParseException e){
                e.printStackTrace();
            }

            if(usedDate.equals( newestDate )){
                x = -50;
                double total = punt.getTimestamp();
                for (Double angle : punt.getAngles()) {
                    x += total / punt.getAngles().size();
                    y = angle;
                    series.appendData( new DataPoint( x, y ), true, punt.getAngles().size() );
                }
            }
        }

        // data toevoegen aan graph
        graph.removeAllSeries();
        graph.addSeries( series );

        //vertical axsis title
        graph.getGridLabelRenderer().setVerticalAxisTitle( "Angle" );
        graph.getGridLabelRenderer().setVerticalAxisTitleColor( Color.BLUE );
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize( 40 );

        //horizontal axsis title
        graph.getGridLabelRenderer().setHorizontalAxisTitle(  "mSec"  );
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor( Color.BLUE );
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize( 40 );

        //layout grafiek
        graph.getGridLabelRenderer().setGridColor( Color.BLACK );
        graph.getGridLabelRenderer().setHighlightZeroLines( true );
        graph.getGridLabelRenderer().setVerticalLabelsColor( Color.BLACK );
        graph.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.HORIZONTAL );
        graph.getViewport().setBackgroundColor( Color.WHITE );

        //miliseconds onzichtbaar
        graph.getGridLabelRenderer().setHorizontalLabelsVisible( false );

        // vieuwport waarde tussen 180 en - 180 y-as
        graph.getViewport().setYAxisBoundsManual( true );
        graph.getViewport().setMinY( -180 );
        graph.getViewport().setMaxY( 180 );

        // vieuwport waarde tussen 0 en maxvalue array (ms) x-as
        graph.getViewport().setXAxisBoundsManual( true );
        graph.getViewport().setMinX( 0 );
        graph.getViewport().setMaxX( Double.parseDouble( Collections.max( mSec ) ) );

        //scaling en scrolling
        graph.getViewport().setScalable( true );
        graph.getViewport().setScalableY( true );

        //title grafiek
        graph.setTitle( "Latest Stretchfeedback" );
        graph.setTitleTextSize( 60 );
        graph.setTitleColor( Color.BLACK );

        //layout data
        series.setColor( Color.RED );
        series.setDrawDataPoints( true );
        series.setDataPointsRadius( 6 );
        series.setThickness( 4 );
    }

    public void readData() {
        // Read from the database
        databaseFXDatapoint.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    fxDatapoint retDatapoint = child.getValue( fxDatapoint.class );

                    FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String current = CurrentUser.getUid();
                    String testuser = retDatapoint.getId();
                    if (testuser.equals( current )) {
                        datapointList.add( retDatapoint );
                    }

                }
                createGraph();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w( "database", "Failed to read data.", error.toException() );
            }
        } );
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        stopRepeatingTask();
        super.onDestroy();
    }

    //Data checker
    Runnable mDataChecker = new Runnable() {
        @Override
        public void run() {  //Repeating task 
            try {
                try {
                    data = new String( BluetoothManager.getInstance( ctx ).buffer, StandardCharsets.UTF_8 );
                } finally {
                    mHandler.postDelayed( mDataChecker, mInterval );
                }
            } catch (Exception ex) {

            }
        }
    };

    void startRepeatingTask() {
        mDataChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks( mDataChecker );
    }

    //OnClickListener naar settings activity
    public void onClickSettings(View view) {
        openApplicationSettingsActivity();
    }

    //Intent method naar exercise activity
    private void openExerciseActivity() {
        Intent intent = new Intent( HomeActivity.this, ExerciseActivity.class );
        startActivity( intent );
    }

    //Intent method naar settings activity
    private void openApplicationSettingsActivity() {
        Intent intent = new Intent( HomeActivity.this, Settings.class );
        startActivity( intent );
    }

    //android lifecycle
    @Override
    public void onBackPressed() {
        moveTaskToBack( true );
    }

    //historyActivity intent
    public void onClickHistory(View view) {
        Intent intent = new Intent( HomeActivity.this, HistoryActivity.class );
        startActivity( intent );
    }

    //AccountActivity intent
    public void onClickAccount(View view) {
        Intent intent = new Intent( HomeActivity.this, AccountActivity.class );
        startActivity( intent );
    }

    public void onClickNextExercise(View view) {
        ImageView img, img2;
        img = findViewById( R.id.imageView );
        img2 = findViewById( R.id.imageView2 );
        img.setImageResource( R.drawable.pic1 );
        img2.setImageResource( R.drawable.pic2 );

    }

    public void onClickSelectedExercise(View view) {
        ImageView img, img2;
        img = findViewById( R.id.imageView );
        img2 = findViewById( R.id.imageView2 );
        img.setImageResource( R.drawable.pic2 );
        img2.setImageResource( R.drawable.pic1 );
    }

    public List<fxDatapoint> filterTodaysData() {
        List<fxDatapoint> todaysPoints = new ArrayList<>();
        datapoints = fbrepo.ReadFromDatabase();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat( "dd-MMM-yyyy" );
        String today = df.format( c );
        String pointDatum;
        for (fxDatapoint point : datapoints) {
            pointDatum = df.format( point.getDatum() );
            if (pointDatum == today) {
                todaysPoints.add( point );
            }
        }

        if (todaysPoints.isEmpty()) return null;
        return todaysPoints;
    }

}
