package com.android.barcode;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DeliveryBhMxAdapter extends RecyclerView.Adapter<DeliveryBhMxAdapter.ViewHolder> {

    private List<Bh_Mx_info> mWl_info;
    private Context contxt;
    private DeliveryBhMx activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView wltextview;
        TextView hwhtextview;
        TextView sltextview;
        ImageView image;

        public ViewHolder(View view){
            super(view);
            wlview = view;

            wltextview = (TextView) view.findViewById(R.id.textViewbhmxwl);
            hwhtextview = (TextView)view.findViewById(R.id.textViewbhmxhwh);
            sltextview = (TextView) view.findViewById(R.id.textViewbhmxsl);
            image = (ImageView)view.findViewById(R.id.imageViewbhmx);

        }
    }
    public DeliveryBhMxAdapter(List<Bh_Mx_info> wllist,Context context, DeliveryBhMx activity){
        mWl_info=wllist;
        this.contxt = (DeliveryBhMx) context;
        this.activity = (DeliveryBhMx) activity;
    }

    @Override
    public DeliveryBhMxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bh_mx,parent,false);
        final DeliveryBhMxAdapter.ViewHolder holder = new DeliveryBhMxAdapter.ViewHolder(view);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Bh_Mx_info wl_info = mWl_info.get(position);
                remove_item(position);
                ///Toast.makeText(view.getContext(),"点击"+wl_info.getwl(),Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(DeliveryBhMxAdapter.ViewHolder holder, int position) {
        Bh_Mx_info wl_info = mWl_info.get(position);
        if(wl_info.getsl().equals("数量")){
            holder.image.setImageResource(R.drawable.bank);
        }else{
            holder.image.setImageResource(R.drawable.delete);
        }

        holder.wltextview.setText(wl_info.getwl());
        holder.hwhtextview.setText(wl_info.gethwh());
        holder.sltextview.setText(wl_info.getsl());

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

}
