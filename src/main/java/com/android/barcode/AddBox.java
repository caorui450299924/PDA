package com.android.barcode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class AddBox extends Activity {
    String box_code;
    EditText edt_wl;
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private IntentFilter intentFilter;
    EditText edt_sl;
    Button btn_addbox;
    Button btn_tj;
    RecyclerView recyclerView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                if(data.indexOf("9")>=0){
                    edt_wl.setText(data);
                }else{
                    Toast.makeText(AddBox.this,"请先扫描产品",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbox);
        Intent intent = getIntent();
        box_code = intent.getStringExtra("box_code");
        edt_wl=(EditText)findViewById(R.id.editview_add_box);
        edt_sl=(EditText)findViewById(R.id.editview_add_box_sl);
        btn_addbox=(Button)findViewById(R.id.button_add_box_mx);
        recyclerView=(RecyclerView)findViewById(R.id.recycle_add_box);
        btn_tj=(Button)findViewById(R.id.button_add_box_mx3);
        edt_wl.setEnabled(false);
        ///添加箱
        btn_addbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_wl.getText().toString().isEmpty()){
                    Toast.makeText(AddBox.this,"请先扫描产品",Toast.LENGTH_LONG).show();
                    return;
                }
                if(edt_sl.getText().toString().isEmpty()){
                    Toast.makeText(AddBox.this,"请先输入数量",Toast.LENGTH_LONG).show();
                    return;
                }
                ///添加recycleview

                CP_info cp_info = new CP_info(edt_wl.getText().toString(),"初始",Integer.parseInt(edt_sl.getText().toString()));
                ListMap.addcp_info_list(cp_info);
                LinearLayoutManager layoutManager = new LinearLayoutManager(AddBox.this);
                recyclerView.setLayoutManager(layoutManager);
                AddBoxAdapter adapter  =new AddBoxAdapter(ListMap.getcp_info_list(),AddBox.this,AddBox.this);
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new SpacesItemDecoration());


            }
        });
        ///提交
        btn_tj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ListMap.getcp_info_listsize()==0){
                    Toast.makeText(AddBox.this,"没有数据不能提交",Toast.LENGTH_LONG).show();
                    return;
                }
                ///集合存储本次的随机数
                Vector<Integer> v = new Vector<Integer>();
                ///获取具体时间
                for(int i=0;i<ListMap.getcp_info_listsize();i++){
                    Box_Int_info box_int_info = new Box_Int_info(box_code);
                    for(int m=0;m<ListMap.getcp_info_list().get(i).getsl();m++){
                        ///使用随机数获取铭牌号
                        Random random = new Random();
                        int number = random.nextInt(100)+1;
                        if(!v.contains(number)){
                            //不在集合中，就添加
                            v.add(number);
                        }else{
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        String randomnum = String.valueOf(number);
                        for(int n=randomnum.length()+1;n<=3;n++){
                            randomnum="0"+randomnum;
                        }
                        SimpleDateFormat fmt = new SimpleDateFormat("yyMMddHHmmssSSS"); //精确到毫秒
                        String suffix = fmt.format(new Date());
                        String mphNew = "CS"+suffix+randomnum;
                        ///Products_info products_info = new Products_info("初始",ListMap.getcp_info_list().get(i).getwl());
                        Products_info products_info = new Products_info(mphNew,ListMap.getcp_info_list().get(i).getwl());
                        box_int_info.addproduct_list(products_info);
                        Log.d("初始铭牌号数据",mphNew);
                    }
                    ListMap.addbox_int_list(box_int_info);
                }
                ListMap.clearcp_info_list();
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver,intentFilter);
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }
}
