package com.android.barcode;

public class YC_Ckd {
   private String id;
   private String code;
   private String customer;
   private String trans_entity;
   private String car_number;
   private String order_code;
   private String delivery_code;

    public YC_Ckd(String id, String code, String customer, String trans_entity, String car_number, String order_code, String delivery_code) {
        this.id = id;
        this.code = code;
        this.customer = customer;
        this.trans_entity = trans_entity;
        this.car_number = car_number;
        this.order_code = order_code;
        this.delivery_code = delivery_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTrans_entity() {
        return trans_entity;
    }

    public void setTrans_entity(String trans_entity) {
        this.trans_entity = trans_entity;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getDelivery_code() {
        return delivery_code;
    }

    public void setDelivery_code(String delivery_code) {
        this.delivery_code = delivery_code;
    }
}

