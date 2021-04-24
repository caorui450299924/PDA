package com.android.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.android.barcode.Post.post_com;

public class LoadingSupport extends Activity {
    //接受广播
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private String START_SCAN_ACTION = "com.geomobile.se4500barcode";
    private IntentFilter intentFilter;
    private TextView dis_box;
    private Button scan ;
    private RecyclerView rev_support;
    private RecyclerView recyclerView;
    private TextView textView_load_dis;
    private RadioButton radioButton;
    private RadioButton radioButton2;
    AlertDialog builder=null;
    List<Box_info> support_list = new ArrayList<>();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                String supportcode = textView_load_dis.getText().toString();
                if(supportcode.isEmpty()){///托号为空没提交
                    if(data.indexOf("TRSUT")>=0){
                        int sl = searchsupport(Getcode.getsupportcode(data));
                        Log.d("查询箱数量返回","返回数量"+String.valueOf(sl));
                        if(sl==0){
                            savesupport(Getcode.getsupportcode(data));
                        }

                    }else{
                        Toast.makeText(LoadingSupport.this,"请先扫描新托码",Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    if(data.indexOf("BOX")<0){
                        Toast.makeText(LoadingSupport.this,"请扫描箱码",Toast.LENGTH_LONG).show();
                        return;
                    }
                    addbox(Getcode.getboxcode(data));
                }

            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingsupport);
        recyclerView = (RecyclerView)findViewById(R.id.rev_load);
        textView_load_dis = (TextView) findViewById(R.id.textView_load_dis);
        textView_load_dis.setText(ListMap.getspcode());
        radioButton = (RadioButton)findViewById(R.id.Button_load1);
        radioButton2 = (RadioButton)findViewById(R.id.Button_load2);
        ///显示List_mao数据
        resume_display();
        ///定义广播接收数据
        scan =(Button)findViewById(R.id.btn_support_confirm);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changesupport();
            }
        });


    }
    private void startScan() {
        SystemProperties.set("persist.sys.scanstopimme","false");
        Intent intent = new Intent();
        intent.setAction(START_SCAN_ACTION);
        sendBroadcast(intent, null);
    }
    public void resume_display(){
        ///二次打开显示对象中的数据
        if(ListMap.getbox_listconut()>0){
            LinearLayoutManager layoutManager = new LinearLayoutManager(LoadingSupport.this);
            recyclerView.setLayoutManager(layoutManager);
            LoadingSupportAdapter adapter  =new LoadingSupportAdapter(ListMap.getbox_list(),LoadingSupport.this,LoadingSupport.this);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration());
        }
    }
    public void addbox(String boxcode){
        try {
            JSONObject req_box_id = new JSONObject();
            JSONObject boxInfo = new JSONObject();
            String box_code = boxcode;
            for(int i =0;i<ListMap.getbox_listconut();i++){
                if(ListMap.getbox_list().get(i).getBox_code().equals(box_code)){
                    Toast.makeText(LoadingSupport.this,"当前箱："+boxcode+"已扫描",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            req_box_id.put("BOX_CODE",box_code);
            boxInfo.put("boxInfo",req_box_id);
            String reslut_box = post_com(boxInfo,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxInfo");
            String res = reslut_box;
            JSONObject box_res_json = new JSONObject(reslut_box);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText(LoadingSupport.this,"查询数据失败："+d.getString("Message").toString(),Toast.LENGTH_LONG).show();
                return;
            }
            JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
            ///显示listview

            if(box_Jsons.length()>1){
                Toast.makeText(LoadingSupport.this,"查询箱返回多条数据",Toast.LENGTH_LONG).show();
                return;
            }
            int SUPPORT_STATE = Integer.parseInt(box_Jsons.getJSONObject(0).get("SUPPORT_STATE").toString());
            String SUPPORT_CODE = box_Jsons.getJSONObject(0).get("SUPPORT_CODE").toString();
            if(SUPPORT_STATE>=3){
                Toast.makeText(LoadingSupport.this,"该箱已发货，请核实",Toast.LENGTH_LONG).show();
                return;
            }
            for(int i= 0;i< box_Jsons.length();i++){
                Box_info box_info = new Box_info(box_Jsons.getJSONObject(i).get("BOX_ID").toString(),box_code,R.drawable.delete);
                support_list.add(box_info);
                ListMap.addbox_list(box_info);
            }

            if(!SUPPORT_CODE.isEmpty()){
                builder = new AlertDialog.Builder(LoadingSupport.this)
                        .setTitle("该箱已经成托")
                        .setMessage("确认换托吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ///显示数据
                                List<Box_info> support_list_temp = new ArrayList<>();
                                support_list_temp=ListMap.getbox_list();

                                LinearLayoutManager layoutManager = new LinearLayoutManager(LoadingSupport.this);
                                recyclerView.setLayoutManager(layoutManager);
                                LoadingSupportAdapter adapter  =new LoadingSupportAdapter(support_list_temp,LoadingSupport.this,LoadingSupport.this);
                                recyclerView.setAdapter(adapter);
                                recyclerView.addItemDecoration(new SpacesItemDecoration());

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                builder.dismiss();
                            }
                        })
                        .show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void savesupport(String supportcode){
        if(supportcode.indexOf("TRSUT")<0){
            Toast.makeText(LoadingSupport.this,"扫码内容不是托码",Toast.LENGTH_LONG).show();
            return;
        }
        if(radioButton.isChecked()==false&&radioButton2.isChecked()==false){
            Toast.makeText(LoadingSupport.this,"请选择整托或散托",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            ///请求接口将托码保存到数据库
            JSONObject req_support_insert = new JSONObject();
            JSONObject support = new JSONObject();
            req_support_insert.put("CREATEUSER",ListMap.getuserid());
            req_support_insert.put("SUPPORT_CODE",supportcode);
            req_support_insert.put("SUPPORT_STATE",0);
            if(radioButton.isChecked()==true){
                req_support_insert.put("ALL_STATE",1);
            }else {
                req_support_insert.put("ALL_STATE",0);
            }
            ///req_support_insert.put("ALL_STATE",0);
            req_support_insert.put("Pdacj","pda");
            support.put("support",req_support_insert);

            String reslut_getsupnum = post_com(support,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/CreateNewSupport_code_pda");
            Log.d("保存托码返回",reslut_getsupnum);
            JSONObject box_res_json = new JSONObject(reslut_getsupnum);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText(LoadingSupport.this,"保存托码失败"+d.getString("Message").toString(),Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject d1 = new JSONObject(d.get("Data").toString());
            Toast.makeText(LoadingSupport.this,"保存托码成功",Toast.LENGTH_LONG).show();
            textView_load_dis.setText(supportcode);
            ListMap.setspcode(d1.getString("SUPPORT_CODE").toString());
            ListMap.setspid(d1.getInt("SUPPORT_ID"));

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(LoadingSupport.this,"扫描托码出错"+e.getMessage(),Toast.LENGTH_LONG).show();
            return;
        }



    }
    public int searchsupport(String supportcode){
        ///查询托信息
        try {
            JSONObject req_supportreport = new JSONObject();
            JSONObject supportInfo = new JSONObject();
            supportInfo.put("SUPPORT_CODE",supportcode);
            supportInfo.put("SelectState",1);
            ///supportInfo.put("CREATEUSER",ListMap.getuserid());
            supportInfo.put("START_TIME","");
            supportInfo.put("END_TIME","");
            req_supportreport.put("supportInfo",supportInfo);
            String box_res = post_com(req_supportreport,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QuerySupportInfo");
            Log.d("查询托数据返回",box_res);
            JSONObject box_res_json = null;
            box_res_json = new JSONObject(box_res);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText(LoadingSupport.this,"查询托数据失败",Toast.LENGTH_LONG).show();
                return -1;
            }
            JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
            if(box_Jsons.length()>1){
                Toast.makeText(LoadingSupport.this,"查询托返回多条数据",Toast.LENGTH_LONG).show();
                return -1;
            }
            if(box_Jsons.length()==1){
                ListMap.setspcode(box_Jsons.getJSONObject(0).get("SUPPORT_CODE").toString());
                textView_load_dis.setText(supportcode);
                ListMap.setspid(Integer.parseInt(box_Jsons.getJSONObject(0).get("SUPPORT_ID").toString()));

            }
            return box_Jsons.length();

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    public void changesupport(){
        try {
            if(ListMap.getbox_listconut()<=0){
                Toast.makeText(LoadingSupport.this,"没有箱数据，不能提交",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject support = new JSONObject();
            JSONArray req_jsons = new JSONArray();
            ///JSONObject req_json = new JSONObject();
            JSONObject req_boxs = new JSONObject();
            for(int i=0;i<ListMap.getbox_list().size();i++){
                ///req_json.put("BOX_ID",ListMap.getbox_list().get(i).getBox_id());
                ///req_json.put("BOX_CODE",ListMap.getbox_list().get(i).getBox_code());
                req_jsons.put(ListMap.getbox_list().get(i).getBox_id());
            }
            req_boxs.put("boxId",req_jsons);
            req_boxs.put("supportId",ListMap.getspid());
            String create_box_res = post_com(req_boxs,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/ChengeSupport");
            JSONObject box_res_json = new JSONObject(create_box_res);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText(LoadingSupport.this,"查询数据失败"+d.getString("Message").toString(),Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(LoadingSupport.this,"提交成功",Toast.LENGTH_LONG).show();
            ListMap.clearbox_list();
            ListMap.setspid(0);
            ListMap.setspcode("");
            finish();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        ListMap.setspid(0);
        ListMap.setspcode("");
        ///unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        try {
            intentFilter = new IntentFilter();
            intentFilter.addAction(RECE_DATA_ACTION);
            registerReceiver(receiver,intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }
}
