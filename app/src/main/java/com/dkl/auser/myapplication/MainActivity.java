package com.dkl.auser.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Button SearchBtn;
    private Button DiscoverBtn;
    private Button EditBtn;
    private ListView listView;
    private BluetoothAdapter btAdapter;
    private final static int REQUEST_ENABLE_BT=1;
    private Set<BluetoothDevice> device;
    private ArrayList<String> btDeviceList;
    private String TAG="BT_T";
    private boolean receiverFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"Search click");
        context = this;
        SearchBtn = (Button)findViewById(R.id.Search_btn);
        SearchBtn.setOnClickListener(new BtnOnClickListener());

        DiscoverBtn = (Button)findViewById(R.id.Discover_btn);
        DiscoverBtn .setOnClickListener(new BtnOnClickListener());

        EditBtn =  (Button)findViewById(R.id.Edit_btn);
        EditBtn.setOnClickListener(new BtnOnClickListener());


        btDeviceList = new ArrayList<String>();
        listView = (ListView)findViewById(R.id.BT_ListView);
        listView.setAdapter(null);
        listView.setOnItemClickListener(new MyItemClick());



        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(this,"no Bluetooth",Toast.LENGTH_SHORT).show();
            finish();
        }else if(!btAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQUEST_ENABLE_BT);


        }else{
            device = btAdapter.getBondedDevices();

            Log.d(TAG,"getBondedDevices1"+ device.size());

            if(device.size()>0){
                for(BluetoothDevice device_data :device){
                    btDeviceList.add("paired : "+ device_data.getName() + "\n" + device_data.getAddress());

                }
                listView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,btDeviceList ));
            }
            Log.d(TAG,"Search click");
        }



    }

    protected void onActivityResult(int code, int resultcode, Intent date) {
        if(resultcode == REQUEST_ENABLE_BT){
            if(resultcode == RESULT_CANCELED){
                Toast.makeText(this,"Enabling BT failed", Toast.LENGTH_SHORT).show();
                finish();
            }else if(resultcode == RESULT_OK){
                device = btAdapter.getBondedDevices();

                Log.d(TAG,"getBondedDevices11");

                if(device.size()>0){
                    for(BluetoothDevice device_data :device){
                        btDeviceList.add("paired : "+ device_data.getName() + "\n" + device_data.getAddress());

                        Log.d(TAG,device_data.getName() + "\n" + device_data.getAddress());

                    }
                    listView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,btDeviceList ));

                    Log.d(TAG,"getbondeded_device2");
                }

            }

        }

super.onActivityResult(code , resultcode ,  date);
    }


    private class BtnOnClickListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            switch(view.getId()){


                case R.id.Search_btn:
                    Log.d(TAG,"Search click");
                    btAdapter.startDiscovery();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver,filter);
                    receiverFlag = true;
                    Toast.makeText(context,"Begin to scan",Toast.LENGTH_SHORT).show();
                    break;

                case R.id.Discover_btn:
                    Log.d(TAG,"Discover click");
                    Intent disintent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    disintent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,150);
                    startActivity(disintent);

                    break;
                case R.id.Edit_btn:

                    Log.d(TAG,"Edit click");

                    Intent newintent = new Intent(context,edit_activity.class);
                    startActivity(newintent);

                    break;
            }


        }
    }

   private BroadcastReceiver receiver = new BroadcastReceiver()

    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDeviceList.add("Found: "+device.getName()+"\n"+device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,btDeviceList));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btAdapter.cancelDiscovery();
        if(receiverFlag){
            unregisterReceiver(receiver);

        }


    }

    private class MyItemClick implements android.widget.AdapterView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            btAdapter.cancelDiscovery();
            String remoteDeviceName = parent.getItemAtPosition(position).toString();
            Intent newIntent = new Intent(context,edit_activity.class);
            newIntent.putExtra("remoteDevice",remoteDeviceName);
            startActivity(newIntent);
        }
    }
}
