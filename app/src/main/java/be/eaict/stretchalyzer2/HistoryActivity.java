package be.eaict.stretchalyzer2;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import be.eaict.stretchalyzer2.DOM.FBRepository;
import be.eaict.stretchalyzer2.DOM.fxDatapoint;

public class HistoryActivity extends AppCompatActivity {

    //Graph declaration
    BarGraphSeries<DataPoint> series;
    private String numberMs;
    private String numberAngle;
    private ArrayList<String> angle = new ArrayList<>();
    private ArrayList<String> mSec = new ArrayList<>();
    private double x, y;
    final List<fxDatapoint> datapointList = new ArrayList<>();
    private List<Double> percentageMax = new ArrayList<>();
    private List<Double> percentageMin = new ArrayList<>();
    DatabaseReference databaseFXDatapoint;
    FBRepository fbrepo = new FBRepository();

    TextView txtUpPercentage, txtDownPercentage;

    //declaration timepicker dialog
    TextView startDate, endDate;
    Calendar cal1, cal2;
    int sDay, eDay, sMonth, eMonth, sYear, eYear;
    String dateEnd, dateStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_history );
        this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        databaseFXDatapoint = fbrepo.instantiate();
        timePicker();
        readData();
        createGraph();
    }

    // timepicker
    public void timePicker() {
        startDate = findViewById( R.id.textviewBeginDate );
        endDate = findViewById( R.id.textviewEndDate );
        cal1 = Calendar.getInstance();
        cal2 = Calendar.getInstance();

        eDay = cal1.get( Calendar.DAY_OF_MONTH );
        eMonth = cal1.get( Calendar.MONTH );
        eYear = cal1.get( Calendar.YEAR );

        sDay = cal1.get( Calendar.DAY_OF_MONTH );
        sMonth = cal2.get( Calendar.MONTH );
        sYear = cal2.get( Calendar.YEAR );

        eDay++;
        sMonth = sMonth + 1;
        eMonth = eMonth + 1;

        dateStart = sDay + "-" + sMonth + "-" + sYear;
        dateEnd = eDay + "-" + eMonth + "-" + eYear;
        startDate.setText( dateStart );
        endDate.setText( dateEnd );
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
        GraphView graph = findViewById( R.id.graph3 );

        //data aan graph toevoegen
        series = new BarGraphSeries<>();


        Date currentDate = null, dtStartDate = null, dtEndDate = null;
        SimpleDateFormat filter = new SimpleDateFormat( "dd-MM-yyyy" );
        percentageMax.clear();
        percentageMin.clear();
        try {
            dtStartDate = filter.parse( startDate.getText().toString() );
            dtEndDate = filter.parse( endDate.getText().toString() );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        x = 0;
        for (fxDatapoint punt : datapointList) {
            try {
                currentDate = filter.parse( punt.getDatum() );

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if ((currentDate.after( dtStartDate ) && currentDate.before( dtEndDate )) || currentDate.equals( dtStartDate ) || currentDate.equals( dtEndDate )) {
                double max = 0, min = 0, temp1 = 0, temp2 = -1000, temp3 = 0, temp4 = 1000;

                for (Double angle : punt.getAngles()) {
                    temp1 = angle;
                    if (temp2 == -1000) {
                        max = temp1;
                        temp2 = temp1;
                    }
                    if (temp1 > temp2) {
                        max = temp1;
                        temp2 = temp1;
                    }
                }

                x += 1;
                y = max;
                percentageMax.add( max );
                series.appendData( new DataPoint( x, y ), true, punt.getAngles().size() );

                for (Double angle : punt.getAngles()) {
                    temp3 = angle;
                    if (temp3 == 1000) {
                        min = temp3;
                        temp4 = temp3;
                    }
                    if (temp3 < temp4) {
                        min = temp3;
                        temp4 = temp3;
                    }
                }

                x += 1;
                y = min;
                percentageMin.add( min );
                series.appendData( new DataPoint( x, y ), true, punt.getAngles().size() );

                x += 1;
            }
        }


        // data toevoegen aan graph
        graph.removeAllSeries();
        graph.addSeries( series );
        setPercentages();

        //horizontal axsis title
        graph.getGridLabelRenderer().setHorizontalAxisTitle( "Date" );
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor( Color.BLUE );
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize( 40 );

        //layout grafiek
        graph.getGridLabelRenderer().setGridColor( Color.BLACK );
        graph.getGridLabelRenderer().setHighlightZeroLines( true );
        graph.getGridLabelRenderer().setVerticalLabelsColor( Color.BLACK );
        graph.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.HORIZONTAL );
        graph.getViewport().setBackgroundColor( Color.WHITE );

        //waarde labels laten zien of niet
        graph.getGridLabelRenderer().setVerticalLabelsVisible( false );
        graph.getGridLabelRenderer().setHorizontalLabelsVisible( true );

        // vieuwport waarde tussen 200 en - 200 y-as
        graph.getViewport().setYAxisBoundsManual( true );
        graph.getViewport().setMinY( -200 );
        graph.getViewport().setMaxY( 200 );

        // vieuwport waarde tussen 0 en maxvalue array (ms) x-as
        graph.getViewport().setXAxisBoundsManual( true );
        graph.getViewport().setMinX( 0 );
        graph.getViewport().setMaxX( 20 );

        //scaling en scrolling
        graph.getViewport().setScalable( true );
        graph.getViewport().setScalableY( true );

        //title grafiek
        graph.setTitle( "Progression of Stretching" );
        graph.setTitleTextSize( 50 );
        graph.setTitleColor( Color.BLACK );

        //layout data
        series.setDrawValuesOnTop( true );
        series.setValuesOnTopColor( Color.RED );

        //kleuren bargraph
        series.setValueDependentColor( new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb( (int) data.getX() * 255 / 4, (int) Math.abs( data.getY() * 255 / 6 ), 100 );
            }
        } );
    }

    public void OnClickShowHistoryGraph(View view) {
        // firebase data ophalen
    }

    public void OnClickEnd(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog( HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (checkDataFields( dayOfMonth, month, year, "end" ).equals( "" )) {
                    endDate.setText( dayOfMonth + "-" + month + "-" + year );
                } else {
                    endDate.setText( checkDataFields( dayOfMonth, month, year, "end" ) );
                }
                createGraph();
            }
        }, eYear, eMonth, eDay );
        datePickerDialog.show();
    }

    public void OnClickStart(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog( HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate.setText( dayOfMonth + "-" + month + "-" + year );
                if (checkDataFields( dayOfMonth, month, year, "start" ).equals( "" )) {
                } else {
                    endDate.setText( checkDataFields( dayOfMonth, month, year, "start" ) );
                }
                createGraph();
            }

        }, sYear, sMonth, sDay );
        datePickerDialog.show();
    }

    private String checkDataFields(int Day, int Month, int Year, String startOrEnd) {
        Calendar clStart, clEnd;
        clStart = Calendar.getInstance();
        clEnd = Calendar.getInstance();

        String dateEnd = "";
        SimpleDateFormat df = new SimpleDateFormat( "dd-MM-yyyy" );
        String strStDate, strEnDate;

        if (startOrEnd.equals( "start" )) {
            strEnDate = endDate.getText().toString();
            try {
                clEnd.setTime( df.parse( strEnDate ) );
            } catch (ParseException e) {
                e.printStackTrace();
            }

            clStart.set( Year, Month, Day );

            if (clEnd.after( clStart )) {
                dateEnd = Day + "-" + Month + "-" + Year;
                return dateEnd;
            }
            return "";

        } else if (startOrEnd.equals( "end" )) {
            strStDate = startDate.getText().toString();
            try {
                clStart.setTime( df.parse( strStDate ) );
            } catch (ParseException e) {
                e.printStackTrace();
            }

            clEnd.set( Year, Month, Day );

            if (clStart.before( clEnd )) {
                dateEnd = Day + "-" + Month + "-" + Year;
                return dateEnd;
            }
            return "";
        }

        return "";
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

    public void setPercentages() {
        txtDownPercentage = findViewById( R.id.txtDownProcent );
        txtUpPercentage = findViewById( R.id.txtUpProcent );
        txtUpPercentage.setText( setMaxPercentage() + "%" );
        txtDownPercentage.setText( setMinPercentage() + "%" );
    }

    public int setMaxPercentage() {
        double avg = 0, total = 0, decrease = 0;
        int percentage = 0;
        if (percentageMax.size() > 0) {
            for (double punt : percentageMax) {
                total += punt;
            }
            avg = total / percentageMax.size();
            decrease = avg - percentageMax.get( percentageMax.size() - 1 );
            percentage = (int) (decrease / avg * 100);
            percentage = -percentage;
        } else {
            percentage = 0;
        }
        return percentage;
    }

    public int setMinPercentage() {
        double avg = 0, total = 0, decrease = 0;
        int percentage = 0;
        if (percentageMin.size() > 0) {
            for (double punt : percentageMin) {
                total += (-punt);
            }
            avg = total / percentageMin.size();
            decrease = avg + percentageMin.get( percentageMin.size() - 1 );
            percentage = (int) (decrease / avg * 100);
            percentage = -percentage;
        } else {
            percentage = 0;
        }
        return percentage;
    }
}


