package be.eaict.stretchalyzer2;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

    //declaration datepicker
    private TextView mDisplayDate1;
    private DatePickerDialog.OnDateSetListener mDateSetListener1;
    private TextView mDisplayDate2;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;


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
        graph.getViewport().setBackgroundColor(Color.WHITE);

        //waarde labels laten zien of niet
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);

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
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

      /*  DatePickerDialog dialog = new DatePickerDialog(
                HistoryActivity.this,)*/

    }

    public void OnClickStart(View view) {
    }
}

/*    // generate Dates
    Calendar calendar = Calendar.getInstance();
    Date d1 = calendar.getTime();
calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();

        GraphView graph = (GraphView) findViewById(R.id.graph);

// you can directly pass Date objects to DataPoint-Constructor
// this will convert the Date to double via Date#getTime()
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
        new DataPoint(d1, 1),
        new DataPoint(d2, 5),
        new DataPoint(d3, 3)
        });

        graph.addSeries(series);

// set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d3.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);*/
