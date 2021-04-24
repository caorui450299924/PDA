package com.android.barcode;

public class Support_info {
    private int tuoid;
    private String tuocode;
    private String tuozt;
    private String tuosfzt;
    private String tuocjsj;
    private String tuojs;
    private String tuoxs;
    public  Support_info(int tuoid,String tuocode,String tuozt,String tuosfzt,String tuocjsj,String tuojs,String tuoxs){
        this.tuoid = tuoid;
        this.tuocode = tuocode;
        this.tuozt = tuozt;
        this.tuosfzt = tuosfzt;
        this.tuocjsj = tuocjsj;
        this.tuojs = tuojs;
        this.tuoxs = tuoxs;
    }
    public int gettuoid(){ return tuoid; }
    public String gettuocode(){
        return tuocode;
    }
    public String gettuozt(){ return tuozt; }
    public String gettuosfzt(){ return tuosfzt; }
    public String gettuocjsj(){ return tuocjsj; }
    public String gettuojs(){ return tuojs; }
    public String gettuoxs(){ return tuoxs; }
}
