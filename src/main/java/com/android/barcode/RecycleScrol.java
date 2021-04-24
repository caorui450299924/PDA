package com.android.barcode;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

public class RecycleScrol extends RecyclerView.OnScrollListener {
    private int adapterNowPos;
    private int allItems;
    private FhdMx context;
    public RecycleScrol(Context context){
        this.context =(FhdMx) context;
    }
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int nowPos =1;
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager l = (LinearLayoutManager)recyclerView.getLayoutManager();
        adapterNowPos = l.findFirstVisibleItemPosition();
        allItems = l.getItemCount();
        String s = adapterNowPos+1+"/"+allItems;
        if(nowPos!=adapterNowPos+1){
            nowPos=adapterNowPos+1;
            ///Toast.makeText(context,"第"+String.valueOf(nowPos)+"页",Toast.LENGTH_SHORT).show();
        }
        Log.d("滚动位置",s);
    }
}
