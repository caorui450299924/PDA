package com.android.barcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.util.Log.d;

public class UpdateManager {
    private Context mContext;
    private Login activity;
    //提示语
    private String updateMsg = "有最新的软件，请下载安装";
    //返回的安装包url
    ///private String apkUrl = "https://vapp.meide-casting.com/mdapp/servlet/DownLoadServlet?fileName=fmpda.apk";
    ///更新时将文件上传到目录
    private String apkUrl = "https://vapp.meide-casting.com/mdapp/uploadFile/fmpda.apk";
    ///private String apkUrl = "http://app.mi.com/download/634713";
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    /* 下载包安装路径 */
    ///private static final String savePath = "/sdcard/updatedemo/";

    private static final String savePath = Environment.getExternalStorageDirectory().getPath();

    private static final String saveFileName = savePath + "/updateDemo.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private String versionCode;


    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
        this.activity = (Login)context;
    }

    //外部接口让主Activity调用
    public void checkUpdateInfo(){
        versionCode = getAppVersionCode(mContext);
        getNowVersionCode();
        ///showNoticeDialog();
    }
    ///获取系统版本号
    public static String getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode + "";
    }

    private void showNoticeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        ///不能取消更新
        /*
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        */
        noticeDialog = builder.create();
        noticeDialog.setCancelable(false);
        noticeDialog.show();
    }

    private void showDownloadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar)v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[] { new Post.TrustAllManager() }, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new Post.TrustHostnameVerifier());
                ///URL url = new URL(apkUrl);
                ///HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                URL url = new URL(apkUrl);
                HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
                conn.setConnectTimeout(60000);
                ///conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Charset", "utf-8");
                conn.connect();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                File file = new File(savePath);
                if(!file.exists()){
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do{
                    int numread = is.read(buf);
                    count += numread;
                    progress =(int)(((float)count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if(numread <= 0){
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf,0,numread);
                }while(!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }catch (Exception e){
                d("下载文件",e.getMessage());
                e.printStackTrace();
            }

        }
    };
    public void downpdaapk(){

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apkUrl)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("下载更新包失败",e.getMessage());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                    InputStream is =response.body().byteStream();
                    long length = response.body().contentLength();
                    Log.d("更新文件大小1", String.valueOf(is.available()));
                    ///String root = Environment.getExternalStorageDirectory().getPath();
                    File file = new File(savePath,"updateDemo.apk");
                    FileWriter fw = null;
                    if(!file.exists()){
                        fw = new FileWriter(file, false);
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                        int count = 0;
                        byte buf[] = new byte[1024];
                        do{
                            int numread = is.read(buf);
                            ///int numread = 9;
                            count += numread;
                            progress =(int)(((float)count / length) * 100);
                            //更新进度
                            mHandler.sendEmptyMessage(DOWN_UPDATE);
                            if(numread <= 0){
                                //下载完成通知安装
                                mHandler.sendEmptyMessage(DOWN_OVER);
                                break;
                            }
                            fos.write(buf,0,numread);
                        }while(!interceptFlag);//点击取消就停止下载.
                        fos.close();
                        is.close();
                        //fw.flush();
                        //fw.close();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }catch (Exception e){
                        d("下载文件",e.getMessage());
                        e.printStackTrace();
                    }


                }
            });

    }




    /**
     * 下载apk
     * @param url
     */

    private void downloadApk(){
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
        /*
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (mContext.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    mContext.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
         */

        ///downpdaapk();
    }
    /**
     * 安装apk
     * @param url
     */
    private void installApk(){
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", apkfile);
        Intent i = new Intent(Intent.ACTION_VIEW);
        ///临时读
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ///临时写
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION| FLAG_GRANT_WRITE_URI_PERMISSION);
        i.setDataAndType(contentUri, "application/vnd.android.package-archive");
        mContext.startActivity(i);
        ///activity.bt_log.setEnabled(true);

    }
    private void getNowVersionCode(){
        try{
            JSONObject req_bh_mx = new JSONObject();
            JSONObject paramobj =new JSONObject();
            paramobj.put("sys","cx");
            req_bh_mx.put("namingid","com.dwb.reporter.customerlogin.get_bbxx");
            req_bh_mx.put("paramobj",paramobj);
            Log.d("查询APP最新版本号开始","开始查询"+ListMap.imsUrl+"com.dwb.reporter.queryfmfc.customerlogin.biz.ext");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_bh_mx.toString());
            final Request request = new Request.Builder()
                    .url(ListMap.imsUrl+"com.dwb.reporter.queryfmfc.customerlogin.biz.ext")
                    .post(requestBody)
                    ///.addHeader("cookie","JSESSIONID="+Login.sessionId)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("查询版本号失败",e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    try{
                        JSONObject resJson = new JSONObject(res);
                        JSONArray objs = resJson.getJSONArray("objs");
                        String updateInfo ="";
                        String updateLog = "";
                        if(objs.length()<1){
                            return;
                        }
                        boolean pdaFlag =false;
                        for(int i =0;i<objs.length();i++){
                            if(objs.getJSONObject(i).get("XT").equals("fmpda")){
                                pdaFlag=true;
                                updateInfo= objs.getJSONObject(i).get("VERSION_CODE").toString();
                                updateLog = objs.getJSONObject(i).get("REMARK").toString();
                            }
                        }
                        if(!pdaFlag){
                            LogFileUtils.writeLogFile("查询版本号为空");
                            return;
                        }

                        if(!versionCode.equals(updateInfo)){
                            updateMsg=updateMsg+"\n"+updateLog;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showNoticeDialog();
                                }
                            });
                        }else{
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.bt_log.setEnabled(true);
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        return;
                    }

                }
            });







        }catch (Exception e){
            e.printStackTrace();
            return ;
        }
    }
}
