package com.android.barcode;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;

public class AppCompatActivityBase extends AppCompatActivity {
    public String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    public IntentFilter intentFilter;
    public BroadcastReceiver receiver ;
    @Override
    protected void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
