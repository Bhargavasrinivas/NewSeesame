package com.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.Utils;
import com.google.firebase.database.collection.LLRBNode;
import com.seesame.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class OrderAdpter extends RecyclerView.Adapter<OrderAdpter.ViewHolder> {

    private Context mContext;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private double totalDistance;
    private String currentTime, orderTime;
    private int expiryTime;

    public OrderAdpter(FragmentActivity activity, ArrayList<HashMap<String, String>> orderedMapList) {
        this.mContext = activity;
        this.orderedMapList = orderedMapList;
    }

    @NonNull
    @Override
    public OrderAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.order_template, parent, false);
        return new OrderAdpter.ViewHolder(view);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull OrderAdpter.ViewHolder holder, int position) {

        try {

            orderedMapList.get(position);
            holder.tv_hotlename.setText(orderedMapList.get(position).get("deliveryAreaname"));
            holder.tv_price.setText(orderedMapList.get(position).get("orderPrice") + "$");


            double customerLat = Double.parseDouble(orderedMapList.get(position).get("deliverylatitude"));
            double customerLang = Double.parseDouble(orderedMapList.get(position).get("deliverylongititude"));
            totalDistance = calculateDistanceInKilometer(Utils.userlat, Utils.userlang, customerLat, customerLang);
            holder.tv_distance.setText(String.valueOf(totalDistance) + "kms");


            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = df.format(c.getTime());

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

                if (mins < 0) {
                    holder.tv_expiretime.setText("Exprired");
                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));

                } else {
                    int differnceTime = expiryTime - mins;
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









    /*    for(int i = 0; i>orderedMapList.size();i++){
            holder.tv_hotlename.setText(orderedMapList.get(i).get("deliveryAreaname"));
            holder.tv_hotlename.setText(orderedMapList.get(i).get("orderPrice"));
            String UserId = orderedMapList.get(i).get("customerUserId");
            Log.i("AdpterUserId ", UserId);
        }*/
    }

    @Override
    public int getItemCount() {


        return orderedMapList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_hotlename, tv_distance, tv_expiretime, tv_price, btn_accept;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_hotlename = itemView.findViewById(R.id.tv_hotlename);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_expiretime = itemView.findViewById(R.id.tv_expiretime);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_accept = itemView.findViewById(R.id.btn_accept);


        }
    }


/*
    private function distance(lat1, lon1, lat2, lon2) {
        var p = 0.017453292519943295;    // Math.PI / 180
        var c = Math.cos;
        var a = 0.5 - c((lat2 - lat1) * p)/2 +
                c(lat1 * p) * c(lat2 * p) *
                        (1 - c((lon2 - lon1) * p))/2;

        return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
    }




    */


    public double calculateDistanceInKilometer(double userLat, double userLng,
                                               double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));


        //   Log.i("Distance ", String.valueOf((int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c))));
        totalDistance = Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c);

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }


}
