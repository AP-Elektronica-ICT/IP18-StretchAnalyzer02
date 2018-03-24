package be.eaict.stretchalyzer2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    private String data;
    private int mInterval = 100;
    private Handler mHandler;
    private Context ctx;
    private TextToSpeech tts;
    private Button btnStart;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ctx = this.getApplicationContext();
        btnStart = findViewById( R.id.btnStart );
        text = "Start exercising!";


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Locale locale = new Locale("en", "IN"  );
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        btnStart.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak( text );
            }
        } );

    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
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





    Runnable mDataChecker = new Runnable() {
        @Override
        public void run() {
            try{
                try {
                    data = new String(BluetoothManager.getInstance(ctx).buffer, StandardCharsets.UTF_8);
                } finally {
                    mHandler.postDelayed(mDataChecker, mInterval);
                }
            }catch (Exception ex){

            }
        }
    };

    void startRepeatingTask() {
        mDataChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mDataChecker);
    }

}
