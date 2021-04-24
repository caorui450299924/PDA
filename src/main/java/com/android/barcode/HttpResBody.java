package com.android.barcode;

import android.app.Activity;

import org.json.JSONObject;

public class HttpResBody {
    private JSONObject res;
    private Activity activity;
    private int souce_id;
    private String compent;
    public HttpResBody(JSONObject res,Activity activity,int souce_id,String compent){
        this.res = res;
        this.activity = activity;
        this.souce_id = souce_id;
        this.compent = compent;
    }
    public JSONObject getres(){ return res; }
    public Activity  getactivity(){ return activity; }
    public int getsouce_id(){ return souce_id; }
    public String getcompent(){ return compent; }
}
