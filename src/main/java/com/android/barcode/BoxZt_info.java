package com.android.barcode;

public class BoxZt_info {
    private String box_id;
    private String box_code;
    private String js;
    private String wl;
    public BoxZt_info(String box_id,String box_code,String js,String wl){
        this.box_id = box_id;
        this.box_code = box_code;
        this.js = js;
        this.wl =wl;
    }
    public String getBox_id(){
        return box_id;
    }
    public String getBox_code(){
        return box_code;
    }
    public String getjs(){
        return js;
    }
    public String getwl(){
        return wl;
    }
}
