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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CaoRuiActivityCk_x extends AppCompatActivityBase {
    TextView edt_wl;
    EditText edt_sl;
    TextView edt_dhwh;
    Spinner spinner_hwh;
    Button btn_bkck_ck;
    String sl;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caoruiactivityck_x);
        edt_wl = (TextView) findViewById(R.id.mhck_wl);
        edt_sl = (EditText)findViewById(R.id.mhck_sl);
        edt_dhwh = (TextView)findViewById(R.id.mhck_hwh);
        btn_bkck_ck = (Button) findViewById(R.id.btn_bkmhck_ck);
        Intent intent = getIntent();
        String wl = intent.getStringExtra("wl");
        //Log.d("wl",wl);
        edt_wl.setText(wl);
        edt_wl.setFocusable(false);
        String hwh = intent.getStringExtra("hwh");
        edt_dhwh.setText(hwh);
        edt_dhwh.setFocusable(false);
        edt_sl.setText("0");
        edt_sl.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        sl = intent.getStringExtra("sl");
        //Log.d("sl",sl);
        BK_info_mhck bk_info_mh=new BK_info_mhck(intent.getStringExtra("wl"),intent.getStringExtra("lch"),intent.getStringExtra("jjc"),"",intent.getStringExtra("pz"),
                intent.getStringExtra("gg"),intent.getStringExtra("xs"),intent.getStringExtra("cpbm"),intent.getStringExtra("kbh"),intent.getStringExtra("gdh"),
                intent.getStringExtra("gys"),intent.getStringExtra("czfl"),intent.getStringExtra("bmcl"),"");
        intBtn_rk(sl,bk_info_mh);
        //intBtn_rk();

    }

    public void onResume(){
        super.receiver = receiver;
        super.onResume();
    }



    public void intBtn_rk(final String jjc, final BK_info_mhck bk_info_mhck){
        btn_bkck_ck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_bkck_ck.setEnabled(false);
               /* Log.d("hwh",edt_dhwh.getText().toString());
                Log.d("wl",edt_wl.getText().toString());
                Log.d("sl",edt_sl.getText().toString());*/
                int a = Integer.parseInt(jjc);
                int b = Integer.parseInt(edt_sl.getText().toString());
                if (b > a){
                    CaoRuiActivityCk_x.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityCk_x.this,"入库数量不可告于结存数量，结存数量为"+jjc, Toast.LENGTH_LONG).show();
                        }
                    });
                    btn_bkck_ck.setEnabled(true);
                    return;
                }
                if (b <= 0 ){
                    CaoRuiActivityCk_x.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityCk_x.this,"入库数量不可为0或负数", Toast.LENGTH_LONG).show();
                        }
                    });
                    btn_bkck_ck.setEnabled(true);
                    return;
                }
                confirmck(edt_wl.getText().toString(),edt_dhwh.getText().toString(),edt_sl.getText().toString(),ListMap.getuserid(),bk_info_mhck);
            }
        });
    }
    public void confirmck(String wl,String hwh,String num,String user,BK_info_mhck bk_info_mh){
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("pz",bk_info_mh.getPz());
            req_supportreport.put("gg",bk_info_mh.getGg());
            req_supportreport.put("xs",bk_info_mh.getXs());
            req_supportreport.put("czfl",bk_info_mh.getCzfl());
            req_supportreport.put("bmcl",bk_info_mh.getBmcl());
            req_supportreport.put("cpbm",bk_info_mh.getCpbm());
            req_supportreport.put("sl",num);
            req_supportreport.put("lch",bk_info_mh.getLch());
            req_supportreport.put("hwh",hwh);
            req_supportreport.put("kbh",bk_info_mh.getKbh());
            req_supportreport.put("gdh",bk_info_mh.getGdh());
            req_supportreport.put("gys",bk_info_mh.getGys());
            req_supportreport.put("user",user);
           /* Log.d("pz",bk_info_mh.getPz());
            Log.d("gg",bk_info_mh.getGg());
            Log.d("xs",bk_info_mh.getXs());
            Log.d("czfl",bk_info_mh.getCzfl());
            Log.d("bmcl",bk_info_mh.getBmcl());
            Log.d("cpbm",bk_info_mh.getCpbm());
            Log.d("sl",num);
            Log.d("lch",bk_info_mh.getLch());
            Log.d("hwh",hwh);
            Log.d("kbh",bk_info_mh.getKbh());
            Log.d("gdh",bk_info_mh.getGdh());
            Log.d("gys",bk_info_mh.getGys());
            Log.d("user",user);*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(CaoRuiActivityCk_x.this).show();
        Log.d("出库调用接口开始时间",bk_info_mh.getPz() + "" + bk_info_mh.getGg() +"" + bk_info_mh.getXs() + "" + user + "" + "" + hwh + "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/app/fmbkck")
                ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        Log.d("出库调用接口结束时间",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(CaoRuiActivityCk_x.this).dismiss();
                final String message = e.getMessage();
                CaoRuiActivityCk_x.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CaoRuiActivityCk_x.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                btn_bkck_ck.setEnabled(true);
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(CaoRuiActivityCk_x.this).dismiss();
                final String box_res = response.body().string();
                //Log.d("查询半库货位数据返回",box_res);
                try{
                    JSONObject box_res_json = new JSONObject(box_res);
                    String code = box_res_json.getString("flag").toString();
                    final String msg =box_res_json.getString("msg").toString();
                    if(!code.equals("1")){
                        CaoRuiActivityCk_x.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CaoRuiActivityCk_x.this,"执行出库失败"+msg, Toast.LENGTH_LONG).show();
                            }
                        });
                        btn_bkck_ck.setEnabled(true);
                        return;
                    }
                    CaoRuiActivityCk_x.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityCk_x.this,"执行出库成功"+msg, Toast.LENGTH_LONG).show();
                            CaoRuiActivityCk_x.this.finish();
                        }
                    });
                }catch (Exception e){
                    Log.e("解析查询箱数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    CaoRuiActivityCk_x.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityCk_x.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    btn_bkck_ck.setEnabled(true);
                    return;
                }
            }
        });

    }

}
