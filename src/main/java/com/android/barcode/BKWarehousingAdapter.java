package com.android.barcode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BKWarehousingAdapter extends RecyclerView.Adapter<BKWarehousingAdapter.ViewHolder> {
    private List<BK_info> mWl_info;
    private Context contxt;
    private BKWarehousing activity;
    public CustomDialogCommon.Builder builder ;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View wlview;
        ImageView imageView_box_report;
        TextView tv_bkrk_wl;
        TextView tv_bkrk_hwh;
        TextView tv_bkrk_sl;

        public ViewHolder(View view){
            super(view);
            wlview = view;
            tv_bkrk_wl = (TextView) view.findViewById(R.id.tv_bkrk_wl);
            tv_bkrk_hwh = (TextView)view.findViewById(R.id.tv_bkrk_hwh);
            tv_bkrk_sl = (TextView) view.findViewById(R.id.tv_bkrk_sl);
            imageView_box_report = (ImageView)view.findViewById(R.id.imv_bkrk_dis);
        }
    }
    public BKWarehousingAdapter(List<BK_info> wllist,Context context, BKWarehousing activity){
        mWl_info=wllist;
        this.contxt = (BKWarehousing) context;
        this.activity = (BKWarehousing) activity;
    }

    @Override
    public BKWarehousingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bkwarehousing,parent,false);
        final BKWarehousingAdapter.ViewHolder holder = new BKWarehousingAdapter.ViewHolder(view);
        holder.wlview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                final BK_info bk_info = mWl_info.get(position);
                ///Toast.makeText(view.getContext(),"点击"+String.valueOf(position),Toast.LENGTH_SHORT).show();
                builder = new CustomDialogCommon.Builder(contxt);
                builder.setTitle("半库入库");
                builder.setMessage("物料:"+bk_info.getWl());
                Log.d("选择产品",bk_info.getWl());
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
                        confirmrk(bk_info.getWl(),bk_info.getHwh(),builder.cpsl.getText().toString(),ListMap.getuserid());
                        ///隐藏
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                builder.cpmph.setEnabled(false);
                builder.cpsl.setText("0");
                builder.cpmph.setText("货位号:"+bk_info.getHwh());
                /*
                Intent intent = new Intent();
                intent.setClass(contxt,BoxDetail.class);
                intent.putExtra("zt","cx");
                BK_info support_info = mWl_info.get(position);
                intent.putExtra("box_code",String.valueOf(support_info.getWl()));
                intent.putExtra("box_id",String.valueOf(support_info.getHwh()));
                contxt.startActivity(intent);
                */
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
    public void onBindViewHolder(BKWarehousingAdapter.ViewHolder holder, int position) {
        BK_info wl_info = mWl_info.get(position);
        holder.imageView_box_report.setImageResource(R.drawable.box_report);
        holder.tv_bkrk_wl.setText("物料："+String.valueOf(wl_info.getWl()));
        holder.tv_bkrk_hwh.setText("货位号："+wl_info.getHwh());
        holder.tv_bkrk_sl.setText("结存："+wl_info.getSl());
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
    ///确认入库
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

    }


}
