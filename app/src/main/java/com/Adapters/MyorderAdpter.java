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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.screens.ChatActivity;
import com.screens.UserListActivity;
import com.seesame.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyorderAdpter extends RecyclerView.Adapter<MyorderAdpter.Viewholder> {


    private Context context;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private double totalDistance;
    private String currentDate, currentTime, orderTime, cuisines;
    private int expiryTime;
    List<String> timeList;
    private String spinnerVal;
    private MyorderAdpter myorderAdpter;

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
    public void onBindViewHolder(@NonNull final MyorderAdpter.Viewholder holder, final int position) {


        try {


            orderedMapList.get(position);
            holder.tv_hotlename.setText(orderedMapList.get(position).get("resturntName"));
            holder.tv_price.setText(orderedMapList.get(position).get("orderPrice") + "$");
            holder.tv_expiretime.setText("Exprires in " + orderedMapList.get(position).get("expireTime") + "mins");
            holder.btn_accept.setText(orderedMapList.get(position).get("orderStatus"));


            holder.layout_expiretime.setVisibility(View.GONE);
            holder.switch_order.setVisibility(View.GONE);
            timeList = new ArrayList<>();
            timeList.add("Extend expriry time");
            timeList.add("15");
            timeList.add("30");
            timeList.add("45");
            timeList.add("60");
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

            SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
            currentTime = timeFormate.format(new Date());
            expiryTime = Integer.parseInt(orderedMapList.get(position).get("expireTime"));

            orderTime = orderedMapList.get(position).get("orderTime");

            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Accepted"))) {
                holder.tv_expiretime.setVisibility(View.GONE);
                holder.switch_order.setVisibility(View.VISIBLE);
            }

            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Expired"))) {
                holder.layout_expiretime.setVisibility(View.VISIBLE);
            }

            if ((orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Completed"))) {
                holder.tv_expiretime.setVisibility(View.GONE);
                holder.switch_order.setVisibility(View.GONE);
                holder.img_closeOrder.setVisibility(View.GONE);
            }


            holder.spinner_expiretime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spinnerposition, long id) {

                    String orderId = orderedMapList.get(position).get("orderId");

                    /*  Toast.makeText(context, "orderId " + orderId, Toast.LENGTH_SHORT).show();*/

                    spinnerVal = String.valueOf(parent.getItemAtPosition(spinnerposition));

                    if (!spinnerVal.equalsIgnoreCase("Extend expriry time")) {


                        holder.layout_expiretime.setVisibility(View.GONE);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                        reference.child(orderId).child("orderStatus").setValue("Pending");
                        reference.child(orderId).child("expireTime").setValue(spinnerVal);
                        reference.child(orderId).child("orderDate").setValue(currentDate);
                        reference.child(orderId).child("orderStatusDate").setValue(currentDate);
                        reference.child(orderId).child("orderStatusTime").setValue(currentTime);
                        notifyDataSetChanged();

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


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
                    holder.layout_expiretime.setVisibility(View.VISIBLE);
                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                    deletingCategoriData(orderedMapList.get(position).get("orderStatus"));
                    return;

                }

                String expTime = orderedMapList.get(position).get("expireTime");

                if ((expTime.equalsIgnoreCase("1 Day") || expTime.equalsIgnoreCase("3 Day") ||
                        expTime.equalsIgnoreCase("5 Day") || expTime.equalsIgnoreCase("7 Day"))) {


                } else {


                    if (difference_In_Days > 0) {

                        holder.tv_expiretime.setText("Exprired");
                        holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                        holder.tv_expiretime.setVisibility(View.GONE);
                        holder.btn_accept.setText("Exprired");
                        holder.layout_expiretime.setVisibility(View.VISIBLE);
                        String orderId = orderedMapList.get(position).get("orderId");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                        reference.child(orderId).child("orderStatus").setValue("Exprired");
                        deletingCategoriData(orderedMapList.get(position).get("orderStatus"));

                    } else {

                        // int mins = (int) (differncetime / (1000 * 60)) % 60;

                        if (difference_In_Minutes < 0) {

                            holder.tv_expiretime.setText("Exprired");
                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                            holder.tv_expiretime.setVisibility(View.GONE);
                            holder.btn_accept.setText("Exprired");
                            holder.layout_expiretime.setVisibility(View.VISIBLE);
                            String orderId = orderedMapList.get(position).get("orderId");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                            reference.child(orderId).child("orderStatus").setValue("Exprired");

                            deletingCategoriData(orderedMapList.get(position).get("orderStatus"));


                        } else {

                            int differnceTime = (int) (expiryTime - difference_In_Minutes);
                            if (differnceTime <= 0) {
                                holder.tv_expiretime.setText("Exprired");
                                holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                                holder.tv_expiretime.setVisibility(View.GONE);
                                holder.btn_accept.setText("Exprired");
                                holder.layout_expiretime.setVisibility(View.VISIBLE);
                                String orderId = orderedMapList.get(position).get("orderId");
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                                reference.child(orderId).child("orderStatus").setValue("Exprired");
                                deletingCategoriData(orderedMapList.get(position).get("orderStatus"));

                            } else {
                                // Log.i("GrandTime ", String.valueOf(differnceTime));
                                holder.tv_expiretime.setText("Exprires in " + differnceTime + " mins");
                                //  Log.i("DiffernceTime ", String.valueOf(differnceTime));
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


                    if (orderedMapList.get(position).get("orderStatus").equalsIgnoreCase("Accepted")) {


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
                        userInfo.putExtras(bundle);
                        context.startActivity(userInfo);

                    } else {

                        String customeruserId = orderedMapList.get(position).get("customerUserId");

                        /*  Condition to find who is the creator of post . on Click of Chat post creator of post  will go to delivery lists
                         * partner will go to chat directly*/

                        if (Utils.userId.equalsIgnoreCase(customeruserId)) {

                            Intent orderinfo = new Intent(context, UserListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("orderId", orderedMapList.get(position).get("orderId"));
                            bundle.putString("OrderName", orderedMapList.get(position).get("resturntName"));
                            bundle.putString("cuisines", orderedMapList.get(position).get("cuisines"));
                            bundle.putString("price", orderedMapList.get(position).get("orderPrice"));
                            bundle.putString("pageData","Mychats");
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
                            userInfo.putExtras(bundle);
                            context.startActivity(userInfo);
                        }


                    }



                }
            });


            holder.switch_order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        // do something when check is selected

                        //   Toast.makeText(context,"True ",Toast.LENGTH_SHORT).show();

                        //  alertBox("Are you sure you want to change this order status to completed ?","Switch");
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Are you sure you want to change this order status to completed ?");
                        builder.setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {


                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                                        reference.child(orderedMapList.get(position).get("orderId")).child("orderStatus").setValue("Completed");

                                        String partnerId = orderedMapList.get(position).get("partnerUserId");
                                        String customerUserId = orderedMapList.get(position).get("customerUserId");
                                        if (Utils.userId.equalsIgnoreCase(customerUserId)) {

                                            reference.child(orderedMapList.get(position).get("orderId")).child("ownerRating").setValue("true");
                                        } else {

                                            reference.child(orderedMapList.get(position).get("orderId")).child("partnerRating").setValue("true");
                                        }
                                        notifyDataSetChanged();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        holder.switch_order.setChecked(false);

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();


                    } else {
                        //do something when unchecked
                        // Toast.makeText(context,"False ",Toast.LENGTH_SHORT).show();
                    }

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

                            String orderId = orderedMapList.get(position).get("orderId");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                            reference.child(orderId).child("orderStatus").setValue("Cancelled");
                            reference.child(orderId).child("orderCancel").setValue(currentDate);
                            reference.child(orderId).child("orderStatusDate").setValue(currentDate);
                            reference.child(orderId).child("orderStatusTime").setValue(currentDate);
                            //  myorderAdpter.notifyDataSetChanged();
                            //  notifyItemRangeChanged(Integer.parseInt(orderId), orderedMapList.size());
                            notifyDataSetChanged();
                            Toast.makeText(context, "Succesfully cancelled order", Toast.LENGTH_SHORT).show();


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
    public int getItemCount() {
        return orderedMapList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView tv_hotlename, tv_distance, tv_expiretime, tv_price, btn_accept, tv_chat;
        private ImageButton img_closeOrder;
        private Switch switch_order;
        private Spinner spinner_expiretime;
        private View layout_expiretime;

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

        }
    }


    private void alertBox(String ttl, String data) {


    }

    private void expireTimeCalculation() {


        try {

            int minutesDay = 1440;


            int hours = minutesDay / 60; //since both are ints, you get an int
            int minutes = minutesDay % 60;

            if (hours < 24) {

                int hoursleft = minutesDay / 60;

                // Toast.makeText(context, "Hours " + hoursleft, Toast.LENGTH_SHORT).show();

            } else {

            }


        } catch (Exception e) {


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

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void acceptAdsPopup() {


    }


}
