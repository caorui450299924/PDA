package com.android.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.barcode.Post.post_com_cookie;

public class DeliveryBhMx extends Activity {
    List<Bh_Mx_info> bh_list = new ArrayList<>();
    RecyclerView recyclerView;
    Button bh ;
    String ids;
    AlertDialog builder=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverybhmx);
        try {
            ///LoadingDialog.getInstance(DeliveryBhMx.this).show();
            Setpost.setmainworkpost();
            Intent intent = getIntent();
            ids = intent.getStringExtra("ids");
            JSONObject req_bh_mx = new JSONObject();
            JSONObject paramobj =new JSONObject();
            paramobj.put("ids",ids);
            req_bh_mx.put("namingid","com.dwb.reporter.queryfmfc.get_delievry_hwhmx");
            req_bh_mx.put("paramobj",paramobj);
            String res_bh = post_com_cookie(req_bh_mx,"https://www.vapp.meide-casting.com/ims/com.dwb.reporter.queryfmfc.querybh.biz.ext",ListMap.getsessionId());
            ///LoadingDialog.getInstance(DeliveryBhMx.this).dismiss();
            JSONObject res_json = new JSONObject(res_bh);
            JSONArray objs = res_json.getJSONArray("objs");
            Bh_Mx_info bh_info1= new Bh_Mx_info("                     物料","货位号","数量");
            bh_list.add(bh_info1);
            for(int i=0;i<objs.length();i++){
                Bh_Mx_info bh_info = new Bh_Mx_info(objs.getJSONObject(i).getString("MATERIAL_CODE"),objs.getJSONObject(i).getString("LOCATION_CODE"),objs.getJSONObject(i).getString("EXTIMATE_QTY"));
                bh_list.add(bh_info);
            }
            recyclerView = (RecyclerView)findViewById(R.id.recycle_bhmx);
            LinearLayoutManager layoutManager = new LinearLayoutManager(DeliveryBhMx.this);
            recyclerView.setLayoutManager(layoutManager);
            DeliveryBhMxAdapter adapter  =new DeliveryBhMxAdapter(bh_list,DeliveryBhMx.this,DeliveryBhMx.this);
            recyclerView.setAdapter(adapter);
            ///recyclerView.addItemDecoration(new SpacesItemDecoration());
            bh = (Button)findViewById(R.id.btn_bh_mx);
            bh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONObject req_bhmx = new JSONObject();
                        req_bhmx.put("ids",ids);
                        req_bhmx.put("type","BH");
                        String res_bh = post_com_cookie(req_bhmx,"https://www.vapp.meide-casting.com/ims/com.dwb.reporter.queryfmfc.updatebh.biz.ext",ListMap.getsessionId());
                        JSONObject res_json = new JSONObject(res_bh);
                        if(res_json.getString("X_FLAG").equals("1")){
                            Toast.makeText(DeliveryBhMx.this,"备货成功",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(DeliveryBhMx.this,"备货失败："+res_json.getString("X_MSG"),Toast.LENGTH_LONG).show();
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            builder = new AlertDialog.Builder(DeliveryBhMx.this)
                    .setTitle("温馨提示：")
                    .setMessage("您是否要退出程序？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    DeliveryBhMx.this.finish();
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
}
