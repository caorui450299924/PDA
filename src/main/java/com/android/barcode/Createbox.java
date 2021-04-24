package com.android.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.android.barcode.Post.post_com;

public class Createbox extends Activity {

    RecyclerView recyclerView;
    EditText textView_scan;
    AlertDialog builder=null;
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                Log.d("PDA扫码","扫描件码返回"+data);
                ///if(data.indexOf("BOX")>=0){
                if(data.indexOf("BOX")>=0||data.indexOf("TRSUT")>=0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Createbox.this,"不能扫描箱码或托码",Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }
                ///查询数据库物料编码
                ///String mph = data.substring(0,data.length()-1);
                ///设置显示框内容
                Log.d("扫描内容",data);
                setlistview(Getcode.getproductcode(data));


            }
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createbox_layout);
        Intent intent = getIntent();

        TextView textView6 = (TextView)findViewById(R.id.textView6);
        textView_scan = (EditText)findViewById(R.id.edit_boxscan);
        textView_scan.setEnabled(false);
        textView6.setText(intent.getStringExtra("box_code"));
        RecyclerView listview_box = (RecyclerView)findViewById(R.id.listview_box);
        Button button4 = (Button)findViewById(R.id.button4);
        button4.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///将数据保存到数据库
                if(ListMap.getwl_listconut()<=0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Createbox.this,"扫描内容为空，不能提交",Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }


                ///ArrayList<HashMap<String, Object>> itemlist_box = ListMap.getListItem();
                List<Wl_info> itemlist_box_temp = ListMap.getwl_list();
                List<Wl_info> itemlist_box = new ArrayList<>();
                for(int i=0;i<itemlist_box_temp.size();i++){
                    if(itemlist_box_temp.get(i).getBoxcode().equals(ListMap.getboxcode())){
                        itemlist_box.add(itemlist_box_temp.get(i));
                    }
                }
                if(itemlist_box.size()<=0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Createbox.this,"扫描内容为空，不能提交",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                    return;
                }

                JSONArray products = new JSONArray();
                JSONObject quest_josn = new JSONObject();

                try {
                    for(int i=0;i<itemlist_box.size();i++){

                        JSONObject item = new JSONObject();
                        ///item.put("PRODUCT_CODE",itemlist_box.get(i).get("mph").toString());
                        ///item.put("wl",itemlist_box.get(i).get("mph").toString());
                        item.put("PRODUCT_CODE",itemlist_box.get(i).getMph());
                        item.put("MATERIAL_CODE",itemlist_box.get(i).getWl());
                        item.put("CREATEUSER",ListMap.getuserid());
                        products.put(item);

                    }
                    quest_josn.put("products",products);
                    JSONObject box = new JSONObject();
                    box.put("CREATEUSER",ListMap.getuserid());
                    if(ListMap.getsupportid()==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Createbox.this,"保存托ID为0，不能提交",Toast.LENGTH_SHORT).show();
                            }
                        });

                        return;
                    }
                    box.put("SUPPORT_ID",ListMap.getsupportid());
                    box.put("BOX_CODE",ListMap.getboxcode());
                    quest_josn.put("box",box);
                    ///请求服务器
                    String create_box_res = post_com(quest_josn,"https://221.214.27.228/Service/MDWechatService.asmx/Parking1");
                    Log.d("保存箱返回",create_box_res);
                    JSONObject box_res_json = new JSONObject(create_box_res);
                    JSONObject d = new JSONObject(box_res_json.get("d").toString());
                    String code = d.getString("Code").toString();
                    if(!code.equals("200")){
                        final String mes = d.getString("Message").toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Createbox.this,"查询数据失败"+mes,Toast.LENGTH_LONG).show();
                            }
                        });

                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Createbox.this,"提交成功",Toast.LENGTH_LONG).show();
                        }
                    });
                    ListMap.clearwl_list();
                    finish();


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });

        ///如果扫描中退出显示之前扫描内容

        if(ListMap.getwl_listconut()!=0){
            List<Wl_info> wl_list = ListMap.getwl_list();
            ///只显示箱号一样的数据
            boolean bj = false;
            for(int i=0;i<wl_list.size();i++){
                if(wl_list.get(i).getBoxcode().equals(ListMap.getboxcode())){///一个相等说明全部相等
                    bj = true;
                    break;
                }
            }
            if(bj==true){
                recyclerView = (RecyclerView)findViewById(R.id.listview_box);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                CreateBoxAdapter adapter  =new CreateBoxAdapter(ListMap.getwl_list(),"ks",Createbox.this);
                recyclerView.setAdapter(adapter);
                TextView counttext1 = (TextView)findViewById(R.id.textView4);
                int sl1 = ListMap.getwl_listconut();
                counttext1.setText(String.valueOf(sl1));
            }
        }



    }
    ///Listview添加数据

    public void setlistview(final String mph){
        ///防止扫描重复
        List<Wl_info> wl_temp = ListMap.getwl_list();

        for(int i=0;i<ListMap.getwl_listconut();i++){
            if(wl_temp.get(i).getMph().equals(mph)){
                textView_scan.setText("");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Createbox.this,"已经扫码的件码，不要重复扫描",Toast.LENGTH_LONG).show();
                    }
                });

                return ;
            }
        }
        ///防止主线程请求报错
        Setpost.setmainworkpost();
        JSONObject post_da = new JSONObject();
        try {
            post_da.put("productCode",mph);
            ///请求服务器
            ////LoadingDialog.getInstance(Createbox.this).show();
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),post_da.toString());
            final Request request = new Request.Builder()
                    .url("http://221.214.27.228/Service/MDWechatService.asmx/QueryProductMaterialByProductCode")
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("okhttp请求失败",e.getMessage());
                    final String mes3=e.getMessage();
                    ///LoadingDialog.getInstance(Createbox.this).dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Createbox.this,"请求失败"+mes3,Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String reslut_box = response.body().string();
                    Log.d("okhttp请求成功",reslut_box);
                    ///LoadingDialog.getInstance(Createbox.this).dismiss();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                Log.d("查询物料返回",reslut_box);
                                if(reslut_box.indexOf("Message")<0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Createbox.this,"查询物料失败"+reslut_box,Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    textView_scan.setText("");
                                    return ;
                                }
                                String d = new JSONObject(reslut_box).getString("d");
                                JSONObject d1= new JSONObject(d);

                                Log.d("查询铭牌号返回Message:",d1.getString("Message"));
                                if(!d1.getString("Message").equals("查询成功")){
                                    final String mes1=d1.getString("Message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Createbox.this,"查询物料失败"+mes1,Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    textView_scan.setText("");
                                    return ;
                                }
                                ///获取返回值中的物料
                                String Data = new JSONObject(d).getString("Data");
                                if(Data.equals("[]")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Createbox.this,"物料在数据库不存在",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    textView_scan.setText("");
                                    return ;
                                }
                                String MATERIAL_CODE = new JSONArray(Data).getJSONObject(0).getString("MATERIAL_CODE");
                                ///使用recycleview
                                Wl_info wl_info = new Wl_info(MATERIAL_CODE,mph,R.drawable.delete,ListMap.getboxcode());

                                List<Wl_info> wl_list_temp = new ArrayList<>();
                                wl_list_temp = ListMap.getwl_list();
                                ///只显示箱号一样的数据

                                for(int i=0;i<wl_list_temp.size();i++){
                                    if(!wl_list_temp.get(i).getBoxcode().equals(ListMap.getboxcode())){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Createbox.this,"扫描件码出错"+"，箱码和缓存物料不匹配，请先提交上一箱码",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        return ;
                                    }
                                }

                                ListMap.addwl_list(wl_info);
                                List<Wl_info> wl_list = ListMap.getwl_list();
                                RecyclerView recyclerView1 = (RecyclerView)findViewById(R.id.listview_box);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(Createbox.this);
                                recyclerView1.setLayoutManager(layoutManager);
                                CreateBoxAdapter adapter  =new CreateBoxAdapter(wl_list,"sm",Createbox.this);
                                recyclerView1.setAdapter(adapter);
                                refresh();
                            }catch (Exception e){
                                e.printStackTrace();
                                final String mes2=e.getMessage();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Createbox.this,"扫描件码出错"+mes2,Toast.LENGTH_SHORT).show();
                                    }
                                });

                                textView_scan.setText("");
                                return ;
                            }
                        }
                    });
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
            final String mes3=e.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Createbox.this,"扫描件码出错"+mes3,Toast.LENGTH_SHORT).show();
                }
            });

            textView_scan.setText("");
            return ;
        }
    }

    ///刷新数量
    public  void refresh(){
        TextView counttext1 = (TextView)findViewById(R.id.textView4);
        int sl1 = ListMap.getwl_listconut();
        counttext1.setText(String.valueOf(sl1));
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
        unregisterReceiver(receiver);
        super.onStop();
    }
    /**
     *
     * @brief 退出前保证数组为空
     * @author Administrator
     * @param
     * @return
     * @time 2020-11-09 11:14
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(ListMap.getwl_listconut()>0){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Createbox.this,"请清空产品后退出程序",Toast.LENGTH_LONG).show();
                    }
                });

                return true;
            }
            builder = new AlertDialog.Builder(Createbox.this)
                    .setTitle("温馨提示：")
                    .setMessage("您是否要退出扫描箱码？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Createbox.this.finish();
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
