package com.android.barcode;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import okhttp3.OkHttpClient;

import static com.android.barcode.OkHttpUnit.httppost;

public class TestFunction extends Activity {
    public Button btn1 ;
    public TextView textView5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testfunction);
        btn1 = (Button)findViewById(R.id.button_testfunction);
        textView5 = (TextView)findViewById(R.id.textView5);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject resbody = new JSONObject();
                try {
                    resbody.put("loginName", "huangguangchao");
                    resbody.put("loginPassword", "imsims");
                }catch (Exception e){
                    e.printStackTrace();
                }
                httppost(resbody,TestFunction.this);
        }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
