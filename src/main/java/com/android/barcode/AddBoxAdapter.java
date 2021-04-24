package com.android.barcode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AddBoxAdapter extends RecyclerView.Adapter<AddBoxAdapter.ViewHolder> {
    private List<CP_info> mWl_info;

    private Context contxt;
    private AddBox activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView wl;
        TextView sl;
        ImageView image;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            wl = (TextView) view.findViewById(R.id.textView_addboxwl);
            sl=(TextView)view.findViewById(R.id.textView_addbox_sl);
            image = (ImageView) view.findViewById(R.id.imageView_addbox);
        }
    }
    public AddBoxAdapter(List<CP_info> wllist, Context context, AddBox activity){
        mWl_info=wllist;
        this.contxt = (AddBox) context;
        this.activity = (AddBox) activity;
    }

    @Override
    public AddBoxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addbox,parent,false);
        final AddBoxAdapter.ViewHolder holder = new AddBoxAdapter.ViewHolder(view);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                CP_info wl_info = mWl_info.get(position);
                remove_item(position);
                ///activity.refresh();

            }
        });

        ///ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AddBoxAdapter.ViewHolder holder, int position) {
        CP_info wl_info = mWl_info.get(position);
        holder.wl.setText(wl_info.getwl());
        holder.sl.setText(String.valueOf(wl_info.getsl()));
        holder.image.setImageResource(R.drawable.delete);


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
