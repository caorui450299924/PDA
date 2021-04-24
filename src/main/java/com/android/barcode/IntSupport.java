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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.android.barcode.Post.post_com;

public class IntSupport extends Activity {
    Button addbox ;
    Button confirm;
    Button smzt;
    Button searchbox;
    RadioButton Button_intsup1;
    RadioButton Button_intsup2;
    AlertDialog builder1=null;
    AlertDialog builder2=null;
    TextView textView_intsup_dis;
    RecyclerView recyclerView;
    CheckBox checkBoxZt;
    public String DialogBz = "N";
    CustomDialog.Builder builder =null;
    private String RECE_DATA_ACTION = ListMap.broacastName;
    private IntentFilter intentFilter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                String supportcode = textView_intsup_dis.getText().toString();
                if(supportcode.isEmpty()){
                    if(data.indexOf("TRSUT")>=0){
                        int sl = searchsupport(Getcode.getsupportcode(data));
                        Log.d("查询箱数量返回","返回数量"+String.valueOf(sl));
                        if(sl==0){
                            savesupport(Getcode.getsupportcode(data));
                        }

                    }else{
                        Toast.makeText(IntSupport.this,"请先扫描发货托码",Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    if(data.indexOf("_")>=0&&data.indexOf(",")>=0){///扫描的产品
                        if(supportcode.isEmpty()){
                            Toast.makeText(IntSupport.this,"请先扫描发货托码",Toast.LENGTH_LONG).show();
                            return;
                        }
                        ///获取新箱码
                        String box_code = searchbox_code();
                        if(box_code.indexOf("获取箱码失败")>=0){
                            Toast.makeText(IntSupport.this,box_code,Toast.LENGTH_LONG).show();
                            return;
                        }
                        ///获取数量
                        String wlNum =data.substring(data.indexOf(",")+1,data.length());
                        ///获取物料
                        String wlMc =data.substring(0,data.indexOf(","));
                        addNewbox(wlMc,box_code,wlNum);
                    }else if(data.indexOf("BOX")>=0){///扫描的替他托的箱码
                        if(checkBoxZt.isChecked()){
                            ScanZt(Getcode.getboxcode(data));
                        }else{
                            addbox(Getcode.getboxcode(data));
                        }
                    }else if(data.indexOf("ProductCode")>=0){
                        if(DialogBz.equals("N")){
                            Toast.makeText(IntSupport.this,"请先扫描备货单",Toast.LENGTH_LONG).show();
                            return;
                        }
                        String js = builder.cpsl.toString();
                        builder.cpmph.setText(Getcode.getproductcode(data));
                        builder.cpsl.setText("1");

                    }
                    else{
                        Toast.makeText(IntSupport.this,"请先扫描内容不正确",Toast.LENGTH_LONG).show();
                        return;
                    }



                }
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intsupport);
        Log.d("BoxPt","onCreate");
        confirm = (Button)findViewById(R.id.btn_int_sup);
        addbox = (Button)findViewById(R.id.btn_int_box);
        searchbox=(Button)findViewById(R.id.btn_int_sup2);
        Button_intsup1=(RadioButton)findViewById(R.id.Button_intsup1);
        Button_intsup2 =(RadioButton)findViewById(R.id.Button_intsup2);
        textView_intsup_dis = (TextView)findViewById(R.id.textView_intsup_dis);
        recyclerView = (RecyclerView)findViewById(R.id.rev_int_sup);
        smzt = (Button)findViewById(R.id.btn_smzt);
        checkBoxZt = (CheckBox)findViewById(R.id.checkBoxZt);
        ///查询明细产品
        addbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///addbox();
                if(textView_intsup_dis.getText().toString().isEmpty()){
                    Toast.makeText(IntSupport.this,"请先扫描托码",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(IntSupport.this,BoxPt.class);
                intent.putExtra("support_id",String.valueOf(ListMap.getspid_int()));
                intent.putExtra("type","托产品明细");
                intent.putExtra("support_code",textView_intsup_dis.getText().toString());
                startActivity(intent);
            }
        });
        ///提交
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Box_Int_info> boxIntInfoList = ListMap.getbox_int_list();
                if(boxIntInfoList.size()==0){
                    Toast.makeText(IntSupport.this,"没有数据要提交",Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    JSONObject req = new JSONObject();
                    if(textView_intsup_dis.getText().toString().isEmpty()){
                        Toast.makeText(IntSupport.this,"没有托号，请扫描",Toast.LENGTH_LONG).show();
                        return;
                    }
                    req.put("support_id",ListMap.getspid_int());
                    req.put("CREATEUSER",ListMap.getuserid());
                    JSONArray box_list =new JSONArray();
                    for(int i=0;i<boxIntInfoList.size();i++){
                        JSONArray wl_list = new JSONArray();
                        JSONObject wl_obj = new JSONObject();
                        for(int m=0;m<boxIntInfoList.get(i).getproduct_list().size();m++){
                            JSONObject products =new JSONObject();
                            products.put("PRODUCT_CODE",boxIntInfoList.get(i).getproduct_list().get(m).getmph());
                            products.put("MATERIAL_CODE",boxIntInfoList.get(i).getproduct_list().get(m).getwl());
                            wl_list.put(products);
                        }
                        wl_obj.put("box_code",boxIntInfoList.get(i).getBox_code());
                        wl_obj.put("products",wl_list);
                        box_list.put(wl_obj);
                    }
                    req.put("box_list",box_list);
                    Log.d("提交拼托",req.toString());
                    LoadingDialog.getInstance(IntSupport.this).show();
                    ///使用soap拼接
                    ///String WSDL_URI ="http://10.0.2.40:8092/Service/MDWechatService.asmx";
                    String WSDL_URI ="http://www.vapp.meide-casting.com/Service/MDWechatService.asmx";
                    //命名空间
                    String namespace ="http://tempuri.org/";
                    String methodName ="AddBoxToSupport";//要调用的方法名称
                    final String SOAP_ACTION = "http://tempuri.org/" + "AddBoxToSupport";//用来定义消息请求的地址，也就是消息发送到哪个操作
                    SoapObject soapObject =new SoapObject(namespace, methodName);
                    //如果需要参数  需要调用soapObject.addProperty()方法
                    soapObject.addProperty("request",req.toString());
                    final SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                    //发送请求，设置bodyOut
                    envelope.bodyOut= soapObject;
                    //.net的webservice设置donet为true
                    envelope.dotNet=true;
                   final HttpTransportSE httpTransportSE =new HttpTransportSE(WSDL_URI);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                httpTransportSE.call(SOAP_ACTION, envelope);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (envelope.getResponse() != null) {
                                    SoapObject so = (SoapObject) envelope.bodyIn;
                                    final String rs = so.getProperty(0).toString();//获取WebService返回值（此处为字符串）
                                    Log.d("拼托返回",rs);
                                    LoadingDialog.getInstance(IntSupport.this).dismiss();
                                    if(rs.indexOf("成功")>=0){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(IntSupport.this,"提交成功",Toast.LENGTH_LONG).show();
                                                ///finish();
                                                ListMap.clearbox_int_list();
                                                LinearLayoutManager layoutManager = new LinearLayoutManager(IntSupport.this);
                                                recyclerView.setLayoutManager(layoutManager);
                                                IntSupportAdapter adapter  =new IntSupportAdapter(ListMap.getbox_int_list(),IntSupport.this,IntSupport.this);
                                                recyclerView.setAdapter(adapter);
                                                recyclerView.addItemDecoration(new SpacesItemDecoration());
                                            }
                                        });
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(IntSupport.this,"提交失败返回："+rs,Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        return;
                                    }

                                    Log.d("soap返回",rs);

                                }
                            } catch (SoapFault soapFault) {
                                soapFault.printStackTrace();
                            }
                        }
                    }).start();

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(IntSupport.this,"提交失败"+e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }



            }
        });
        ///查询已转托的箱
        searchbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_intsup_dis.getText().toString().isEmpty()){
                    Toast.makeText(IntSupport.this,"请先扫描托码",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(IntSupport.this,BoxPt.class);
                intent.putExtra("support_id",String.valueOf(ListMap.getspid_int()));
                intent.putExtra("type","托明细");
                intent.putExtra("support_code",textView_intsup_dis.getText().toString());
                startActivity(intent);
            }
        });
        ///扫描整托
        smzt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxZt.setChecked(!checkBoxZt.isChecked());
            }
        });


    }

    @Override
    protected void onResume() {
        /*
        textView_intsup_dis.setText(ListMap.getspcode_int());
        Log.d("BoxPt","onResume");
        if(ListMap.getbox_int_listconut()>0){
            LinearLayoutManager layoutManager = new LinearLayoutManager(IntSupport.this);
            recyclerView.setLayoutManager(layoutManager);
            IntSupportAdapter adapter  =new IntSupportAdapter(ListMap.getbox_int_list(),IntSupport.this,IntSupport.this);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration());
        }
        */
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver,intentFilter);
        refreshScreen();
        checkBoxZt.setChecked(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("BoxPt","onStop");
        ListMap.clearbox_int_list();
        unregisterReceiver(receiver);
    }

    public void savesupport(String supportcode){
        if(supportcode.indexOf("TRSUT")<0){
            Toast.makeText(IntSupport.this,"扫码内容不是托码",Toast.LENGTH_LONG).show();
            return;
        }
        if(Button_intsup1.isChecked()==false&&Button_intsup2.isChecked()==false){
            Toast.makeText(IntSupport.this,"请选择整托或散托",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            ///请求接口将托码保存到数据库
            JSONObject req_support_insert = new JSONObject();
            JSONObject support = new JSONObject();
            req_support_insert.put("CREATEUSER",ListMap.getuserid());
            req_support_insert.put("SUPPORT_CODE",supportcode);
            req_support_insert.put("SUPPORT_STATE",0);
            if(Button_intsup1.isChecked()==true){
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
                Toast.makeText(IntSupport.this,"保存托码失败"+d.getString("Message").toString(),Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject d1 = new JSONObject(d.get("Data").toString());
            Toast.makeText(IntSupport.this,"保存托码成功",Toast.LENGTH_LONG).show();
            textView_intsup_dis.setText(supportcode);
            ListMap.setspcode_int(d1.getString("SUPPORT_CODE").toString());
            ListMap.setspid_int(d1.getInt("SUPPORT_ID"));

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(IntSupport.this,"扫描托码出错"+e.getMessage(),Toast.LENGTH_LONG).show();
            return;
        }



    }
    public int searchsupport(String supportcode){
        ///查询托信息
        try {
            Setpost.setmainworkpost();
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
                Toast.makeText(IntSupport.this,"查询托数据失败",Toast.LENGTH_LONG).show();
                return -1;
            }
            JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
            if(box_Jsons.length()>1){
                Toast.makeText(IntSupport.this,"查询托返回多条数据",Toast.LENGTH_LONG).show();
                return -1;
            }
            if(box_Jsons.length()==1){
                ListMap.setspcode_int(box_Jsons.getJSONObject(0).get("SUPPORT_CODE").toString());
                textView_intsup_dis.setText(supportcode);
                ListMap.setspid_int(Integer.parseInt(box_Jsons.getJSONObject(0).get("SUPPORT_ID").toString()));

            }
            return box_Jsons.length();

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    public void addbox(String boxcode){
        try {
            JSONObject req_box_id = new JSONObject();
            JSONObject boxInfo = new JSONObject();
            String box_code = boxcode;
            for(int i =0;i<ListMap.getbox_listconut();i++){
                if(ListMap.getbox_int_list().get(i).getBox_code().equals(box_code)){
                    Toast.makeText(IntSupport.this,"当前箱："+boxcode+"已扫描",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            req_box_id.put("BOX_CODE",box_code);
            req_box_id.put("CREATEUSER","");
            req_box_id.put("END_TIME","");
            req_box_id.put("START_TIME","");
            req_box_id.put("SelectState",0);

            boxInfo.put("boxInfo",req_box_id);
            String reslut_box = post_com(boxInfo,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxInfo");
            Log.d("查询拼箱返回",reslut_box);
            String res = reslut_box;
            JSONObject box_res_json = new JSONObject(reslut_box);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText( IntSupport.this,"查询数据失败："+d.getString("Message").toString(),Toast.LENGTH_LONG).show();
                return;
            }
            JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
            ///显示listview

            if(box_Jsons.length()>1){
                Toast.makeText(IntSupport.this,"查询箱返回多条数据",Toast.LENGTH_LONG).show();
                return;
            }

            if(box_Jsons.length()<1){
                Toast.makeText(IntSupport.this,"没有查询到箱码",Toast.LENGTH_LONG).show();
                return;
            }
            int SUPPORT_STATE = 0;
            Log.d("SUPPORT_STATE",box_Jsons.getJSONObject(0).get("SUPPORT_STATE").toString());
            if(!box_Jsons.getJSONObject(0).get("SUPPORT_STATE").toString().equals("null")){
                SUPPORT_STATE = Integer.parseInt(box_Jsons.getJSONObject(0).get("SUPPORT_STATE").toString());
            }
            String SUPPORT_CODE = box_Jsons.getJSONObject(0).get("SUPPORT_CODE").toString();
            if(SUPPORT_STATE>=3){
                Toast.makeText(IntSupport.this,"该箱已发货，请核实",Toast.LENGTH_LONG).show();
                return;
            }
            for(int i= 0;i< box_Jsons.length();i++){
                Box_Int_info box_info = new Box_Int_info(box_code);
                box_info.setJs(box_Jsons.getJSONObject(0).get("COUNT").toString());
                box_info.setWl(box_Jsons.getJSONObject(0).get("wl").toString());
                ListMap.addbox_int_list(box_info);
            }

            if(!SUPPORT_CODE.isEmpty()){
                builder1 = new AlertDialog.Builder(IntSupport.this)
                        .setTitle("该箱已经成托")
                        .setMessage("确认换托吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ///显示数据
                                /*
                                List<Box_Int_info> support_list_temp = new ArrayList<>();
                                support_list_temp=ListMap.getbox_int_list();
                                */
                                LinearLayoutManager layoutManager = new LinearLayoutManager(IntSupport.this);
                                recyclerView.setLayoutManager(layoutManager);
                                IntSupportAdapter adapter  =new IntSupportAdapter(ListMap.getbox_int_list(),IntSupport.this,IntSupport.this);
                                recyclerView.setAdapter(adapter);
                                recyclerView.addItemDecoration(new SpacesItemDecoration());

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                builder1.dismiss();
                            }
                        })
                        .show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String searchbox_code(){
        try {
            JSONObject req_delete_support = new JSONObject();
            req_delete_support.put("sl",1);
            Log.d("拼托查询托码请求",req_delete_support.toString());
            String create_box_res = post_com(req_delete_support,"https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxCodeWx");
            Log.d("拼托查询托码返回",create_box_res);
            JSONObject box_res_json = new JSONObject(create_box_res);
            JSONObject d = new JSONObject(box_res_json.get("d").toString());
            String code = d.getString("Code").toString();
            if(!code.equals("200")){
                Toast.makeText(IntSupport.this,"查询箱码失败"+d.getString("Message").toString(),Toast.LENGTH_LONG).show();
                return "查询箱码失败"+d.getString("Message").toString();
            }
            JSONArray d1 = new JSONArray(d.get("Data").toString());
            return d1.get(0).toString();

        }catch (Exception e){
            e.printStackTrace();
            return  "查询箱码失败"+e.getMessage();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("BoxPt","onDestroy");
        super.onDestroy();
    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            builder2 = new AlertDialog.Builder(IntSupport.this)
                    .setTitle("温馨提示：")
                    .setMessage("您是否要退出拼托？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    IntSupport.this.finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    builder2.dismiss();
                                }
                            }).show();
        }
        return true;
    }
    public void addbox(){
        if(textView_intsup_dis.getText().toString().isEmpty()){
            Toast.makeText(IntSupport.this,"请先扫描托码",Toast.LENGTH_LONG).show();
            return;
        }
        String box_code = searchbox_code();
        if(box_code.indexOf("查询箱码失败")>=0){
            Toast.makeText(IntSupport.this,box_code,Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent =new Intent(IntSupport.this,AddBox.class);
        intent.putExtra("box_code",box_code);
        startActivity(intent);
    }
    public void addNewbox(final String wl, final String box_code, final String wlNum){
        ///CustomDialog.Builder builder  = new CustomDialog.Builder(IntSupport.this);
        builder  = new CustomDialog.Builder(IntSupport.this);
        if(DialogBz.equals("Y")){
            ///Toast.makeText(IntSupport.this,"已经打开界面请重试",Toast.LENGTH_LONG).show();
            return;
        }
        final String wlbm = wl;
        builder.setTitle(wl);
        builder.setMessage("请输入数量！");
        ///DialogBz="Y";
        ///builder.setTitle("提示");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(builder.cpsl.getText().toString().isEmpty()){
                    Toast.makeText(IntSupport.this,"请输入数量！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!builder.cpmph.getText().toString().isEmpty()){
                    if(!builder.cpsl.getText().toString().equals("1")){
                        Toast.makeText(IntSupport.this,"请输入铭牌号后只能输入1件",Toast.LENGTH_LONG).show();
                        builder.cpsl.setText("1");
                        return;
                    }
                }
                ///Toast.makeText(IntSupport.this,builder.cpsl.getText(),Toast.LENGTH_LONG).show();
                ///添加产品
                ///清空List
                ListMap.clearcp_info_list();
                CP_info cp_info = new CP_info(wlbm,"初始",Integer.parseInt(builder.cpsl.getText().toString()));
                ListMap.addcp_info_list(cp_info);
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
                        String mphNew ="";
                        if(builder.cpmph.getText().toString().isEmpty()){
                            mphNew = "CS"+suffix+randomnum;///铭牌号按照CS加日期加随机数生成
                        }else{
                            mphNew = builder.cpmph.getText().toString();
                        }

                        ///Products_info products_info = new Products_info("初始",ListMap.getcp_info_list().get(i).getwl());
                        Products_info products_info = new Products_info(mphNew,ListMap.getcp_info_list().get(i).getwl());
                        box_int_info.addproduct_list(products_info);
                        box_int_info.setJs(String.valueOf(box_int_info.getproduct_list().size()));
                        box_int_info.setWl(wl);
                        Log.d("初始铭牌号数据",mphNew);
                    }
                    ListMap.addbox_int_list(box_int_info);
                }
                ///隐藏
                dialog.dismiss();
                ///刷新界面
                refreshScreen();
                ///设置你的操作事项
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
        builder.cpsl.setText(wlNum);
    }
    ///刷新
    public void refreshScreen(){
    textView_intsup_dis.setText(ListMap.getspcode_int());
    Log.d("BoxPt","onResume");
    if(ListMap.getbox_int_listconut()>0){
        LinearLayoutManager layoutManager = new LinearLayoutManager(IntSupport.this);
        recyclerView.setLayoutManager(layoutManager);
        IntSupportAdapter adapter  =new IntSupportAdapter(ListMap.getbox_int_list(),IntSupport.this,IntSupport.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration());
    }
}
    @Override
    public void finish() {
        super.finish();
        ListMap.setspcode_int("");
    }
    public void ScanZt(String th){
        JSONObject req_supportreport = new JSONObject();
        JSONObject supportInfo = new JSONObject();
        try {
             req_supportreport = new JSONObject();
             supportInfo = new JSONObject();
             supportInfo.put("BOX_CODE",th);
             supportInfo.put("CREATEUSER","");
             supportInfo.put("END_TIME","");
             supportInfo.put("START_TIME","");
             supportInfo.put("SelectState",0);
            req_supportreport.put("boxInfo",supportInfo);
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
            final Request request = new Request.Builder()
                    .url("http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/QueryBoxInfo")
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    final String message = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(IntSupport.this,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String box_res = response.body().string();
                    Log.d("查询拼托箱码返回",box_res);
                    try {
                        JSONObject box_res_json = new JSONObject(box_res);
                        JSONObject d = new JSONObject(box_res_json.get("d").toString());
                        String code = d.getString("Code").toString();
                        if (!code.equals("200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(IntSupport.this, "查询数据失败", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                        JSONArray box_Jsons = new JSONArray(d.get("Data").toString());
                        ///判断箱数量
                        if(box_Jsons.length()>1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(IntSupport.this, "查询箱返回多条数据", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                        if(box_Jsons.length()<1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(IntSupport.this, "查询箱返回空", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                        String supportId = box_Jsons.getJSONObject(0).get("SUPPORT_ID").toString();
                        String supportCode = box_Jsons.getJSONObject(0).get("SUPPORT_CODE").toString();
                        ///是否有托码
                        if(supportCode.isEmpty()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(IntSupport.this, "查询箱没有托码", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                        ///打开整托界面
                        Intent intent = new Intent(IntSupport.this,BoxZt.class);
                        intent.putExtra("supportId",supportId);
                        intent.putExtra("supportCode",supportCode);
                        startActivity(intent);

                    }catch (Exception e){
                        e.printStackTrace();
                }
            }
            });





        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
