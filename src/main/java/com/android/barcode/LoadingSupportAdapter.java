package com.android.barcode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LoadingSupportAdapter extends RecyclerView.Adapter<LoadingSupportAdapter.ViewHolder> {
    private List<Box_info> mWl_info;

    private Context contxt;
    private LoadingSupport activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView mph;
        ImageView image;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            mph = (TextView) view.findViewById(R.id.tv_mph);
            image = (ImageView) view.findViewById(R.id.wl_image);
        }
    }
    public LoadingSupportAdapter(List<Box_info> wllist, Context context, LoadingSupport activity){
        mWl_info=wllist;
        this.contxt = (LoadingSupport) context;
        this.activity = (LoadingSupport) activity;
    }

    @Override
    public LoadingSupportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_box,parent,false);
        final LoadingSupportAdapter.ViewHolder holder = new LoadingSupportAdapter.ViewHolder(view);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Box_info wl_info = mWl_info.get(position);
                remove_item(position);
                ///activity.refresh();

            }
        });

        ///ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LoadingSupportAdapter.ViewHolder holder, int position) {
        Box_info wl_info = mWl_info.get(position);
        holder.mph.setText(wl_info.getBox_code());
        holder.mph.setTextSize(22);
        holder.mph.setGravity(Gravity.CENTER);
        holder.image.setImageResource(wl_info.getimageid());


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
