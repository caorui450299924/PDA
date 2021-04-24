package com.android.barcode;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class FhdMx extends AppCompatActivity {
    TextView textView_fhdmx_kh_dis ;
    Button btn_fhdmx_delete;
    Button btn_fhdmx_print;
    Button btn_fhdmx_fh;
    RecyclerView recycle_fhdmx;
    String ids;
    boolean isFirstLaunch =false;
    AlertDialog builder=null;
    List<String> supportList = new ArrayList<>();
    String fhwl ;
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                if(data.indexOf("TRSUT")<0) {
                    Toast.makeText(FhdMx.this,"扫描二维码不是箱码",Toast.LENGTH_LONG).show();
                    return;
                }
                ///设置显示框内容
                Log.d("发货扫描内容",data);
                ///box_code.setText(Getcode.getboxcode(data));
                ///display(Getcode.getboxcode(data));
                displayScaninfo(Getcode.getsupportcode(data));
            }
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fhdmx);
        textView_fhdmx_kh_dis = (TextView)findViewById(R.id.textView_fhdmx_kh_dis);
        btn_fhdmx_delete = (Button)findViewById(R.id.btn_fhdmx_delete);
        btn_fhdmx_print = (Button)findViewById(R.id.btn_fhdmx_print);
        btn_fhdmx_fh = (Button)findViewById(R.id.btn_fhdmx_fh);
        recycle_fhdmx =(RecyclerView)findViewById(R.id.recycle_fhdmx);
        Intent intent =getIntent();
        textView_fhdmx_kh_dis.setText(intent.getStringExtra("khm"));
        ids=intent.getStringExtra("ids");
        ///确认发货
        btn_fhdmx_fh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmExport();
            }
        });
        ///打印发货明细
        btn_fhdmx_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///displayScaninfo("TRSUT2020110704814");
                http();

            }
        });
    }
    public void display(){
        try {
            LoadingDialog.getInstance(FhdMx.this).show();
            JSONObject req_bh_mx = new JSONObject();
            JSONObject paramobj =new JSONObject();
            paramobj.put("ids",ids);
            paramobj.put("sys","cx");
            req_bh_mx.put("namingid","com.dwb.reporter.queryfmfc.get_delievry_sj");
            req_bh_mx.put("paramobj",paramobj);
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_bh_mx.toString());
            final Request request = new Request.Builder()
                    .url("http://www.vapp.meide-casting.com/ims/com.dwb.reporter.queryfmfc.customerlogin.biz.ext")
                    .post(requestBody)
                    /*.addHeader("cookie","JSESSIONID="+Login.sessionId)*/
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("okhttp请求失败",e.getMessage());
                    LoadingDialog.getInstance(FhdMx.this).dismiss();
                    return;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res_bh = response.body().string();
                    LoadingDialog.getInstance(FhdMx.this).dismiss();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                Log.d("查询发货数据返回",res_bh);
                                JSONObject res_json = new JSONObject(res_bh);
                                JSONArray objs = res_json.getJSONArray("objs");
                                Fh_info fh_info_temp = new Fh_info("物料","实发","预发");
                                ListMap.addfh_info_list(fh_info_temp);
                                for(int i=0;i<objs.length();i++){
                                    Fh_info fh_info = new Fh_info(objs.getJSONObject(i).getString("MATERIAL_CODE"),"0",objs.getJSONObject(i).getString("EXTIMATE_QTY"));
                                    ListMap.addfh_info_list(fh_info);
                                }
                                ListMap.addfh_info_list(new Fh_info("底部","底部","底部"));
                                ///recyclerView = (RecyclerView)findViewById(R.id.recycle_delivery_bh);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(FhdMx.this);
                                recycle_fhdmx.setLayoutManager(layoutManager);
                                recycle_fhdmx.setOnScrollListener(new RecycleScrol(FhdMx.this));
                                FhdMxAdapter adapter  =new FhdMxAdapter(ListMap.getfh_info_list(),FhdMx.this,FhdMx.this);
                                recycle_fhdmx.setAdapter(adapter);
                                recycle_fhdmx.addItemDecoration(new SpacesItemDecoration());
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(FhdMx.this,"查询发货数据失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(FhdMx.this,"查询发货数据失败"+e.getMessage(),Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    protected void onResume() {
        IntentFilter iFilter = new IntentFilter();
        //注册系统广播  接受扫描到的数据
        iFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver, iFilter);
        isFirstLaunch=true;
        super.onResume();
    }

    @Override
    protected void onStop() {
        ListMap.clearfh_info_list();
        unregisterReceiver(receiver);
        super.onStop();
    }
    /**
     *
     * @brief 查询箱码的ID
     * @author Administrator
     * @param
     * @return
     * @time 2020-11-06 9:37
     */
    public void displayScaninfo(String support_code){
        try {
            JSONObject req_bh_mx = new JSONObject();
            req_bh_mx.put("supportCode",support_code);
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_bh_mx.toString());
            LoadingDialog.getInstance(FhdMx.this).show();
            final Request request = new Request.Builder()
                    .url("http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryProductCountBySupportCode")
                    .post(requestBody)
                    /*.addHeader("cookie","JSESSIONID="+Login.sessionId)*/
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("okhttp请求失败",e.getMessage());
                    LoadingDialog.getInstance(FhdMx.this).dismiss();
                    return;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res_bh = response.body().string();
                    ///Toast.makeText(FhdMx.this,"9999999"+res_bh,Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(FhdMx.this).dismiss();
                            try{
                                Log.d("查询发货数据返回",res_bh);
                                JSONObject res_json = new JSONObject(res_bh);
                                JSONObject d = res_json.getJSONObject("d");
                                if(!d.get("Code").toString().equals("200")){
                                    Log.d("okhttp请求失败",d.get("Message").toString());
                                    return;
                                }
                                JSONArray objs = d.getJSONArray("Data");
                                if(objs.length()<1){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(FhdMx.this,"查询产品数据为空",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    return;
                                }
                                ///扫描数据是否在发货数据中存在
                                for (int i=0;i<objs.length();i++){
                                    boolean cunzai = false;
                                    for(int m=0;m<ListMap.getfh_info_listsize();m++){
                                        ///查询到的数据有一个不等于发货数量
                                        if(!objs.getJSONObject(i).get("MATERIAL_CODE").toString().equals(ListMap.getfh_info_list().get(m).getwl())){
                                            fhwl=objs.getJSONObject(i).get("MATERIAL_CODE").toString();
                                            ///cunzai=false;
                                        }else{
                                            cunzai=true;
                                        }
                                    }
                                    ///询换完如果cunzai等于false说明在发货数据中不存在
                                    if(cunzai==false){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(FhdMx.this,"扫描托中的产品"+fhwl+"在发货产品中不存在",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        return;
                                    }
                                }
                                ///判断完成后判断数量
                                for (int i=0;i<objs.length();i++){
                                    for(int m=0;m<ListMap.getfh_info_listsize();m++){
                                        ///查询到的数据有一个不等于发货数量
                                        if(!objs.getJSONObject(i).get("MATERIAL_CODE").toString().equals(ListMap.getfh_info_list().get(m).getwl())){
                                            fhwl=objs.getJSONObject(i).get("MATERIAL_CODE").toString();
                                        }else{
                                            int sf = Integer.parseInt(ListMap.getfh_info_list().get(m).getsf());
                                            int cansf = Integer.parseInt(ListMap.getfh_info_list().get(m).getyf())-sf-Integer.parseInt(objs.getJSONObject(i).get("PRODUCTNUM").toString());
                                            if(cansf<0){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(FhdMx.this,"扫描托中的产品"+fhwl+"数量大于发货产品中产品数量",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                                return;
                                            }
                                        }
                                    }

                                }
                                ///更新ListMap
                                for (int i=0;i<objs.length();i++){
                                    for(int m=0;m<ListMap.getfh_info_listsize();m++){
                                        ///查询到的数据有一个不等于发货数量
                                        if(!objs.getJSONObject(i).get("MATERIAL_CODE").toString().equals(ListMap.getfh_info_list().get(m).getwl())){
                                        }else{
                                            int sf = Integer.parseInt(ListMap.getfh_info_list().get(m).getsf());
                                            sf = sf + Integer.parseInt(objs.getJSONObject(i).get("PRODUCTNUM").toString());
                                            ListMap.getfh_info_list().get(m).setsf(String.valueOf(sf));
                                        }
                                    }

                                }
                                ///添加托id到集合
                                supportList.add(objs.getJSONObject(0).get("SUPPORT_ID").toString());
                                ///显示Recycleview
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(FhdMx.this);
                                        recycle_fhdmx.setLayoutManager(layoutManager);
                                        recycle_fhdmx.setOnScrollListener(new RecycleScrol(FhdMx.this));
                                        FhdMxAdapter adapter  =new FhdMxAdapter(ListMap.getfh_info_list(),FhdMx.this,FhdMx.this);
                                        recycle_fhdmx.setAdapter(adapter);
                                        recycle_fhdmx.addItemDecoration(new SpacesItemDecoration());
                                    }
                                });



                            }catch (Exception e){
                                e.printStackTrace();
                                return;
                            }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(FhdMx.this,"查询托数据失败"+e.getMessage(),Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus&&isFirstLaunch){
            Log.d("Finishing",String.valueOf(FhdMx.this.isFinishing()));
            display();
            isFirstLaunch = false;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            builder = new AlertDialog.Builder(FhdMx.this)
                    .setTitle("温馨提示：")
                    .setMessage("您是否要退出发货？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    FhdMx.this.finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    builder.dismiss();
                                }
                            }).show();
        }
        return true;
    }

    public void confirmExport(){
        ///校验是否全部装完
        boolean bhFlag = true;
        if(ListMap.getfh_info_listsize()==0){
            Toast.makeText(FhdMx.this,"未查询到发货数据,不能确认发货",Toast.LENGTH_LONG).show();
            return;
        }
        for (int i =0;i<ListMap.getfh_info_listsize();i++){
            if(!ListMap.getfh_info_list().get(i).getyf().equals(ListMap.getfh_info_list().get(i).getsf())){
                bhFlag=false;
                break;
            }
        }
        if(!bhFlag){
            Toast.makeText(FhdMx.this,"发货数据和托数据不符，不能确认发货",Toast.LENGTH_LONG).show();
            return;
        }
        if(supportList.size()<1){
            Toast.makeText(FhdMx.this,"没有扫描托，不能确认发货",Toast.LENGTH_LONG).show();
            return;
        }
        String supportids = "";
        for(int n =0;n<supportList.size();n++){
            if(supportids.equals("")){
                supportids=supportList.get(n);
            }else{
                supportids +=','+supportList.get(n).toString();
            }
        }

        try {
            JSONObject req_bh_mx = new JSONObject();
            req_bh_mx.put("ids",ids);
            req_bh_mx.put("type","FH");
            req_bh_mx.put("tid",supportids);
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_bh_mx.toString());
            LoadingDialog.getInstance(FhdMx.this).show();
            final Request request = new Request.Builder()
                    .url("http://www.vapp.meide-casting.com/ims/com.dwb.reporter.queryfmfc.updatebh.biz.ext")
                    .post(requestBody)
                    .addHeader("cookie","JSESSIONID="+Login.sessionId)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("okhttp请求失败",e.getMessage());
                    LoadingDialog.getInstance(FhdMx.this).dismiss();
                    return;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res_bh = response.body().string();
                    LoadingDialog.getInstance(FhdMx.this).dismiss();
                    try{
                        Log.d("查询发货数据返回",res_bh);
                        JSONObject res_json = new JSONObject(res_bh);
                        if(!res_json.get("X_FLAG").toString().equals("1")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FhdMx.this,"确认发货失败",Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FhdMx.this,"确认发货成功",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        return;
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(FhdMx.this,"确认发货失败"+e.getMessage(),Toast.LENGTH_LONG).show();
            return;
        }






    }

    public void http(){
        try{
            JSONObject req_bh_mx = new JSONObject();
            req_bh_mx.put("name","support_code");
            req_bh_mx.put("age","23");
            JSONObject res = new JSONObject();
            res.put("People",req_bh_mx);
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_bh_mx.toString());
            ///LoadingDialog.getInstance(FhdMx.this).show();
            final Request request = new Request.Builder()
                    .url("http://192.168.31.205:6066/actionapi/UserInfo/CheckUserName")
                    .post(requestBody)
                    /*.addHeader("cookie","JSESSIONID="+Login.sessionId)*/
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("http失败",e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("http成功",response.body().string());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }





}
