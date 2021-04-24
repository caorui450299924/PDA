package com.android.barcode;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DeliveryBhAdapter extends RecyclerView.Adapter<DeliveryBhAdapter.ViewHolder> {
    private List<Bh_info> mWl_info;
    private Context contxt;
    private String  activityName;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView lxtextview;
        TextView carnumtextview;
        TextView khmtextview;
        TextView ckmctextview;
        TextView fhrqtextview;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            lxtextview = (TextView) view.findViewById(R.id.textView_tuoid);
            carnumtextview = (TextView)view.findViewById(R.id.textView_tuocode);
            khmtextview = (TextView) view.findViewById(R.id.textView_tuozt);
            ckmctextview = (TextView)view.findViewById(R.id.textView_tuosfzt);
            fhrqtextview = (TextView)view.findViewById(R.id.textView_tuocjsj);
        }
    }
    public DeliveryBhAdapter(List<Bh_info> wllist,Context context,String classname){
        mWl_info=wllist;
        activityName = classname;
        if(classname.equals("DeliveryBh")){
            this.contxt = (DeliveryBh) context;
        }else if(classname.equals("Fhd")){
            this.contxt = (Fhd) context;
        }

        ///this.activity = (DeliveryBh) activity;
    }

    @Override
    public DeliveryBhAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_support_search,parent,false);
        final DeliveryBhAdapter.ViewHolder holder = new DeliveryBhAdapter.ViewHolder(view);
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                ///Toast.makeText(view.getContext(),"点击"+String.valueOf(position),Toast.LENGTH_SHORT).show();
                if(activityName.equals("DeliveryBh")){
                    Intent intent = new Intent();
                    intent.setClass(contxt,DeliveryBhMx.class);
                    Bh_info bh_info = mWl_info.get(position);
                    intent.putExtra("ids",bh_info.getids());
                    contxt.startActivity(intent);
                }else if(activityName.equals("Fhd")){
                    Intent intent = new Intent();
                    intent.setClass(contxt,FhdMx.class);
                    Bh_info bh_info = mWl_info.get(position);
                    intent.putExtra("ids",bh_info.getids());
                    intent.putExtra("khm",bh_info.getkhm());
                    contxt.startActivity(intent);
                }else{
                    Toast.makeText(view.getContext(),"传入参数不正确",Toast.LENGTH_SHORT).show();
                }

            }
        });
        /*
        holder.tuoidtextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Support_info wl_info = mWl_info.get(position);
                remove_item(position);
                ///activity.refresh();
                Toast.makeText(view.getContext(),"点击"+wl_info.gettuoid(),Toast.LENGTH_SHORT).show();
            }
        });
        */
        return holder;
    }

    @Override
    public void onBindViewHolder(DeliveryBhAdapter.ViewHolder holder, int position) {
        Bh_info wl_info = mWl_info.get(position);
        holder.lxtextview.setText("类型：备货");
        holder.carnumtextview.setText("车号："+wl_info.getcar_num());
        holder.khmtextview.setText("客户名："+wl_info.getkhm());
        holder.ckmctextview.setText("仓库："+wl_info.getckmc());
        holder.fhrqtextview.setText("日期："+wl_info.getfhrq());
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
