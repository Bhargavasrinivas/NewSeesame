package com.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    String theLastMessage, price, cuisines, orderName;
    private ArrayList<HashMap<String, String>> userMapList;

    public UserListadapter(UserListActivity userListActivity, ArrayList<HashMap<String, String>> userMapList, boolean b, String price, String cuisines, String orderName) {

        this.mContext = userListActivity;
        this.userMapList = userMapList;
        this.ischat = b;
        this.price = price;
        this.cuisines = cuisines;
        this.orderName = orderName;

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




      /*  if (ischat) {
            lastMessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }
*/
     /*   holder.img_on.setVisibility(View.VISIBLE);
        holder.img_off.setVisibility(View.VISIBLE);
*/
    /*    if (ischat){

            if (user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               /* Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);*/
                //     Log.i("SenderId ", user.getId());
               /* Util.receiverId = user.getId();
                mContext.startActivity(new Intent(mContext, ChatActivity.class));*/
             /*   Util.receiverId = user.getId();
                Intent intent = new Intent(mContext, ChatActivity.class);
                mContext.startActivity(intent);*/

            /*    orderChatuserList.put("chatId", uniqueId);
                orderChatuserList.put("orderId", orderId);
                orderChatuserList.put("deliveryAgentId", senderId);
                orderChatuserList.put("deliveryAgentName", "DeliveryAgnet");*/

                String orderId = userMapList.get(position).get("orderId");
                Intent userInfo = new Intent(mContext, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("orderId", userMapList.get(position).get("orderId"));
                bundle.putString("receiverId", userMapList.get(position).get("deliveryAgentId"));
                bundle.putString("senderId", Utils.userId);
                bundle.putString("pageData", "Customer");
                bundle.putString("orderName", userMapList.get(position).get("orderName"));
                bundle.putString("cuisines", cuisines);
                bundle.putString("price", price);
                bundle.putString("resturntName", userMapList.get(position).get("orderName"));

                userInfo.putExtras(bundle);

                mContext.startActivity(userInfo);


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
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

}

