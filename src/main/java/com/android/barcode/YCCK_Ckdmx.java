package com.android.barcode;

public class YCCK_Ckdmx {
   private String material;
   private String num;
   private String hwh;
   private String actualQuantity;
   private String unloadedQty;


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

    public String getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(String actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public String getUnloadedQty() {
        return unloadedQty;
    }

    public void setUnloadedQty(String unloadedQty) {
        this.unloadedQty = unloadedQty;
    }

    public YCCK_Ckdmx(String material, String num, String hwh, String actualQuantity, String unloadedQty) {
        this.material = material;
        this.num = num;
        this.hwh = hwh;
        this.actualQuantity = actualQuantity;
        this.unloadedQty = unloadedQty;
    }
}

