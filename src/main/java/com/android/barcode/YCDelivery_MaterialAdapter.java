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

public class YCDelivery_MaterialAdapter extends RecyclerView.Adapter<YCDelivery_MaterialAdapter.ViewHolder> {
    private List<YCCK_Ckdmx> yc_ckds;
    private Context contxt;
    private YCDelivery_Mteriale activity;
    public CustomDialogCommon.Builder builder ;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView material;
        TextView num;
        TextView hwh;
        TextView actualQuantity;
        TextView unloadedQty;

        public ViewHolder(View view){
            super(view);
            wlview = view;
            material = (TextView)view.findViewById(R.id.tv_yc_delivery_material);
            num = (TextView)view.findViewById(R.id.tv_yc_delivery_num);
            hwh = (TextView)view.findViewById(R.id.tv_yc_delivery_hwh);
            actualQuantity = (TextView)view.findViewById(R.id.tv_yc_delivery_sf);
            unloadedQty = (TextView)view.findViewById(R.id.tv_yc_delivery_sf1);
            imageView_box_report = (ImageView)view.findViewById(R.id.imv_yc_delivery_material);
        }
    }
    public YCDelivery_MaterialAdapter(List<YCCK_Ckdmx> list, Context context, YCDelivery_Mteriale activity){
        yc_ckds=list;
        this.contxt = (YCDelivery_Mteriale) context;
        this.activity = (YCDelivery_Mteriale) activity;
    }

    @Override
    public YCDelivery_MaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ycdelivery_material,parent,false);
        final YCDelivery_MaterialAdapter.ViewHolder holder = new YCDelivery_MaterialAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(YCDelivery_MaterialAdapter.ViewHolder holder, int position) {
        YCCK_Ckdmx ycck_ckdmx = yc_ckds.get(position);
        holder.imageView_box_report.setImageResource(R.drawable.box_report);
        holder.material.setText("物料编码:"+ycck_ckdmx.getMaterial());
        holder.num.setText("数量："+ycck_ckdmx.getNum());
        holder.hwh.setText("出库库位:"+ycck_ckdmx.getHwh());
        holder.actualQuantity.setText("实发数量"+ycck_ckdmx.getActualQuantity());
        holder.unloadedQty.setText("少发数量"+ycck_ckdmx.getUnloadedQty());
    }

    @Override
    public int getItemCount() {
        return yc_ckds.size();
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
