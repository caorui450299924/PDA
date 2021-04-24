package com.android.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BKDelivery extends AppCompatActivityBase {
    TextView tv_bkck_cp;
    TextView tv_bkck_hwh;
    EditText edt_sl;
    Button btn_bkck_ck;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BKDelivery.super.RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                ///设置显示框内容
                Log.d("扫描箱码内容",data);
                if(data.indexOf("_")>0){//物料
                    tv_bkck_cp.setText(data);
                }else if(data.indexOf(",")>0&&data.indexOf("hwh")>=0){
                    String hwh = data.substring(data.indexOf("CP,")+3);
                    tv_bkck_hwh.setText(hwh);
                    edt_sl.setEnabled(true);
                    edt_sl.setText("0");
                }else{
                    Toast.makeText(BKDelivery.this,"请扫描产品或货位号",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bkdelivery);
        tv_bkck_cp= (TextView)findViewById(R.id.tv_bkck_cp);
        tv_bkck_hwh=(TextView)findViewById(R.id.tv_bkck_hwh);
        edt_sl=(EditText)findViewById(R.id.edt_sl);
        btn_bkck_ck=(Button)findViewById(R.id.btn_bkck_ck);
        intBtn();
    }

    @Override
    protected void onResume() {
        super.receiver=receiver;
        super.onResume();
    }
    public void intBtn(){
        edt_sl.setEnabled(false);
        btn_bkck_ck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject req_supportreport = new JSONObject();
                try {
                    req_supportreport.put("wl",tv_bkck_cp.getText());
                    req_supportreport.put("hwh",tv_bkck_hwh.getText());
                    req_supportreport.put("num",edt_sl.getText());
                    req_supportreport.put("username",ListMap.getuserid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
                LoadingDialog.getInstance(BKDelivery.this).show();
                final Request request = new Request.Builder()
                        .url("http://www.vapp.meide-casting.com/app/ck")
                        ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LoadingDialog.getInstance(BKDelivery.this).dismiss();
                        final String message = e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BKDelivery.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        LoadingDialog.getInstance(BKDelivery.this).dismiss();
                        final String box_res = response.body().string();
                        Log.d("查询半库入库数据返回",box_res);
                        try{
                            JSONObject box_res_json = new JSONObject(box_res);
                            String code = box_res_json.getString("flag").toString();
                            if(!code.equals("1")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(BKDelivery.this,"执行出库失败", Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BKDelivery.this,"执行成功", Toast.LENGTH_LONG).show();
                                }
                            });


                        }catch (Exception e){
                            Log.e("解析查询箱数据失败",e.getMessage());
                            Toast.makeText(BKDelivery.this,"查询数据失败："+e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
            }
        });
    }

}
