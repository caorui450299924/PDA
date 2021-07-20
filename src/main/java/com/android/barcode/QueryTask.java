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

public class QueryTask extends AppCompatActivityBase {
    EditText edt_wl;
    EditText edt_sl;
    EditText edt_tmh;
    Button qr;
    String lry;
    String zcdw;
    String tmh1;
    String kczt;
    RecyclerView recyclerView;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(QueryTask.super.RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                ///设置显示框内容
                Log.d("扫描箱码内容",data);
                String tmh = data;
                gettmh(tmh);
            }
        }
    };

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fmtask);
        edt_tmh = (EditText)findViewById(R.id.fmtask_tmh);
        edt_wl = (EditText) findViewById(R.id.fmtask_code);
        edt_sl = (EditText)findViewById(R.id.fmtask_js);
        qr = (Button) findViewById(R.id.fmtask_qr);
        edt_wl.setFocusable(false);
        edt_sl.setFocusableInTouchMode(false);
        intBtn_qr();
    }


    public void onResume(){
        super.receiver = receiver;
        super.onResume();
    }



    public void intBtn_qr(){
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_sl.getText().toString().equals("") || edt_wl.getText().toString().equals("")  ){
                    QueryTask.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("aaa","!!!");
                            Toast.makeText(QueryTask.this,"请先扫描条码号", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                confirmrk();
            }
        });
    }

    public void gettmh(final String tmh){
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("tmh",tmh);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(QueryTask.this).show();
        final Request request = new Request.Builder()
                .url("https://www.vapp.meide-casting.com/app/fmbktask")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(QueryTask.this).dismiss();
                final  String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryTask.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(QueryTask.this).dismiss();


                List<String> list1 = new ArrayList<String>();
                final String box_res1 = response.body().string();
                Log.d("123456",box_res1);
                try {
                    JSONObject box_res_json = new JSONObject(box_res1);
                    String code = box_res_json.getString("flag").toString();
                    final String msg = box_res_json.getString("msg").toString();
                    if (!code.equals("1")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QueryTask.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    final JSONArray box_Jsons = new JSONArray(box_res_json.get("data").toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                edt_wl.setText(box_Jsons.getJSONObject(0).get("code").toString());
                                edt_sl.setText(box_Jsons.getJSONObject(0).get("js").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    lry = box_Jsons.getJSONObject(0).get("lry").toString();
                    zcdw = box_Jsons.getJSONObject(0).get("zcdw").toString();
                    kczt = box_Jsons.getJSONObject(0).get("kczt").toString();
                    tmh1 = tmh;
                }catch (Exception e){
                    Log.e("解析查询数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QueryTask.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });
    }
    public void clear(){
        edt_tmh.setText("");
        edt_sl.setText("");
        edt_wl.setText("");
    }
    public void confirmrk(){
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("lry",lry);
            req_supportreport.put("zcdw",zcdw);
            req_supportreport.put("tmh",tmh1);
            req_supportreport.put("kczt",kczt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(QueryTask.this).show();
        final Request request = new Request.Builder()
                .url("https://www.vapp.meide-casting.com/app/fmbktaskinsert")
                ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(QueryTask.this).dismiss();
                final String message = e.getMessage();
                QueryTask.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryTask.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(QueryTask.this).dismiss();
                final String box_res = response.body().string();
                //Log.d("查询半库货位数据返回",box_res);
                try{
                    JSONObject box_res_json = new JSONObject(box_res);
                    String code = box_res_json.getString("flag").toString();
                    final String msg =box_res_json.getString("msg").toString();
                    if(!code.equals("1")){
                        QueryTask.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QueryTask.this,"执行失败"+msg, Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    QueryTask.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QueryTask.this,"执行成功"+msg, Toast.LENGTH_LONG).show();
                            clear();
                        }

                    });
                }catch (Exception e){
                    Log.e("解析查询箱数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    QueryTask.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QueryTask.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });

    }

}
