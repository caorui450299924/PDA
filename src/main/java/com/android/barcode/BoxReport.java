package com.android.barcode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.barcode.Post.post_com;

public class BoxReport extends Activity {
    Button searchbox ;
    RecyclerView recycle ;
    EditText edit_boxreport;
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                if(data.indexOf("BOX")<0) {
                    Toast.makeText(BoxReport.this,"扫描二维码不是箱码",Toast.LENGTH_LONG).show();
                    return;
                }
                ///设置显示框内容
                Log.d("扫描箱码内容",data);
                edit_boxreport.setText(Getcode.getboxcode(data));
                display(Getcode.getboxcode(data));
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boxreport);
        searchbox = (Button)findViewById(R.id.button_boxreport);
        recycle = (RecyclerView)findViewById(R.id.recycle_boxreport);
        edit_boxreport = (EditText)findViewById(R.id.editview_boxreport);
        searchbox = (Button)findViewById(R.id.button_boxreport);
        edit_boxreport.setEnabled(false);
        searchbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display("");
            }
        });
        if(ListMap.getuserid().equals("101936")){
            ListMap.intFlag="Y";
        }

    }
    public void display(String box_code){
        ///final String supportcode = box_code;
        ///查询托信息
        try {
            Setpost.setmainworkpost();
            JSONObject req_supportreport = new JSONObject();
            JSONObject supportInfo = new JSONObject();
            supportInfo.put("BOX_CODE",box_code);
            if(ListMap.getroleid().equals("2")){
                supportInfo.put("CREATEUSER","");
            }else{
                supportInfo.put("CREATEUSER",ListMap.getuserid());
            }
            ///supportInfo.put("CREATEUSER",ListMap.getuserid());
            supportInfo.put("END_TIME","");
            supportInfo.put("START_TIME","");
            supportInfo.put("SelectState",0);
            req_supportreport.put("boxInfo",supportInfo);
            String box_res = post_com(req_supportreport,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxInfo");
            Log.d("查询箱数据返回",box_res);
            JSONObject box_res_json = null;
            box_res_json = new JSONObject(box_res);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText(BoxReport.this,"查询箱数据失败",Toast.LENGTH_LONG).show();
                return;
            }
            JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
            List<Box_Report_info> wl_detail_list = new ArrayList<>();
            for(int i=0;i<box_Jsons.length();i++){
                String boxid = box_Jsons.getJSONObject(i).get("BOX_ID").toString();
                String boxcode = box_Jsons.getJSONObject(i).get("BOX_CODE").toString();
                int js = Integer.parseInt(box_Jsons.getJSONObject(i).get("COUNT").toString());
                String cjsj = box_Jsons.getJSONObject(i).get("CREATETIME").toString();
                String suppoortcode = box_Jsons.getJSONObject(i).get("SUPPORT_CODE").toString();
                String supportstate = box_Jsons.getJSONObject(i).get("SUPPORT_STATE_DESC").toString();
                Box_Report_info wl_detail = new Box_Report_info(boxid,boxcode,suppoortcode,supportstate,js,cjsj);
                wl_detail_list.add(wl_detail);
            }
            ///recyclerView显示数据
            LinearLayoutManager layoutManager = new LinearLayoutManager(BoxReport.this);
            recycle.setLayoutManager(layoutManager);
            BoxReportAdapter adapter  =new BoxReportAdapter(wl_detail_list,BoxReport.this,BoxReport.this);
            recycle.setAdapter(adapter);
            recycle.addItemDecoration(new SpacesItemDecoration());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        ///unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        IntentFilter iFilter = new IntentFilter();
        //注册系统广播  接受扫描到的数据
        iFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver, iFilter);
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        ListMap.intFlag="N";
    }
}
