package com.example.bhashi.datatttt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Bhashi on 1/27/2017.
 */
public class BluetoothClient implements Runnable {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;

    byte[] readBuffer;
    int readBufferPosition;
    private static final byte[] TERM_SEND = {0x0d};
    private Thread t;
    TextView labelRPM;
    TextView labelSpeed;
    TextView protocolView;

    public BluetoothClient(BluetoothDevice mmDevice, TextView labelRPM, TextView labelSpeed, TextView protocolView) {
        // Tries to open a connection to the bluetooth device
//        ParcelUuid[] uuids = mmDevice.getUuids();
        //BluetoothSocket socket = mmDevice.createRfcommSocketToServiceRecord(uuids[0].getUuid());
        // UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        this.mmDevice = mmDevice;
        this.labelRPM = labelRPM;
        this.labelSpeed = labelSpeed;
        this.protocolView = protocolView;
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            //mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuids[0].getUuid());
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //myLabel.setText("Bluetooth Opened");
    }

    public void run() {

        try {
            final String protocol = new String(SendReceive("ATSP0"));
            String displayProtocol = new String(SendReceive("ATDP"));
            protocolView.post(new Runnable() {
                public void run() {
                    protocolView.setText(protocol);
                }
            });

            SendReceive("ATE0");  //echo off
            SendReceive("ATS0");
            while (true) {
                String RPM = new String(SendReceive("010C"));
                final int rpmConverted = Integer.valueOf(RPM.substring(4), 16) / 4;
                String speed = new String(SendReceive("010D"));
                long time = System.currentTimeMillis();
                Log.d("MainActivity", "Current Timestamp: " + time);
                final int speedConverted = Integer.valueOf(speed.substring(4), 16);
                //SendToCEP thread2 = new SendToCEP(speedConverted,time);
                //thread2.start();
                labelRPM.post(new Runnable() {
                    public void run() {
                        labelRPM.setText(rpmConverted + "");
                    }
                });
                labelSpeed.post(new Runnable() {
                    public void run() {
                        labelSpeed.setText(speedConverted + "");
                    }
                });
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        t = new Thread(this, "thread 1");
        t.start();
    }


    public byte[] SendReceive(String command) throws IOException {

        byte[] buffer = new byte[1024];
        int char1 = -1;

        int i = 0;
        int length = mmInputStream.available();

        for (int j = 0; j < length; j++) {
            char1 = mmInputStream.read();
        }

        length = mmInputStream.available();

        write(command, TERM_SEND);

        length = mmInputStream.available();

        while ((char1 = mmInputStream.read()) != '>') {
            buffer[i] = (byte) char1;
            i++;
        }

        String tmp = new String(buffer, 0, i);

        String response = tmp.trim();

        return response.getBytes();


    }

    public void write(byte[] buffer) {
        try {
            mmOutputStream.write(buffer);
        } catch (IOException e) {
            Log.e("write method", "Exception during write", e);
        }
    }

    public void write(String str, byte[] term) {
        byte[] one = str.getBytes();
        byte[] two = term;
        byte[] combined = new byte[one.length + two.length];
        System.arraycopy(one, 0, combined, 0, one.length);
        System.arraycopy(two, 0, combined, one.length, two.length);
        write(combined);
    }

}
