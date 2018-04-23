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
import android.widget.ToggleButton;

import java.util.Calendar;

import be.eaict.stretchalyzer2.DOM.GlobalData;

/**
 * Created by cedric on 25/03/2018.
 */

public class Settings extends AppCompatActivity {

    private int hour = 10;
    private int minute = 0;
    private EditText hourtTemp;
    private EditText minuteTemp;
    private ToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hourtTemp = findViewById(R.id.txtHours);
        minuteTemp = findViewById(R.id.txtMinutes);

        toggle = findViewById( R.id.toggleButtonHardware );
        toggle.setChecked( GlobalData.Sensor );
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


    public void onToggleClicked(View view) {
        if (toggle.isChecked()) {
            GlobalData.Sensor = true;
            Toast.makeText(Settings.this,"Gebruik van sensordata "+String.valueOf(GlobalData.Sensor), Toast.LENGTH_LONG).show();
        } else {
            GlobalData.Sensor = false;
            Toast.makeText(Settings.this,"Gebruik van sensordata "+ String.valueOf(GlobalData.Sensor), Toast.LENGTH_LONG).show();
        }
    }
}
