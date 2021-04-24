package com.android.barcode;

public class Fh_info {
    private String wl;
    private String sf;
    private String yf;

    public  Fh_info(String wl,String sf,String yf){
        this.wl = wl;
        this.sf = sf;
        this.yf = yf;

    }
    public String getwl(){ return wl; }
    public String getsf(){ return sf; }
    public String getyf(){ return yf; }
    /**
     *
     * @brief setter
     * @author Administrator
     * @param
     * @return
     * @time 2020-11-07 10:35
     */
    public void setwl(String wl){ this.wl = wl; }
    public void setsf(String sf){ this.sf= sf; }
    public void setyf(String yf){ this.yf= yf; }
}
