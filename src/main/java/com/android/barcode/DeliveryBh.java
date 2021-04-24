package com.android.barcode;

import android.app.Activity;
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

import static com.android.barcode.Post.post_com_cookie;

public class DeliveryBh extends Activity {
    Button btn_search ;
    List<Bh_info> bh_list = new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverybh);
        btn_search = (Button)findViewById(R.id.button_delivery_bh);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        display();
    }

    public void display(){
        try {
            JSONObject req_bh = new JSONObject();
            JSONObject paramobj = new JSONObject();
            paramobj.put("ATTRIBUTE1","1");
            req_bh.put("namingid","com.dwb.reporter.queryfmfc.get_delievry_bh");
            req_bh.put("paramobj",paramobj);
            Log.d("查询备货数据开始","开始查询");

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_bh.toString());
            LoadingDialog.getInstance(DeliveryBh.this).show();
            final Request request = new Request.Builder()
                    .url("http://www.vapp.meide-casting.com/ims/com.dwb.reporter.queryfmfc.querybh.biz.ext")
                    .post(requestBody)
                    .addHeader("cookie","JSESSIONID="+Login.sessionId)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("okhttp请求失败",e.getMessage());
                    LoadingDialog.getInstance(DeliveryBh.this).dismiss();
                    Toast.makeText(DeliveryBh.this,"查询备货数据失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res_bh = response.body().string();
                    LoadingDialog.getInstance(DeliveryBh.this).dismiss();
                    ///2020-11-09
                    bh_list.clear();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                Log.d("查询备货数据返回",res_bh);
                                JSONObject res_json = new JSONObject(res_bh);
                                if(!res_json.isNull("exception")){
                                    ///用户失效，跳转到登录
                                    Toast.makeText(DeliveryBh.this,"登录失效，请重新登录",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(DeliveryBh.this,Login.class);
                                    startActivity(intent);
                                    return;
                                }

                                JSONArray objs = res_json.getJSONArray("objs");
                                for(int i=0;i<objs.length();i++){
                                    Bh_info bh_info = new Bh_info(objs.getJSONObject(i).getString("VEHICLE_BRAND"),objs.getJSONObject(i).getString("CUSTOMER_SHORT_NAME"),objs.getJSONObject(i).getString("INVENTORY_DESC"),objs.getJSONObject(i).getString("PLAN_DELIVERY_DATE"),objs.getJSONObject(i).getString("IDS"));
                                    bh_list.add(bh_info);
                                }
                                recyclerView = (RecyclerView)findViewById(R.id.recycle_delivery_bh);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(DeliveryBh.this);
                                recyclerView.setLayoutManager(layoutManager);
                                DeliveryBhAdapter adapter  =new DeliveryBhAdapter(bh_list,DeliveryBh.this,"DeliveryBh");
                                recyclerView.setAdapter(adapter);
                                recyclerView.addItemDecoration(new SpacesItemDecoration());
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(DeliveryBh.this,"查询备货数据失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(DeliveryBh.this,"查询备货数据失败"+e.getMessage(),Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

}
