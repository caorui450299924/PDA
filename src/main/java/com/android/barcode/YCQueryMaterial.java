package com.android.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YCQueryMaterial extends AppCompatActivityBase {
    Button btn_cx ;
    TextView tv_dis;
    RecyclerView recyclerView;
    List<BK_info> bk_infos ;
    String barcodeType;
    String param;
    String wl;
    String hwhfinal ;
    String rktype;
    public CustomDialogCommon.Builder builder ;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(YCQueryMaterial.super.RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                ///设置显示框内容
                Log.d("扫描箱码内容",data);
                if(data.indexOf("_")>0){
                    tv_dis.setText("物料："+data);
                    barcodeType="wl";
                    param=data;
                    wl=data;
                }else if(data.indexOf(",")>0&&data.indexOf("hwh")>=0){
                    String hwh = data.substring(data.indexOf("YC,")+3);
                    String hwh1[] = data.split(",");
                    Log.d("hwh1",hwh1[1]);
                    param=hwh;
                    hwhfinal=hwh;
                    Log.d("半库货位号",hwh);
                    tv_dis.setText("货位号："+hwh);
                    barcodeType="hwh";
                    if(builder!=null){
                        builder.cpmph.setText("货位号:"+hwh);
                        builder.cpsl.setText("0");
                    }
                }else{
                    barcodeType="null";
                    Toast.makeText(YCQueryMaterial.this,"请扫描产品或货位号",Toast.LENGTH_LONG).show();
                    return;
                }
                ///Toast.makeText(BKWarehousing.this,"扫码数据"+data,Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ycquerymaterial);
        btn_cx=(Button)findViewById(R.id.btn_ycjccx);
        tv_dis=(TextView)findViewById(R.id.tv_ycjc);
        recyclerView=(RecyclerView)findViewById(R.id.recycle_ycjc);
        bk_infos=new ArrayList<>();
        intBtn_cx();
    }

    @Override
    protected void onResume() {
        super.receiver=receiver;
        super.onResume();
    }

    public void intBtn_cx(){
        btn_cx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_dis.getText().equals("请扫描二维码")){
                    Toast.makeText(YCQueryMaterial.this,"请先扫描二维码数据",Toast.LENGTH_LONG).show();
                    return;
                }
                ///清除list中数据
                bk_infos.clear();
                if(barcodeType.equals("wl")){
                    searchwl("http://www.vapp.meide-casting.com/app/jjc","wl");
                }else if(barcodeType.equals("hwh")){
                    searchwl("http://www.vapp.meide-casting.com/app/wl","hwh");
                }

            }
        });
    }

    ///根据物料、货位号查询结存
    public void searchwl(String url,String name){
        JSONObject req_supportreport = new JSONObject();
        try {
        req_supportreport.put(name,param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        //Log.d("查询数据",req_supportreport.toString());
        //Log.d("查询数据1",requestBody.toString());
        LoadingDialog.getInstance(YCQueryMaterial.this).show();
        final Request request = new Request.Builder()
                ///.url("http://www.vapp.meide-casting.com/app/jjc")
                .url(url)
                ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(YCQueryMaterial.this).dismiss();
                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(YCQueryMaterial.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            public  String toUtf8(String str) {
                String result = null;
                Log.d("查询数据转换",str);
                try {
                    result = new String(str.getBytes("UTF-8"), "UTF-8");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(YCQueryMaterial.this).dismiss();
                //String  a = response.body().string();
                //Log.d("6666",a);
                //String a = response.body().toString();
                //Log.d("查询返回数据",a);
                //final String box_res = response.body().string();
                String a = response.body().string();

                final String box_res = toUtf8(a);
                Log.d("查询半库货位数据返回1",box_res);
                try{
                    JSONObject box_res_json = new JSONObject(box_res);
                    String code = box_res_json.getString("flag").toString();
                    final String msg =box_res_json.getString("msg").toString();
                    if(!code.equals("1")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YCQueryMaterial.this,"查询数据失败"+msg, Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    JSONArray box_Jsons = new JSONArray(box_res_json.get("data").toString());
                    int productsl = 0;
                    for(int i= 0;i< box_Jsons.length();i++){
                        BK_info bk_info = new BK_info(box_Jsons.getJSONObject(i).get("wl").toString(),box_Jsons.getJSONObject(i).get("hwh").toString(),Integer.valueOf(box_Jsons.getJSONObject(i).get("jjc").toString()));
                        bk_infos.add(bk_info);
                    }
                    final int boxs = box_Jsons.length();
                    final int jss = productsl;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(YCQueryMaterial.this);
                            recyclerView.setLayoutManager(layoutManager);
                            YCQueryMaterialAdapter adapter  = new YCQueryMaterialAdapter(bk_infos,YCQueryMaterial.this,YCQueryMaterial.this);
                            recyclerView.setAdapter(adapter);
                            recyclerView.addItemDecoration(new SpacesItemDecoration());
                        }
                    });


                }catch (Exception e){
                    Log.e("解析查询箱数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(YCQueryMaterial.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });
    }

}
