package com.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.Utils;
import com.seesame.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MyorderAdpter extends RecyclerView.Adapter<MyorderAdpter.Viewholder> {


    private Context context;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private double totalDistance;
    private String currentDate, currentTime, orderTime;
    private int expiryTime;

    public MyorderAdpter(FragmentActivity activity, ArrayList<HashMap<String, String>> orderedMapList) {

        this.context = activity;
        this.orderedMapList = orderedMapList;


    }

    @NonNull
    @Override
    public MyorderAdpter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_ordertemplate, parent, false);
        return new MyorderAdpter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyorderAdpter.Viewholder holder, int position) {


        try {

            orderedMapList.get(position);
            holder.tv_hotlename.setText(orderedMapList.get(position).get("resturntName"));
            holder.tv_price.setText(orderedMapList.get(position).get("orderPrice") + "$");
            holder.tv_expiretime.setText("Exprires in " + orderedMapList.get(position).get("expireTime") + "mins");

            double customerLat = Double.parseDouble(orderedMapList.get(position).get("deliverylatitude"));
            double customerLang = Double.parseDouble(orderedMapList.get(position).get("deliverylongititude"));

/*
            totalDistance = calculateDistanceInKilometer(Utils.userlat, Utils.userlang, customerLat, customerLang);
            Log.i("totalDistance", String.valueOf(totalDistance));
            holder.tv_distance.setText(String.valueOf(totalDistance) + "kms");*/


            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = df.format(c.getTime());

            SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
            currentTime = timeFormate.format(new Date());
            expiryTime = Integer.parseInt(orderedMapList.get(position).get("expireTime"));


            orderTime = orderedMapList.get(position).get("orderTime");

            Log.i("AdpterOrdertime ", orderTime);

            try {

                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                Date currentT = format.parse(currentTime);
                Date OldT = format.parse(orderTime);
                long differncetime = currentT.getTime() - OldT.getTime();
                int mins = (int) (differncetime / (1000 * 60)) % 60;
                Log.i("ExpireTime ", String.valueOf(mins));

                int differnceTime = expiryTime - mins;
                if (differnceTime <= 0) {
                    holder.tv_expiretime.setText("Exprired");
                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                } else {
                    Log.i("GrandTime ", String.valueOf(differnceTime));
                    holder.tv_expiretime.setText("Exprires in " + differnceTime + " mins");
                }
            } catch (Exception e) {

            }



            /*   Clicklistner for btn accept */
            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });


        } catch (Exception e) {

            Log.i("Exception", e.getMessage().toString());
        }


    }

    @Override
    public int getItemCount() {
        return orderedMapList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView tv_hotlename, tv_distance, tv_expiretime, tv_price, btn_accept;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tv_hotlename = itemView.findViewById(R.id.tv_hotlename);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_expiretime = itemView.findViewById(R.id.tv_expiretime);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_accept = itemView.findViewById(R.id.btn_accept);


        }
    }
}
