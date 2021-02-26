package com.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.screens.APIService;
import com.screens.ChatActivity;
import com.screens.RatingActivity;
import com.screens.UserListActivity;
import com.seesame.Client;
import com.seesame.Data;
import com.seesame.MyResponse;
import com.seesame.R;
import com.seesame.Sender;
import com.seesame.Token;
import com.seesame.ui.Myorders.MyorderFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyorderAdpter extends RecyclerView.Adapter<MyorderAdpter.Viewholder> {


    private Context context;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private double totalDistance;
    private String currentDate, currentTime, orderTime, cuisines, orderDateTime, orderStatus;
    private int expiryTime;
    List<String> timeList;
    private String spinnerVal;
    private MyorderAdpter myorderAdpter;
    HashMap<String, String> orderredMap;
    APIService apiService;

    public MyorderAdpter(FragmentActivity activity, ArrayList<HashMap<String, String>> orderedMapList, String orderStatus) {

        this.context = activity;
        this.orderedMapList = orderedMapList;
        this.orderStatus = orderStatus;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyorderAdpter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_ordertemplate, parent, false);
        return new MyorderAdpter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyorderAdpter.Viewholder holder, final int position) {


        try {


            orderedMapList.get(position);

            //  Toast.makeText(context, orderStatus, Toast.LENGTH_SHORT).show();
            Picasso.with(context).load(orderedMapList.get(position).get("orderImg")).fit().into(holder.img_myorder);
            holder.tv_hotlename.setText(orderedMapList.get(position).get("resturntName"));
            holder.tv_price.setText(orderedMapList.get(position).get("orderPrice") + "$");


            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                holder.btn_accept.setText(" ");
                holder.layout_accpet.setVisibility(View.GONE);
                holder.img_closeOrder.setVisibility(View.GONE);
            } else {
                holder.btn_accept.setText(orderedMapList.get(position).get("orderStatus"));
            }

            SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
            currentTime = timeFormate.format(new Date());

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat simpledf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            orderDateTime = simpledf.format(cal.getTime());

            holder.layout_expiretime.setVisibility(View.GONE);
            holder.switch_order.setVisibility(View.GONE);
            timeList = new ArrayList<>();
            timeList.add("Extend expiry time");
            timeList.add("15 mins");
            timeList.add("30 mins");
            timeList.add("45 mins");
            timeList.add("60 mins");
            timeList.add("1 Day");
            timeList.add("2 Day");
            timeList.add("3 Day");
            timeList.add("5 Day");
            timeList.add("7 Day");
            ArrayAdapter timedpter = new ArrayAdapter(context, R.layout.spinner_item, timeList);
            timedpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner_expiretime.setAdapter(timedpter);


            double customerLat = Double.parseDouble(orderedMapList.get(position).get("deliverylatitude"));
            double customerLang = Double.parseDouble(orderedMapList.get(position).get("deliverylongititude"));


            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = df.format(c.getTime());

            SimpleDateFormat timeFormatenew = new SimpleDateFormat("HH:mm:ss");
            currentTime = timeFormatenew.format(new Date());


            orderTime = orderedMapList.get(position).get("orderTime");


            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Accepted"))) {
                holder.tv_expiretime.setVisibility(View.GONE);
                holder.switch_order.setVisibility(View.VISIBLE);
                holder.img_closeOrder.setVisibility(View.GONE);
            }

            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                holder.layout_expiretime.setVisibility(View.VISIBLE);
                holder.tv_expiretime.setText("Expired");
                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
            }


            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Cancelled"))) {
                // holder.layout_expiretime.setVisibility(View.VISIBLE);
                holder.img_closeOrder.setVisibility(View.GONE);
                holder.tv_expiretime.setVisibility(View.GONE);
                holder.tv_chat.setVisibility(View.GONE);

            }

            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Completed"))) {
                holder.tv_expiretime.setVisibility(View.GONE);
                holder.switch_order.setVisibility(View.GONE);
                holder.img_closeOrder.setVisibility(View.GONE);
                holder.tv_chat.setVisibility(View.GONE);
            }

            holder.layout_main.setVisibility(View.VISIBLE);

            holder.spinner_expiretime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spinnerposition, long id) {

                    try {
                        String orderId = orderedMapList.get(position).get("orderId");

                        spinnerVal = String.valueOf(parent.getItemAtPosition(spinnerposition));

                        if (!spinnerVal.equalsIgnoreCase("Extend expiry time")) {


                            holder.layout_expiretime.setVisibility(View.GONE);

                            holder.tv_expiretime.setTextColor(Color.parseColor("#4c4c4c"));
                        //    holder.tv_expiretime.setText("Expired");
                            holder.layout_accpet.setVisibility(View.VISIBLE);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                            reference.child(orderId).child("orderStatus").setValue("Pending");
                            reference.child(orderId).child("expireTime").setValue(spinnerVal);
                            reference.child(orderId).child("orderDate").setValue(currentDate);
                            reference.child(orderId).child("orderStatusDate").setValue(orderDateTime);
                            reference.child(orderId).child("orderStatusTime").setValue(currentTime);

                            orderDetailtoCancel("Expired");

                        }
                    } catch (Exception e) {

                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
            try {

                String orderStatus = orderedMapList.get(position).get("orderStatus");

                if ((orderStatus.equalsIgnoreCase("Pending"))) {
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


                    String expTime = orderedMapList.get(position).get("expireTime");

                    if ((expTime.equalsIgnoreCase("1 Day") || expTime.equalsIgnoreCase("3 Day") ||
                            expTime.equalsIgnoreCase("5 Day") || expTime.equalsIgnoreCase("7 Day"))) {


                        if (expTime.equalsIgnoreCase("1 Day")) {

                            long hoursaDay = 24;
                            long minsinhour = 60;

                            long finalhour = 24 - difference_In_Hours;
                            long finalminutes = 60 - difference_In_Minutes;
                            if (finalhour == 0) {

                                if (finalminutes == 0) {
                                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                    holder.tv_expiretime.setText(" Expired");

                                    holder.spinner_expiretime.setVisibility(View.VISIBLE);
                                    String orderId = orderedMapList.get(position).get("orderId");
                                    if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                                    } else {
                                        changeOrderStatudsExpired(orderId);
                                    }

                                    // Condintion to hide expired orders
                                    if (orderStatus.equalsIgnoreCase("Pending")) {
                                        holder.layout_main.setVisibility(View.GONE);
                                    }


                                } else {
                                    holder.layout_main.setVisibility(View.VISIBLE);
                                    holder.tv_expiretime.setText(" Expires in " + finalminutes + " mins");
                                }

                            } else {

                                if (difference_In_Hours == 0) {
                                    holder.tv_expiretime.setText("Expires in " + 1 + " Day");

                                } else {

                                    holder.layout_main.setVisibility(View.VISIBLE);
                                    holder.tv_expiretime.setText(" Expires in " + finalhour + " hrs");
                                }

                            }


                        } else if (expTime.equalsIgnoreCase("3 Day")) {

                            long daysLeftover = (3 - difference_In_Days);
                            if (difference_In_Days > 3) {
                                holder.tv_expiretime.setText(" Expired");
                                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                // Condintion to hide expired orders
                                if (orderStatus.equalsIgnoreCase("Pending")) {
                                    holder.layout_main.setVisibility(View.GONE);
                                }

                            } else if (daysLeftover == 1) {
                                long finalhour = 24 - difference_In_Hours;
                                long finalminutes = 60 - difference_In_Minutes;
                                if (finalhour == 0) {

                                    if (difference_In_Minutes == 0) {
                                        holder.tv_expiretime.setText(" Expired");
                                        // Condintion to hide expired orders
                                        if (orderStatus.equalsIgnoreCase("Pending")) {
                                            holder.layout_main.setVisibility(View.GONE);
                                        }

                                        holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                        holder.spinner_expiretime.setVisibility(View.VISIBLE);
                                        String orderId = orderedMapList.get(position).get("orderId");
                                        if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                                        } else {
                                            changeOrderStatudsExpired(orderId);
                                        }

                                    } else {
                                        holder.layout_main.setVisibility(View.VISIBLE);
                                        holder.tv_expiretime.setText(" Expires in " + finalminutes + " mins");

                                    }
                                } else {
                                    holder.tv_expiretime.setText(" Expires in " + finalhour + " hrs");
                                }
                            } else {
                                long daysLeft = (3 - difference_In_Days);
                                holder.tv_expiretime.setText(" Expires in " + daysLeft + " Days ");
                            }

                        } else if (expTime.equalsIgnoreCase("5 Day")) {

                            long daysLeftover = (5 - difference_In_Days);
                            if (difference_In_Days > 5) {
                                holder.tv_expiretime.setText("Expired");
                                // Condintion to hide expired orders
                                if (orderStatus.equalsIgnoreCase("Pending")) {
                                    holder.layout_main.setVisibility(View.GONE);
                                }

                                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            } else if (daysLeftover == 1) {
                                long finalhour = 24 - difference_In_Hours;
                                long finalminutes = 60 - difference_In_Minutes;
                                if (finalhour == 0) {

                                    if (difference_In_Minutes == 0) {
                                        holder.tv_expiretime.setText(" Expired");
                                        // Condintion to hide expired orders
                                        if (orderStatus.equalsIgnoreCase("Pending")) {
                                            holder.layout_main.setVisibility(View.GONE);
                                        }

                                        holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                        holder.spinner_expiretime.setVisibility(View.VISIBLE);
                                        String orderId = orderedMapList.get(position).get("orderId");
                                        if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                                        } else {
                                            changeOrderStatudsExpired(orderId);
                                        }

                                    } else {
                                        holder.layout_main.setVisibility(View.VISIBLE);
                                        holder.tv_expiretime.setText(" Expires in " + finalminutes + " mins");

                                    }
                                } else {
                                    //  holder.tv_expiretime.setText(" Expires in " + finalhour + " hours" + " " + finalminutes + " Minutes");
                                    holder.tv_expiretime.setText(" Expires in " + finalhour + " hrs");
                                    holder.layout_main.setVisibility(View.VISIBLE);
                                }
                            } else {
                                holder.layout_main.setVisibility(View.VISIBLE);
                                long daysLeft = (5 - difference_In_Days);
                                holder.tv_expiretime.setText(" Expires in " + daysLeft + " Days ");
                            }

                        } else {

                            // 7 Days

                            long daysLeftover = (7 - difference_In_Days);
                            if (difference_In_Days > 7) {
                                holder.tv_expiretime.setText(" Expired");
                                // Condintion to hide expired orders
                                if (orderStatus.equalsIgnoreCase("Pending")) {
                                    holder.layout_main.setVisibility(View.GONE);
                                }

                                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                holder.spinner_expiretime.setVisibility(View.VISIBLE);
                                String orderId = orderedMapList.get(position).get("orderId");
                                if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                                } else {
                                    changeOrderStatudsExpired(orderId);
                                }


                            } else if (daysLeftover == 1) {
                                long finalhour = 24 - difference_In_Hours;
                                long finalminutes = 60 - difference_In_Minutes;
                                if (finalhour == 0) {

                                    if (difference_In_Minutes == 0) {
                                        holder.tv_expiretime.setText(" Expired");
                                        // Condintion to hide expired orders
                                        if (orderStatus.equalsIgnoreCase("Pending")) {
                                            holder.layout_main.setVisibility(View.GONE);
                                        }

                                        holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                    } else {
                                        holder.layout_main.setVisibility(View.VISIBLE);
                                        holder.tv_expiretime.setText(" Expires in " + finalminutes + " mins");

                                    }
                                } else {
                                    //  holder.tv_expiretime.setText(" Expires in " + finalhour + " hours" + " " + finalminutes + " Minutes");
                                    holder.tv_expiretime.setText(" Expires in " + finalhour + " hrs");
                                    holder.layout_main.setVisibility(View.VISIBLE);
                                }
                            } else {
                                holder.layout_main.setVisibility(View.VISIBLE);
                                long daysLeft = (7 - difference_In_Days);
                                holder.tv_expiretime.setText(" Expires in " + daysLeft + " Days ");
                            }

                        }
                    } else {

                        String time = orderedMapList.get(position).get("expireTime");
                        String expiryTimes = time.substring(0, 2);
                        //    expiryTime = Integer.parseInt(orderedMapList.get(position).get("expireTime"));
                        expiryTime = Integer.parseInt(expiryTimes);

                        if (difference_In_Hours >= 1) {

                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            holder.tv_expiretime.setText("Expired");
                            holder.tv_chat.setVisibility(View.GONE);
                            String orderId = orderedMapList.get(position).get("orderId");
                            // Condintion to hide expired orders
                            if (orderStatus.equalsIgnoreCase("Pending")) {
                                holder.layout_main.setVisibility(View.GONE);
                            }

                            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                            } else {
                                changeOrderStatudsExpired(orderId);
                            }
                            holder.spinner_expiretime.setVisibility(View.VISIBLE);
                            return;

                        }


                        if (difference_In_Days > 0) {

                            holder.tv_expiretime.setText("Expired");
                            // Condintion to hide expired orders
                            if (orderStatus.equalsIgnoreCase("Pending")) {
                                holder.layout_main.setVisibility(View.GONE);
                            }

                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            //  reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Expired");
                            holder.tv_chat.setVisibility(View.GONE);

                            String orderId = orderedMapList.get(position).get("orderId");
                            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                            } else {
                                changeOrderStatudsExpired(orderId);
                            }
                            holder.spinner_expiretime.setVisibility(View.VISIBLE);

                        } else {


                            if (difference_In_Minutes < 0) {

                                holder.tv_expiretime.setText("Expired");
                                // Condintion to hide expired orders
                                if (orderStatus.equalsIgnoreCase("Pending")) {
                                    holder.layout_main.setVisibility(View.GONE);
                                }

                                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                holder.tv_chat.setVisibility(View.GONE);
                                String orderId = orderedMapList.get(position).get("orderId");
                                if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {

                                } else {
                                    changeOrderStatudsExpired(orderId);
                                }

                                holder.spinner_expiretime.setVisibility(View.VISIBLE);
                            } else {

                                int differnceTime = (int) (expiryTime - difference_In_Minutes);
                                if (differnceTime <= 0) {
                                    holder.tv_expiretime.setText("Expired");
                                    // Condintion to hide expired orders
                                    if (orderStatus.equalsIgnoreCase("Pending")) {
                                        holder.layout_main.setVisibility(View.GONE);
                                    }

                                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                    holder.spinner_expiretime.setVisibility(View.VISIBLE);
                                    String orderId = orderedMapList.get(position).get("orderId");
                                    if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {

                                    } else {
                                        changeOrderStatudsExpired(orderId);
                                    }


                                } else {
                                    holder.layout_main.setVisibility(View.VISIBLE);
                                    holder.tv_expiretime.setText("Exprires in " + differnceTime + " mins");

                                }

                            }

                        }

                    }

                } else {


                }

            } catch (Exception e) {

            }


            holder.tv_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String customeruserId = orderedMapList.get(position).get("customerUserId");

                    if (orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Accepted")) {

                        holder.img_closeOrder.setVisibility(View.GONE);
                        Intent userInfo = new Intent(context, ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("orderId", orderedMapList.get(position).get("orderId"));
                        bundle.putString("orderName", orderedMapList.get(position).get("orderName"));
                        bundle.putString("cuisines", orderedMapList.get(position).get("cuisines"));
                        bundle.putString("price", orderedMapList.get(position).get("orderPrice"));
                        bundle.putString("resturntName", orderedMapList.get(position).get("resturntName"));
                        bundle.putString("orderStatus", orderedMapList.get(position).get("orderStatus"));
                        bundle.putString("groupData", orderedMapList.get(position).get("groupData"));
                        bundle.putString("deliveryPartner", orderedMapList.get(position).get("deliveryPartner"));

                        if (Utils.userId.equalsIgnoreCase(customeruserId)) {
                            bundle.putString("pageData", "Customer");
                            bundle.putString("receiverId", orderedMapList.get(position).get("partnerUserId"));
                            bundle.putString("senderId", orderedMapList.get(position).get("customerUserId"));
                        } else {
                            bundle.putString("pageData", "DeliveryAgent");
                            bundle.putString("receiverId", orderedMapList.get(position).get("customerUserId"));
                            bundle.putString("senderId", orderedMapList.get(position).get("partnerUserId"));
                        }

                        userInfo.putExtras(bundle);
                        context.startActivity(userInfo);

                    } else {



                        /*  Condition to find who is the creator of post . on Click of Chat post creator of post  will go to delivery lists
                         * partner will go to chat directly*/

                        if (Utils.userId.equalsIgnoreCase(customeruserId)) {

                            Intent orderinfo = new Intent(context, UserListActivity.class);
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
                            context.startActivity(orderinfo);
                        } else {

                            Intent userInfo = new Intent(context, ChatActivity.class);
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
                            context.startActivity(userInfo);
                        }


                    }


                }
            });


            holder.switch_order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                    LayoutInflater factory = LayoutInflater.from(context);
                    final View deleteDialogView = factory.inflate(R.layout.custom_popup, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(context).create();
                    deleteDialog.setView(deleteDialogView);
                    TextView tv_msg;
                    tv_msg = deleteDialogView.findViewById(R.id.tv_msg);

                    tv_msg.setText("Are you sure you want to change this order status to completed ?");

                    deleteDialogView.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();

                            if (isChecked) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                                reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Completed");
                                String partnerId = orderedMapList.get(position).get("partnerUserId");
                                String customerUserId = orderedMapList.get(position).get("customerUserId");

                                if (Utils.userId.equalsIgnoreCase(customerUserId)) {

                                    // To show rating page and then update DB
                                    //    reference.child(orderedMapList.get(position).get("orderId")).child("ownerRating").setValue("true");
                                    Intent orderinfo = new Intent(context, RatingActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("orderId", orderedMapList.get(position).get("orderId"));
                                    bundle.putString("myuserId", orderedMapList.get(position).get("customerUserId"));
                                    bundle.putString("deliverusrId", orderedMapList.get(position).get("partnerUserId"));
                                    bundle.putString("userRole", "owner");
                                    orderinfo.putExtras(bundle);
                                    context.startActivity(orderinfo);
                                    //  notification(Utils.userId,partnerId,);
                                    String msg = "Order is COMPLETED , thanks again for being part of SeeSame";
                                    sendMsg(msg, Utils.userId, partnerId, orderedMapList.get(position).get("orderId"));

                                } else {
                                    // To show rating page and then update DB
                                    Intent orderinfo = new Intent(context, RatingActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("orderId", orderedMapList.get(position).get("orderId"));
                                    bundle.putString("myuserId", orderedMapList.get(position).get("customerUserId"));
                                    bundle.putString("deliverusrId", orderedMapList.get(position).get("partnerUserId"));
                                    bundle.putString("userRole", "Customer");
                                    orderinfo.putExtras(bundle);
                                    context.startActivity(orderinfo);
                                    String msg = "Order is COMPLETED , thanks again for being part of SeeSame";
                                    sendMsg(msg, partnerId, Utils.userId, orderedMapList.get(position).get("orderId"));
                                    // reference.child(orderedMapList.get(position).get("orderId")).child("partnerRating").setValue("true");
                                }
                                notifyDataSetChanged();
                                deleteDialog.dismiss();


                            } else {
                                //do something when unchecked
                                // Toast.makeText(context,"False ",Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                    deleteDialogView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();

                        }
                    });

                    deleteDialog.show();


                }
            });


            holder.img_closeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    LayoutInflater factory = LayoutInflater.from(context);
                    final View deleteDialogView = factory.inflate(R.layout.custom_popup, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(context).create();
                    deleteDialog.setView(deleteDialogView);
                    TextView tv_msg;
                    tv_msg = deleteDialogView.findViewById(R.id.tv_msg);

                    tv_msg.setText("Are you sure want to cancel this Ad");

                    deleteDialogView.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();

                            String orderId = orderedMapList.get(position).get("orderId");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                            reference.child(orderId).child("orderStatus").setValue("Cancelled");
                            reference.child(orderId).child("orderCancel").setValue(currentDate);
                            reference.child(orderId).child("orderStatusDate").setValue(currentDate);
                            reference.child(orderId).child("orderStatusTime").setValue(currentDate);
                            Toast.makeText(context, "Succesfully cancelled your order", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                            orderDetailtoCancel("Cancel");


                        }
                    });
                    deleteDialogView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();

                        }
                    });

                    deleteDialog.show();


                }
            });


        } catch (Exception e) {

            Log.i("Exception", e.getMessage().toString());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 6000);

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

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView tv_hotlename, tv_distance, tv_expiretime, tv_price, btn_accept, tv_chat;
        private ImageButton img_closeOrder;
        private Switch switch_order;
        private Spinner spinner_expiretime;
        private View layout_expiretime, layout_accpet, layout_main;
        private ImageView img_myorder;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tv_hotlename = itemView.findViewById(R.id.tv_hotlename);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_expiretime = itemView.findViewById(R.id.tv_expiretime);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            tv_chat = itemView.findViewById(R.id.tv_chat);
            img_closeOrder = itemView.findViewById(R.id.img_closeOrder);
            switch_order = itemView.findViewById(R.id.switch_order);
            spinner_expiretime = itemView.findViewById(R.id.spinner_expiretime);
            layout_expiretime = itemView.findViewById(R.id.layout_expiretime);
            img_myorder = itemView.findViewById(R.id.img_myorder);
            layout_accpet = itemView.findViewById(R.id.layout_accpet);
            layout_main = itemView.findViewById(R.id.layout_main);

        }
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


                    Intent orderIntent = new Intent();
                    Bundle b = new Bundle();

                    //  orderIntent.putExtras()


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void updateOrder(ArrayList<HashMap<String, String>> orderedMapList) {

        this.orderedMapList = orderedMapList;
        notifyDataSetChanged();
    }

    private void changeOrderStatudsExpired(String orderId) {

        try {
            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Orders");
            dbreference.child(orderId).child("orderStatus").setValue("Expired");
            // orderDetailtoCancel();

        } catch (Exception e) {

        }

    }


    private void orderDetailtoCancel(final String info) {

        try {
            orderedMapList = new ArrayList<HashMap<String, String>>();
            orderedMapList.clear();

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
            reference.orderByChild("customerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                //  reference.orderByChild("customerUserId").equalTo(Utils.userId).orderByChild("partnerUserId").equalTo("").addListenerForSingleValueEvent(new ValueEventListener() {


                // reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        orderredMap = new HashMap<>();
                        orderredMap.put("orderId", String.valueOf(snapshot1.child("orderId").getValue()));
                        orderredMap.put("orderDate", String.valueOf(snapshot1.child("orderDate").getValue()));
                        orderredMap.put("orderTime", String.valueOf(snapshot1.child("orderTime").getValue()));
                        orderredMap.put("orderPrice", String.valueOf(snapshot1.child("orderPrice").getValue()));
                        orderredMap.put("deliveryPartner", String.valueOf(snapshot1.child("deliveryPartner").getValue()));
                        orderredMap.put("expireTime", String.valueOf(snapshot1.child("expireTime").getValue()));
                        orderredMap.put("cuisines", String.valueOf(snapshot1.child("cuisines").getValue()));
                        orderredMap.put("orderCategoryId", String.valueOf(snapshot1.child("orderCategoryId").getValue()));
                        orderredMap.put("serviceSelecetd", String.valueOf(snapshot1.child("serviceSelecetd").getValue()));
                        orderredMap.put("orderStatus", String.valueOf(snapshot1.child("orderStatus").getValue()));
                        orderredMap.put("customerName", String.valueOf(snapshot1.child("customerName").getValue()));
                        orderredMap.put("customerUserId", String.valueOf(snapshot1.child("customerUserId").getValue()));
                        orderredMap.put("customerUserId", String.valueOf(snapshot1.child("customerUserId").getValue()));
                        orderredMap.put("deliveryPostalCode", String.valueOf(snapshot1.child("deliveryPostalCode").getValue()));
                        orderredMap.put("deliverylatitude", String.valueOf(snapshot1.child("deliverylatitude").getValue()));
                        orderredMap.put("deliverylongititude", String.valueOf(snapshot1.child("deliverylongititude").getValue()));
                        orderredMap.put("deliveryPlaceId", String.valueOf(snapshot1.child("deliveryPlaceId").getValue()));
                        orderredMap.put("deliveryAreaname", String.valueOf(snapshot1.child("deliveryAreaname").getValue()));
                        orderredMap.put("deliveryAddress", String.valueOf(snapshot1.child("deliveryAddress").getValue()));
                        orderredMap.put("partnerName", String.valueOf(snapshot1.child("partnerName").getValue()));

                        orderredMap.put("partnerUserId", String.valueOf(snapshot1.child("partnerUserId").getValue()));
                        orderredMap.put("partnerChatId", String.valueOf(snapshot1.child("partnerChatId").getValue()));
                        orderredMap.put("partnerPostalCode", String.valueOf(snapshot1.child("partnerPostalCode").getValue()));
                        orderredMap.put("partnerlatitude", String.valueOf(snapshot1.child("partnerlatitude").getValue()));
                        orderredMap.put("partnerlongititude", String.valueOf(snapshot1.child("partnerlongititude").getValue()));
                        orderredMap.put("partnerPlaceId", String.valueOf(snapshot1.child("partnerPlaceId").getValue()));
                        orderredMap.put("partnerAreaName", String.valueOf(snapshot1.child("partnerAreaName").getValue()));
                        orderredMap.put("partnerAddress", String.valueOf(snapshot1.child("partnerAddress").getValue()));
                        orderredMap.put("orderStatusDate", String.valueOf(snapshot1.child("orderStatusDate").getValue()));
                        orderredMap.put("orderStatusTime", String.valueOf(snapshot1.child("orderStatusTime").getValue()));
                        orderredMap.put("orderCancel", String.valueOf(snapshot1.child("orderCancel").getValue()));
                        orderredMap.put("orderAccepted", String.valueOf(snapshot1.child("orderAccepted").getValue()));
                        orderredMap.put("orderCompleted", String.valueOf(snapshot1.child("orderCompleted").getValue()));

                        orderredMap.put("resturntName", String.valueOf(snapshot1.child("resturntName").getValue()));
                        orderredMap.put("resturntAddress", String.valueOf(snapshot1.child("resturntAddress").getValue()));
                        orderredMap.put("resturntPostalCode", String.valueOf(snapshot1.child("resturntPostalCode").getValue()));

                        orderredMap.put("ownerRating", String.valueOf(snapshot1.child("ownerRating").getValue()));
                        orderredMap.put("partnerRating", String.valueOf(snapshot1.child("partnerRating").getValue()));
                        orderredMap.put("orderImg", String.valueOf(snapshot1.child("orderImg").getValue()));

                        String orderStatus = String.valueOf(snapshot1.child("orderStatus").getValue());


                        if (info.equalsIgnoreCase("Cancel")) {
                            if (orderStatus.equalsIgnoreCase("Pending")) {

                                orderedMapList.add(orderredMap);
                            }
                        } else {

                            if (orderStatus.equalsIgnoreCase("Expired")) {

                                orderedMapList.add(orderredMap);
                            }
                        }


                    }
                    updateOrder(orderedMapList);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } catch (Exception e) {


        }


    }

    private void notification(final String senderUiD, final String recevieverUid, final String message) {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(recevieverUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(senderUiD, R.drawable.location, message + " ", "Seesame",
                            recevieverUid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {

                                        Log.i("ResponeMgs", response.message().toString());
                                        Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();

                                        if (response.body().success != 1) {
                                            //    Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed!" + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void sendMsg(String msg, String senderId, String receiverId, String orderId) {

        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("chats");
        String uniqueId = refernce.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("chatId", uniqueId);
        hashMap.put("sender", senderId);
        hashMap.put("receiver", receiverId);
        hashMap.put("message", msg);
        hashMap.put("orderId", orderId);
        refernce.child(uniqueId).setValue(hashMap);

    }

}
