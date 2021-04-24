package com.android.barcode;

public  class Getcode {
    ///获取件码
    public static String getproductcode(String code){
        if(code.indexOf("https")>=0){
            return code.substring(code.indexOf("ProductCode=")+12,code.length());
        }else{
            return code;
        }
    }
    ///获取箱码
    public static String getboxcode(String code){
        if(code.indexOf("https")>=0){
            return code.substring(code.indexOf("BoxCode=")+8,code.length());
        }else{
            return code;
        }
    }
    ///获取托码
    public static String getsupportcode(String code){
        if(code.indexOf("https")>=0){
            return code.substring(code.indexOf("SupportCode=")+12,code.length());
        }else{
            return code;
        }
    }
    ///获取货位号
    public static String gethwh(String code){
        return code;
    }



}
