package com.android.barcode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BoxPtAdapter extends RecyclerView.Adapter<BoxPtAdapter.ViewHolder> {
    private List<BoxPt_info> mWl_info;
    private BoxPt activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView wl;
        TextView box_code;
        TextView sl;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            wl = (TextView) view.findViewById(R.id.textView_box_wlpt);
            box_code = (TextView)view.findViewById(R.id.textView_box_codept);
            sl = (TextView)view.findViewById(R.id.textView_boxslpt);
        }
    }
    public BoxPtAdapter(List<BoxPt_info> wllist,Context context){
        mWl_info=wllist;
        this.activity = (BoxPt)context;
    }

    @Override
    public BoxPtAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_box_pt,parent,false);
        final BoxPtAdapter.ViewHolder holder = new BoxPtAdapter.ViewHolder(view);
        /*
        holder.wlimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Wl_info_detail wl_info = mWl_info.get(position);
                remove_item(position);
                activity.refresh();
                Toast.makeText(view.getContext(),"点击"+wl_info.getWl(),Toast.LENGTH_SHORT).show();
            }
        });
        */
        ///ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BoxPtAdapter.ViewHolder holder, int position) {
        BoxPt_info wl_info = mWl_info.get(position);
        holder.wl.setText("物料："+wl_info.getWl());
        holder.wl.setText("物料："+wl_info.getWl());
        holder.wl.setText("物料："+wl_info.getWl());

    }

    @Override
    public int getItemCount() {
        return mWl_info.size();
    }
}
