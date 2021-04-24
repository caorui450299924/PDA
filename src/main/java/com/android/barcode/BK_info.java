package com.android.barcode;

public class BK_info {
    private String wl;
    private String hwh;
    private int sl;
    public BK_info(String wl,String hwh,int sl){
        this.wl = wl;
        this.hwh = hwh;
        this.sl = sl;
    }
    public String getWl(){
        return wl;
    }
    public String getHwh(){
        return hwh;
    }
    public int getSl(){
        return sl;
    }
}

