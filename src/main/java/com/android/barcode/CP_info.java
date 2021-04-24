package com.android.barcode;

public class CP_info {
    private String wl;
    private String mph;
    private int sl;
    public CP_info(String wl,String mph,int sl){
        this.wl = wl;
        this.mph = mph;
        this.sl = sl;
    }
    public String getwl(){
        return wl;
    }
    public String getmph(){
        return mph;
    }
    public int getsl(){
        return sl;
    }
}
