package com.android.barcode;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HwhSearch extends AppCompatActivity {
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    EditText hwh_code ;
    RecyclerView product_hwh ;
    Button product_search ;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                if(data.indexOf("HWH")<0) {
                    Toast.makeText(HwhSearch.this,"扫描二维码不是货位号",Toast.LENGTH_LONG).show();
                    return;
                }
                ///设置显示框内容
                Log.d("扫描内容",data);
                hwh_code.setText(Getcode.gethwh(data));
                ///display(Getcode.gethwh(data));
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hwhsearch);
        hwh_code = (EditText)findViewById(R.id.editview_hwh);
        product_search=(Button)findViewById(R.id.button_hwh);
        product_hwh = (RecyclerView)findViewById(R.id.recycle_hwh);

    }
    public void display(String hwh){
        try {
            LoadingDialog.getInstance(HwhSearch.this).show();
            JSONObject supportInfo = new JSONObject();
            supportInfo.put("hwh",hwh);
            Log.d("查询货位数据传入",supportInfo.toString());
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),supportInfo.toString());
            final Request request = new Request.Builder()
                    .url("http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryHwh")
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LoadingDialog.getInstance(HwhSearch.this).show();
                    Log.d("okhttp请求失败",e.getMessage());
                    Toast.makeText(HwhSearch.this,"okhttp请求失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LoadingDialog.getInstance(HwhSearch.this).show();
                    final String box_res = response.body().string();
                    Log.d("okhttp请求成功",box_res);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                Toast.makeText(HwhSearch.this,"查询成功",Toast.LENGTH_SHORT).show();
                                Log.d("查询货位数据返回",box_res);
                                JSONObject box_res_json = box_res_json = new JSONObject(box_res);
                                JSONObject d = new JSONObject(box_res_json.get("d").toString());
                                String code = d.getString("Code").toString();
                                if(!code.equals("200")){
                                    Toast.makeText(HwhSearch.this,"查询货位数据失败"+d.getString("Message").toString(),Toast.LENGTH_LONG).show();
                                    return;
                                }

                                JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
                                List<Product_Location_info> wl_detail_list = new ArrayList<>();
                                for(int i=0;i<box_Jsons.length();i++){
                                    int xs = Integer.parseInt(box_Jsons.getJSONObject(i).get("xs").toString());
                                    String wlbm = box_Jsons.getJSONObject(i).get("wl").toString();
                                    String hwh = box_Jsons.getJSONObject(i).get("hwh").toString();
                                    Product_Location_info wl_detail = new Product_Location_info(wlbm,hwh,xs);
                                    wl_detail_list.add(wl_detail);
                                }
                                ///显示数据
                                ///recyclerView显示数据
                                ///product_location = (RecyclerView)findViewById(R.id.recycle_product_location);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(HwhSearch.this);
                                product_hwh.setLayoutManager(layoutManager);
                                ProductLocationAdapter adapter  =new ProductLocationAdapter(wl_detail_list);
                                product_hwh.setAdapter(adapter);
                                product_hwh.addItemDecoration(new SpacesItemDecoration());
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(HwhSearch.this,"查询货位数据失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        IntentFilter iFilter = new IntentFilter();
        //注册系统广播  接受扫描到的数据
        iFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver, iFilter);
        super.onResume();
    }
}
