package com.android.barcode;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUnit {
    private static String result ;
    public static String httppost(JSONObject res, Activity activity){
        final Activity activity_fin  = activity;
        OkHttpClient client = new OkHttpClient();
        Log.d("okhttp请求参数",res.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),res.toString());
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/ims/com.sie.crm.mobile.mobileMd.verifyUser.biz.ext")
                //.url("http://vapp.meide-casting.com/ims/com.sie.crm.mobile.mobileMd.verifyUser.biz.ext")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp请求失败",e.getMessage());
                result="请求失败"+e.getMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Log.d("okhttp请求成功",res);
                activity_fin.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(activity_fin,"登录成功",Toast.LENGTH_SHORT).show();
                        TextView textView5;
                        textView5 = activity_fin.findViewById(R.id.textView5);
                        textView5.setText("登录成功");
                    }
                });
                result = res;
            }
        });
        return result;
    }



}
