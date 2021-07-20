package com.android.barcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class Login extends AppCompatActivity {
    public static String user_id = "";
    public static String sessionId = "";
    public static String roleid = "";
    private UpdateManager mUpdateManager;
    Button bt_log;
    Button bt_exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ///LogFileUtils.writeLogFile("123456");
        ///Toast.makeText(Login.this,"日志"+LogFileUtils.readLogText(),Toast.LENGTH_SHORT).show();

        bt_log = (Button)findViewById(R.id.bt_log);
        bt_exit = (Button)findViewById(R.id.bt_bos);

        final EditText yhm = (EditText)findViewById(R.id.et_username);
        final EditText mima = (EditText)findViewById(R.id.et_password);
        ///检查更新
        mUpdateManager = new UpdateManager(this);
        mUpdateManager.checkUpdateInfo();
        bt_log.setEnabled(false);
        //yhm.setText("dingweibin");
        //mima.setText("imsims");
        ///申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (Login.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    Login.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }



        bt_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(yhm.getText().toString().isEmpty()){
                    Toast.makeText(Login.this,"请输入账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mima.getText().toString().isEmpty()){
                    Toast.makeText(Login.this,"请输入密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("开始执行","onClick");
                new Thread(runnable).start();
            }
        });
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    ///请求http
    private String TrustAllHttps(JSONObject json,String url) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] { new TrustAllManager() }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new TrustHostnameVerifier());
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");// 设置请求类型为post
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();

            Log.d("传入参数",json.toString());

            byte[] bpara = json.toString().getBytes();

            conn.getOutputStream().write(bpara, 0, bpara.length);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();



            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = br.readLine();
            ///JSONObject login_relut = new JSONObject(line);

            return line;

        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            return "请求出错"+e.getMessage();
        }

    }

    private class TrustHostnameVerifier implements HostnameVerifier {

        // 信任所有主机-对于任何证书都不做检查
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            JSONObject login_json = new JSONObject();
            try {

                final EditText yhm = (EditText)findViewById(R.id.et_username);
                final EditText mima = (EditText)findViewById(R.id.et_password);

                login_json.put("loginName", yhm.getText());
                login_json.put("loginPassword", mima.getText());

                /*
                login_json.put("loginName", "zhangdaozhong");
                login_json.put("loginPassword", "imsims");
                */

                String relut =  TrustAllHttps(login_json,"https://www.vapp.meide-casting.com/ims/com.sie.crm.mobile.mobileMd.verifyUser.biz.ext");
                //String relut =  TrustAllHttps(login_json,"https://vapp.meide-casting.com/ims/com.sie.crm.mobile.mobileMd.verifyUser.biz.ext");
                String retMsg = "";
                JSONObject obj = null;
                JSONArray role = null;
                String retFalg;
                String login_msg;
                Bundle data = new Bundle();
                Message msg = new Message();
                Log.d("登录时返回消息",relut);
                if(relut.indexOf("exception")>0||relut.indexOf("请求出错")>=0){
                    retFalg="F";
                    login_msg=relut;
                    return;
                }else {
                    JSONObject res_login = new JSONObject(relut);
                    retFalg =res_login.get("retFalg").toString();
                    login_msg = res_login.get("retMsg").toString();
                    retMsg = new JSONObject(relut).get("retMsg").toString();
                    if(!retFalg.equals("S")){
                        data.putString("login_msg",retMsg);
                        data.putString("relut",relut);
                        data.putString("retFalg",retFalg);
                        data.putString("login_msg",login_msg);
                        msg.setData(data);
                        handler.sendMessage(msg);
                        return;
                    }

                    obj = new JSONObject(new JSONObject(relut).get("obj").toString());
                    role = new JSONArray(new JSONObject(relut).get("userrole").toString());
                    user_id = obj.get("userId").toString();
                    ListMap.setuserid(user_id);
                    sessionId = obj.get("uniqueId").toString();
                    ListMap.setsessionId(sessionId);
                    roleid = role.getJSONObject(0).getString("ROLEID");
                    ListMap.setroleid(roleid);
                }

                data.putString("login_msg",retMsg);
                data.putString("relut",relut);
                data.putString("retFalg",retFalg);
                data.putString("login_msg",login_msg);
                msg.setData(data);
                handler.sendMessage(msg);

                Log.d("请求返回结果",retMsg);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("login_msg");
            String relut = data.getString("relut");
            String flag = data.getString("retFalg");
            String x_msg = data.getString("login_msg");
            if(!flag.equals("S")){
                Toast.makeText(Login.this,"登录失败"+x_msg,Toast.LENGTH_SHORT).show();
                return;
            }
            if(relut.indexOf("exception")>0){
                try {
                    Toast.makeText(Login.this,"系统异常"+ (new JSONObject((new JSONObject(relut)).get("exception").toString())).get("message").toString(),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }

            Toast.makeText(Login.this,val,Toast.LENGTH_SHORT).show();
            Log.d("val的值：",val);
            if(val.equals("成功")){
                Log.d("跳转",val);
                Intent intent = new Intent(Login.this,Menu.class);
                startActivity(intent);
            }



            ///Log.i("mylog","请求结果-->" + val);
        }
    };


}
