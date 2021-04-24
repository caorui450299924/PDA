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

public class SupportReportAdapter extends RecyclerView.Adapter<SupportReportAdapter.ViewHolder> {
    private List<Support_info> mWl_info;
    private Context contxt;
    private SupportReport activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        TextView tuoidtextview;
        TextView tuocodetextview;
        TextView tuozttextview;
        TextView tuosfzttextview;
        TextView tuocjsjtextview;
        TextView textView_tuoxs;
        TextView textView_tuojs;
        TextView textView_tuocjsj;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            tuoidtextview = (TextView) view.findViewById(R.id.textView_tuoid);
            tuocodetextview = (TextView)view.findViewById(R.id.textView_tuocode);
            tuozttextview = (TextView) view.findViewById(R.id.textView_tuozt);
            tuosfzttextview = (TextView)view.findViewById(R.id.textView_tuosfzt);
            textView_tuoxs = (TextView)view.findViewById(R.id.textView_tuoxs);
            textView_tuojs = (TextView)view.findViewById(R.id.textView_tuojs);
            textView_tuocjsj = (TextView)view.findViewById(R.id.textView_tuocjsj);


        }
    }
    public SupportReportAdapter(List<Support_info> wllist,Context context, SupportReport activity){
        mWl_info=wllist;
        this.contxt = (SupportReport) context;
        this.activity = (SupportReport) activity;
    }

    @Override
    public SupportReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_support_search,parent,false);
        final SupportReportAdapter.ViewHolder holder = new SupportReportAdapter.ViewHolder(view);
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                ///Toast.makeText(view.getContext(),"点击"+String.valueOf(position),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(contxt,Scansupport.class);
                intent.putExtra("zt","cx");
                Support_info support_info = mWl_info.get(position);
                intent.putExtra("supportid",String.valueOf(support_info.gettuoid()));
                intent.putExtra("supportcode",support_info.gettuocode());
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
    public void onBindViewHolder(SupportReportAdapter.ViewHolder holder, int position) {
        Support_info wl_info = mWl_info.get(position);
        holder.tuoidtextview.setText("托ID："+String.valueOf(wl_info.gettuoid()));
        holder.tuocodetextview.setText("托号："+wl_info.gettuocode());
        holder.tuozttextview.setText("托状态："+wl_info.gettuozt());
        holder.tuosfzttextview.setText("是否整托："+wl_info.gettuosfzt());
        holder.textView_tuoxs.setText("总箱数："+wl_info.gettuoxs());
        holder.textView_tuojs.setText("总件数："+wl_info.gettuojs());
        holder.textView_tuocjsj.setText("时间："+wl_info.gettuocjsj());
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
