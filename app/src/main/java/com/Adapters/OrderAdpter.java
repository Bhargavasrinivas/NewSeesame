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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.screens.ChatActivity;
import com.screens.UserListActivity;
import com.seesame.R;
import com.squareup.picasso.Picasso;

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
    private String currentTime, orderTime, userId, info;
    private int expiryTime;
    OrderAdpter orderAdpter;
    HashMap<String, String> orderredMap;
    private  Refresh refresh;



    public OrderAdpter(Context context, ArrayList<HashMap<String, String>> orderedMapList, String info,Refresh refresh) {
        this.mContext = context;
        this.orderedMapList = orderedMapList;
        this.info = info;
        this.refresh = refresh;
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
            double customerLat = Double.parseDouble(orderedMapList.get(position).get("deliverylatitude"));
            double customerLang = Double.parseDouble(orderedMapList.get(position).get("deliverylongititude"));


            if (info.equalsIgnoreCase("Change")) {
                totalDistance = calculateDistanceInKilometer(Utils.userChanegedlat, Utils.userChanegedlang, customerLat, customerLang);
            } else {
                totalDistance = calculateDistanceInKilometer(Utils.userlat, Utils.userlang, customerLat, customerLang);
            }
            //  if (totalDistance <= 1) {

            holder.tv_noOrders.setVisibility(View.GONE);
            if (orderedMapList.get(position).get("customerUserId").equalsIgnoreCase(Utils.userId)) {

                //holder.tv_distance.setText(" ");
                holder.tv_distance.setVisibility(View.GONE);
            } else {
                holder.tv_distance.setText(String.valueOf(totalDistance) + "kms");
            }

            String groupData = orderedMapList.get(position).get("groupData");

            if (groupData.equalsIgnoreCase("Sell Home Made Product")) {
                holder.tv_pricelbl.setText("Qunatity-");

                String qty = orderedMapList.get(position).get("orderQuantity");
                String price = orderedMapList.get(position).get("orderPrice");
                holder.tv_price.setText(qty + " " + price + "$");

            } else {
                holder.tv_price.setText(orderedMapList.get(position).get("orderPrice") + "$");
            }

            Picasso.with(mContext).load(orderedMapList.get(position).get("orderImg")).fit().into(holder.img_order);
            holder.tv_hotlename.setText(orderedMapList.get(position).get("resturntName"));
            SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
            currentTime = timeFormate.format(new Date());


            String orderDate = orderedMapList.get(position).get("orderStatusDate");

            userId = orderedMapList.get(position).get("customerUserId");

            orderTime = orderedMapList.get(position).get("orderTime");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
            try {

                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                Date currentT = format.parse(currentTime);
                Date OldT = format.parse(orderTime);
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


                if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                    //    holder.layout_expiretime.setVisibility(View.VISIBLE);
                 /*   holder.tv_expiretime.setText("Expired");
                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));*/
                    holder.tv_expiretime.setVisibility(View.GONE);
                    holder.tv_distance.setVisibility(View.VISIBLE);
                    holder.tv_distance.setText("Expired");

                }

                String expTime = orderedMapList.get(position).get("expireTime");

                if ((expTime.equalsIgnoreCase("1 Day") || expTime.equalsIgnoreCase("3 Day") ||
                        expTime.equalsIgnoreCase("5 Day") || expTime.equalsIgnoreCase("7 Day"))) {


                    if (expTime.equalsIgnoreCase("1 Day")) {

                       /* long finalhour = 24 - 24;
                        long finalminutes = 60 - 60;*/
                        long hoursaDay = 24;
                        long minsinhour = 60;

                        long finalhour = 24 - difference_In_Hours;
                        long finalminutes = 60 - difference_In_Minutes;
                        if (finalhour == 0) {

                            if (finalminutes == 0) {
                                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                holder.tv_expiretime.setText(" Expired");
                                holder.layout_main.setVisibility(View.GONE);
                                String orderId = orderedMapList.get(position).get("orderId");
                                DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                                dbreference.child(orderId).child("orderStatus").setValue("Expired");
                                notifyDataSetChanged();
                            } else {

                                holder.tv_expiretime.setText(" Expires in " + finalminutes + " Minutes");
                            }

                        } else {

                            if (difference_In_Hours == 0) {
                                holder.tv_expiretime.setText("Expires in " + 1 + " Day");

                            } else {

                                //  holder.tv_expiretime.setText(" Expires in " + finalhour + " hours" + " " + finalminutes + " Minutes");
                                holder.tv_expiretime.setText(" Expires in " + finalhour + " hours");
                            }

                        }


                    } else if (expTime.equalsIgnoreCase("3 Day")) {

                        long daysLeftover = (3 - difference_In_Days);
                        if (difference_In_Days > 3) {
                            holder.tv_expiretime.setText(" Expired");
                            holder.layout_main.setVisibility(View.GONE);
                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                            holder.tv_chat.setVisibility(View.GONE);
                            String orderId = orderedMapList.get(position).get("orderId");
                            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                            dbreference.child(orderId).child("orderStatus").setValue("Expired");
                            notifyDataSetChanged();
                        } else if ((daysLeftover == 1 || daysLeftover == 0)) {
                            long finalhour = 24 - difference_In_Hours;
                            long finalminutes = 60 - difference_In_Minutes;
                            if (finalhour == 0) {

                                if (difference_In_Minutes == 0) {
                                    holder.tv_expiretime.setText(" Expired");
                                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                    holder.layout_main.setVisibility(View.GONE);
                                    reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                                    holder.tv_chat.setVisibility(View.GONE);
                                    String orderId = orderedMapList.get(position).get("orderId");
                                    DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                                    dbreference.child(orderId).child("orderStatus").setValue("Expired");
                                    notifyDataSetChanged();
                                } else {
                                    holder.tv_expiretime.setText(" Expires in " + finalminutes);

                                }
                            } else {
                                //   holder.tv_expiretime.setText(" Expires in " + finalhour + " hours" + " " + finalminutes + " Minutes");
                                holder.tv_expiretime.setText(" Expires in " + finalhour + " hours");

                            }
                        } else {
                            long daysLeft = (3 - difference_In_Days);
                            holder.tv_expiretime.setText(" Expires in " + daysLeft + " Days ");
                        }

                    } else if (expTime.equalsIgnoreCase("5 Day")) {

                        long daysLeftover = (5 - difference_In_Days);
                        if (difference_In_Days > 5) {
                            holder.tv_expiretime.setText(" Expired");
                            holder.layout_main.setVisibility(View.GONE);
                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            String orderId = orderedMapList.get(position).get("orderId");
                            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                            dbreference.child(orderId).child("orderStatus").setValue("Expired");
                            notifyDataSetChanged();
                        } else if ((daysLeftover == 1 || daysLeftover == 0)) {
                            long finalhour = 24 - difference_In_Hours;
                            long finalminutes = 60 - difference_In_Minutes;
                            if (finalhour == 0) {

                                if (difference_In_Minutes == 0) {
                                    holder.tv_expiretime.setText(" Expired");
                                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                    holder.layout_main.setVisibility(View.GONE);
                                    String orderId = orderedMapList.get(position).get("orderId");
                                    DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                                    dbreference.child(orderId).child("orderStatus").setValue("Expired");
                                    notifyDataSetChanged();
                                } else {
                                    holder.tv_expiretime.setText(" Expires in " + finalminutes);

                                }
                            } else {
                                //holder.tv_expiretime.setText(" Expires in " + finalhour + " hours" + " " + finalminutes + " Minutes");
                                holder.tv_expiretime.setText(" Expires in " + finalhour + " hours");
                            }
                        } else {
                            long daysLeft = (5 - difference_In_Days);
                            holder.tv_expiretime.setText(" Expires in " + daysLeft + " Days ");
                        }

                    } else {

                        // 7 Days

                        long daysLeftover = (7 - difference_In_Days);
                        if (difference_In_Days > 7) {
                            holder.tv_expiretime.setText(" Expired");
                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            holder.layout_main.setVisibility(View.GONE);
                            String orderId = orderedMapList.get(position).get("orderId");
                            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                            dbreference.child(orderId).child("orderStatus").setValue("Expired");
                            notifyDataSetChanged();

                        } else if ((daysLeftover == 1 || daysLeftover == 0)) {
                            long finalhour = 24 - difference_In_Hours;
                            long finalminutes = 60 - difference_In_Minutes;
                            if (finalhour == 0) {

                                if (difference_In_Minutes == 0) {
                                    holder.tv_expiretime.setText(" Expired");
                                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                    holder.layout_main.setVisibility(View.GONE);
                                    String orderId = orderedMapList.get(position).get("orderId");
                                    DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                                    dbreference.child(orderId).child("orderStatus").setValue("Expired");
                                    notifyDataSetChanged();
                                } else {
                                    holder.tv_expiretime.setText(" Expires in " + finalminutes);

                                }
                            } else {
                                //   holder.tv_expiretime.setText(" Expires in " + finalhour + " hours" + " " + finalminutes + " Minutes");
                                holder.tv_expiretime.setText(" Expires in " + finalhour + " hours");
                            }
                        } else {
                            long daysLeft = (7 - difference_In_Days);
                            holder.tv_expiretime.setText(" Expires in " + daysLeft + " Days ");
                        }

                    }
                } else {


                    if (difference_In_Hours >= 1) {

                        holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                        holder.tv_expiretime.setText("Expired");
                        holder.tv_chat.setVisibility(View.GONE);
                        String orderId = orderedMapList.get(position).get("orderId");
                        DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                        dbreference.child(orderId).child("orderStatus").setValue("Expired");
                        holder.layout_main.setVisibility(View.GONE);
                        return;

                    }
                    String time = orderedMapList.get(position).get("expireTime");
                    String expiryTimes = time.substring(0, 2);
                    //    expiryTime = Integer.parseInt(orderedMapList.get(position).get("expireTime"));
                    expiryTime = Integer.parseInt(expiryTimes);

                    //  expiryTime = Integer.parseInt(orderedMapList.get(position).get("expireTime"));
                    if (difference_In_Days > 0) {

                        holder.tv_expiretime.setText("Expired");
                        holder.layout_main.setVisibility(View.GONE);
                        holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                        reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                        holder.tv_chat.setVisibility(View.GONE);
                        String orderId = orderedMapList.get(position).get("orderId");
                        DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                        dbreference.child(orderId).child("orderStatus").setValue("Expired");
                        notifyDataSetChanged();
                    } else {


                        if (difference_In_Minutes < 0) {

                            holder.tv_expiretime.setText("Expired");
                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                            holder.tv_chat.setVisibility(View.GONE);
                            String orderId = orderedMapList.get(position).get("orderId");
                            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                            dbreference.child(orderId).child("orderStatus").setValue("Expired");
                            notifyDataSetChanged();
                            holder.layout_main.setVisibility(View.GONE);
                        } else {

                            int differnceTime = (int) (expiryTime - difference_In_Minutes);
                            if (differnceTime <= 0) {
                                holder.tv_expiretime.setText("Expired");
                                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                                holder.tv_chat.setVisibility(View.GONE);
                                String orderId = orderedMapList.get(position).get("orderId");
                                DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
                                dbreference.child(orderId).child("orderStatus").setValue("Expired");
                                notifyDataSetChanged();
                                holder.layout_main.setVisibility(View.GONE);
                            } else {

                                holder.tv_expiretime.setText("Exprires in " + differnceTime + " mins");

                            }

                        }

                    }

                }


            } catch (Exception e) {

            }

/*

            } else {

                holder.layout_main.setVisibility(View.GONE);
                holder.tv_noOrders.setVisibility(View.VISIBLE);

            }
*/



            /*   Clicklistner for btn accept */
            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            holder.tv_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (orderedMapList.get(position).get("customerUserId").equalsIgnoreCase(Utils.userId)) {


                        if (orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Accepted")) {

                            String resName = orderedMapList.get(position).get("resturntName");

                            Intent userInfo = new Intent(mContext, ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("orderId", orderedMapList.get(position).get("orderId"));
                            bundle.putString("receiverId", orderedMapList.get(position).get("partnerUserId"));
                            bundle.putString("senderId", orderedMapList.get(position).get("customerUserId"));
                            bundle.putString("pageData", "Customer");
                            bundle.putString("orderName", orderedMapList.get(position).get("orderName"));
                            bundle.putString("cuisines", orderedMapList.get(position).get("cuisines"));
                            bundle.putString("price", orderedMapList.get(position).get("orderPrice"));
                            bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));
                            bundle.putString("orderStatus", orderedMapList.get(position).get("orderStatus"));
                            bundle.putString("groupData", orderedMapList.get(position).get("groupData"));
                            bundle.putString("deliveryPartner", orderedMapList.get(position).get("deliveryPartner"));
                            userInfo.putExtras(bundle);
                            mContext.startActivity(userInfo);

                        } else {

                            Intent orderinfo = new Intent(mContext, UserListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("orderId", orderedMapList.get(position).get("orderId"));
                            bundle.putString("OrderName", orderedMapList.get(position).get("resturntName"));
                            bundle.putString("cuisines", orderedMapList.get(position).get("cuisines"));
                            bundle.putString("price", orderedMapList.get(position).get("orderPrice"));
                            bundle.putString("orderStatus", orderedMapList.get(position).get("orderStatus"));
                            bundle.putString("groupData", orderedMapList.get(position).get("groupData"));
                            bundle.putString("deliveryPartner", orderedMapList.get(position).get("deliveryPartner"));
                            bundle.putString("pageData", "Mychats");
                            orderinfo.putExtras(bundle);
                            mContext.startActivity(orderinfo);

                        }


                    } else {

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
                        bundle.putString("orderStatus", orderedMapList.get(position).get("orderStatus"));
                        bundle.putString("groupData", orderedMapList.get(position).get("groupData"));
                        bundle.putString("deliveryPartner", orderedMapList.get(position).get("deliveryPartner"));
                        bundle.putString("partnerName", orderedMapList.get(position).get("customerName"));
                        orderInfo.putExtras(bundle);
                        mContext.startActivity(orderInfo);

                    }


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

    }


    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
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

                        String restunntName = orderedMapList.get(i).get("resturntName").toLowerCase();

                        //||restunntName.contains(filterPattern)

                        if ((restunntName.contains(charString.toLowerCase()))) {

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


                        } else {


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
                   /* orderedMapList.clear();
                    orderedMapList = (ArrayList<HashMap<String, String>>) results.values;
                   // notifyDataSetChanged();
                    refresh.yourDesiredMethod();*/



                 //  mContext.orderAdpter. notifyDataSetChanged();

                    orderedMapList.clear();
                    orderedMapList.addAll((Collection<? extends HashMap<String, String>>) results.values);
                    refresh.yourDesiredMethod();
                }


            }
        };
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_hotlename, tv_distance, tv_expiretime, tv_price, btn_accept, tv_chat, tv_noOrders, tv_pricelbl;
        private ImageView img_order;
        private View layout_main;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_hotlename = itemView.findViewById(R.id.tv_hotlename);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_expiretime = itemView.findViewById(R.id.tv_expiretime);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            tv_chat = itemView.findViewById(R.id.tv_chat);
            img_order = itemView.findViewById(R.id.img_order);
            layout_main = itemView.findViewById(R.id.layout_main);
            tv_noOrders = itemView.findViewById(R.id.tv_noOrders);
            tv_pricelbl = itemView.findViewById(R.id.tv_pricelbl);
        }
    }

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

    private void deletingCategoriData(String categorie) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.keepSynced(true);
        reference.orderByChild("categorieName").equalTo(categorie).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    //   edt_username.setText((CharSequence) snapshot1.child("userName").getValue());

                    long count = (long) snapshot1.child("count").getValue();

                    String categorieId = (String) snapshot1.child("id").getValue();

                    Log.i("Count ", String.valueOf(count));


                    //  if (count == 0) {

                    count = count - 1;
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
                    //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
                    reference.child(categorieId).child("count").setValue(count);
                    Log.i("CountIncrese ", String.valueOf(count));
                    //   }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
