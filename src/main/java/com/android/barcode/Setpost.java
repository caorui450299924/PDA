package com.android.barcode;

import android.os.StrictMode;

public class Setpost {
    ///防止主线程请求报错
    public static void setmainworkpost(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        };

    }
}
