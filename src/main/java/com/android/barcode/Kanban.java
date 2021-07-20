package com.android.barcode;

public class Kanban {
    private String rq;
    private String pz;
    private String gg;
    private String xs;
    private String czfl;
    private String bmcl;
    private String code;
    private String xzjc;
    private String zzgh;
    private String lch;
    private String hwh;

    public Kanban(String rq, String pz, String gg, String xs, String czfl, String bmcl, String code, String xzjc, String zzgh, String lch, String hwh) {
        this.rq = rq;
        this.pz = pz;
        this.gg = gg;
        this.xs = xs;
        this.czfl = czfl;
        this.bmcl = bmcl;
        this.code = code;
        this.xzjc = xzjc;
        this.zzgh = zzgh;
        this.lch = lch;
        this.hwh = hwh;
    }

    @Override
    public String toString() {
        return "Kanban{" +
                "rq='" + rq + '\'' +
                ", pz='" + pz + '\'' +
                ", gg='" + gg + '\'' +
                ", xs='" + xs + '\'' +
                ", czfl='" + czfl + '\'' +
                ", bmcl='" + bmcl + '\'' +
                ", code='" + code + '\'' +
                ", xzjc='" + xzjc + '\'' +
                ", zzgh='" + zzgh + '\'' +
                ", lch='" + lch + '\'' +
                ", hwh='" + hwh + '\'' +
                '}';
    }

    public String getRq() {
        return rq;
    }

    public void setRq(String rq) {
        this.rq = rq;
    }

    public String getPz() {
        return pz;
    }

    public void setPz(String pz) {
        this.pz = pz;
    }

    public String getGg() {
        return gg;
    }

    public void setGg(String gg) {
        this.gg = gg;
    }

    public String getXs() {
        return xs;
    }

    public void setXs(String xs) {
        this.xs = xs;
    }

    public String getCzfl() {
        return czfl;
    }

    public void setCzfl(String czfl) {
        this.czfl = czfl;
    }

    public String getBmcl() {
        return bmcl;
    }

    public void setBmcl(String bmcl) {
        this.bmcl = bmcl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getXzjc() {
        return xzjc;
    }

    public void setXzjc(String xzjc) {
        this.xzjc = xzjc;
    }

    public String getZzgh() {
        return zzgh;
    }

    public void setZzgh(String zzgh) {
        this.zzgh = zzgh;
    }

    public String getLch() {
        return lch;
    }

    public void setLch(String lch) {
        this.lch = lch;
    }

    public String getHwh() {
        return hwh;
    }

    public void setHwh(String hwh) {
        this.hwh = hwh;
    }
}
