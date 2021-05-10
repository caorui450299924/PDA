package com.android.barcode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class FMQueryMaterialAdapter extends RecyclerView.Adapter<FMQueryMaterialAdapter.ViewHolder> {
    private List<FM_Material> fm_materials;
    private Context contxt;
    private FMQueryMaterial activity;
    public CustomDialogCommon.Builder builder ;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView tv_fm_wl;
        TextView tv_fm_hwh;
        TextView tv_fm_jjc;
        TextView tv_fm_lch;
        TextView tv_fm_bs;
        TextView tv_fm_gxsj;

        public ViewHolder(View view){
            super(view);
            wlview = view;
            tv_fm_bs = (TextView) view.findViewById(R.id.tv_fm_jccx_bs);
            tv_fm_wl = (TextView) view.findViewById(R.id.tv_fm_jccx_cp);
            tv_fm_hwh = (TextView) view.findViewById(R.id.tv_fm_jccx_hwh);
            tv_fm_jjc = (TextView) view.findViewById(R.id.tv_fm_jccx_jjc);
            tv_fm_lch = (TextView) view.findViewById(R.id.tv_fm_jccx_lch);
            tv_fm_gxsj = (TextView) view.findViewById(R.id.tv_fm_jccx_gxsj);
            imageView_box_report = (ImageView)view.findViewById(R.id.imv_fm_jccx);
        }
    }
    public FMQueryMaterialAdapter(List<FM_Material> wllist, Context context, FMQueryMaterial activity){
        fm_materials=wllist;
        this.contxt = (FMQueryMaterial) context;
        this.activity = (FMQueryMaterial) activity;
    }

    @Override
    public FMQueryMaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fmquerymateial,parent,false);
        final FMQueryMaterialAdapter.ViewHolder holder = new FMQueryMaterialAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(FMQueryMaterialAdapter.ViewHolder holder, int position) {
        FM_Material wl_info = fm_materials.get(position);
        holder.imageView_box_report.setImageResource(R.drawable.box_report);
        holder.tv_fm_wl.setText("物料："+String.valueOf(wl_info.getWl()));
        holder.tv_fm_hwh.setText("货位号："+wl_info.getHwh());
        holder.tv_fm_jjc.setText("结存："+wl_info.getJjc());
        holder.tv_fm_gxsj.setText("更新时间"+wl_info.getGxsj());
        holder.tv_fm_lch.setText("炉次号"+wl_info.getLch());
        holder.tv_fm_bs.setText("单位"+wl_info.getBs());
    }

    @Override
    public int getItemCount() {
        return fm_materials.size();
    }



}
