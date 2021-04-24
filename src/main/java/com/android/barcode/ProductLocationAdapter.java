package com.android.barcode;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProductLocationAdapter extends RecyclerView.Adapter<ProductLocationAdapter.ViewHolder> {
    private List<Product_Location_info> mWl_info;
    ///private Context contxt;
    ///private ProductLocation activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView textView_wl;
        TextView textView_hwh;
        TextView textView_xs;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            textView_wl = (TextView) view.findViewById(R.id.textView_product_wl);
            textView_hwh = (TextView)view.findViewById(R.id.textView_product_hwh);
            textView_xs = (TextView) view.findViewById(R.id.textView_product_xs);

        }
    }
    public ProductLocationAdapter(List<Product_Location_info> wllist){
        mWl_info=wllist;
        ///this.contxt = (ProductLocation) context;
        ///this.activity = (ProductLocation) activity;
    }

    @Override
    public ProductLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_location,parent,false);
        final ProductLocationAdapter.ViewHolder holder = new ProductLocationAdapter.ViewHolder(view);
        /*
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent();
                intent.setClass(contxt,BoxDetail.class);
                intent.putExtra("zt","cx");
                Product_Location_info support_info = mWl_info.get(position);
                intent.putExtra("box_code",String.valueOf(support_info.getBox_code()));
                intent.putExtra("box_id",String.valueOf(support_info.getBox_id()));
                contxt.startActivity(intent);
            }
        });
         */
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductLocationAdapter.ViewHolder holder, int position) {
        Product_Location_info wl_info = mWl_info.get(position);
        holder.textView_wl.setText("物料："+wl_info.getwl());
        holder.textView_hwh.setText("货位号："+wl_info.gethwh());
        holder.textView_xs.setText("箱数："+String.valueOf(wl_info.getxs()));
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
