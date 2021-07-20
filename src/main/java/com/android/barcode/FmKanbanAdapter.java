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

public class FmKanbanAdapter extends RecyclerView.Adapter<FmKanbanAdapter.ViewHolder> {
    private List<Kanban> Kanbans;
    private Context contxt;
    private FmKanban activity;
    public CustomDialogCommon.Builder builder ;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView tv_fmkb_code;
        TextView tv_fmkb_lch;
        TextView tv_fmkb_hwh;
        TextView tv_fmkb_rq;
        TextView tv_fmkb_js;
        TextView tv_fmkb_zzgh;
        public ViewHolder(View view){
            super(view);
            wlview = view;
            tv_fmkb_code = (TextView) view.findViewById(R.id.tv_fmkb_code);
            tv_fmkb_lch = (TextView) view.findViewById(R.id.tv_fmkb_lch);
            tv_fmkb_hwh = (TextView) view.findViewById(R.id.tv_fmkb_hwh);
            tv_fmkb_rq = (TextView) view.findViewById(R.id.tv_fmkb_rq);
            tv_fmkb_js = (TextView) view.findViewById(R.id.tv_fmkb_js);
            tv_fmkb_zzgh = (TextView) view.findViewById(R.id.tv_fmkb_zzgh);
            imageView_box_report = (ImageView)view.findViewById(R.id.imv_fmkb_dis);
        }
    }
    public FmKanbanAdapter(List<Kanban> wllist, Context context, FmKanban FmKanban){
        Kanbans=wllist;
        this.contxt = (FmKanban) context;
        this.activity = (FmKanban) FmKanban;
    }

    @Override
    public FmKanbanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fmkanban,parent,false);
        final FmKanbanAdapter.ViewHolder holder = new FmKanbanAdapter.ViewHolder(view);
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("~~~~","~~~~~~");

                int position = holder.getAdapterPosition();





                /*int position = holder.getAdapterPosition();
                final BK_info_mh bk_info_mh = mWl_info.get(position);
                builder = new CustomDialogCommon.Builder(contxt);
                builder.setTitle("半库入库");
                builder.setMessage("物料:"+bk_info_mh.getWl());
                Log.d("选择产品",bk_info_mh.getWl());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(Integer.valueOf(builder.cpsl.getText().toString())<=0){
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity,"输入的数量不能小于等于0", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                        //confirmrk(bk_info_mh.getWl(),"B-F016",builder.cpsl.getText().toString(),ListMap.getuserid());
                        ///隐藏
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                builder.cpmph.setEnabled(false);
                builder.cpsl.setText("0");
                builder.cpmph.setText("货位号:");*/
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(FmKanbanAdapter.ViewHolder holder, int position) {
        Kanban  kanban = Kanbans.get(position);
        holder.imageView_box_report.setImageResource(R.drawable.box_report);
        holder.tv_fmkb_code.setText("物料："+kanban.getCode());
        holder.tv_fmkb_lch.setText("炉次号："+kanban.getLch());
        holder.tv_fmkb_hwh.setText("货位号："+kanban.getHwh());
        holder.tv_fmkb_rq.setText("日期："+kanban.getRq());
        holder.tv_fmkb_js.setText("件数："+kanban.getXzjc());
        holder.tv_fmkb_zzgh.setText("组装工号："+kanban.getZzgh());
    }

    @Override
    public int getItemCount() {
        return Kanbans.size();
    }

    /*///确认入库
    public void confirmrk(String wl,String hwh,String num,String user){
        JSONObject req_supportreport = new JSONObject();
        try {
            req_supportreport.put("wl",wl);
            req_supportreport.put("hwh",hwh);
            req_supportreport.put("num",num);
            req_supportreport.put("username",user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),req_supportreport.toString());
        LoadingDialog.getInstance(activity).show();
        final Request request = new Request.Builder()
                .url("http://www.vapp.meide-casting.com/app/rk")
                ///.url("http://10.0.2.40:8092/Service/MDWechatService.asmx/QueryBoxInfo")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.getInstance(activity).dismiss();
                final String message = e.getMessage();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"查询数据失败"+message, Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialog.getInstance(activity).dismiss();
                final String box_res = response.body().string();
                Log.d("查询半库货位数据返回",box_res);
                try{
                    JSONObject box_res_json = new JSONObject(box_res);
                    String code = box_res_json.getString("flag").toString();
                    final String msg =box_res_json.getString("msg").toString();
                    if(!code.equals("1")){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity,"执行入库失败"+msg, Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,"执行入库成功"+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (Exception e){
                    Log.e("解析查询箱数据失败",e.getMessage());
                    final String msg = e.getMessage();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,"查询数据失败："+msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        });

    }*/


}
