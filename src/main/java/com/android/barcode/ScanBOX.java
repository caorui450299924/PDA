package com.android.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.android.barcode.Post.post_com;

public class ScanBOX extends Activity {
    ArrayList<HashMap<String, Object>> box_list_database;
    AlertDialog builder=null;
    EditText edit_boxscan;
    TextView tv_supportcode;
    ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {

                String action = intent.getAction();
                if (action.equals(RECE_DATA_ACTION)) {
                    String data = intent.getStringExtra("se4500");
                    Log.d("PDA扫码","扫描箱码返回"+data);
                    ///if(data.indexOf("\n")>0){
                        if(data.indexOf("BOX")<0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ScanBOX.this,"扫码内容不是箱码",Toast.LENGTH_SHORT).show();
                                }
                            });

                            return;
                        }
                        ///查询数据库物料编码
                        String box = Getcode.getboxcode(data);
                        edit_boxscan.setText(box);
                        for(int i=0;i<box_list_database.size();i++){
                            if(box_list_database.get(i).get("BOX_CODE").toString().equals(box)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ScanBOX.this,"扫码箱已存在",Toast.LENGTH_SHORT).show();
                                    }
                                });

                                return;
                            }
                        }
                        ListMap.setboxcode(box);
                        ///设置显示框内容
                        ///Log.d("扫描内容",box);
                        ///调转到扫件界面
                        Intent intent1 = new Intent(ScanBOX.this,Createbox.class);
                        intent1.putExtra("box_code",box);
                        startActivity(intent1);
                    ///}

                }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanbox);
        ///box_list_database = desplay(ListMap.getsupportid());
        ///显示数据库中该托中的箱
        ///box_list_database = desplay(ListMap.getsupportid());///onResume调用
        ///输入框获取扫码内容
        Intent intent = getIntent();
        String suppoort = intent.getStringExtra("support_code");

        edit_boxscan =(EditText)findViewById(R.id.edit_boxscan_box);
        edit_boxscan.setEnabled(false);
        tv_supportcode = (TextView)findViewById(R.id.tv_suppoert_dis);
        tv_supportcode.setText("托号："+suppoort);




    }

    @Override
    protected void onResume() {
        IntentFilter iFilter = new IntentFilter();
        //注册系统广播  接受扫描到的数据
        iFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver, iFilter);
        super.onResume();
        box_list_database = desplay(ListMap.getsupportid());
    }
    ///刷新显示数量
    public void refresh(int sl){
        TextView textView = (TextView)findViewById(R.id.textView4_box);
        textView.setText(String.valueOf(sl));
    }
    ///显示数据库中的箱
    public ArrayList<HashMap<String, Object>> desplay(int support_id){
        ///查询托下的箱数据
        ///请求
        ///请求json
        JSONObject support_json = new JSONObject();
        JSONObject box = new JSONObject();
        listItem.clear();
        try {
            box.put("SUPPORT_ID",support_id);
            support_json.put("box",box);
        } catch (JSONException e) {
            e.printStackTrace();
            final String mes=e.getMessage();
            listItem.clear();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ScanBOX.this,"生成请求json失败"+mes,Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }
        ///Okhttp
        LoadingDialog.getInstance(ScanBOX.this).show();

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),support_json.toString());
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxT")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp请求失败",e.getMessage());
                LoadingDialog.getInstance(ScanBOX.this).dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanBOX.this,"请求失败",Toast.LENGTH_SHORT).show();
                    }
                });

                listItem.clear();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String box_res = response.body().string();
                Log.d("okhttp请求成功",box_res);
                LoadingDialog.getInstance(ScanBOX.this).dismiss();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Log.d("查询箱码返回",box_res);
                        try {
                            JSONObject box_res_json = new JSONObject(box_res);
                            JSONObject d = new JSONObject(box_res_json.get("d").toString());
                            String code = d.getString("Code").toString();
                            if(!code.equals("200")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ScanBOX.this,"查询数据失败",Toast.LENGTH_LONG).show();
                                    }
                                });

                                listItem.clear();

                            }
                            JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
                            ///显示listview
                            ListView box_list = (ListView)findViewById(R.id.listview_box_box);
                            ///ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
                            for(int i= 0;i< box_Jsons.length();i++){
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("BOX_CODE",box_Jsons.getJSONObject(i).get("BOX_CODE").toString());
                                map.put("BOX_ID",box_Jsons.getJSONObject(i).get("BOX_ID").toString());
                                ///if(i==box_Jsons.length()-1){
                                listItem.add(map);
                                ///}
                            }
                            refresh(listItem.size());
                            final ArrayList<HashMap<String, Object>>  listItem_temp = listItem;
                            SimpleAdapter listItemAdapter =new SimpleAdapter(ScanBOX.this,listItem,
                                    R.layout.item_scan_box,new String[] {"BOX_CODE"},
                                    new int[] {R.id.tv_scan_mph});
                            box_list.setAdapter(listItemAdapter);
                            box_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(ScanBOX.this,BoxDetail.class);
                                    intent.putExtra("box_code",listItem_temp.get(i).get("BOX_CODE").toString());
                                    intent.putExtra("box_id",listItem_temp.get(i).get("BOX_ID").toString());
                                    startActivity(intent);
                                }
                            });
                        }
                        catch(JSONException e) {
                            Log.e("使用Json解析创建托结果失败",e.getMessage());
                            final String mess = e.getMessage();
                            listItem.clear();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ScanBOX.this,"查询数据失败"+mess,Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                });
            }
        });
        return listItem;

    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            builder = new AlertDialog.Builder(ScanBOX.this)
                    .setTitle("温馨提示：")
                    .setMessage("您是否要退出扫描箱码？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    ScanBOX.this.finish();
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

    @Override
    protected void onDestroy() {
        ///unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        listItem.clear();///20201027
        super.onStop();
    }
}
