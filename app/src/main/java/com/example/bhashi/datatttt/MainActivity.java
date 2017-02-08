package com.example.bhashi.datatttt;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {

    ListView listViewPaired;
    ArrayList<String> arrayListpaired;
    ArrayAdapter<String> adapter, detectedAdapter;
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    ListItemClickedonPaired listItemClickedonPaired;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;

    TextView myLabel;
    TextView labelSpeed;
    TextView labelRPM;
    TextView protocolView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewPaired = (ListView) findViewById(R.id.listViewPaired);
        arrayListpaired = new ArrayList<String>();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        listItemClickedonPaired = new ListItemClickedonPaired();
        listViewPaired.setAdapter(adapter);
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayListpaired);
        listViewPaired.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listViewPaired.invalidateViews();
        listViewPaired.setOnItemClickListener(listItemClickedonPaired);

        try {

            Button openButton = (Button) findViewById(R.id.open);
            myLabel = (TextView) findViewById(R.id.textView);
            labelRPM = (TextView) findViewById(R.id.RPMtextField);
            labelSpeed = (TextView) findViewById(R.id.textField);
            protocolView = (TextView) findViewById(R.id.protocol);


            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // try {
                    findBT();
                    //  openBT();
                    //    } catch (IOException ex) {
                    //   }

                }
            });


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This will find a bluetooth device
    void findBT() {

        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    arrayListpaired.add(device.getName() + "\n" + device.getAddress());
                    arrayListPairedBluetoothDevices.add(device);
                    adapter.notifyDataSetChanged();
                    listViewPaired.invalidateViews();
                }
            }
            myLabel.setText("Device Found");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ListItemClickedonPaired implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mmDevice = arrayListPairedBluetoothDevices.get(position);

            BluetoothClient thread1 = new BluetoothClient(mmDevice, labelRPM, labelSpeed, protocolView);
            thread1.start();

        }
    }


}

