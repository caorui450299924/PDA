package com.android.barcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class YCDelivery_Mteriale extends AppCompatActivityBase {
    TextView ckdh;
    Button ck;
    Button ck_0;
    RecyclerView recyclerView;
    List<YCCK_Ckdmx> yc_ckds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ycdelivery_material);
        ckdh = (TextView) findViewById(R.id.yc_delivery_material_code);
        ck = (Button) findViewById(R.id.yc_delivery_material_ck);
        ck_0 = (Button) findViewById(R.id.yc_delivery_material_0ck);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_yc_delivery_material);
        yc_ckds = new ArrayList<>();
        Intent intent = getIntent();
        String ckdh1 = intent.getStringExtra("ckdh");
        String id = intent.getStringExtra("id");
        ckdh.setText(ckdh1);
        intBtn_cx(id);
        intBtn_ck(id);

    }

    public void onResume(){
        super.receiver = receiver;
        super.onResume();
    }

    public void intBtn_ck(final String id){
        ck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject req_supportreport = new JSONObject();
                try {
                    req_supportreport.put("id",id);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
                LoadingDialog.getInstance(YCDelivery_Mteriale.this).show();
                final Request request = new Request.Builder().url("http://www.vapp.meide-casting.com/app/ycck").post(requestBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LoadingDialog.getInstance(YCDelivery_Mteriale.this).dismiss();
                        final  String message = e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        LoadingDialog.getInstance(YCDelivery_Mteriale.this).dismiss();
                        final String box_res1 = response.body().string();
                        try {
                            JSONObject box_res_json = new JSONObject(box_res1);
                            String code = box_res_json.getString("flag").toString();
                            final String msg = box_res_json.getString("msg").toString();
                            if (!code.equals("1")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(YCDelivery_Mteriale.this,"成功"+msg,Toast.LENGTH_LONG).show();
                                    YCDelivery_Mteriale.this.finish();
                                }
                            });



                        }catch (JSONException e){
                            Log.e("解析查询数据失败",e.getMessage());
                            final String msg = e.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                    }
                });

            }
        });

        ck_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject req_supportreport = new JSONObject();
                try {
                    req_supportreport.put("id",id);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
                LoadingDialog.getInstance(YCDelivery_Mteriale.this).show();
                final Request request = new Request.Builder().url("http://www.vapp.meide-casting.com/app/yclck").post(requestBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LoadingDialog.getInstance(YCDelivery_Mteriale.this).dismiss();
                        final  String message = e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                                intBtn_cx(id);
                            }
                        });
                        return;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        LoadingDialog.getInstance(YCDelivery_Mteriale.this).dismiss();
                        final String box_res1 = response.body().string();
                        try {
                            JSONObject box_res_json = new JSONObject(box_res1);
                            String code = box_res_json.getString("flag").toString();
                            final String msg = box_res_json.getString("msg").toString();
                            if (!code.equals("1")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                                        intBtn_cx(id);
                                    }
                                });
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(YCDelivery_Mteriale.this,"更新成功"+msg,Toast.LENGTH_LONG).show();
                                    intBtn_cx(id);
                                }
                            });



                        }catch (JSONException e){
                            Log.e("解析查询数据失败",e.getMessage());
                            final String msg = e.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                                    intBtn_cx(id);
                                }
                            });



                            return;
                        }
                    }
                });

            }
        });
    }
    public void intBtn_cx(String id){
        yc_ckds.clear();
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("id",id);
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(YCDelivery_Mteriale.this).show();
        final Request request = new Request.Builder().url("http://www.vapp.meide-casting.com/app/ycckmxcx").post(requestBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(YCDelivery_Mteriale.this).dismiss();
                final  String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(YCDelivery_Mteriale.this).dismiss();
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
                                Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    JSONArray box_Jsons = new JSONArray(box_res_json.get("data").toString());
                    int productsl = 0;
                    for (int i = 0 ;i < box_Jsons.length();i++){
                        YCCK_Ckdmx yc_ckdmx = new YCCK_Ckdmx(box_Jsons.getJSONObject(i).get("material").toString(),box_Jsons.getJSONObject(i).get("num").toString(),box_Jsons.getJSONObject(i).get("hwh").toString(),
                                box_Jsons.getJSONObject(i).get("actualQuantity").toString(),box_Jsons.getJSONObject(i).get("unloadedQty").toString());
                        yc_ckds.add(yc_ckdmx);
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
                            LinearLayoutManager layoutManager = new LinearLayoutManager(YCDelivery_Mteriale.this);
                            recyclerView.setLayoutManager(layoutManager);
                            YCDelivery_MaterialAdapter adapter  =new YCDelivery_MaterialAdapter(yc_ckds, YCDelivery_Mteriale.this, YCDelivery_Mteriale.this);
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
                            Toast.makeText(YCDelivery_Mteriale.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });

    }


}
