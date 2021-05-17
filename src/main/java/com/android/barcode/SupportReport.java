package com.android.barcode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.android.barcode.Post.post_com;

public class SupportReport extends Activity {
    EditText supporteditview;
    Button btn_support_confirm;
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                if(data.indexOf("TRSUT")<0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SupportReport.this,"扫描二维码不是托码",Toast.LENGTH_LONG).show();
                        }
                    });

                    return;
                }
                ///设置显示框内容
                Log.d("扫描内容",data);
                supporteditview.setText(Getcode.getsupportcode(data));
                display(Getcode.getsupportcode(data));
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supportreport);
        Button btn_support_search = (Button)findViewById(R.id.button_support_search);
        btn_support_confirm = (Button)findViewById(R.id.button_support_confirm);
        btn_support_confirm.setEnabled(false);
        supporteditview = (EditText)findViewById(R.id.editview_support_report);
        supporteditview.setEnabled(false);
        btn_support_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(supporteditview.getText().toString().isEmpty()&&ListMap.getroleid().equals("2")){///仓库查询箱时必须有托号
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SupportReport.this,"请输入托号",Toast.LENGTH_LONG).show();
                        }
                    });

                    return;
                }
                display(supporteditview.getText().toString());
            }
        });


    }
    public void display(String supoort_code){
        final String supportcode = supoort_code;
        ///查询托信息
        try {
            JSONObject req_supportreport = new JSONObject();
            JSONObject supportInfo = new JSONObject();
            supportInfo.put("SUPPORT_CODE",supportcode);
            supportInfo.put("SelectState",1);
            if(ListMap.getroleid().equals("2")){
                supportInfo.put("CREATEUSER","");
            }else{
                supportInfo.put("CREATEUSER",ListMap.getuserid());
            }
            supportInfo.put("START_TIME","");
            supportInfo.put("END_TIME","");
            req_supportreport.put("supportInfo",supportInfo);
            Log.d("查询托数据传入",req_supportreport.toString());

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
            LoadingDialog.getInstance(SupportReport.this).show();
            final Request request = new Request.Builder()
                    .url("http://221.214.27.228/Service/MDWechatService.asmx/QuerySupportInfo")
                    ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QuerySupportInfo")
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("okhttp请求失败",e.getMessage());
                    LoadingDialog.getInstance(SupportReport.this).dismiss();
                    final String mes=e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SupportReport.this,"查询数据失败"+mes,Toast.LENGTH_LONG).show();
                        }
                    });

                    return;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String box_res = response.body().string();
                    Log.d("okhttp请求成功",box_res);
                    LoadingDialog.getInstance(SupportReport.this).dismiss();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                Log.d("查询托数据返回",box_res);
                                JSONObject box_res_json = null;
                                box_res_json = new JSONObject(box_res);
                                JSONObject d = new JSONObject(box_res_json.get("d").toString());
                                String code = d.getString("Code").toString();
                                if(!code.equals("200")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SupportReport.this,"查询数据失败",Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    return;
                                }
                                JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
                                List<Support_info> wl_detail_list = new ArrayList<>();
                                final int th=Integer.parseInt(box_Jsons.getJSONObject(0).get("SUPPORT_ID").toString());
                                for(int i=0;i<box_Jsons.length();i++){

                                    int tuoid = Integer.parseInt(box_Jsons.getJSONObject(i).get("SUPPORT_ID").toString());
                                    String tuocode = box_Jsons.getJSONObject(i).get("SUPPORT_CODE").toString();
                                    String tuozt = box_Jsons.getJSONObject(i).get("SUPPORT_STATE_DESC").toString();
                                    String tuosfzt = box_Jsons.getJSONObject(i).get("ALL_STATE_DESC").toString();
                                    String tuocjsj = box_Jsons.getJSONObject(i).get("CREATETIME").toString();
                                    ///2020-11-14
                                    String tuojs = box_Jsons.getJSONObject(i).get("ProductCount").toString();
                                    String tuoxs = box_Jsons.getJSONObject(i).get("BoxCount").toString();

                                    Support_info wl_detail = new Support_info(tuoid,tuocode,tuozt,tuosfzt,tuocjsj,tuojs,tuoxs);
                                    wl_detail_list.add(wl_detail);
                                }
                                ///recyclerView显示数据
                                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycle_supportreport);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(SupportReport.this);
                                recyclerView.setLayoutManager(layoutManager);
                                SupportReportAdapter adapter  =new SupportReportAdapter(wl_detail_list,SupportReport.this,SupportReport.this);
                                recyclerView.setAdapter(adapter);
                                recyclerView.addItemDecoration(new SpacesItemDecoration());
                                btn_support_confirm.setEnabled(true);
                                btn_support_confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        intBtn_cx(th);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                                final String mes1=e.getMessage();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SupportReport.this,"查询数据失败"+mes1,Toast.LENGTH_LONG).show();
                                    }
                                });

                                return;
                            }
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            final String mes2=e.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SupportReport.this,"查询数据失败"+mes2,Toast.LENGTH_LONG).show();
                }
            });

            return;
        }
    }
    public void intBtn_cx(final int th){

                //Log.d("pz",pz);
                // Log.d("gg",gg);
                Log.d("th",th+"");
                JSONObject req_supportreport = new JSONObject();
                try {
                    req_supportreport.put("th",th);
                    req_supportreport.put("user",ListMap.getuserid());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
                LoadingDialog.getInstance(SupportReport.this).show();
                final Request request = new Request.Builder().url("http://www.vapp.meide-casting.com/app/fmzprk").post(requestBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LoadingDialog.getInstance(SupportReport.this).dismiss();
                        final  String message = e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SupportReport.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        LoadingDialog.getInstance(SupportReport.this).dismiss();
                        final String box_res1 = response.body().string();
                        try {
                            JSONObject box_res_json = new JSONObject(box_res1);
                            String code = box_res_json.getString("flag").toString();
                            final String msg = box_res_json.getString("msg").toString();
                            if (!code.equals("1")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SupportReport.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SupportReport.this,"成功"+msg,Toast.LENGTH_LONG).show();
                                    SupportReport.this.finish();
                                }
                            });



                        }catch (JSONException e){
                            Log.e("解析查询数据失败",e.getMessage());
                            final String msg = e.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SupportReport.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                    }
                });



    }

    @Override
    protected void onDestroy() {
        ///unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        IntentFilter iFilter = new IntentFilter();
        //注册系统广播  接受扫描到的数据
        iFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver, iFilter);
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
}
