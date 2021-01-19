package com.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.collection.LLRBNode;
import com.screens.ChatActivity;
import com.screens.UserListActivity;
import com.seesame.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class OrderAdpter extends RecyclerView.Adapter<OrderAdpter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private ArrayList<HashMap<String, String>> orderedMapfilteredList;
    private ArrayList<HashMap<String, String>> dumyorderedList;
    private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private double totalDistance, ttlDistanceinmeters;
    private String currentTime, orderTime;
    private int expiryTime;
    HashMap<String, String> orderredMap;


    public OrderAdpter(Context context, ArrayList<HashMap<String, String>> orderedMapList) {
        this.mContext = context;
        this.orderedMapList = orderedMapList;
        this.dumyorderedList = new ArrayList<HashMap<String, String>>(orderedMapList);
    }

    @NonNull
    @Override
    public OrderAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.order_template, parent, false);
        return new OrderAdpter.ViewHolder(view);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull OrderAdpter.ViewHolder holder, final int position) {

        try {

            orderedMapList.get(position);

            holder.tv_hotlename.setText(orderedMapList.get(position).get("resturntName"));
            holder.tv_price.setText("Looking to add " + orderedMapList.get(position).get("orderPrice") + "$");

            double customerLat = Double.parseDouble(orderedMapList.get(position).get("deliverylatitude"));
            double customerLang = Double.parseDouble(orderedMapList.get(position).get("deliverylongititude"));
            totalDistance = calculateDistanceInKilometer(Utils.userlat, Utils.userlang, customerLat, customerLang);
            holder.tv_distance.setText(String.valueOf(totalDistance) + "kms");


            SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
            currentTime = timeFormate.format(new Date());
            expiryTime = Integer.parseInt(orderedMapList.get(position).get("expireTime"));


            String orderDate = orderedMapList.get(position).get("orderStatusDate");
            Log.i("orderDate ", orderDate);


            orderTime = orderedMapList.get(position).get("orderTime");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
            try {

                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                Date currentT = format.parse(currentTime);
                Date OldT = format.parse(orderTime);
                long differncetime = currentT.getTime() - OldT.getTime();
                //      int mins = (int) (differncetime / (1000 * 60)) % 60;


                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = dateFormat.format(calendar.getTime());

                String dateOrder = orderedMapList.get(position).get("orderStatusDate");
                Date d1 = dateFormat.parse(currentDate);
                Date d2 = dateFormat.parse(dateOrder);

                long difference_In_Time = d1.getTime() - d2.getTime();
                long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
                long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
                long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;


                holder.tv_expiretime.setText("Exprires in " + difference_In_Minutes + " mins");

                if (difference_In_Hours >= 1) {
                    // holder.tv_expiretime.setText("Exprired");
                    holder.tv_expiretime.setVisibility(View.GONE);
                    holder.btn_accept.setText("Exprired");
                   // holder.layout_expiretime.setVisibility(View.VISIBLE);
                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                    holder.tv_chat.setVisibility(View.GONE);
                    String orderId = orderedMapList.get(position).get("orderId");
                    DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                    dbreference.child(orderId).child("orderStatus").setValue("Exprired");
                    return;

                }

                String expTime = orderedMapList.get(position).get("expireTime");

                if ((expTime.equalsIgnoreCase("1 Day") || expTime.equalsIgnoreCase("3 Day") ||
                        expTime.equalsIgnoreCase("5 Day") || expTime.equalsIgnoreCase("7 Day"))) {


                } else {


                    if (difference_In_Days > 0) {

                        holder.tv_expiretime.setText("Exprired");
                        holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                        reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                        holder.tv_chat.setVisibility(View.GONE);
                        String orderId = orderedMapList.get(position).get("orderId");
                        DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                        dbreference.child(orderId).child("orderStatus").setValue("Exprired");
                        notifyDataSetChanged();

                    } else {



                        if (difference_In_Minutes < 0) {

                            holder.tv_expiretime.setText("Exprired");
                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                            holder.tv_chat.setVisibility(View.GONE);
                            String orderId = orderedMapList.get(position).get("orderId");
                            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                            dbreference.child(orderId).child("orderStatus").setValue("Exprired");
                            notifyDataSetChanged();

                        } else {

                            int differnceTime = (int) (expiryTime - difference_In_Minutes);
                            if (differnceTime <= 0) {
                                holder.tv_expiretime.setText("Exprired");
                                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                                holder.tv_chat.setVisibility(View.GONE);
                                String orderId = orderedMapList.get(position).get("orderId");
                                DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                                dbreference.child(orderId).child("orderStatus").setValue("Exprired");
                                notifyDataSetChanged();

                            } else {

                                holder.tv_expiretime.setText("Exprires in " + differnceTime + " mins");

                            }

                        }

                    }

                }


            } catch (Exception e) {

            }


            /*   Clicklistner for btn accept */
            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            holder.tv_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //    mContext.startActivity(new Intent(mContext, UserListActivity.class));
                    //  mContext.startActivity(new Intent(mContext, ChatActivity.class));


                    String orderId, customerId;
                    orderId = orderedMapList.get(position).get("orderId");
                    customerId = orderedMapList.get(position).get("customerUserId");
                    Intent orderInfo = new Intent(mContext, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderId);
                    bundle.putString("receiverId", customerId);
                    bundle.putString("senderId", Utils.userId);
                    bundle.putString("pageData", "DeliveryAgent");
                    bundle.putString("customerName", orderedMapList.get(position).get("customerName"));
                    bundle.putString("customerUserId", orderedMapList.get(position).get("customerUserId"));
                    bundle.putString("orderId", orderedMapList.get(position).get("orderId"));
                    bundle.putString("orderDate", orderedMapList.get(position).get("orderDate"));
                    bundle.putString("orderTime", orderedMapList.get(position).get("orderTime"));
                    bundle.putString("orderPrice", orderedMapList.get(position).get("orderPrice"));
                    bundle.putString("deliveryPartner", orderedMapList.get(position).get("deliveryPartner"));
                    bundle.putString("cuisines", orderedMapList.get(position).get("cuisines"));
                    bundle.putString("price", orderedMapList.get(position).get("orderPrice"));
                    bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));

                   /* bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));
                    bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));
                    bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));
                    bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));
                    bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));
                    bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));
                    bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));*/
                    orderInfo.putExtras(bundle);
                    mContext.startActivity(orderInfo);

                  /*  Intent chat = new Intent(mContext, ChatActivity.class);
                    chat.putExtra("orderId ", "abcde");
                    chat.putExtra("receiverId ", customerId);
                    mContext.startActivity(chat);*/





                   /* Log.i("Name", orderedMapList.get(position).get("resturntName"));
                    Log.i("OrderID", orderedMapList.get(position).get("orderId"));
                    Log.i("cuUserIdID", orderedMapList.get(position).get("customerUserId"));
                    */

                }
            });


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            }, 9000);


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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {


                ArrayList<HashMap<String, String>> filteredList;
                orderedMapfilteredList = new ArrayList<HashMap<String, String>>();

                String charString = charSequence.toString();

                //  orderedMapfilteredList

                if (charString.isEmpty()) {

                    // orderedMapList = orderedMapfilteredList;
                    orderedMapList.clear();
                    orderedMapList.addAll(dumyorderedList);
                    //   notifyDataSetChanged();

                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    //orderedMapList.get(position);

                      /* for(orderedMapList item  : orderedMapfilteredList){


                       }*/


                    for (int i = 0; i < orderedMapList.size(); i++) {

                        if ((orderedMapList.get(i).get("resturntName").toLowerCase().contains(filterPattern) || orderedMapList.get(i).get("orderPrice").toLowerCase().contains(filterPattern))) {

                            orderredMap = new HashMap<>();
                            orderredMap.put("orderId", orderedMapList.get(i).get("resturntName"));
                            orderredMap.put("orderDate", orderedMapList.get(i).get("orderDate"));
                            orderredMap.put("orderTime", orderedMapList.get(i).get("orderTime"));
                            orderredMap.put("orderPrice", orderedMapList.get(i).get("orderPrice"));
                            orderredMap.put("deliveryPartner", orderedMapList.get(i).get("deliveryPartner"));
                            orderredMap.put("expireTime", orderedMapList.get(i).get("expireTime"));
                            orderredMap.put("cuisines", orderedMapList.get(i).get("cuisines"));
                            orderredMap.put("orderCategoryId", orderedMapList.get(i).get("orderCategoryId"));
                            orderredMap.put("serviceSelecetd", orderedMapList.get(i).get("serviceSelecetd"));
                            orderredMap.put("orderStatus", orderedMapList.get(i).get("orderStatus"));
                            orderredMap.put("customerName", orderedMapList.get(i).get("customerName"));
                            orderredMap.put("customerUserId", orderedMapList.get(i).get("customerUserId"));
                            orderredMap.put("deliveryPostalCode", orderedMapList.get(i).get("deliveryPostalCode"));
                            orderredMap.put("deliverylatitude", orderedMapList.get(i).get("deliverylatitude"));
                            orderredMap.put("deliverylongititude", orderedMapList.get(i).get("resturntName"));
                            orderredMap.put("deliveryPlaceId", orderedMapList.get(i).get("deliveryPlaceId"));
                            orderredMap.put("deliveryAreaname", orderedMapList.get(i).get("deliveryAreaname"));
                            orderredMap.put("deliveryAddress", orderedMapList.get(i).get("deliveryAddress"));
                            orderredMap.put("partnerName", orderedMapList.get(i).get("partnerName"));

                            orderredMap.put("partnerUserId", orderedMapList.get(i).get("partnerUserId"));
                            orderredMap.put("partnerChatId", orderedMapList.get(i).get("partnerChatId"));
                            orderredMap.put("partnerPostalCode", orderedMapList.get(i).get("partnerPostalCode"));
                            orderredMap.put("partnerlatitude", orderedMapList.get(i).get("partnerlatitude"));
                            orderredMap.put("partnerlongititude", orderedMapList.get(i).get("partnerlongititude"));
                            orderredMap.put("partnerPlaceId", orderedMapList.get(i).get("partnerPlaceId"));
                            orderredMap.put("partnerAreaName", orderedMapList.get(i).get("partnerAreaName"));
                            orderredMap.put("partnerAddress", orderedMapList.get(i).get("partnerAddress"));
                            orderredMap.put("orderStatusDate", orderedMapList.get(i).get("orderStatusDate"));
                            orderredMap.put("orderStatusTime", orderedMapList.get(i).get("orderStatusTime"));
                            orderredMap.put("orderCancel", orderedMapList.get(i).get("orderCancel"));
                            orderredMap.put("orderAccepted", orderedMapList.get(i).get("orderAccepted"));
                            orderredMap.put("resturntName", orderedMapList.get(i).get("resturntName"));
                            orderredMap.put("resturntAddress", orderedMapList.get(i).get("resturntAddress"));
                            orderredMap.put("resturntPostalCode", orderedMapList.get(i).get("resturntPostalCode"));

                            try {

                                orderedMapfilteredList.add(orderredMap);
                            } catch (Exception e) {

                                //  Log.i("ListExc", e.getMessage().toString());
                            }


                        }


                    }


                }


                FilterResults results = new FilterResults();

                results.values = orderedMapfilteredList;
                return results;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {


                if (charSequence.toString().isEmpty()) {

                  /*  orderedMapList.clear();
                    orderedMapList.addAll(dumyorderedList);*/
                    notifyDataSetChanged();


                } else {
                    orderedMapList.clear();
                    orderedMapList.addAll((Collection<? extends HashMap<String, String>>) results.values);
                    notifyDataSetChanged();
                    //   Log.i("listSizechnage", String.valueOf(dumyorderedList.size()));
                }


            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_hotlename, tv_distance, tv_expiretime, tv_price, btn_accept, tv_chat;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_hotlename = itemView.findViewById(R.id.tv_hotlename);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_expiretime = itemView.findViewById(R.id.tv_expiretime);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            tv_chat = itemView.findViewById(R.id.tv_chat);

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

        ttlDistanceinmeters = (totalDistance * 1000);

        Log.i("DistnaceInMeters ", String.valueOf(ttlDistanceinmeters));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }


    private void updateDb(){


    }


}
