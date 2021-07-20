package com.android.barcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListMap {
    ///private static ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    private static String supportcode ;
    private static String boxcode;
    private static int boxid;
    private static int supportid;
    private static String userid;
    private static String sessionId;
    private static String roleid;
    ///拼托使用
    private static String spcode;
    private static int  spid;
    ///仓库拼托使用
    private static String spcode_int;
    private static int spid_int;



    ///创建箱使用
    private static List<Wl_info> wl_list = new ArrayList<>();
    ///拼托显示扫码的箱码用
    private static List<Box_info> box_list = new ArrayList<>();
    ///拼托显示扫码的箱码用
    private static List<Box_Int_info> box_int_list = new ArrayList<>();

    ///拼托显示扫描产品
    private static List<CP_info> cp_info_list = new ArrayList<>();

    ///发货显示数据

    private static List<Fh_info> fh_info_list = new ArrayList<>();

    ///IMS路径
    public static String imsUrl ="https://www.vapp.meide-casting.com/ims/";

    ///接口路径
    public static String requestUrl ="http://www.vapp.meide-casting.com/Service/MDWechatService.asmx/";

    public static String requestUrlTsl ="https://www.vapp.meide-casting.com/Service/MDWechatService.asmx/";

    ///掌机的广播名称

    public static String broacastName ="com.se4500.onDecodeComplete";

    ///发货时判断是否可以拆箱

    public static String intFlag ="N";

    ///拼托时显示整托数据

    private static List<BoxZt_info> boxZt_infos = new ArrayList<>();



    ///Supportcode
    public static void setSupportcode(String th){
        supportcode=th;
    }
    public static String getSupportcode(){ return supportcode; }
    ///boxcode
    public static void setboxcode(String th){ boxcode=th; }
    public static String getboxcode(){ return boxcode; }
    ///boxid
    public static void setboxid(int th){ boxid=th; }
    public static int getboxid(){ return boxid; }

    ///supportid
    public static void setsupportid(int th){
        supportid=th;
    }
    public static int getsupportid(){ return supportid; }
    ///userid
    public static void setuserid(String th){
        userid=th;
    }
    public static String getuserid(){ return userid; }
    ///sessionId
    public static void setsessionId(String th){
        sessionId=th;
    }
    public static String getsessionId(){ return sessionId; }
    ///roleid
    public static void setroleid(String th){
        roleid=th;
    }
    public static String getroleid(){ return roleid; }

    ///wl_list
    public static void setwl_list(List<Wl_info> th){
        wl_list=th;
    }
    public static List<Wl_info> getwl_list(){ return wl_list; }
    public static void addwl_list(Wl_info th){
        wl_list.add(th);
    }
    public static int getwl_listconut(){
        return wl_list.size();
    }
    public static void removewl_list(int position){
        wl_list.remove(position);
    }
    public static void clearwl_list(){
        wl_list.clear();
    }
    ///box_list
    public static void setbox_list(List<Box_info> th){
        box_list=th;
    }
    public static List<Box_info> getbox_list(){ return box_list; }
    public static void addbox_list(Box_info th){
        box_list.add(th);
    }
    public static int getbox_listconut(){
        return box_list.size();
    }
    public static void removebox_list(int position){
        box_list.remove(position);
    }
    public static void clearbox_list(){
        box_list.clear();
    }
    ///box_int_list
    public static void setbox_int_list(List<Box_Int_info> th){
        box_int_list=th;
    }
    public static List<Box_Int_info> getbox_int_list(){ return box_int_list; }
    public static void addbox_int_list(Box_Int_info th){
        box_int_list.add(th);
    }
    public static int getbox_int_listconut(){
        return box_int_list.size();
    }
    public static void clearbox_int_list(){
        box_int_list.clear();
    }


    ///cp_info_list
    public static void setcp_info_list(List<CP_info> th){
        cp_info_list=th;
    }
    public static List<CP_info> getcp_info_list(){ return cp_info_list; }
    public static void addcp_info_list(CP_info th){
        cp_info_list.add(th);
    }
    public static int getcp_info_listsize(){ return cp_info_list.size(); }
    public static void clearcp_info_list(){
        cp_info_list.clear();
    }

    ///fh_info_list


    public static void setfh_info_list(List<Fh_info> th){
        fh_info_list=th;
    }
    public static List<Fh_info> getfh_info_list(){ return fh_info_list; }
    public static void addfh_info_list(Fh_info th){
        fh_info_list.add(th);
    }
    public static int getfh_info_listsize(){ return fh_info_list.size(); }
    public static void clearfh_info_list(){
        fh_info_list.clear();
    }


    ///boxZt_infos

    public static void setboxZt_infos(List<BoxZt_info> th){
        boxZt_infos=th;
    }
    public static List<BoxZt_info> getboxZt_infos(){ return boxZt_infos; }
    public static void addboxZt_infos(BoxZt_info th){
        boxZt_infos.add(th);
    }
    public static int getboxZt_infossize(){ return boxZt_infos.size(); }
    public static void clearboxZt_infos(){
        boxZt_infos.clear();
    }







    ///spcode
    public static void setspcode(String th){
        spcode=th;
    }
    public static String getspcode(){ return spcode; }
    ///spid
    public static void setspid(int th){
        spid=th;
    }
    public static int getspid(){ return spid; }
    ///spcode_int
    public static void setspcode_int(String th){
        spcode_int=th;
    }
    public static String getspcode_int(){ return spcode_int; }
    ///spid_int
    public static void setspid_int(int th){ spid_int=th; }
    public static int getspid_int(){ return spid_int; }









}
