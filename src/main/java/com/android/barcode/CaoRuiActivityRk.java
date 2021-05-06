package com.android.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import org.w3c.dom.ls.LSException;

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

public class CaoRuiActivityRk extends AppCompatActivityBase {
    TextView edt_wl;
    EditText edt_sl;
    TextView edt_dhwh;
    Spinner spinner_hwh;
    Button btn_bkrk_rk;
    String dhwh;
    String sl;
    private String[] A01 = {"A01","A02","A03","A04","A05","A06","A07","A08","A09","A10","A11","A12"};
    private String[] A02 = {"B01","B02","B03","B04","B05","B06","B07","B08","B09","B10","B11","B12"};
    //final private String[] a = {};
    RecyclerView recyclerView;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CaoRuiActivityRk.super.RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                ///设置显示框内容
                Log.d("扫描箱码内容",data);
                if(data.indexOf(",")>0&&data.indexOf("hwh")>=0){
                    String hwh = data.substring(data.indexOf("CP,")+3);
                    edt_dhwh.setText(hwh);
                    edt_dhwh.setFocusable(false);
                    //Log.d("aaa",hwh);
                    jmxg();
                }else{
                    Toast.makeText(CaoRuiActivityRk.this,"请扫描货位号",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    };

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caoruiactivityrk);
        edt_wl = (TextView) findViewById(R.id.mh_wl);
        edt_sl = (EditText)findViewById(R.id.mh_sl);
        edt_dhwh = (TextView)findViewById(R.id.mh_dhwh);
        spinner_hwh = (Spinner)findViewById(R.id.spinner_hwh);
        btn_bkrk_rk = (Button) findViewById(R.id.btn_bkrk_rk);
        Intent intent = getIntent();
        String wl = intent.getStringExtra("wl");
        //Log.d("wl",wl);
        edt_wl.setText(wl);
        edt_wl.setFocusable(false);
        edt_sl.setFocusableInTouchMode(false);
        sl = intent.getStringExtra("sl");
        //Log.d("sl",sl);
        BK_info_mh bk_info_mh=new BK_info_mh(intent.getStringExtra("wl"),intent.getStringExtra("lch"),intent.getStringExtra("jjc"),"",intent.getStringExtra("pz"),
                intent.getStringExtra("gg"),intent.getStringExtra("xs"),intent.getStringExtra("cpbm"),intent.getStringExtra("kbh"),intent.getStringExtra("gdh"),
                intent.getStringExtra("gys"),intent.getStringExtra("czfl"),intent.getStringExtra("bmcl"));
        intBtn_rk(sl,bk_info_mh);
        //intBtn_rk();

    }


    public void onResume(){
        super.receiver = receiver;
        super.onResume();
    }

    public void jmxg(){
        edt_sl.setFocusableInTouchMode(true);
        edt_sl.setText("0");
        edt_sl.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        //Log.d("货位号",edt_dhwh.getText().toString());
        dhwh = edt_dhwh.getText().toString();
        getHwh(dhwh);
    }

    public void getHwh(String dhwh){
        String hwh = dhwh;
        List<String> list = new ArrayList<String>();
        getHwh1(hwh);
        //list = getHwh1(hwh);
        /*String hwh1 = hwh.substring(1);
        Log.d("hwh1",hwh1);
        if (hwh.indexOf("A") >= 0){
            for (int i = 0;i < A01.length ; i++){
                list.add(A01[i]);
                Log.d("小货位号",A01[i]);
            }
            for (int j = 0 ; j < list.size() ; j++){
                Log.d("hwh",list.get(j));
            }
        }else{
            for (int i = 0;i < A02.length ; i++){
                list.add(A02[i]);
                Log.d("小货位号",A02[i]);
            }
            for (int j = 0 ; j < list.size() ; j++){
                Log.d("hwh",list.get(j));
            }
        }
        *//*for (int i = 0;i < a.length ; i++){
            list.add(a[i]);
            Log.d("小货位号",a[i]);
        }*//*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.item_caoruiactivityrk,R.id.spinner_text,list);
        spinner_hwh.setAdapter(adapter);
        spinner_hwh.setPrompt("小货位号");*/

        /*btn_bkrk_rk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("选择数据",spinner_hwh.getSelectedItem().toString());

            }
        });*/
    }

    public void intBtn_rk(final String jjc, final BK_info_mh bk_info_mh){
        btn_bkrk_rk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btn_bkrk_rk.setEnabled(false);
                /*Log.d("选择数据",spinner_hwh.getSelectedItem().toString());
                Log.d("hwh",edt_dhwh.getText().toString());
                Log.d("hwhz",edt_dhwh.getText().toString() + "-" + spinner_hwh.getSelectedItem().toString());
                Log.d("wl",edt_wl.getText().toString());
                Log.d("sl",edt_sl.getText().toString());*/
                int a = Integer.parseInt(jjc);
                int b = Integer.parseInt(edt_sl.getText().toString());
                if (b > a){
                    CaoRuiActivityRk.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityRk.this,"入库数量不可告于结存数量，结存数量为"+jjc, Toast.LENGTH_LONG).show();
                        }
                    });
                    btn_bkrk_rk.setEnabled(true);
                    return;
                }
                if (b <= 0 ){
                    CaoRuiActivityRk.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityRk.this,"入库数量不可为0或负数", Toast.LENGTH_LONG).show();
                        }
                    });
                    btn_bkrk_rk.setEnabled(true);
                    return;
                }
                confirmrk(edt_wl.getText().toString(),spinner_hwh.getSelectedItem().toString(),edt_sl.getText().toString(),ListMap.getuserid(),bk_info_mh);
            }
        });
    }

    public void getHwh1(String hwh){
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("hwh",hwh);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(CaoRuiActivityRk.this).show();
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/app/fmbkhwh")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(CaoRuiActivityRk.this).dismiss();
                final  String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CaoRuiActivityRk.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(CaoRuiActivityRk.this).dismiss();
                List<String> list1 = new ArrayList<String>();
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
                                Toast.makeText(CaoRuiActivityRk.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    JSONArray box_Jsons = new JSONArray(box_res_json.get("data").toString());
                    int productsl = 0;

                    for (int i = 0 ;i < box_Jsons.length();i++){
                        String hwh = box_Jsons.getJSONObject(i).get("hwh").toString();
                        list1.add(hwh);
                        /*Log.d("查询返回数据","66666666");
                        Log.d("hwh",box_Jsons.getJSONObject(i).get("hwh").toString());*/
                    }
                    setHwh(list1);
                }catch (JSONException e){
                    Log.e("解析查询数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityRk.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });


        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.item_caoruiactivityrk,R.id.spinner_text,list1);
        spinner_hwh.setAdapter(adapter);
        spinner_hwh.setPrompt("小货位号");*/
       // return  list1;
    }

    public void setHwh(List<String> hwh){
        final List<String> list = hwh;

        //String[] a = new String[list.size()];
        /*for (int j = 0;j<list.size();j++){
            //a[j] = list.get(j);
            Log.d("hwh12",list.get(j));
        }*/
        /*List<String> list2 = new ArrayList<String>();
        Log.d("a",a.length+"");
        for (int i = 0;i < a.length ; i++){
            list.add(a[i]);
            Log.d("小货位号",a[i]);
        }*/
        CaoRuiActivityRk.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.d("run","调用");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CaoRuiActivityRk.this,R.layout.item_caoruiactivityrk,R.id.spinner_text,list);
                spinner_hwh.setAdapter(adapter);
                spinner_hwh.setPrompt("小货位号");
            }
        });

    }
    public void confirmrk(String wl,String hwh,String num,String user,BK_info_mh bk_info_mh){
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(CaoRuiActivityRk.this).show();
        Log.d("入库调用接口开始时间",bk_info_mh.getPz() + "" + bk_info_mh.getGg() +"" + bk_info_mh.getXs() + "" + user + "" + "" + hwh + "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/app/fmbkrk")
                ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        Log.d("入库调用接口结束时间",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(CaoRuiActivityRk.this).dismiss();
                final String message = e.getMessage();
                CaoRuiActivityRk.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CaoRuiActivityRk.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                btn_bkrk_rk.setEnabled(true);
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(CaoRuiActivityRk.this).dismiss();
                final String box_res = response.body().string();
                //Log.d("查询半库货位数据返回",box_res);
                try{
                    JSONObject box_res_json = new JSONObject(box_res);
                    String code = box_res_json.getString("flag").toString();
                    final String msg =box_res_json.getString("msg").toString();
                    if(!code.equals("1")){
                        CaoRuiActivityRk.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CaoRuiActivityRk.this,"执行入库失败"+msg, Toast.LENGTH_LONG).show();
                            }
                        });
                        btn_bkrk_rk.setEnabled(true);
                        return;
                    }
                    CaoRuiActivityRk.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityRk.this,"执行入库成功"+msg, Toast.LENGTH_LONG).show();
                            CaoRuiActivityRk.this.finish();
                        }
                    });
                }catch (Exception e){
                    Log.e("解析查询箱数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    CaoRuiActivityRk.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CaoRuiActivityRk.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    btn_bkrk_rk.setEnabled(true);
                    return;
                }
            }
        });

    }

}
