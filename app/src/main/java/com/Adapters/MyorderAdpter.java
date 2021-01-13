package com.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.screens.UserListActivity;
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
    public void onBindViewHolder(@NonNull MyorderAdpter.Viewholder holder, final int position) {


        try {

            orderedMapList.get(position);
            holder.tv_hotlename.setText(orderedMapList.get(position).get("resturntName"));
            holder.tv_price.setText(orderedMapList.get(position).get("orderPrice") + "$");
            holder.tv_expiretime.setText("Exprires in " + orderedMapList.get(position).get("expireTime") + "mins");

            holder.btn_accept.setText(orderedMapList.get(position).get("orderStatus"));

            double customerLat = Double.parseDouble(orderedMapList.get(position).get("deliverylatitude"));
            double customerLang = Double.parseDouble(orderedMapList.get(position).get("deliverylongititude"));


            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = df.format(c.getTime());

            SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
            currentTime = timeFormate.format(new Date());
            expiryTime = Integer.parseInt(orderedMapList.get(position).get("expireTime"));


            orderTime = orderedMapList.get(position).get("orderTime");


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

           /*     if (difference_In_Hours >= 1) {
                    holder.tv_expiretime.setText("Exprired");
                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));
                    return;

                }

                if (difference_In_Days > 0) {

                    holder.tv_expiretime.setText("Exprired");
                    holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));

                } else {

                   // int mins = (int) (differncetime / (1000 * 60)) % 60;

                    if (difference_In_Minutes < 0) {

                        holder.tv_expiretime.setText("Exprired");
                        holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));

                    } else {

                        int differnceTime = (int) (expiryTime - difference_In_Minutes);
                        if (differnceTime <= 0) {
                            holder.tv_expiretime.setText("Exprired");
                            holder.tv_expiretime.setTextColor(Color.parseColor("#ff1a1a"));

                        } else {
                            // Log.i("GrandTime ", String.valueOf(differnceTime));
                            holder.tv_expiretime.setText("Exprires in " + differnceTime + " mins");
                            //  Log.i("DiffernceTime ", String.valueOf(differnceTime));
                        }

                    }

                } */


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


                    Intent orderinfo = new Intent(context, UserListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderedMapList.get(position).get("orderId"));
                    bundle.putString("OrderName", orderedMapList.get(position).get("resturntName"));
                    orderinfo.putExtras(bundle);
                    context.startActivity(orderinfo);


                    //   context.startActivity(new Intent(context, UserListActivity.class));


                }
            });


            holder.img_closeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure you want to cancel this Free add?");
                    builder.setMessage("")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    String orderId = orderedMapList.get(position).get("orderId");
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                                    reference.child(orderId).child("orderStatus").setValue("Cancelled");
                                    reference.child(orderId).child("orderCancel").setValue(currentDate);
                                    reference.child(orderId).child("orderStatusDate").setValue(currentDate);
                                    reference.child(orderId).child("orderStatusTime").setValue(currentDate);
                                    notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


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
        private TextView tv_hotlename, tv_distance, tv_expiretime, tv_price, btn_accept, tv_chat;
        private ImageButton img_closeOrder;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tv_hotlename = itemView.findViewById(R.id.tv_hotlename);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_expiretime = itemView.findViewById(R.id.tv_expiretime);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            tv_chat = itemView.findViewById(R.id.tv_chat);
            img_closeOrder = itemView.findViewById(R.id.img_closeOrder);


        }
    }
}
