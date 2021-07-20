package com.android.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Menu extends Activity {
    private ListView Lv = null;
    AlertDialog builder=null;
    List<Map<String, Object>> list_temp = new ArrayList<Map<String, Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        Log.d("进入display","onCreate");
        display();
    }

    ///获取图片ID
    public Map<String, Integer>  getDrawableImage(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        Field[] fields = R.drawable.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                map.put(fields[i].getName(), fields[i].getInt(R.drawable.class));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        fields = null;
        return  map;
    }
    ///退出需要确定
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            builder = new AlertDialog.Builder(Menu.this)
                    .setTitle("温馨提示：")
                    .setMessage("您是否要退出程序？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Menu.this.finish();
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
    protected void onResume() {
        super.onResume();
        ///Log.d("进入display","onResume");
        ///display();
    }

    public void display(){
        Log.d("MENU","进入display");
        ///Map<String, Object> map_temp = new HashMap<String, Object>();
        try {
            LoadingDialog.getInstance(Menu.this).show();
            JSONObject getmenu_json = new JSONObject();
            getmenu_json.put("namingid", "com.dwb.reporter.queryfmfc.get_pda_menu");
            JSONObject getmenu_json_tj = new JSONObject();
            getmenu_json_tj.put("roleid", Login.roleid);
            getmenu_json.put("paramobj", getmenu_json_tj);
            Log.d("请求菜单开始",getmenu_json.toString());
            Log.d("session",Login.sessionId);
            ///请求服务器
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),getmenu_json.toString());
            final Request request = new Request.Builder()
                    .url("https://www.vapp.meide-casting.com/ims/com.dwb.reporter.queryfmfc.querybh.biz.ext")
                    //.url("http://221.214.27.228/ims/com.dwb.reporter.queryfmfc.querybh.biz.ext")
                    //.url("https://vapp.meide-casting.com/ims/com.dwb.reporter.queryfmfc.querybh.biz.ext")
                    //.url("http://10.0.1.31:8081/ims/com.dwb.reporter.queryfmfc.querybh.biz.ext")
                    .post(requestBody)
                    .addHeader("cookie","JSESSIONID="+Login.sessionId)
                    .build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LoadingDialog.getInstance(Menu.this).dismiss();
                    Log.d("okhttp请求失败",e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LoadingDialog.getInstance(Menu.this).dismiss();
                    final String res = response.body().string();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                Log.d("请求菜单结束",res);
                                JSONObject result_json = new JSONObject(res);
                                if(!result_json.isNull("exception")){
                                    ///用户失效，跳转到登录
                                    Toast.makeText(Menu.this,"登录失效，请重新登录",Toast.LENGTH_LONG).show();
                                    finish();
                                    return;
                                }
                                JSONArray menu_jsonarr = new JSONArray(new JSONObject(res).getString("objs"));
                                for(int i=0;i<=menu_jsonarr.length()-1;i++) {
                                    Map<String, Object> map_temp = new HashMap<String, Object>();
                                    map_temp.put("name", menu_jsonarr.getJSONObject(i).getString("MENU"));
                                    map_temp.put("menulist", menu_jsonarr.getJSONObject(i).getString("PAGE"));
                                    map_temp.put("photo", menu_jsonarr.getJSONObject(i).getString("IMG"));
                                    list_temp.add(map_temp);
                                }

                                Lv = (ListView) findViewById(R.id.Lv);
                                final String[] name = new String[list_temp.size()];
                                final String [] menulist = new String[list_temp.size()];
                                final int[] photo = new int[list_temp.size()];
                                Map<String, Integer>  imageid = new HashMap<String, Integer>();
                                imageid = getDrawableImage();
                                for(int i=0;i<=list_temp.size()-1;i++){
                                    name[i]= list_temp.get(i).get("name").toString();
                                    menulist[i]= list_temp.get(i).get("menulist").toString();
                                    ///photo[i] = Integer.parseInt(list_temp.get(i).get("photo").toString());
                                    photo[i] = Integer.parseInt(imageid.get(list_temp.get(i).get("photo").toString()).toString());
                                }
                                ///存储跳转的界面
                                final String[] message = new String[]{"31", "32", "33"};
                                List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

                                for(int i=0;i<=list_temp.size()-1;i++){
                                    Map<String, Object> map1 = new HashMap<String, Object>();
                                    map1.put("photo", photo[i]);
                                    map1.put("name", name[i]);
                                    data.add(map1);
                                }

                                Lv.setAdapter(new SimpleAdapter(Menu.this, data, R.layout.item, new String[]{"photo", "name"}, new int[]{R.id.iv, R.id.tv_name}));
                                Lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                                        Bundle bundle = new Bundle();
                                        Intent intent = new Intent();
                                        intent.putExtras(bundle);
                                        Class temp_class = null;
                                        try {
                                            temp_class = Class.forName(menulist[arg2]);
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        intent.setClass(Menu.this, temp_class);
                                        if(menulist[arg2].equals("com.android.barcode.Scansupport")){
                                            intent.putExtra("zt","zt");
                                            intent.putExtra("supportid","");
                                            intent.putExtra("supportcode","");
                                        }
                                        ///Log.i("message", message[arg2]);
                                        startActivity(intent);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                    });
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
