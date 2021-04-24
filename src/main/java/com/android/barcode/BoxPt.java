package com.android.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BoxPt extends Activity {
    ListView box_list;
    String support_id;
    String type;
    String support_code;
    TextView sl;
    TextView slDis;
    TextView productConut;
    ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boxpt);
        Intent intent =getIntent();
        support_id=intent.getStringExtra("support_id");
        type=intent.getStringExtra("type");
        support_code=intent.getStringExtra("support_code");
        box_list = (ListView) findViewById(R.id.listview_boxpt);
        slDis = (TextView)findViewById(R.id.tv_boxpt_dis);
        sl = (TextView)findViewById(R.id.textView10);
        sl.setText("0");
        productConut=(TextView)findViewById(R.id.textView11);
        productConut.setText("0");
        ///display(support_id);
    }
    public void display(String support_id){
        JSONObject req_supportreport = new JSONObject();
        JSONObject supportInfo = new JSONObject();
        try {
            supportInfo.put("SUPPORT_ID",support_id);
            supportInfo.put("CREATEUSER","");
            ///supportInfo.put("CREATEUSER",ListMap.getuserid());
            supportInfo.put("END_TIME","");
            supportInfo.put("START_TIME","");
            supportInfo.put("SelectState",0);
            req_supportreport.put("boxInfo",supportInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ///String box_res = post_com(support_json,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxT");
        ///String box_res = post_com(support_json,"http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxT");
        ///OkHttpClient client = new OkHttpClient();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(BoxPt.this).show();
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxInfo")
                ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(BoxPt.this).dismiss();
                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxPt.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(BoxPt.this).dismiss();
                final String box_res = response.body().string();
                Log.d("查询拼托箱码返回",box_res);
                try {
                    JSONObject box_res_json = new JSONObject(box_res);
                    JSONObject d = new JSONObject(box_res_json.get("d").toString());
                    String code = d.getString("Code").toString();
                    if(!code.equals("200")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BoxPt.this,"查询数据失败", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
                    int productsl = 0;
                    for(int i= 0;i< box_Jsons.length();i++){
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("BOX_CODE","箱码："+box_Jsons.getJSONObject(i).get("BOX_CODE").toString());
                        map.put("BOX_ID",box_Jsons.getJSONObject(i).get("BOX_ID").toString());
                        map.put("wlCount","物料"+box_Jsons.getJSONObject(i).get("wl").toString()+","+box_Jsons.getJSONObject(i).get("COUNT").toString()+"件");
                        ///if(i==box_Jsons.length()-1){
                        productsl=productsl+Integer.parseInt(box_Jsons.getJSONObject(i).get("COUNT").toString());
                        listItem.add(map);
                        ///}
                    }





                    ///显示listview
                    sl.setText(String.valueOf(box_Jsons.length())+"箱");
                    productConut.setText(String.valueOf(productsl)+"件");
                    ///refresh(listItem.size());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ArrayList<HashMap<String, Object>> listItem_temp = listItem;
                            SimpleAdapter listItemAdapter =new SimpleAdapter(BoxPt.this,listItem,
                                    R.layout.item_int_box,new String[] {"BOX_CODE","wlCount"},
                                    new int[] {R.id.textView_box_code,R.id.textView_box_sl});
                            box_list.setAdapter(listItemAdapter);
                            box_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(BoxPt.this,BoxDetail.class);
                                    intent.putExtra("box_code",listItem_temp.get(i).get("BOX_CODE").toString());
                                    intent.putExtra("box_id",listItem_temp.get(i).get("BOX_ID").toString());
                                    ListMap.intFlag="Y";
                                    startActivity(intent);
                                }
                            });

                        }
                    });


                }
                catch(JSONException e) {
                    Log.e("使用Json解析创建托结果失败",e.getMessage());
                    return;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        listItem.clear();
        if(type.equals("托明细")){
            display(support_id);
        }else{
            displaymx(support_code);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        ///更新拆箱标记为N
        ListMap.intFlag="N";
    }
    public void displaymx(String support_code){
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("supportcode",support_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder()
                                  .connectTimeout(60, TimeUnit.SECONDS)
                                  .writeTimeout(60, TimeUnit.SECONDS)
                                  .readTimeout(60, TimeUnit.SECONDS)
                                  .build();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(BoxPt.this).show();
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryProductBySupportCode")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(BoxPt.this).dismiss();
                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxPt.this,"查询明细数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(BoxPt.this).dismiss();
                final String box_res = response.body().string();
                Log.d("查询拼托箱码返回",box_res);
                try {
                    JSONObject box_res_json = new JSONObject(box_res);
                    JSONObject d = new JSONObject(box_res_json.get("d").toString());
                    String code = d.getString("Code").toString();
                    if(!code.equals("200")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BoxPt.this,"查询数据失败", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
                    int productsl = 0;
                    for(int i= 0;i< box_Jsons.length();i++){
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("BOX_CODE",box_Jsons.getJSONObject(i).get("MATERIAL_CODE").toString());
                        map.put("wlCount",box_Jsons.getJSONObject(i).get("productcount").toString()+"件");
                        productsl=productsl+Integer.parseInt(box_Jsons.getJSONObject(i).get("productcount").toString());
                        listItem.add(map);
                    }
                    productConut.setText(String.valueOf(productsl)+"件");
                    sl.setText(String.valueOf(box_Jsons.length())+"箱");
                    ///refresh(listItem.size());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ///显示listview
                            sl.setVisibility(View.GONE);
                            slDis.setVisibility(View.GONE);
                            final ArrayList<HashMap<String, Object>> listItem_temp = listItem;
                            SimpleAdapter listItemAdapter =new SimpleAdapter(BoxPt.this,listItem,
                                    R.layout.item_int_box,new String[] {"BOX_CODE","wlCount"},
                                    new int[] {R.id.textView_box_code,R.id.textView_box_sl});
                            box_list.setAdapter(listItemAdapter);

                        }
                    });


                }
                catch(JSONException e) {
                    Log.e("使用Json解析创建托结果失败",e.getMessage());
                    return;
                }
            }
        });
    }


}
