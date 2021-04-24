package com.android.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.barcode.ListMap.intFlag;
import static com.android.barcode.Post.post_com;

public class BoxDetail extends Activity {
    Button button_box_delete ;
    AlertDialog builder=null;
    String box_id;
    String box_code;
    List<Wl_info_detail> wl_detail_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boxdetail);
        try {
            Intent intent = getIntent();
            box_code = intent.getStringExtra("box_code");
            box_id = intent.getStringExtra("box_id");
            TextView textView =(TextView)findViewById(R.id.textView7);
            textView.setText(box_code);
            ///查询箱中的产品
            JSONObject box_request = new JSONObject();
            JSONObject box_request_in = new JSONObject();
            box_request_in.put("BOX_ID",Integer.parseInt(box_id));
            box_request.put("product",box_request_in);
            Setpost.setmainworkpost();
            String box_res = post_com(box_request,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryProductT");
            JSONObject box_res_json = null;
            box_res_json = new JSONObject(box_res);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText(BoxDetail.this,"查询数据失败",Toast.LENGTH_LONG).show();
                return;
            }
            JSONArray box_Jsons = new JSONArray(d.get("Data").toString());

            for(int i=0;i<box_Jsons.length();i++){
                Wl_info_detail wl_detail = new Wl_info_detail(box_Jsons.getJSONObject(i).get("MATERIAL_CODE").toString(),box_Jsons.getJSONObject(i).get("PRODUCT_CODE").toString());
                wl_detail_list.add(wl_detail);
            }
            ///recyclerView显示数据
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rlv_box_detail);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            BoxDetailAdapter adapter  =new BoxDetailAdapter(wl_detail_list);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration());
            ///按钮点击
            button_box_delete = (Button)findViewById(R.id.button_delete_box);
            if(ListMap.getroleid().equals("2")){
                if(!intFlag.equals("Y")){
                    button_box_delete.setEnabled(false);
                }
            }
            button_box_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder = new AlertDialog.Builder(BoxDetail.this)
                            .setTitle("温馨提示：")
                            .setMessage("您是否要拆箱？")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            BoxDetail.this.delebox();
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
            });
        } catch (Exception e) {
            Log.e("打开箱明细失败",e.getMessage());
            e.printStackTrace();
        }

    }
    public void delebox(){
        try{
            for(int i=0;i<wl_detail_list.size();i++){
                if(wl_detail_list.get(i).getMph().indexOf("CS")<0&&ListMap.getroleid().equals("2")){
                    Toast.makeText(BoxDetail.this,"不是初始数据不能拆箱",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            JSONObject req_box_delete = new JSONObject();
            req_box_delete.put("boxId",box_id);
            req_box_delete.put("user",ListMap.getuserid());
            String box_res = post_com(req_box_delete,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/DeleteBox");
            JSONObject box_res_json = new JSONObject(box_res);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText(BoxDetail.this,"查询数据失败",Toast.LENGTH_LONG).show();
                return;
            }
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
