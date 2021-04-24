package com.android.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

public class YCWarehousingOperation extends AppCompatActivityBase {
    TextView cp;
    EditText sl;
    TextView hw;
    Button rk;
    String jjc;
    //final private String[] a = {};
    RecyclerView recyclerView;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(YCWarehousingOperation.super.RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                ///设置显示框内容
                Log.d("hw：",data);
                if(data.indexOf(",")>0&&data.indexOf("hwh")>=0){
                    String hwh = data.substring(data.indexOf("YC,")+3);
                    hw.setText(hwh);
                    //hw.setFocusable(false);
                }else{
                    Toast.makeText(YCWarehousingOperation.this,"请扫描货位号",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    };

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ycwarehousingoperation);
        cp = (TextView) findViewById(R.id.yc_operation_cp);
        sl = (EditText)findViewById(R.id.yc_operation_sl);
        hw = (TextView)findViewById(R.id.yc_operation_hw);
        rk = (Button) findViewById(R.id.btn_bkrk_rk);
        Intent intent = getIntent();
        String wl = intent.getStringExtra("wl");
        //Log.d("wl",wl);
        cp.setText(wl);
        cp.setFocusable(false);
        sl.setFocusableInTouchMode(false);
        jjc = intent.getStringExtra("sl");
        //Log.d("sl",sl);
        YC_CP yc_cp=new YC_CP(intent.getStringExtra("wl"),intent.getStringExtra("lch"),intent.getStringExtra("jjc"),"",intent.getStringExtra("pz"),
                intent.getStringExtra("gg"),intent.getStringExtra("xs"),intent.getStringExtra("cpbm"),intent.getStringExtra("kbh"),intent.getStringExtra("gdh"),
                intent.getStringExtra("gys"),intent.getStringExtra("czfl"),intent.getStringExtra("bmcl"));
        intBtn_rk(jjc,yc_cp);
        //intBtn_rk();

    }


    public void onResume(){
        super.receiver = receiver;
        super.onResume();
    }


    public void intBtn_rk(final String jjc, final YC_CP yc_cp){
        rk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                rk.setEnabled(false);
                int a = Integer.parseInt(jjc);
                int b = Integer.parseInt(sl.getText().toString());
                if (b > a){
                    YCWarehousingOperation.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(YCWarehousingOperation.this,"入库数量不可告于结存数量，结存数量为"+jjc, Toast.LENGTH_LONG).show();
                        }
                    });
                    rk.setEnabled(true);
                    return;
                }
                if (b <= 0 ){
                    YCWarehousingOperation.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(YCWarehousingOperation.this,"入库数量不可为0或负数", Toast.LENGTH_LONG).show();
                        }
                    });
                    rk.setEnabled(true);
                    return;
                }
                confirmrk(cp.getText().toString(),hw.getText().toString(),sl.getText().toString(),ListMap.getuserid(),yc_cp);
            }
        });
    }


    public void confirmrk(String wl,String hwh,String num,String user,YC_CP yc_cp){
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("pz",yc_cp.getPz());
            req_supportreport.put("gg",yc_cp.getGg());
            req_supportreport.put("xs",yc_cp.getXs());
            req_supportreport.put("czfl",yc_cp.getCzfl());
            req_supportreport.put("bmcl",yc_cp.getBmcl());
            req_supportreport.put("cpbm",yc_cp.getCpbm());
            req_supportreport.put("sl",num);
            req_supportreport.put("lch",yc_cp.getLch());
            req_supportreport.put("hwh",hwh);
            req_supportreport.put("kbh",yc_cp.getKbh());
            req_supportreport.put("gdh",yc_cp.getGdh());
            req_supportreport.put("gys",yc_cp.getGys());
            req_supportreport.put("user",user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(YCWarehousingOperation.this).show();
        Log.d("入库调用接口开始时间",yc_cp.getPz() + "" + yc_cp.getGg() +"" + yc_cp.getXs() + "" + user + "" + "" + hwh + "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/app/rk")
                ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        Log.d("入库调用接口结束时间",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(YCWarehousingOperation.this).dismiss();
                final String message = e.getMessage();
                YCWarehousingOperation.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(YCWarehousingOperation.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                rk.setEnabled(true);
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(YCWarehousingOperation.this).dismiss();
                final String box_res = response.body().string();
                //Log.d("查询半库货位数据返回",box_res);
                try{
                    JSONObject box_res_json = new JSONObject(box_res);
                    String code = box_res_json.getString("flag").toString();
                    final String msg =box_res_json.getString("msg").toString();
                    if(!code.equals("1")){
                        YCWarehousingOperation.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YCWarehousingOperation.this,"执行入库失败"+msg, Toast.LENGTH_LONG).show();
                            }
                        });
                        rk.setEnabled(true);
                        return;
                    }
                    YCWarehousingOperation.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(YCWarehousingOperation.this,"执行入库成功"+msg, Toast.LENGTH_LONG).show();
                            YCWarehousingOperation.this.finish();
                        }
                    });
                }catch (Exception e){
                    Log.e("解析查询箱数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    YCWarehousingOperation.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(YCWarehousingOperation.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    rk.setEnabled(true);
                    return;
                }
            }
        });

    }

}
