package com.android.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Warehousing extends AppCompatActivityBase {
    private IntentFilter intentFilter;
    public BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.se4500.onDecodeComplete")) {
                String data = intent.getStringExtra("se4500");
                ///设置显示框内容
                Log.d("扫描箱码内容",data);
                Toast.makeText(Warehousing.this,"扫描数据"+data,Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warehousing);
    }

    @Override
    protected void onResume() {
        super.receiver= receiver;
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
