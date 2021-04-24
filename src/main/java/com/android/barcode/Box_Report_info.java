package com.android.barcode;

public class Box_Report_info {
    private String box_id;
    private String box_code;
    private String supportcode ;
    private String supportstate;
    private int js;
    private String cjsj;
    public Box_Report_info(String box_id,String box_code,String supportcode,String supportstate,int js,String cjsj){
        this.box_id = box_id;
        this.box_code = box_code;
        this.supportcode = supportcode;
        this.js = js;
        this.cjsj = cjsj;
        this.supportstate = supportstate;
    }
    public String getBox_id(){
        return box_id;
    }
    public String getBox_code(){
        return box_code;
    }
    public String getsupportcode(){ return supportcode; }
    public String getsupportstate(){ return supportstate; }
    public int getjs(){
        return js;
    }
    public String getcjsj(){
        return cjsj;
    }

}
