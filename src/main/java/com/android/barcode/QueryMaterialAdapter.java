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

public class QueryMaterialAdapter extends RecyclerView.Adapter<QueryMaterialAdapter.ViewHolder> {
    private List<FM_Material> mWl_info;
    private Context contxt;
    private QueryMaterial activity;
    public CustomDialogCommon.Builder builder ;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView tv_fm_wl;
        TextView tv_fm_lch;
        TextView tv_fm_jjc;
        TextView tv_fm_hwh;
        TextView tv_fm_bs;
        TextView tv_fm_gxsj;

        public ViewHolder(View view){
            super(view);
            wlview = view;
            tv_fm_wl = (TextView) view.findViewById(R.id.tv_querymaterila_wl);
            tv_fm_lch = (TextView) view.findViewById(R.id.tv_querymaterila_lch);
            tv_fm_jjc = (TextView) view.findViewById(R.id.tv_querymaterila_jjc);
            tv_fm_hwh = (TextView) view.findViewById(R.id.tv_querymaterila_hwh);
            tv_fm_bs = (TextView) view.findViewById(R.id.tv_querymaterila_bs);
            tv_fm_gxsj = (TextView) view.findViewById(R.id.tv_querymaterila_gxsj);
            imageView_box_report = (ImageView)view.findViewById(R.id.imv_querymaterila_dis);
        }
    }
    public QueryMaterialAdapter(List<FM_Material> wllist, Context context, QueryMaterial activity){
        mWl_info=wllist;
        this.contxt = (QueryMaterial) context;
        this.activity = (QueryMaterial) activity;
    }

    @Override
    public QueryMaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_querymaterial,parent,false);
        final QueryMaterialAdapter.ViewHolder holder = new QueryMaterialAdapter.ViewHolder(view);
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("~~~~","~~~~~~");
//                Intent intent = new Intent(QueryMaterialAdapter.this.activity,CaoRuiActivityRk.class);
//                int position = holder.getAdapterPosition();
//                intent.putExtra("wl",String.valueOf(mWl_info.get(position).getWl()));
//                intent.putExtra("sl",mWl_info.get(position).getJjc());
//                intent.putExtra("pz",mWl_info.get(position).getPz());
//                intent.putExtra("gg",mWl_info.get(position).getGg());
//                intent.putExtra("xs",mWl_info.get(position).getXs());
//                intent.putExtra("czfl",mWl_info.get(position).getCzfl());
//                intent.putExtra("bmcl",mWl_info.get(position).getBmcl());
//                intent.putExtra("cpbm",mWl_info.get(position).getCpbm());
//                intent.putExtra("kbh",mWl_info.get(position).getKbh());
//                intent.putExtra("gdh",mWl_info.get(position).getGdh());
//                intent.putExtra("gys",mWl_info.get(position).getGys());
//                intent.putExtra("lch",mWl_info.get(position).getLch());
//                intent.putExtra("hwh",mWl_info.get(position).getHwh());
//                contxt.startActivity(intent);



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
    public void onBindViewHolder(QueryMaterialAdapter.ViewHolder holder, int position) {
        FM_Material wl_info = mWl_info.get(position);
        holder.imageView_box_report.setImageResource(R.drawable.box_report);
        holder.tv_fm_wl.setText("物料："+String.valueOf(wl_info.getWl()));
        holder.tv_fm_lch.setText("炉次号："+wl_info.getLch());
        holder.tv_fm_jjc.setText("件数："+wl_info.getJjc());
        holder.tv_fm_hwh.setText("货位号："+wl_info.getHwh());
        holder.tv_fm_bs.setText("单位："+wl_info.getBs());
        holder.tv_fm_gxsj.setText("更新时间："+wl_info.getGxsj());
    }

    @Override
    public int getItemCount() {
        return mWl_info.size();
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
