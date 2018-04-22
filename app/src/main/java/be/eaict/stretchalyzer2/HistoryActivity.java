package be.eaict.stretchalyzer2;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    //Graph declaration
    BarGraphSeries<DataPoint> series;
    private String numberMs;
    private String numberAngle;
    private ArrayList<String> angle = new ArrayList<>();
    private ArrayList<String> mSec = new ArrayList<>();
    private double x,y;

    //declaration timepicker dialog
    TextView startDate,endDate;
    Calendar cal1,cal2;
    int sDay,eDay,sMonth,eMonth,sYear,eYear;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        timePicker();
        createGraph();
    }

    // timepicker
    public void timePicker(){
        startDate = findViewById(R.id.textviewBeginDate);
        endDate = findViewById(R.id.textviewEndDate);
        cal1  = Calendar.getInstance();
        cal2 = Calendar.getInstance();

        eDay = cal1.get(Calendar.DAY_OF_MONTH);
        eMonth = cal1.get(Calendar.MONTH);
        eYear = cal1.get(Calendar.YEAR);

        sDay = cal1.get(Calendar.DAY_OF_MONTH);
        sMonth = cal2.get(Calendar.MONTH);
        sYear = cal2.get(Calendar.YEAR);

        sMonth = sMonth +1;
        eMonth = eMonth +1;
        String dateStart =  sDay+"/"+sMonth+"/"+sYear;
        String dateEnd =  eDay+"/"+eMonth+"/"+eYear;
        startDate.setText(dateStart);
        endDate.setText(dateEnd);
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
        series = new BarGraphSeries<>();
        for (int i = 0; i<mSec.size(); i++){
            x = Double.parseDouble(mSec.get(i));
            y = Double.parseDouble(angle.get(i));
            series.appendData(new DataPoint(x,y),true,mSec.size());
        }

        // data toevoegen aan graph
        graph.addSeries(series);

        //horizontal axsis title
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLUE);
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(40);

        //layout grafiek
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
        graph.getGridLabelRenderer().setHighlightZeroLines(true);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        //graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getViewport().setBackgroundColor(Color.WHITE);

        //waarde labels laten zien of niet
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));

        // vieuwport waarde tussen 200 en - 200 y-as
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-200);
        graph.getViewport().setMaxY(200);

        // vieuwport waarde tussen 0 en maxvalue array (ms) x-as
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(Double.parseDouble(Collections.max(mSec)));

        //scaling en scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        //title grafiek
        graph.setTitle("Progression of Stretching");
        graph.setTitleTextSize(50);
        graph.setTitleColor(Color.BLACK);

        //layout data
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);

        //kleuren bargraph
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });
    }

    public void OnClickShowHistoryGraph(View view) {
        // firebase data ophalen
    }

    public void OnClickEnd(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                endDate.setText(dayOfMonth+"/"+month+"/"+year);
            }
        },eYear,eMonth,eDay);
        datePickerDialog.show();
    }

    public void OnClickStart(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                startDate.setText(dayOfMonth+"/"+month+"/"+year);
            }
        },sYear,sMonth,sDay);
        datePickerDialog.show();
    }

}


