package com.android.barcode;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QueryMaterial extends AppCompatActivityBase {
    EditText pz;
    EditText q_gg;
    EditText q_xs;
    EditText q_dw;
    Button cx;
    RecyclerView recyclerView;
    List<FM_Material> bk_info_mhs;
    String barcodeType;
    String param;
    String wl;
    String hwhfinal ;
    private AutoCompleteTextView autoCompleteTextView;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.querymaterial);
        q_gg = (EditText)findViewById(R.id.query_gg);
        q_xs = (EditText)findViewById(R.id.query_xs);
        q_dw = (EditText)findViewById(R.id.query_dw);
        //edt_pz = (EditText)findViewById(R.id.edt_pz);
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.query_pz);
        getpz();
        cx = (Button) findViewById(R.id.btn_query_cx);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_querycx);
        bk_info_mhs = new ArrayList<>();
        intBtn_cx();

    }

    public void onResume(){
        super.receiver = receiver;
        super.onResume();
    }
    public void getpz(){
        JSONObject req_supportreport = new JSONObject();
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),"");
        LoadingDialog.getInstance(QueryMaterial.this).show();
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/app/fmpz")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(QueryMaterial.this).dismiss();
                final  String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryMaterial.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(QueryMaterial.this).dismiss();
                List<String> list1 = new ArrayList<String>();
                final String box_res1 = response.body().string();
                try {
                    String box_res2=JSONTokener(box_res1);
                    JSONObject box_res_json = new JSONObject(box_res2);
                    String code = box_res_json.getString("flag").toString();
                    Log.d("flag",code);
                    final String msg = box_res_json.getString("msg").toString();
                    Log.d("msg",msg);
                    //Log.d("data",box_res_json.getString("data").toString());

                    if (!code.equals("1")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QueryMaterial.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    JSONArray box_Jsons = new JSONArray(JSONTokener(box_res_json.get("data").toString()));
                    //Log.d("length",box_Jsons.length()+"");
                    for (int c = 0 ;c < box_Jsons.length();c++){
                        //Log.d("a","a");
                        String pz = box_Jsons.getJSONObject(c).get("pz").toString();
                        list1.add(pz);
                        //Log.d("pz:",c+"");
                    }
                    setPz(list1);
                }catch (JSONException e){
                    Log.e("解析查询数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QueryMaterial.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });



    }
    public void setPz(List<String> pz){
        final List<String> list = pz;
        /*for (int j = 0;j<list.size();j++){
            Log.d("pz1",list.get(j));
        }*/
        QueryMaterial.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.d("run","调用");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(QueryMaterial.this,R.layout.support_simple_spinner_dropdown_item,list);
                autoCompleteTextView.setAdapter(adapter);
            }
        });
    }
    public static String JSONTokener(String str_json) {
        // consume an optional byte order mark (BOM) if it exists
        if (str_json != null && str_json.startsWith("\ufeff")) {
            str_json = str_json.substring(1);
        }
        return str_json;
    }
    public void intBtn_cx(){
        cx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bk_info_mhs.clear();
                String pz = autoCompleteTextView.getText().toString();
                String gg = q_gg.getText().toString();
                String xs = q_xs.getText().toString();
                String bs = q_dw.getText().toString();
                //Log.d("pz",pz);
               // Log.d("gg",gg);
                JSONObject req_supportreport = new JSONObject();
                try {
                    req_supportreport.put("pz",autoCompleteTextView.getText());
                    req_supportreport.put("gg",q_gg.getText());
                    req_supportreport.put("xs",q_xs.getText());
                    req_supportreport.put("bs",q_dw.getText());
                    Log.d("cx",pz + "" + gg + "" + xs + "" + bs);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(1000,TimeUnit.SECONDS).build();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
                LoadingDialog.getInstance(QueryMaterial.this).show();
                final Request request = new Request.Builder().url("http://www.vapp.meide-casting.com/app/fmcx").post(requestBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LoadingDialog.getInstance(QueryMaterial.this).dismiss();
                        final  String message = e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QueryMaterial.this,"查询数据失败"+message,Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        LoadingDialog.getInstance(QueryMaterial.this).dismiss();
                        final String box_res1 = response.body().string();
                        //Log.d("查询数据",box_res1);
                        try {
                            JSONObject box_res_json = new JSONObject(box_res1);
                            String code = box_res_json.getString("flag").toString();
                            final String msg = box_res_json.getString("msg").toString();
                            if (!code.equals("1")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(QueryMaterial.this,"查询数据失败"+msg,Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            JSONArray box_Jsons = new JSONArray(box_res_json.get("data").toString());
                            int productsl = 0;
                            for (int i = 0 ;i < box_Jsons.length();i++){
                                FM_Material bk_info_mh = new FM_Material(box_Jsons.getJSONObject(i).get("wl").toString(),box_Jsons.getJSONObject(i).get("lch").toString(),
                                        box_Jsons.getJSONObject(i).get("jjc").toString(),box_Jsons.getJSONObject(i).get("hwh").toString(),box_Jsons.getJSONObject(i).get("pz").toString(),
                                        box_Jsons.getJSONObject(i).get("gg").toString(), box_Jsons.getJSONObject(i).get("xs").toString(),box_Jsons.getJSONObject(i).get("cpbm").toString(),
                                        box_Jsons.getJSONObject(i).get("kbh").toString(),box_Jsons.getJSONObject(i).get("gdh").toString(), box_Jsons.getJSONObject(i).get("gys").toString(),
                                        box_Jsons.getJSONObject(i).get("czfl").toString(),box_Jsons.getJSONObject(i).get("bmcl").toString(),box_Jsons.getJSONObject(i).get("bs").toString(),
                                        box_Jsons.getJSONObject(i).get("gxsj").toString());
                                bk_info_mhs.add(bk_info_mh);
                                /*Log.d("查询返回数据","66666666");
                                Log.d("wl",box_Jsons.getJSONObject(i).get("wl").toString());
                                Log.d("jjc",box_Jsons.getJSONObject(i).get("jjc").toString());*/
                            }
                            final int boxs = box_Jsons.length();
                            //Log.d("boxs",boxs+"");
                            final int jss = productsl;
                            //Log.d("jss",jss+"");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(QueryMaterial.this);
                                    recyclerView.setLayoutManager(layoutManager);
                                    QueryMaterialAdapter adapter  =new QueryMaterialAdapter(bk_info_mhs, QueryMaterial.this, QueryMaterial.this);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.addItemDecoration(new SpacesItemDecoration());
                                }
                            });
                        }catch (JSONException e){
                            Log.e("解析查询数据失败",e.getMessage());
                            final String msg = e.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(QueryMaterial.this,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                    }
                });

            }
        });
    }


}
