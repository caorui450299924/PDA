package com.android.barcode;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class IntSupportAdapter extends RecyclerView.Adapter<IntSupportAdapter.ViewHolder> {
    private List<Box_Int_info> mWl_info;

    private Context contxt;
    private IntSupport activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView mph;
        TextView js;
        TextView wl;
        ImageView image;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            mph = (TextView) view.findViewById(R.id.textView_xh);
            image = (ImageView) view.findViewById(R.id.imageView_int_delete);
            js= (TextView)view.findViewById(R.id.textView_x_sl);
            wl=(TextView)view.findViewById(R.id.textView_wl);
        }
    }
    public IntSupportAdapter(List<Box_Int_info> wllist, Context context, IntSupport activity){
        mWl_info=wllist;
        this.contxt = (IntSupport) context;
        this.activity = (IntSupport) activity;
    }

    @Override
    public IntSupportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intsupport,parent,false);
        final IntSupportAdapter.ViewHolder holder = new IntSupportAdapter.ViewHolder(view);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Box_Int_info wl_info = mWl_info.get(position);
                remove_item(position);

            }
        });
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Box_Int_info wl_info = mWl_info.get(position);
                Intent intent = new Intent(activity,ProductDetail.class);
                intent.putExtra("box_code",wl_info.getBox_code());
                activity.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(IntSupportAdapter.ViewHolder holder, int position) {
        Box_Int_info wl_info = mWl_info.get(position);
        holder.mph.setText(wl_info.getBox_code());
        holder.mph.setTextSize(22);
        holder.mph.setGravity(Gravity.CENTER);
        holder.js.setText(String.valueOf(wl_info.getJs())+"件");
        holder.image.setImageResource(R.drawable.delete);
        holder.wl.setText("物料："+wl_info.getWl());
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
