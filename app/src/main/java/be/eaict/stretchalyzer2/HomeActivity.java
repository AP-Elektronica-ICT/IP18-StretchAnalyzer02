package be.eaict.stretchalyzer2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Set;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    String data;
    private int mInterval = 100;
    private Handler mHandler;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ctx = this.getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
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
