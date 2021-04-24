package com.android.barcode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FhdMxAdapter extends RecyclerView.Adapter<FhdMxAdapter.ViewHolder> {
    private List<Fh_info> mWl_info;
    private Context contxt;
    private FhdMx activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView wltextview;
        TextView sftextview;
        TextView yftextview;

        public ViewHolder(View view){
            super(view);
            wlview = view;
            wltextview = (TextView) view.findViewById(R.id.textView_fhmx_wl);
            sftextview = (TextView)view.findViewById(R.id.textView_fhmx_sf);
            yftextview = (TextView) view.findViewById(R.id.textView_fhmx_yf);

        }
    }
    public FhdMxAdapter(List<Fh_info> wllist,Context context, FhdMx activity){
        mWl_info=wllist;
        this.contxt = (FhdMx) context;
        this.activity = (FhdMx) activity;
    }

    @Override
    public FhdMxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fhmx,parent,false);
        final FhdMxAdapter.ViewHolder holder = new FhdMxAdapter.ViewHolder(view);
        /*
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Fh_info wl_info = mWl_info.get(position);
                remove_item(position);
                ///Toast.makeText(view.getContext(),"点击"+wl_info.getwl(),Toast.LENGTH_SHORT).show();
            }
        });
        */
        return holder;
    }

    @Override
    public void onBindViewHolder(FhdMxAdapter.ViewHolder holder, int position) {
        Fh_info wl_info = mWl_info.get(position);
        holder.wltextview.setText(wl_info.getwl());
        Log.d("createview",wl_info.getsf()+","+wl_info.getyf());
        if(!wl_info.getsf().equals(wl_info.getyf())&&!wl_info.getsf().equals("实发")){
            if(!wl_info.getsf().equals("底部")&&!wl_info.getsf().equals("实发"))
                holder.sftextview.setBackgroundResource(R.color.colorRed);
        }else{
            holder.sftextview.setBackgroundResource(R.color.colorWithe);
            holder.sftextview.setBackgroundResource(R.drawable.textview_border);
        }
        holder.sftextview.setText(String.valueOf(wl_info.getsf()));
        holder.yftextview.setText(String.valueOf(wl_info.getyf()));

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
