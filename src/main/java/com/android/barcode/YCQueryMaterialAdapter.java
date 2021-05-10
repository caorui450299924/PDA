package com.android.barcode;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YCQueryMaterialAdapter extends RecyclerView.Adapter<YCQueryMaterialAdapter.ViewHolder> {
    private List<BK_info> mWl_info;
    private Context contxt;
    private YCQueryMaterial activity;
    public CustomDialogCommon.Builder builder ;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView tv_bkrk_wl;
        TextView tv_bkrk_hwh;
        TextView tv_bkrk_sl;

        public ViewHolder(View view){
            super(view);
            wlview = view;
            tv_bkrk_wl = (TextView) view.findViewById(R.id.tv_yc_jccx_cp);
            tv_bkrk_hwh = (TextView)view.findViewById(R.id.tv_yc_jccx_hwh);
            tv_bkrk_sl = (TextView) view.findViewById(R.id.tv_yc_jccx_jjc);
            imageView_box_report = (ImageView)view.findViewById(R.id.imv_yc_jccx);
        }
    }
    public YCQueryMaterialAdapter(List<BK_info> wllist, Context context, YCQueryMaterial activity){
        mWl_info=wllist;
        this.contxt = (YCQueryMaterial) context;
        this.activity = (YCQueryMaterial) activity;
    }

    @Override
    public YCQueryMaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ycquerymateial,parent,false);
        final YCQueryMaterialAdapter.ViewHolder holder = new YCQueryMaterialAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(YCQueryMaterialAdapter.ViewHolder holder, int position) {
        BK_info wl_info = mWl_info.get(position);
        holder.imageView_box_report.setImageResource(R.drawable.box_report);
        holder.tv_bkrk_wl.setText("物料："+String.valueOf(wl_info.getWl()));
        holder.tv_bkrk_hwh.setText("货位号："+wl_info.getHwh());
        holder.tv_bkrk_sl.setText("结存："+wl_info.getSl());
    }

    @Override
    public int getItemCount() {
        return mWl_info.size();
    }



}
