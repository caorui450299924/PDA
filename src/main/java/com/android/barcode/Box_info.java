package com.android.barcode;

public class Box_info {
    private String box_id;
    private String box_code;
    private int imageid;
    public Box_info(String box_id,String box_code,int imageid){
        this.box_id = box_id;
        this.box_code = box_code;
        this.imageid = imageid;
    }
    public String getBox_id(){
        return box_id;
    }
    public String getBox_code(){
        return box_code;
    }
    public int getimageid(){
        return imageid;
    }
}
