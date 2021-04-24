package com.android.barcode;

public class BoxPt_info {
    private String wl;
    private String box_code;
    private String sl;
    public BoxPt_info(String wl,String box_code,String sl){
        this.wl = wl;
        this.box_code = box_code;
        this.sl = sl;
    }
    public String getWl(){
        return wl;
    }
    public String getBox_code(){
        return box_code;
    }
    public String getsl(){
        return sl;
    }
}
