package com.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.screens.RatingActivity;
import com.screens.UserListActivity;
import com.seesame.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CompletedOrderAdpter extends RecyclerView.Adapter<CompletedOrderAdpter.ViewHolder> {


    private Context context;
    private ArrayList<HashMap<String, String>> orderedcompletedMapList;
    private double totalDistance;
    private String currentDate, currentTime, orderTime;
    private int expiryTime;

    public CompletedOrderAdpter(FragmentActivity activity, ArrayList<HashMap<String, String>> orderedcompletedMapList) {

        this.context = activity;
        this.orderedcompletedMapList = orderedcompletedMapList;
    }

    @NonNull
    @Override
    public CompletedOrderAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.complete_ordertemplate, parent, false);
        return new CompletedOrderAdpter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedOrderAdpter.ViewHolder holder, final int position) {


        try {

            orderedcompletedMapList.get(position);
            holder.tv_hotlename.setText(orderedcompletedMapList.get(position).get("resturntName"));
            holder.tv_price.setText(orderedcompletedMapList.get(position).get("orderPrice") + "$");

         //   holder.tv_expiretime.setText("Exprires in " + orderedcompletedMapList.get(position).get("expireTime") + "mins");


            holder.tv_expiretime.setText("Completed");


            /*Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = df.format(c.getTime());

            SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
            currentTime = timeFormate.format(new Date());
            expiryTime = Integer.parseInt(orderedcompletedMapList.get(position).get("expireTime"));

*/
         //   orderTime = orderedcompletedMapList.get(position).get("orderTime");






            holder.tv_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent orderinfo = new Intent(context, UserListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderedcompletedMapList.get(position).get("orderId"));
                    orderinfo.putExtras(bundle);
                    context.startActivity(orderinfo);


                    //   context.startActivity(new Intent(context, UserListActivity.class));


                }
            });

            holder.tv_rateUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent orderinfo = new Intent(context, RatingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderedcompletedMapList.get(position).get("orderId"));
                    bundle.putString("myuserId", orderedcompletedMapList.get(position).get("customerUserId"));
                    bundle.putString("deliverusrId", orderedcompletedMapList.get(position).get("partnerUserId"));
                    orderinfo.putExtras(bundle);
                    context.startActivity(orderinfo);


                }
            });


        } catch (Exception e) {

            Log.i("Exception", e.getMessage().toString());
        }
    }

    @Override
    public int getItemCount() {
        return orderedcompletedMapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_hotlename, tv_distance, tv_expiretime, tv_price, tv_chat,tv_rateUs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_expiretime = itemView.findViewById(R.id.tv_expiretime);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_rateUs = itemView.findViewById(R.id.tv_rateUs);
            tv_chat = itemView.findViewById(R.id.tv_chat);
            tv_hotlename = itemView.findViewById(R.id.tv_hotlename);


        }
    }
}
