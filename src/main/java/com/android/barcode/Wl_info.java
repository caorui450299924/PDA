package com.android.barcode;

public class Wl_info {
    private String wl;
    private String mph;
    private int imageId;
    private String boxcode;
    public Wl_info(String wl,String mph,int imageId,String boxcode){
        this.wl = wl;
        this.mph = mph;
        this.imageId = imageId;
        this.boxcode = boxcode;
    }
    public String getWl(){
        return wl;

    }

    public String getMph(){
        return mph;
    }

    public int getImageId(){
        return imageId;
    }
    public  String getBoxcode(){
        return this.boxcode;
    }
}
