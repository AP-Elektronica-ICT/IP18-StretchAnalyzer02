package be.eaict.stretchalyzer2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
    private final String DEVICE_NAME="STUG_IV";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    Context currCtx;

    public static BluetoothManager getInstance(Context ctx) {
        if(btm == null) {
            btm = new BluetoothManager(ctx);
        }
        btm.currCtx = ctx;
        return btm;
    }

    public BluetoothManager(Context ctx) {
        currCtx = ctx;
        if(BTinit())
        {
            if(BTconnect())
            {
                // setUiEnabled(true);
                deviceConnected=true;
                beginListenForData();
                //txtBluetooth.append("\nConnection Opened!\n");
            }
        }
    }

    public boolean BTinit() {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(currCtx,"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
            Log.d("btm", "Device doesnt Support Bluetooth");
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Toast.makeText(currCtx,"Please Pair the Device first",Toast.LENGTH_SHORT).show();
            Log.d("btm", "Please Pair the Device first 1");
            /*
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
             }
             */
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
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
            }
        }
        Log.d("btm", "Found: "+ Boolean.toString(found));
        return found;

    }

    public boolean BTconnect() {
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
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    void beginListenForData() {
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
        outputStream.close();
        inputStream.close();
        socket.close();
        //setUiEnabled(false);
        deviceConnected = false;
        //txtBluetooth.append( "\nConnection Closed!\n" );
    }


    /*public void transmit(String input) {
        byte[] operation = input.getBytes();
        try{
            outputStream.write(operation);
        } catch(IOException e){
            Toast.makeText(currCtx,"Transmission failed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }*/
}
