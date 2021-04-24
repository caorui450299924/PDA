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

public class CreateBoxAdapter extends RecyclerView.Adapter<CreateBoxAdapter.ViewHolder> {
    private List<Wl_info> mWl_info;

    private String lx;
    private Createbox activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView wlimage;
        TextView wl;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            wlimage = (ImageView)view.findViewById(R.id.wl_image);
            wl = (TextView)view.findViewById(R.id.tv_mph);
        }
    }
    public CreateBoxAdapter(List<Wl_info> wllist,String lx, Createbox activity){
        mWl_info=wllist;
        this.lx = lx;
        this.activity = (Createbox) activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_box,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.wlimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Wl_info wl_info = mWl_info.get(position);
                remove_item(position);
                ///remove_with_code(wl_info.getMph());
                activity.refresh();
                ///Toast.makeText(view.getContext(),"点击"+wl_info.getWl(),Toast.LENGTH_SHORT).show();
            }
        });

        ///ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Wl_info wl_info = mWl_info.get(position);
        holder.wlimage.setImageResource(wl_info.getImageId());
        holder.wl.setText(wl_info.getMph());
    }

    @Override
    public int getItemCount() {
        return mWl_info.size();
    }
    public void remove_item(int i) {

        mWl_info.remove(i);
        ///移除对象后不需要再次删除
        ///ListMap.removewl_list(i);
        notifyItemRemoved(i);
        notifyDataSetChanged();

    }
    public void remove_with_code(String code){
        for(int i=0;i<mWl_info.size();i++){
            if(mWl_info.get(i).getMph().equals(code)){
                mWl_info.remove(i);
                if(lx.equals("ks")){
                    ListMap.removewl_list(i);
                }
                notifyItemRemoved(i);
                notifyDataSetChanged();
            }
        }
    }
}
