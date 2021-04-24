package com.android.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class YCWarehousing extends AppCompatActivityBase {
    EditText cp;
    Button cx;
    RecyclerView recyclerView;
    List<YC_CP> yc_cps;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(YCWarehousing.super.RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                ///设置显示框内容
                Log.d("扫描箱码内容",data);
                if(data.indexOf(",")>0&&data.indexOf("cp")>=0){
                    String cp1 = data.substring(data.indexOf("YC,")+3);
                    cp.setText(cp1);
                }else{
                    Toast.makeText(YCWarehousing.this,"请扫描产品",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ycwarehousing);
        cp = (EditText)findViewById(R.id.edit_yc_warehousing_cp);
        cx = (Button) findViewById(R.id.btn_yc_warehousing_cx);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_yc_warehousing);
        yc_cps = new ArrayList<>();
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
                yc_cps.clear();
                JSONObject req_supportreport = new JSONObject();
                try {
                    req_supportreport.put("cp",cp.getText().toString());
                    Log.d("cp",cp.getText().toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
                LoadingDialog.getInstance(YCWarehousing.this).show();
                final Request request = new Request.Builder().url("http://www.vapp.meide-casting.com/app/wl").post(requestBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LoadingDialog.getInstance(YCWarehousing.this).dismiss();
                        final  String message = e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YCWarehousing.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        LoadingDialog.getInstance(YCWarehousing.this).dismiss();
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
                                        Toast.makeText(YCWarehousing.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            JSONArray box_Jsons = new JSONArray(box_res_json.get("data").toString());
                            int productsl = 0;
                            for (int i = 0 ;i < box_Jsons.length();i++){
                                YC_CP yc_cp = new YC_CP(box_Jsons.getJSONObject(i).get("wl").toString(),box_Jsons.getJSONObject(i).get("lch").toString(),
                                        box_Jsons.getJSONObject(i).get("jjc").toString(),box_Jsons.getJSONObject(i).get("hwh").toString(),box_Jsons.getJSONObject(i).get("pz").toString(),
                                        box_Jsons.getJSONObject(i).get("gg").toString(), box_Jsons.getJSONObject(i).get("xs").toString(),box_Jsons.getJSONObject(i).get("cpbm").toString(),
                                        box_Jsons.getJSONObject(i).get("kbh").toString(),box_Jsons.getJSONObject(i).get("gdh").toString(), box_Jsons.getJSONObject(i).get("gys").toString(),
                                        box_Jsons.getJSONObject(i).get("czfl").toString(),box_Jsons.getJSONObject(i).get("bmcl").toString());
                                yc_cps.add(yc_cp);
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
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(YCWarehousing.this);
                                    recyclerView.setLayoutManager(layoutManager);
                                    YCWarehousingAdapter adapter  =new YCWarehousingAdapter(yc_cps, YCWarehousing.this, YCWarehousing.this);
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
                                    Toast.makeText(YCWarehousing.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                    }
                });

            }
        });
    }


}
