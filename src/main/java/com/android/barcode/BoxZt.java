package com.android.barcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BoxZt extends AppCompatActivity {
    String supportId = "";
    String supportCode = "";
    TextView boxsl;
    TextView js;
    RecyclerView recyclerView;
    Button button_boxZtTj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boxzt);
        boxsl=(TextView)findViewById(R.id.textViewZt1);
        js=(TextView)findViewById(R.id.textViewZt2);
        recyclerView=(RecyclerView)findViewById(R.id.listview_boxzt);
        button_boxZtTj=(Button)findViewById(R.id.button_boxZtTj);
        Intent intent = getIntent();
        supportId = intent.getStringExtra("supportId");
        supportCode=intent.getStringExtra("supportCode");
        ///清除List
        ListMap.clearboxZt_infos();
        ///提交按钮
        button_boxZtTj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ListMap.getboxZt_infossize()<1){
                    Toast.makeText(BoxZt.this,"没有数据不能提交！",Toast.LENGTH_LONG).show();
                    return;
                }
                for(int i=0;i<ListMap.getboxZt_infossize();i++){
                    Box_Int_info box_info = new Box_Int_info(ListMap.getboxZt_infos().get(i).getBox_code());
                    box_info.setJs(ListMap.getboxZt_infos().get(i).getjs());
                    box_info.setWl(ListMap.getboxZt_infos().get(i).getwl());
                    ListMap.addbox_int_list(box_info);
                }
                ///清除数据
                ListMap.clearboxZt_infos();
                finish();
            }
        });
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
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(BoxZt.this).show();
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxInfo")
                ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(BoxZt.this).dismiss();
                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxZt.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(BoxZt.this).dismiss();
                final String box_res = response.body().string();
                Log.d("查询拼托箱码返回",box_res);
                try{
                    JSONObject box_res_json = new JSONObject(box_res);
                    JSONObject d = new JSONObject(box_res_json.get("d").toString());
                    String code = d.getString("Code").toString();
                    if(!code.equals("200")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BoxZt.this,"查询数据失败", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
                    int productsl = 0;
                    for(int i= 0;i< box_Jsons.length();i++){
                        BoxZt_info boxZt_info = new BoxZt_info(box_Jsons.getJSONObject(i).get("BOX_ID").toString(),box_Jsons.getJSONObject(i).get("BOX_CODE").toString(),box_Jsons.getJSONObject(i).get("COUNT").toString(),box_Jsons.getJSONObject(i).get("wl").toString());
                        ///if(i==box_Jsons.length()-1){
                        productsl=productsl+Integer.parseInt(box_Jsons.getJSONObject(i).get("COUNT").toString());
                        ListMap.addboxZt_infos(boxZt_info);
                        ///}
                    }
                    final int boxs = box_Jsons.length();
                    final int jss = productsl;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ///显示listview
                            boxsl.setText(String.valueOf(boxs)+"箱");
                            js.setText(String.valueOf(jss)+"件");

                            LinearLayoutManager layoutManager = new LinearLayoutManager(BoxZt.this);
                            recyclerView.setLayoutManager(layoutManager);
                            BoxZtAdapter adapter  =new BoxZtAdapter(ListMap.getboxZt_infos());
                            recyclerView.setAdapter(adapter);
                            recyclerView.addItemDecoration(new SpacesItemDecoration());
                        }
                    });


                }catch (Exception e){
                    Log.e("解析查询箱数据失败",e.getMessage());
                    return;
                }
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        display(supportId);
    }
}
