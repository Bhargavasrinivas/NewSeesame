package com.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.models.RateModel;
import com.models.User;
import com.screens.ChatActivity;
import com.screens.UserListActivity;
import com.seesame.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserListadapter extends RecyclerView.Adapter<UserListadapter.ViewHolder> {

    private Context mContext;
    private boolean ischat;
    String theLastMessage, price, cuisines, orderName, orderStatus;
    private ArrayList<HashMap<String, String>> userMapList;
    private String userName, Isownerpresent, orderAccpetedId, adsStatus, partnerName, deliveryPartner, groupData;
    private RateModel rateModel;
    private ArrayList<RateModel> ratingList = new ArrayList<>();
    private long ttlRateCount = 0;
    private float ttlcountRate;

    public UserListadapter(UserListActivity userListActivity, ArrayList<HashMap<String, String>> userMapList, boolean b, String price, String cuisines, String orderName, String orderStatus) {

        this.mContext = userListActivity;
        this.userMapList = userMapList;
        this.ischat = b;
        this.price = price;
        this.cuisines = cuisines;
        this.orderName = orderName;
        this.orderStatus = orderStatus;

    }


    @NonNull
    @Override
    public UserListadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserListadapter.ViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull UserListadapter.ViewHolder holder, final int position) {


        userMapList.get(position);

        //     final User user = mUsers.get(position);
        holder.username.setText(userMapList.get(position).get("deliveryAgentName"));
        userName = (userMapList.get(position).get("deliveryAgentName"));
        // holder.tv_msg.setText(userMapList.get(position).get("message"));
        Isownerpresent = userMapList.get(position).get("Isownerpresent");
        adsStatus = userMapList.get(position).get("orderStatus");
        orderAccpetedId = userMapList.get(position).get("orderAccpetedId");
        String partnerId = userMapList.get(position).get("deliveryAgentId");


       // holder.rating_bar.setNumStars(5);

        holder.tv_date.setText(userMapList.get(position).get("Date"));

        String date = userMapList.get(position).get("Date");
        String deliverypartner = userMapList.get(position).get("deliveryPartner");


        if (userMapList.get(position).get("groupData").contains("Food")) {

            holder.tv_resturntname.setText(userMapList.get(position).get("resturntName"));
            holder.tv_deliverypartner.setText(userMapList.get(position).get("deliveryPartner"));
            holder.tv_category.setText(userMapList.get(position).get("cuisines"));

        } else if (userMapList.get(position).get("groupData").contains("Groceries")) {
            holder.tv_resturntname.setText("Groceries");
            holder.tv_deliverypartner.setText(userMapList.get(position).get("deliveryPartner"));
            holder.tv_category.setText("");
        } else {
            holder.tv_resturntname.setText("Home-Made");
            holder.tv_deliverypartner.setText(userMapList.get(position).get("cuisines"));
            holder.tv_category.setText("Delicacy");
        }


        fetchUserRatings(holder, partnerId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               /* String orderId = userMapList.get(position).get("orderId");
                Intent userInfo = new Intent(mContext, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("orderId", userMapList.get(position).get("orderId"));
                bundle.putString("receiverId", userMapList.get(position).get("deliveryAgentId"));
                bundle.putString("senderId", Utils.userId);
                bundle.putString("pageData", "Customer");
                bundle.putString("orderName", userMapList.get(position).get("orderName"));
                bundle.putString("cuisines", userMapList.get(position).get("cuisines"));
                bundle.putString("price", price);
                bundle.putString("resturntName", userMapList.get(position).get("orderName"));
                bundle.putString("orderStatus", orderStatus);
                userInfo.putExtras(bundle);
                mContext.startActivity(userInfo);*/

                String adCreatorId = userMapList.get(position).get("orderCreaterId");
                String receiverId = userMapList.get(position).get("deliveryAgentId");
                String orderName = userMapList.get(position).get("orderName");
                String cuisines = userMapList.get(position).get("cuisines");
                String resturntName = userMapList.get(position).get("resturntName");
                String orderID = userMapList.get(position).get("orderId");
                partnerName = userMapList.get(position).get("deliveryAgentName");
                deliveryPartner = userMapList.get(position).get("deliveryPartner");
                groupData = userMapList.get(position).get("groupData");
                price = userMapList.get(position).get("orderPrice");

                if (adCreatorId.equalsIgnoreCase(Utils.userId)) {

                  /*  if (orderAccpetedId.isEmpty()) {

                        Toast.makeText(mContext, "order Is Empty ", Toast.LENGTH_SHORT).show();
                    }*/
                    fetchOrderStatus(orderID, receiverId, orderName, cuisines, orderName, "Customer",price);

                   /* if(orderAccpetedId.equalsIgnoreCase(receiverId)){

                    }
                    */
                } else {
                    fetchOrderStatus(orderID, receiverId, orderName, cuisines, orderName, "DeliveryAgent",price);
                }


            }
        });

    }

    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    /*Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                    }*/
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userMapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg, tv_msg, tv_category, tv_date, tv_resturntname, tv_deliverypartner;
        private RatingBar rating_bar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            tv_msg = itemView.findViewById(R.id.tv_msg);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_resturntname = itemView.findViewById(R.id.tv_resturntname);
            tv_deliverypartner = itemView.findViewById(R.id.tv_deliverypartner);
            rating_bar = itemView.findViewById(R.id.rating_bar);


            /*img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);*/
        }
    }


    private void fetchOrderStatus(final String orderId, final String receiverId, final String orderName,
                                  final String cuisines, final String resturntName, final String role,final String orderPrice) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.keepSynced(true);

        reference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String orderStatus = String.valueOf(snapshot1.child("orderStatus").getValue());

                    // String orderId = userMapList.get(position).get("orderId");
                    Intent userInfo = new Intent(mContext, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", orderId);
                    bundle.putString("receiverId", receiverId);
                    bundle.putString("senderId", Utils.userId);
                    bundle.putString("pageData", role);
                    bundle.putString("orderName", orderName);
                    bundle.putString("cuisines", cuisines);
                    bundle.putString("orderPrice", orderPrice);
                    bundle.putString("resturntName", resturntName);
                    bundle.putString("orderStatus", orderStatus);
                    bundle.putString("Isownerpresent", Isownerpresent);
                    bundle.putString("orderAccpetedId", orderAccpetedId);
                    bundle.putString("adsStatus", adsStatus);
                    bundle.putString("partnerName", partnerName);
                    bundle.putString("groupData", groupData);
                    bundle.putString("deliveryPartner", deliveryPartner);

/*
                    deliveryPartner = userMapList.get(position).get("deliveryPartner");
                    groupData = userMapList.get(position).get("groupData");
*/

                    userInfo.putExtras(bundle);
                    mContext.startActivity(userInfo);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void fetchUserRatings(final ViewHolder holder, String userId) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ratings");
        reference.keepSynced(true);

        reference.orderByChild("userId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ratingList = new ArrayList<RateModel>();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    rateModel = new RateModel();
                    long ratings = (long) snapshot1.child("rateCount").getValue();
                    rateModel.setRateCount((int) ratings);
                    ratingList.add(rateModel);

                    long count = (long) snapshot1.child("rateCount").getValue();

                    ttlRateCount = ttlRateCount + count;


                }
                ttlcountRate = ttlRateCount;

                if (ratingList.size() == 0) {
                    holder.rating_bar.setVisibility(View.GONE);
                } else {


                    float ratettlCount = (ttlcountRate / ratingList.size());
                    // rating_bar.setNumStars(ratettlCount);
                    holder.rating_bar.setRating(ratettlCount);


                    Log.i("ttlRateCount ", String.valueOf(ratettlCount));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //  Toast.makeText(getActivity(), "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }


}

