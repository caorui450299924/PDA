package com.android.barcode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class BoxDetailAdapter extends RecyclerView.Adapter<BoxDetailAdapter.ViewHolder> {
    private List<Wl_info_detail> mWl_info;

    private Context contxt;
    private BoxDetail activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView mph;
        TextView wl;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            mph = (TextView) view.findViewById(R.id.textView_box_mph);
            wl = (TextView)view.findViewById(R.id.textView_box_matail);
        }
    }
    public BoxDetailAdapter(List<Wl_info_detail> wllist){
        mWl_info=wllist;
    }

    @Override
    public BoxDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_box_detail,parent,false);
        final BoxDetailAdapter.ViewHolder holder = new BoxDetailAdapter.ViewHolder(view);
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
    public void onBindViewHolder(BoxDetailAdapter.ViewHolder holder, int position) {
        Wl_info_detail wl_info = mWl_info.get(position);
        holder.mph.setText("铭牌号："+wl_info.getMph());
        holder.wl.setText("物料："+wl_info.getWl());
    }

    @Override
    public int getItemCount() {
        return mWl_info.size();
    }
    /*
    public void remove_item(int i) {

        mWl_info.remove(i);
        ///移除对象后不需要再次删除
        ///ListMap.removewl_list(i);
        notifyItemRemoved(i);
        notifyDataSetChanged();

    }
    */

}
