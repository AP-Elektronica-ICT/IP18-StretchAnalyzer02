package be.eaict.stretchalyzer2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Kevin-Laptop on 21/03/2018.
 */



public class BluetoothManager {

    private static BluetoothManager btm = null;
    private final String DEVICE_NAME="STUG_IV"; //Aan te passen aan de instellingen van de bluetooth van de arduino.
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream inputStream;
    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    Context currCtx;

    public static BluetoothManager getInstance(Context ctx) { //Nieuwe instantie aanmaken
        if(btm == null) {
            btm = new BluetoothManager(ctx);
        }
        btm.currCtx = ctx;
        return btm;
    }

    public BluetoothManager(Context ctx) { // Initaliseren en Connecteren
        currCtx = ctx;
        if(BTinit())
        {
            if(BTconnect())
            {
                deviceConnected=true;
                beginListenForData();
            }
        }
    }

    public boolean BTinit() {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();  // Adapter oproepen
        if (bluetoothAdapter == null) {
            Toast.makeText(currCtx,"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
            Log.d("btm", "Device doesnt Support Bluetooth");
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Toast.makeText(currCtx,"Please enable bluetooth",Toast.LENGTH_SHORT).show();
            Log.d("btm", "Please Pair the Device first 1");

        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();  //Lijst met gepairde devices oproepen
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(currCtx,"Please Pair the Device first",Toast.LENGTH_SHORT).show();
            Log.d("btm", "Please Pair the Device first 2");
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getName().equals(DEVICE_NAME))
                {
                    device=iterator;
                    found=true;
                    break;
                }
                else{
                    Toast.makeText(currCtx,"Please Pair the Device first",Toast.LENGTH_SHORT).show();
                    Log.d("btm", "Please Pair the Device first 3");
                }
            }
        }
        Log.d("btm", "Found: "+ Boolean.toString(found));
        return found;

    }

    public boolean BTconnect() {  //socket connectie aanmaken
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Log.d("btm", "Connected: "+ Boolean.toString(connected));
        Log.d("btm", device.getName());
        Log.d("btm", socket.toString());
        return connected;
    }

    void beginListenForData() {  //Nieuwe thread aanmaken met handler die luistert naar data
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            final byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");
                            handler.post(new Runnable() {
                                public void run()
                                {
                                    buffer = rawBytes;
                                }
                            });

                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    public void onClickStop(View view) throws IOException {
        stopThread = true;
        inputStream.close();
        socket.close();
        deviceConnected = false;
    }

}
