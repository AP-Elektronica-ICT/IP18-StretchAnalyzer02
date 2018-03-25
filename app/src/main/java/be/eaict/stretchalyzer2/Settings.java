package be.eaict.stretchalyzer2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by cedric on 25/03/2018.
 */

public class Settings extends AppCompatActivity {

    private int hour = 10;
    private int minute = 0;
    private EditText hourtTemp;
    private EditText minuteTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hourtTemp = findViewById(R.id.txtHours);
        minuteTemp = findViewById(R.id.txtMinutes);

    }

    //setNotification button click
    public void setNotificationTime(View view) {
        //waarde alarm instellen
        hour = Integer.parseInt(hourtTemp.getText().toString());
        minute = Integer.parseInt(minuteTemp.getText().toString());

        //calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,1);

        //intents (pendingintent nodig)
        Intent intent = new Intent(getApplicationContext(),NotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //alarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //rtc_wakeup --> zorgt ervoor dat de notification ook in sleep mode verschijnt.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);


        //toast message
        String tekst = "De Notification is ingesteld op " + hourtTemp.getText().toString() +":"+ minuteTemp.getText().toString();
        Toast.makeText(Settings.this,tekst, Toast.LENGTH_LONG).show();
    }

      /*  Spinner inputHours = findViewById(R.id.spinnerHours);
        Spinner inputMinutes = findViewById(R.id.spinnerMinutes);
        String[] hours = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
        String[] minutes = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"};
        ArrayAdapter<String> adapterHours = new ArrayAdapter<>(this,R.layout.activity_settings, hours);
        ArrayAdapter<String> adapterMinutes = new ArrayAdapter<>(this,R.layout.activity_settings, hours);
        inputHours.setAdapter(adapterHours);
        inputMinutes.setAdapter(adapterMinutes);*/
}
