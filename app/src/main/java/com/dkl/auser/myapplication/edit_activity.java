package com.dkl.auser.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class edit_activity extends AppCompatActivity {

    private TextView dataTextView;
    private Button cleanBtn;
    private Button sendBtn;
    private EditText sendEdit;
    private static final String TAG = "BT_Edit";
    private Context context;
    private String remoteDeviceInfo;
    private BluetoothAdapter btAdapter;
    private String remoteMacAddress;
    private String deviceMsg;
    private BluetoothChatService mChatService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        

        dataTextView = (TextView)findViewById(R.id.data_textView);
        dataTextView.setText("");

        cleanBtn = (Button)findViewById(R.id.clean_btn);
        cleanBtn.setOnClickListener(new btnOnClickListener());


        sendBtn = (Button)findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(new btnOnClickListener());


        sendEdit = (EditText)findViewById(R.id.send_editText);
        sendEdit.setText("");
        sendEdit.setOnEditorActionListener(textEditListener);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent = getIntent();
        remoteDeviceInfo = intent.getStringExtra("remoteDevice");
        mChatService = new BluetoothChatService(context,mHandler);




        if(remoteDeviceInfo !=null){
            String device = remoteDeviceInfo.substring(10);
            Log.d(TAG,deviceMsg);
            dataTextView.append("Connecting to remote BT device:\n" +deviceMsg+"\n\n");
            remoteMacAddress = remoteDeviceInfo.substring(remoteDeviceInfo.length()-17);
            Log.d(TAG,remoteMacAddress);
            device = btAdapter.getRemoteDevice(remoteMacAddress);
            mChatService.connect(device,true);
        }

        context = this;
    }

    private TextView.OnEditorActionListener textEditListener = new TextView.OnEditorActionListener(){


        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                String message = dataTextView.getText().toString();
                dataTextView.append(">>"+message+"\n");
                sendMessageToBT(message);
                
            }
            
            return false;
        }

        
    };
    
    
    private class btnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.clean_btn:
                    dataTextView.setText("");
                    Toast.makeText(context,"Clean display",Toast.LENGTH_SHORT).show();

                    break;
                case  R.id.send_btn:
                    dataTextView.append(">>"+sendEdit.getText()+"\n");
                    String message = sendEdit.getText().toString();
                    sendMessageToBT(message);
                    break;
            }
        }

        private void sendMessageToBT(String message) {
        }
    }
}
