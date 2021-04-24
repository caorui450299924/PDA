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

public class BoxZtAdapter extends RecyclerView.Adapter<BoxZtAdapter.ViewHolder> {
    private List<BoxZt_info> mWl_info;

    private Context contxt;
    private BoxZt activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView wl;
        TextView box_code;
        TextView sl;
        ImageView image;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            wl = (TextView) view.findViewById(R.id.textView_box_wlpt);
            box_code = (TextView)view.findViewById(R.id.textView_box_codept);
            sl=(TextView) view.findViewById(R.id.textView_boxslpt);
            image=(ImageView) view.findViewById(R.id.imageView_pt);
        }
    }
    public BoxZtAdapter(List<BoxZt_info> wllist){
        mWl_info=wllist;
    }

    @Override
    public BoxZtAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_box_pt,parent,false);
        final BoxZtAdapter.ViewHolder holder = new BoxZtAdapter.ViewHolder(view);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                BoxZt_info wl_info = mWl_info.get(position);
                remove_item(position);
                ///activity.refresh();
                Toast.makeText(view.getContext(),"删除箱："+wl_info.getBox_code(),Toast.LENGTH_SHORT).show();
            }
        });
        ///ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BoxZtAdapter.ViewHolder holder, int position) {
        BoxZt_info wl_info = mWl_info.get(position);
        holder.box_code.setText("箱码："+wl_info.getBox_code());
        holder.wl.setText("物料："+wl_info.getwl());
        holder.sl.setText("数量："+wl_info.getjs());
        holder.image.setImageResource(R.drawable.box);
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
