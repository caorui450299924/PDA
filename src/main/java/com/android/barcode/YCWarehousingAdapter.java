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

public class YCWarehousingAdapter extends RecyclerView.Adapter<YCWarehousingAdapter.ViewHolder> {
    private List<YC_CP> yc_cps;
    private Context contxt;
    private YCWarehousing activity;
    public CustomDialogCommon.Builder builder ;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView cp;
        TextView jjc;
        TextView hwh;

        public ViewHolder(View view){
            super(view);
            wlview = view;
            cp = (TextView) view.findViewById(R.id.tv_yc_operation_cp);
            jjc = (TextView) view.findViewById(R.id.tv_yc_operation_jjc);
            hwh = (TextView) view.findViewById(R.id.tv_yc_operation_hwh);
            imageView_box_report = (ImageView)view.findViewById(R.id.imv_yc_operation);
        }
    }
    public YCWarehousingAdapter(List<YC_CP> list, Context context, YCWarehousing activity){
        yc_cps=list;
        this.contxt = (YCWarehousing) context;
        this.activity = (YCWarehousing) activity;
    }

    @Override
    public YCWarehousingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ycwarehousing,parent,false);
        final YCWarehousingAdapter.ViewHolder holder = new YCWarehousingAdapter.ViewHolder(view);
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("~~~~","~~~~~~");
                Intent intent = new Intent(YCWarehousingAdapter.this.activity,YCWarehousingOperation.class);
                int position = holder.getAdapterPosition();
                intent.putExtra("wl",String.valueOf(yc_cps.get(position).getWl()));
                intent.putExtra("sl",yc_cps.get(position).getJjc());
                intent.putExtra("pz",yc_cps.get(position).getPz());
                intent.putExtra("gg",yc_cps.get(position).getGg());
                intent.putExtra("xs",yc_cps.get(position).getXs());
                intent.putExtra("czfl",yc_cps.get(position).getCzfl());
                intent.putExtra("bmcl",yc_cps.get(position).getBmcl());
                intent.putExtra("cpbm",yc_cps.get(position).getCpbm());
                intent.putExtra("kbh",yc_cps.get(position).getKbh());
                intent.putExtra("gdh",yc_cps.get(position).getGdh());
                intent.putExtra("gys",yc_cps.get(position).getGys());
                intent.putExtra("lch",yc_cps.get(position).getLch());
                contxt.startActivity(intent);



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
    public void onBindViewHolder(YCWarehousingAdapter.ViewHolder holder, int position) {
        YC_CP yc_cp = yc_cps.get(position);
        holder.imageView_box_report.setImageResource(R.drawable.box_report);
        holder.cp.setText("物料："+String.valueOf(yc_cp.getWl()));
        holder.jjc.setText("件数："+yc_cp.getJjc());
        holder.hwh.setText("货位号："+yc_cp.getHwh());
    }

    @Override
    public int getItemCount() {
        return yc_cps.size();
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
