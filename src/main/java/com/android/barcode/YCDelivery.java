package com.android.barcode;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YCDelivery extends AppCompatActivityBase {
    EditText ckdh;
    Button cx;
    RecyclerView recyclerView;
    List<YC_Ckd> yc_ckds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ycdelivery);
        ckdh = (EditText)findViewById(R.id.yc_delivery_ckdh);
        cx = (Button) findViewById(R.id.yc_delivery_cx);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_yc_delivery);
        yc_ckds = new ArrayList<>();
        intBtn_cx();

    }

    public void onResume(){
        super.receiver = receiver;
        super.onResume();
    }

    public void intBtn_cx(){
        cx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ckdh.getText().toString().equals("")){
                    String url = "http://www.vapp.meide-casting.com/app/ycckcx";
                    intBtn_cx1(url);
                }else {
                    String url = "http://www.vapp.meide-casting.com/app/ycckcxbydh";
                    intBtn_cx1(url);
                }
            }
        });
    }
    public void intBtn_cx1(String  url){
        yc_ckds.clear();
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("ckdh",ckdh.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(YCDelivery.this).show();
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(YCDelivery.this).dismiss();
                final  String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(YCDelivery.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(YCDelivery.this).dismiss();
                final String box_res1 = response.body().string();
                //Log.d("查询数据",box_res1);
                try {
                    JSONObject box_res_json = new JSONObject(box_res1);
                    String code = box_res_json.getString("flag").toString();
                    final String msg = box_res_json.getString("msg").toString();
                    if (!code.equals("1")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YCDelivery.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    JSONArray box_Jsons = new JSONArray(box_res_json.get("data").toString());
                    int productsl = 0;
                    for (int i = 0 ;i < box_Jsons.length();i++){
                        YC_Ckd yc_ckd = new YC_Ckd(box_Jsons.getJSONObject(i).get("id").toString(),box_Jsons.getJSONObject(i).get("code").toString(),box_Jsons.getJSONObject(i).get("customer").toString(),
                                box_Jsons.getJSONObject(i).get("trans_entity").toString(),box_Jsons.getJSONObject(i).get("car_number").toString(),box_Jsons.getJSONObject(i).get("order_code").toString(),
                                box_Jsons.getJSONObject(i).get("delivery_code").toString());
                        yc_ckds.add(yc_ckd);
                        //yc_cps.add(yc_cp);
                                /*Log.d("查询返回数据","66666666");
                                Log.d("wl",box_Jsons.getJSONObject(i).get("wl").toString());
                                Log.d("jjc",box_Jsons.getJSONObject(i).get("jjc").toString());*/
                    }
                    final int boxs = box_Jsons.length();
                    //Log.d("boxs",boxs+"");
                    final int jss = productsl;
                    //Log.d("jss",jss+"");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(YCDelivery.this);
                            recyclerView.setLayoutManager(layoutManager);
                            YCDeliveryAdapter adapter  =new YCDeliveryAdapter(yc_ckds, YCDelivery.this, YCDelivery.this);
                            recyclerView.setAdapter(adapter);
                            recyclerView.addItemDecoration(new SpacesItemDecoration());
                        }
                    });
                }catch (JSONException e){
                    Log.e("解析查询数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(YCDelivery.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });

    }


}
