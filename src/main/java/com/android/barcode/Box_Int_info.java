package com.android.barcode;

import java.util.ArrayList;
import java.util.List;

public class Box_Int_info {
    private String box_code;
    private String js;
    private String wl;
    private List<Products_info> product_list =new ArrayList<>();
    public Box_Int_info(String box_code){
        this.box_code = box_code;
    }
    public String getBox_code(){
        return box_code;
    }
    public void setJs(String js){
        this.js = js;
    }
    public void setWl(String wl){
        this.wl =wl;
    }
    public String getWl(){
        return this.wl;
    }
    public String getJs(){
        return this.js;
    }
    public void addproduct_list(Products_info products_info){ product_list.add(products_info); }
    public List<Products_info> getproduct_list(){
        return product_list;
    }


}
