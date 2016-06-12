package com.example.lenovo.smscodedemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity {
    private final int SDK_PERMISSION_REQUEST = 127;
    EventHandler eh;
    EditText text;
    EditText code;
    Button button;
    Button button2;
    String smsCode="";
    IntentFilter intentFilter;
    IntentFilter sendFilter;
    MessageReceiver receiver;
    SendReceiver sendReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SMSSDK.initSDK(this, "13bc13db2c5c0", "46b55d1ebd92915d55f18aab0fa445b3");
        eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        Log.e("sms","提交验证码成功");

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        Log.e("sms","获取验证码成功 ");


                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                        Log.e("sms","获取国家列表成功 "+data.toString());
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh);
        checkPermission();
        init();
    }

    private void init(){
        text=(EditText)findViewById(R.id.editText);
        code=(EditText)findViewById(R.id.editText2);
        button=(Button)findViewById(R.id.button);
        button2=(Button)findViewById(R.id.button2);
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        receiver=new MessageReceiver();
        registerReceiver(receiver, intentFilter);
        sendFilter=new IntentFilter();
        sendFilter.addAction("SENT_SMS_ACTION");
        sendReceiver=new SendReceiver();
        registerReceiver(sendReceiver, sendFilter);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // SMSSDK.getSupportedCountries();
                Log.e("sms","start");
                SMSSDK.getVerificationCode("86",text.getText().toString());

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SMSSDK.submitVerificationCode("86",text.getText().toString(), code.getText().toString());
            }
        });
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
        unregisterReceiver(receiver);
        unregisterReceiver(sendReceiver);
    }

    private void sendMsg(){
        SmsManager manager=SmsManager.getDefault();
        Intent intent=new Intent("SENT_SMS_ACTION");
        PendingIntent pi=PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        manager.sendTextMessage("10086", null, "101", pi, null);
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS }, SDK_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SDK_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();

                } else {

                }
                return;
            }

        }

    }


    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("sms","receive");
            Bundle bundle=intent.getExtras();
            Object[] pdus=(Object[]) bundle.get("pdus");
            SmsMessage[] messages=new SmsMessage[pdus.length];
            for(int i=0;i< messages.length; i++){
                messages[i]=SmsMessage.createFromPdu((byte[] )pdus[i]);
            }
            for(SmsMessage message:messages){

                smsCode+=message.getMessageBody();
            }
            Log.e("from ", messages[0].getOriginatingAddress());
            code.setText(smsCode);
            abortBroadcast();
        }
    }

    class SendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(getResultCode()==RESULT_OK){
                Log.e("sms", "send");

            }
            else{
                Log.e("sms", "failed");
            }

        }
    }
}
