package com.android.barcode;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

public class Post {


    ///文件下载
    public static InputStream getfile(String url){
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] { new Post.TrustAllManager() }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new Post.TrustHostnameVerifier());
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            ///conn.setRequestMethod("GET");// 设置请求类型为post
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Charset","utf-8");
            ///conn.setRequestProperty("cookie","JSESSIONID"+);
            conn.connect();
            Log.d("获取文件长度",String.valueOf(conn.getContentLength()) );
            Log.d("下载文件返回",String.valueOf(conn.getResponseCode()));
            if(conn.getResponseCode()== HttpURLConnection.HTTP_OK){
                return conn.getInputStream();
            }else{
                return null;
            }


        } catch (Exception e) {
            Log.d("Post", e.getMessage());
            return null;
        }
    }
    ///请求http
    public static String post_com(JSONObject json, String url) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] { new Post.TrustAllManager() }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new Post.TrustHostnameVerifier());
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");// 设置请求类型为post
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            ///conn.setRequestProperty("cookie","JSESSIONID"+);
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
            Log.d("line",line);
            return line;

        } catch (Exception e) {
            Log.d("Post", e.getMessage());
            return "调用公用http失败"+e.getMessage();
        }

    }
    ///请求http带cookie
    public static String post_com_cookie(JSONObject json, String url,String cookie) {
        try {

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] { new Post.TrustAllManager() }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new Post.TrustHostnameVerifier());
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");// 设置请求类型为post
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("cookie","JSESSIONID="+cookie);
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
            Log.d("Post", e.getMessage());
            return "调用共用http失败"+e.getMessage();
        }

    }
    public static class TrustHostnameVerifier implements HostnameVerifier {

        // 信任所有主机-对于任何证书都不做检查
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static class TrustAllManager implements X509TrustManager {

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


}
