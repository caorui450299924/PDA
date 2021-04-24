package com.android.barcode;

public class Bh_info {
    private String car_num;
    private String khm;
    private String ckmc;
    private String fhrq;
    private String ids;
    public  Bh_info(String car_num,String khm,String ckmc,String fhrq,String ids){
        this.car_num = car_num;
        this.khm = khm;
        this.ckmc = ckmc;
        this.fhrq = fhrq;
        this.ids = ids;
    }
    public String getcar_num(){ return car_num; }
    public String getkhm(){
        return khm;
    }
    public String getckmc(){ return ckmc; }
    public String getfhrq(){ return fhrq; }
    public String getids(){ return ids; }

}
