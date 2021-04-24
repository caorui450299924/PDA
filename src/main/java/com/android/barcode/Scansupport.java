package com.android.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.barcode.Post;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.android.barcode.Post.*;

public class Scansupport extends Activity {
    String supportcode1;
    TextView support_code;
    RadioButton radioButton;
    RadioButton radioButton2;
    Button button_confirm;
    ///TextView textView2 ;
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            try {

                String action = intent.getAction();
                if (action.equals(RECE_DATA_ACTION)) {
                    final String data = intent.getStringExtra("se4500");
                    Log.d("PDA扫码","装托扫码返回"+data);
                    if(!support_code.getText().toString().isEmpty()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Scansupport.this,"切换托号请重新进入车间装托程序",Toast.LENGTH_LONG).show();
                            }
                        });

                        return;
                    }
                    if(data.indexOf("TRSUT")<0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Scansupport.this,"扫码内容不是托码",Toast.LENGTH_LONG).show();
                            }
                        });

                        return;
                    }
                    if(radioButton.isChecked()==false&&radioButton2.isChecked()==false){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Scansupport.this,"请选择整托或散托",Toast.LENGTH_LONG).show();
                            }
                        });

                        return;
                    }
                    ///设置显示框内容
                    Log.d("PDA扫描托内容",Getcode.getsupportcode(data));
                    ///请求接口将托码保存到数据库
                    JSONObject req_support_insert = new JSONObject();
                    JSONObject support = new JSONObject();
                    req_support_insert.put("CREATEUSER",ListMap.getuserid());
                    req_support_insert.put("SUPPORT_CODE",Getcode.getsupportcode(data));
                    req_support_insert.put("SUPPORT_STATE",0);
                    if(radioButton.isChecked()==true){
                        req_support_insert.put("ALL_STATE",1);
                    }else {
                        req_support_insert.put("ALL_STATE",0);
                    }
                    ///req_support_insert.put("ALL_STATE",0);
                    req_support_insert.put("Pdacj","pda");
                    support.put("support",req_support_insert);
                    ///Okhttp
                    ///LoadingDialog.getInstance(Scansupport.this).show();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),support.toString());
                    final Request request = new Request.Builder()
                            .url("http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/CreateNewSupport_code_pda")
                            .post(requestBody)
                            .build();
                    Call call = client.newCall(request);

                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("okhttp请求失败",e.getMessage());
                            final String mes9=e.getMessage();

                            ///LoadingDialog.getInstance(Scansupport.this).dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Scansupport.this,"okhttp请求失败"+mes9,Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String reslut_getsupnum = response.body().string();
                            ///LoadingDialog.getInstance(Scansupport.this).dismiss();
                            Log.d("okhttp请求成功",reslut_getsupnum);
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    try{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Scansupport.this,"登录成功",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        Log.d("保存托码返回",reslut_getsupnum);
                                        JSONObject box_res_json = new JSONObject(reslut_getsupnum);
                                        JSONObject d = new JSONObject(box_res_json.get("d").toString());
                                        String code = d.getString("Code").toString();
                                        if(!code.equals("200")){
                                            final String mes4=d.getString("Message").toString();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(Scansupport.this,"保存托码失败"+mes4,Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            return;
                                        }
                                        JSONObject d1 = new JSONObject(d.get("Data").toString());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Scansupport.this,"保存托码成功",Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        support_code.setText(Getcode.getsupportcode(data));
                                        ListMap.setSupportcode(d1.getString("SUPPORT_CODE").toString());
                                        ListMap.setsupportid(d1.getInt("SUPPORT_ID"));
                                    }catch (Exception e){
                                        final String mes2=e.getMessage();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Scansupport.this,"保存托码失败"+mes2,Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        return;
                                    }
                                }
                            });
                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
                final String mes3=e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Scansupport.this,"扫描托码出错"+mes3,Toast.LENGTH_LONG).show();
                    }
                });

                return;
            }
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scansupport_layout);
        Log.d("装托","onCreate");
        Button btn_getsupportnum = (Button)findViewById(R.id.btn_getsupportnum);
        Button btn_delete_support = (Button)findViewById(R.id.button_deletesupport);
        button_confirm = (Button)findViewById(R.id.button_confirm);
        if(ListMap.getroleid().equals("2")){
            btn_delete_support.setEnabled(false);
            button_confirm.setEnabled(false);
        }
        support_code = (TextView)findViewById(R.id.textView2);
        btn_getsupportnum.setEnabled(false);
        ///拆托
        btn_delete_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportcode1 = support_code.getText().toString();

                if(supportcode1.isEmpty()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Scansupport.this,"托号为空不能拆托",Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }
                if(ListMap.getSupportcode().isEmpty()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Scansupport.this,"托号为空不能拆托",Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }

                deletesupport();

            }
        });
        ///确认入库

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });
        ///判断调用
        Intent intent = getIntent();
        String zt = intent.getStringExtra("zt");
        if(!zt.isEmpty()&&zt.equals("cx")){
            Button createtuo = (Button)findViewById(R.id.btn_getsupportnum);
            TextView textViewtuo = (TextView)findViewById(R.id.textView2);
            int supportid = Integer.parseInt(intent.getStringExtra("supportid"));
            String supportcode = intent.getStringExtra("supportcode");
            textViewtuo.setText(supportcode);
            ListMap.setSupportcode(supportcode);
            ListMap.setsupportid(supportid);
            btn_getsupportnum.setEnabled(false);

        }else if(!zt.isEmpty()&&zt.equals("zt")){
            ListMap.setsupportid(0);
            ListMap.setSupportcode("");
        }

        ///防止主线程请求报错
        Setpost.setmainworkpost();
        ///生成托码
        btn_getsupportnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///生成托码在界面显示
                TextView textView2 = (TextView)findViewById(R.id.textView2);
                if(!textView2.getText().toString().isEmpty()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Scansupport.this,"当前已创建托，不能再次创建",Toast.LENGTH_LONG).show();
                        }
                    });

                    return;
                }

                JSONObject support = new JSONObject();
                JSONObject support_json = new JSONObject();
                try {
                    support.put("CREATEUSER",Login.user_id);
                    support.put("ALL_STATE","0");
                    support_json.put("support",support);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ///请求
                String reslut_getsupnum = post_com(support_json,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/CreateNewSupport_pda");
                Log.d("请求创建托返回结果",reslut_getsupnum);
                if(reslut_getsupnum.indexOf("调用公用http失败")<0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Scansupport.this,"创建托成功",Toast.LENGTH_SHORT).show();
                        }
                    });

                    try {
                        JSONObject get_supportnum = new JSONObject(reslut_getsupnum);
                        JSONObject d = new JSONObject(get_supportnum.get("d").toString());
                        JSONObject d1 = new JSONObject(d.get("Data").toString());
                        ///修改textviw内容

                        textView2.setText(d1.getString("SUPPORT_CODE").toString());
                        ListMap.setSupportcode(d1.getString("SUPPORT_CODE").toString());
                        ListMap.setsupportid(d1.getInt("SUPPORT_ID"));

                        /*
                        textView2.setText("TRSUT2020100901465");
                        ListMap.setSupportcode("TRSUT2020100901465");
                        ListMap.setsupportid(1468);
                        */
                        ///设置创建托按钮不可用
                        Button btn_getsupportnum = (Button)findViewById(R.id.btn_getsupportnum);
                        btn_getsupportnum.setEnabled(false);

                    }
                    catch(JSONException e) {
                        Log.e("使用Json解析创建托结果失败",e.getMessage());
                    }
                }else{
                    final String mes5=reslut_getsupnum;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Scansupport.this,mes5,Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }
            }
        });
        ///装箱
        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supportcode = support_code.getText().toString();
                if(supportcode.isEmpty()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Scansupport.this,"没有托码不能装箱",Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }
                ///Intent intent = new Intent(Scansupport.this,Createbox.class);
                Intent intent = new Intent(Scansupport.this,ScanBOX.class);
                intent.putExtra("support_code",support_code.getText().toString());
                startActivity(intent);
            }
        });
        radioButton = (RadioButton)findViewById(R.id.radioButton);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);

    }
    ///拆托
    public boolean deletesupport(){
        try {
            JSONObject req_delete_support = new JSONObject();
            req_delete_support.put("supportId",ListMap.getsupportid());
            req_delete_support.put("user",ListMap.getuserid());
            String create_box_res = post_com(req_delete_support,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/DeleteSupport");
            JSONObject box_res_json = new JSONObject(create_box_res);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                final String mes7=d.getString("Message").toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Scansupport.this,"拆托失败"+mes7,Toast.LENGTH_LONG).show();
                    }
                });

                return false;
            }
            ListMap.setSupportcode(null);
            ListMap.setsupportid(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Scansupport.this,"提交成功",Toast.LENGTH_LONG).show();
                }
            });

            finish();
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }
    ///确认入库
    public boolean confirm(){
        try {
            JSONObject req_delete_support = new JSONObject();
            req_delete_support.put("wareHouseUser",ListMap.getuserid());
            req_delete_support.put("supportId",ListMap.getsupportid());
            String create_box_res = post_com(req_delete_support,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/Warehousing");
            JSONObject box_res_json = new JSONObject(create_box_res);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                final String mes8=d.getString("Message").toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Scansupport.this,"确认入库失败"+mes8,Toast.LENGTH_LONG).show();
                    }
                });

                return false;
            }
            ListMap.setSupportcode(null);
            ListMap.setsupportid(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Scansupport.this,"确认入库成功",Toast.LENGTH_LONG).show();
                }
            });

            finish();
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    @Override
    protected void onDestroy() {
        ///unregisterReceiver(receiver);
        ///ListMap.setsupportid(0);
        ///ListMap.setSupportcode("");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        Log.d("装托","onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d("装托","onResume");
        IntentFilter iFilter = new IntentFilter();
        //注册系统广播  接受扫描到的数据
        iFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver, iFilter);
        super.onResume();
    }
}
