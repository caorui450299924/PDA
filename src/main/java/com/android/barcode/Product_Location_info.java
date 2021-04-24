package com.android.barcode;

public class Product_Location_info {
    private String wl;
    private String hwh;
    private int xs;
    public Product_Location_info(String wl,String hwh,int xs){
        this.wl = wl;
        this.hwh = hwh;
        this.xs = xs;
    }
    public String getwl(){ return wl; }
    public String gethwh(){ return hwh; }
    public int getxs(){ return xs; }
}
