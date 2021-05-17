package com.android.barcode;

public class YC_Ckdmx {
   private String material;
   private String num;
   private String hwh;


    public YC_Ckdmx(String material, String num, String hwh) {
        this.material = material;
        this.num = num;
        this.hwh = hwh;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getHwh() {
        return hwh;
    }

    public void setHwh(String hwh) {
        this.hwh = hwh;
    }
}

