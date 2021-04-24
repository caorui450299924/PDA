package com.android.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductDetail extends Activity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetail);
        Intent intent = getIntent();
        String box_code =intent.getStringExtra("box_code");
        recyclerView = (RecyclerView)findViewById(R.id.recy_pro_detail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<Wl_info_detail> wl_detail_list = new ArrayList<>();
        Box_Int_info box_int_info =null;
        for(int i=0;i<ListMap.getbox_int_listconut();i++){
            if(ListMap.getbox_int_list().get(i).getBox_code().equals(box_code)){
                box_int_info = ListMap.getbox_int_list().get(i);
            }
        }
        for(int i=0;i<box_int_info.getproduct_list().size();i++){
            Wl_info_detail wlInfoDetail = new Wl_info_detail(box_int_info.getproduct_list().get(i).getwl(),box_int_info.getproduct_list().get(i).getmph());
            wl_detail_list.add(wlInfoDetail);
        }
        BoxDetailAdapter adapter  =new BoxDetailAdapter(wl_detail_list);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration());
    }
}
