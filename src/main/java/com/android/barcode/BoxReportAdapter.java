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

public class BoxReportAdapter extends RecyclerView.Adapter<BoxReportAdapter.ViewHolder> {
    private List<Box_Report_info> mWl_info;
    private Context contxt;
    private BoxReport activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView textView_boxid;
        TextView textView_boxcode;
        TextView textView_tuo_code;
        TextView textView_tuo_state;
        TextView textView_js;
        TextView textView_box_cjrq;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            textView_boxid = (TextView) view.findViewById(R.id.textView_boxid);
            textView_boxcode = (TextView)view.findViewById(R.id.textView_boxcode);
            textView_tuo_code = (TextView) view.findViewById(R.id.textView_tuo_code);
            textView_tuo_state = (TextView)view.findViewById(R.id.textView_tuo_state);
            textView_js = (TextView)view.findViewById(R.id.textView_js);
            textView_box_cjrq = (TextView)view.findViewById(R.id.textView_box_cjrq);
            imageView_box_report = (ImageView)view.findViewById(R.id.imageView_box_report);
        }
    }
    public BoxReportAdapter(List<Box_Report_info> wllist,Context context, BoxReport activity){
        mWl_info=wllist;
        this.contxt = (BoxReport) context;
        this.activity = (BoxReport) activity;
    }

    @Override
    public BoxReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_box_report,parent,false);
        final BoxReportAdapter.ViewHolder holder = new BoxReportAdapter.ViewHolder(view);
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                ///Toast.makeText(view.getContext(),"点击"+String.valueOf(position),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(contxt,BoxDetail.class);
                intent.putExtra("zt","cx");
                Box_Report_info support_info = mWl_info.get(position);
                intent.putExtra("box_code",String.valueOf(support_info.getBox_code()));
                intent.putExtra("box_id",String.valueOf(support_info.getBox_id()));
                contxt.startActivity(intent);
                ///Intent intent = new Intent(SupportReportAdapter.this,Scansupport.class);
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
    public void onBindViewHolder(BoxReportAdapter.ViewHolder holder, int position) {
        Box_Report_info wl_info = mWl_info.get(position);
        holder.imageView_box_report.setImageResource(R.drawable.box_report);
        holder.textView_boxid.setText("箱ID："+String.valueOf(wl_info.getBox_id()));
        holder.textView_boxcode.setText("箱号："+wl_info.getBox_code());
        holder.textView_tuo_code.setText("托码："+wl_info.getsupportcode());
        holder.textView_tuo_state.setText("托状态："+wl_info.getsupportstate());
        holder.textView_js.setText("件数："+String.valueOf(wl_info.getjs()));
        holder.textView_box_cjrq.setText("创建时间："+wl_info.getcjsj());
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
