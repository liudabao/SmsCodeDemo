package com.example.lenovo.smscodedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity {
    EventHandler eh;

    EditText text;
    EditText code;
    Button button;
    Button button2;

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
                        Log.e("sms","ok1");

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        Log.e("sms","ok2");
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                        Log.e("sms","ok3 "+data.toString());
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh);
        init();
    }

    private void init(){
        text=(EditText)findViewById(R.id.editText);
        code=(EditText)findViewById(R.id.editText2);
        button=(Button)findViewById(R.id.button);
        button2=(Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // SMSSDK.getSupportedCountries();
               SMSSDK.getVerificationCode("86",text.getText().toString());
            }
        });
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

}
