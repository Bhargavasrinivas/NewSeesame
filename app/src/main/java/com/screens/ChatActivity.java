package com.screens;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.Adapters.MessageAdapter;
import com.Adapters.OrderAdpter;
import com.Adapters.UserListadapter;
import com.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.models.Chat;
import com.seesame.Client;
import com.seesame.Data;
import com.seesame.MyResponse;
import com.seesame.R;
import com.seesame.Sender;
import com.seesame.Token;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.screens.LoginActivity.Email;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btn_send, btn_orderacpt;
    private RecyclerView recyclervw;
    private EditText text_send;
    List<Chat> mchat;
    private DatabaseReference dbrefernce;
    private String orderId, receiverId, senderId, pagedata, agentName, orderName, cuisines, price, resturntName = "", orderStatus,
            Isownerpresent, orderAccpetedId, adsStatus, partnerName, groupData, deliveryPartner, currentDate;
    private MessageAdapter messageAdapter;
    private boolean listflag = false, senderFalg = true, receverFlag = true;
    APIService apiService;
    private TextView tv_ordrcancel, tv_orderacpt, tv_ratings;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String userKey = "userKey";
    SharedPreferences sharedpreferences;
    FirebaseUser fuser;
    private TextView tv_welcmmsg, tv_partnername;
    private View layout_wlcmmsg, layout_btns, layout_msgVw, layout_rating;
    HashMap<String, String> userMap;
    private ArrayList<HashMap<String, String>> userMapList;
    private boolean isUserpresent = false, isOwenerpresent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final View parentLayout = findViewById(android.R.id.content);

        initView();


        userMapList = new ArrayList<HashMap<String, String>>();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Utils.chatlist = sharedpreferences.getBoolean("mykey", true);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(c.getTime());
        try {

            String CurrentString = Utils.mailId;
            String[] separated = CurrentString.split("@");
            agentName = separated[0];

            // Toast.makeText(getApplication(), agentName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {


        }


        Bundle b = getIntent().getExtras();
        orderId = b.getString("orderId");
        receiverId = b.getString("receiverId");
        senderId = b.getString("senderId"); // Who loogied in
        pagedata = b.getString("pageData");
        orderName = b.getString("orderName");
        cuisines = b.getString("cuisines");
        price = b.getString("orderPrice");
        resturntName = b.getString("resturntName");
        orderStatus = b.getString("orderStatus");
        Isownerpresent = b.getString("Isownerpresent");
        orderAccpetedId = b.getString("orderAccpetedId");
        adsStatus = b.getString("adsStatus");
        partnerName = b.getString("partnerName");
        groupData = b.getString("groupData");
        deliveryPartner = b.getString("deliveryPartner");

        String txtMsg = getString(R.string.thanksMsg);
        String accptMsg = getString(R.string.accptMsg);
        tv_partnername.setText(partnerName);

        if (cuisines.equalsIgnoreCase("Groceries")) {

            String pricetxt = price + "$";
            String msg = txtMsg + pricetxt + " " + "for Groceries order from" + " " + resturntName + " " + accptMsg;
            tv_welcmmsg.setText(msg);

        } else {
            String pricetxt = price + "$";
            String msg = txtMsg + pricetxt + " " + "for food order at " + resturntName + " " + accptMsg;
            tv_welcmmsg.setText(msg);

        }


        if (pagedata.equalsIgnoreCase("DeliveryAgent")) {
            this.setTitle("Chat" + "-" + b.getString("resturntName"));
            tv_orderacpt.setVisibility(View.VISIBLE);
        /*    if(adsStatus.equalsIgnoreCase("Accepted")){
                if( orderAccpetedId.equalsIgnoreCase(receiverId) ){


                }else {

                    // Chat is read only
                }

            }*/

        } else {

            this.setTitle("Chat" + "-" + b.getString("resturntName"));
            tv_ordrcancel.setVisibility(View.GONE);
            tv_orderacpt.setVisibility(View.GONE);
            layout_wlcmmsg.setVisibility(View.GONE);
            tv_orderacpt.setVisibility(View.GONE);
            btn_orderacpt.setVisibility(View.GONE);
            tv_ordrcancel.setVisibility(View.GONE);
            tv_orderacpt.setVisibility(View.GONE);
        }

        /* Function to read all caht messages */

        readallMessage();


        updateToken(FirebaseInstanceId.getInstance().getToken());

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pagedata.equalsIgnoreCase("DeliveryAgent")) {


                    if (isUserpresent) {
                        if (!text_send.getText().toString().isEmpty()) {
                            sendMsg(text_send.getText().toString().trim());
                            String msg = text_send.getText().toString().trim();
                            text_send.getText().clear();
                            notification(senderId, receiverId, msg, resturntName);
                            if (senderFalg) {
                                notification(senderId, receiverId, msg, resturntName);
                                senderFalg = false;
                            }

                        }
                    } else {

                        insertdeliveryagents("Delivery");
                    }

                } else {


                    if (isOwenerpresent) {


                        if (!text_send.getText().toString().isEmpty()) {
                            sendMsg(text_send.getText().toString().trim());
                            String msg = text_send.getText().toString().trim();
                            text_send.getText().clear();
                            isOwenerpresent = false;
                            if (receverFlag) {
                                notification(senderId, receiverId, msg, resturntName);
                                // notification(receiverId, senderId, msg, "");

                                receverFlag = false;
                            }

                        }

                    } else {

                        insertdeliveryagents("Owner");

                    }


                }
            }
        });


        // Btn Cancel Order event


        tv_ordrcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        tv_orderacpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //       acceptAdsPopup();
                fetchOrderDetails(orderId, "btnclicked");

            }
        });


        tv_ratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pagedata.equalsIgnoreCase("DeliveryAgent")) {


                    if (orderStatus.equalsIgnoreCase("Accepeted")) {

                        if (pagedata.equalsIgnoreCase("DeliveryAgent")) {

                            intent(senderId);
                        } else {

                            intent(receiverId);
                        }

                    } else {

                        intent(receiverId);
                    }


                } else {

                    if (orderStatus.equalsIgnoreCase("Accepeted")) {

                        if (pagedata.equalsIgnoreCase("DeliveryAgent")) {

                            intent(senderId);
                        } else {

                            intent(receiverId);
                        }


                    } else {
                        intent(receiverId);
                    }

                }


            }
        });


    }

    private void intent(String userId) {

        Intent ratingList = new Intent(getApplication(), UserrateListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        ratingList.putExtras(bundle);
        startActivity(ratingList);
        finish();

    }


    private void initView() {

        btn_send = findViewById(R.id.btn_send);
        recyclervw = findViewById(R.id.recyclervw);
        text_send = findViewById(R.id.text_send);
        btn_orderacpt = findViewById(R.id.btn_orderacpt);
        tv_ordrcancel = findViewById(R.id.tv_ordrcancel);
        tv_orderacpt = findViewById(R.id.tv_orderacpt);
        tv_ratings = findViewById(R.id.tv_ratings);
        tv_welcmmsg = findViewById(R.id.tv_welcmmsg);
        layout_wlcmmsg = findViewById(R.id.layout_wlcmmsg);
        layout_msgVw = findViewById(R.id.layout_msgVw);
        tv_partnername = findViewById(R.id.tv_partnername);
        layout_rating = findViewById(R.id.layout_rating);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


    }


    private void sendMsg(String msg) {

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


    private void readallMessage() {
        mchat = new ArrayList<>();

        dbrefernce = FirebaseDatabase.getInstance().getReference("chats");
        dbrefernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId) && chat.getOrderId().equals(orderId) ||
                            chat.getReceiver().equals(senderId) && chat.getSender().equals(receiverId) && chat.getOrderId().equals(orderId)) {
                        mchat.add(chat);
                    }

                    if (mchat.size() == 0) {

                    } else {
                        recyclervw.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclervw.setHasFixedSize(true);
                        messageAdapter = new MessageAdapter(ChatActivity.this, mchat, senderId);
                        recyclervw.setAdapter(messageAdapter);


                    }


                }
                fetchUsers();


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Messages " + databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();


            }
        });


    }


    private void acceptAdsPopup() {

        LayoutInflater factory = LayoutInflater.from(getApplication());
        final View deleteDialogView = factory.inflate(R.layout.custom_popup, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(deleteDialogView);

        //  deleteDialogView.findViewById(R.id.tv_msg).setTex

        TextView tv_msg;

        tv_msg = deleteDialogView.findViewById(R.id.tv_msg);

        tv_msg.setText("Are you sure want to accept this Ad");

        deleteDialogView.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                reference.child(orderId).child("orderStatus").setValue("Accepted");
                reference.child(orderId).child("partnerUserId").setValue(senderId);
                Toast.makeText(getApplicationContext(), "Order has been successfully accepted", Toast.LENGTH_SHORT).show();
                sendMsg(" SeeSame Team .Hurray !!! Order is accepted . Please feel free to chat here about the price and when you are planning to meet up to exchange goods etc.This chat will be saved and you can revisit later");
                //     reverseMsg("Order Has been Successfully accepted , You chat here  freely for smooth transcation");
                //  finish();
                // deletingCategoriData(cuisines);
                deleteDialog.dismiss();
                String mgs = Utils.userName + " " + "accepetd your ad on ";
                notification(senderId, receiverId, mgs, resturntName);
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
                    reference.child(categorieId).child("count").setValue(count);
                    Log.i("CountIncrese ", String.valueOf(count));

                    String mgs = Utils.userName + " " + "accepetd your ad on ";
                    notification(senderId, receiverId, mgs, resturntName);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void fetchUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OrderDeliveryAgentList");
        reference.keepSynced(true);
        reference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userMap = new HashMap<>();
                    userMap.put("chatId", String.valueOf(snapshot.child("chatId").getValue()));
                    userMap.put("deliveryAgentId", String.valueOf(snapshot.child("deliveryAgentId").getValue()));
                    userMap.put("deliveryAgentName", String.valueOf(snapshot.child("deliveryAgentName").getValue()));
                    userMap.put("orderId", String.valueOf(snapshot.child("orderId").getValue()));
                    userMap.put("ownerUserId", String.valueOf(snapshot.child("orderId").getValue()));
                    userMap.put("orderName", orderName);
                    //     userMapList.add(userMap);
                    //   partnerName = String.valueOf(snapshot.child("deliveryAgentName").getValue());


                    if ((snapshot.child("deliveryAgentId").getValue().equals(senderId))) {

                        isUserpresent = true;
                    }
                  /*  if ((snapshot.child("ownerUserId").getValue().equals(senderId))) {

                        isOwenerpresent = true;
                    }*/


                    if ((snapshot.child("Isownerpresent").getValue().equals("true"))) {

                        isOwenerpresent = true;
                    }

                }


                fetchOrderDetails(orderId, "None");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void insertdeliveryagents(String userType) {


        if (userType.equalsIgnoreCase("Owner")) {


            DatabaseReference refernce = FirebaseDatabase.getInstance().getReference();
            String uniqueId = refernce.push().getKey();
            HashMap<String, String> orderChatuserList = new HashMap<>();
            orderChatuserList.put("chatId", uniqueId);
            orderChatuserList.put("orderId", orderId);
            orderChatuserList.put("deliveryAgentId", senderId);
            orderChatuserList.put("Date", currentDate);
            orderChatuserList.put("deliveryAgentName", agentName);
            orderChatuserList.put("ownerUserId", receiverId);
            orderChatuserList.put("cuisines", cuisines);
            orderChatuserList.put("resturntName", resturntName);
            orderChatuserList.put("orderCreaterId", senderId);
            orderChatuserList.put("orderStatus", "Pending");
            orderChatuserList.put("orderAccpetedId", "");
            orderChatuserList.put("Isownerpresent", "true");
            orderChatuserList.put("groupData", groupData);
            orderChatuserList.put("deliveryPartner", deliveryPartner);
            orderChatuserList.put("orderPrice", price);
            refernce.child("OrderDeliveryAgentList").push().setValue(orderChatuserList);

            listflag = true;

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("mykey", false);
            editor.commit();

            if (!text_send.getText().toString().isEmpty()) {
                sendMsg(text_send.getText().toString().trim());
                String msg = text_send.getText().toString().trim();
                text_send.getText().clear();

                notification(senderId, receiverId, msg, cuisines);

                // notification(receiverId, senderId, msg, cuisines);

            }

        } else {

            DatabaseReference refernce = FirebaseDatabase.getInstance().getReference();
            String uniqueId = refernce.push().getKey();
            HashMap<String, String> orderChatuserList = new HashMap<>();
            orderChatuserList.put("chatId", uniqueId);
            orderChatuserList.put("orderId", orderId);
            orderChatuserList.put("Date", currentDate);
            orderChatuserList.put("deliveryAgentId", senderId);
            orderChatuserList.put("deliveryAgentName", agentName);
            orderChatuserList.put("ownerUserId", receiverId);
            orderChatuserList.put("cuisines", cuisines);
            orderChatuserList.put("resturntName", resturntName);
            orderChatuserList.put("orderCreaterId", receiverId);
            orderChatuserList.put("orderStatus", "Pending");
            orderChatuserList.put("orderAccpetedId", "");
            orderChatuserList.put("Isownerpresent", "false");
            orderChatuserList.put("groupData", groupData);
            orderChatuserList.put("deliveryPartner", deliveryPartner);
            orderChatuserList.put("orderPrice", price);
            refernce.child("OrderDeliveryAgentList").push().setValue(orderChatuserList);
            //   refernce.child("OrderDeliveryAgentList").child(orderId).setValue(orderChatuserList);
            listflag = true;

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("mykey", false);
            editor.commit();

            if (!text_send.getText().toString().isEmpty()) {
                sendMsg(text_send.getText().toString().trim());
                String msg = text_send.getText().toString().trim();
                text_send.getText().clear();

                notification(senderId, receiverId, msg, cuisines);
            }

        }


    }


    private void sendtextmsg(String agentId) {


        if (agentId.equalsIgnoreCase(senderId)) {

            sendMsg(text_send.getText().toString().trim());
        } else {

            //  insertdeliveryagents();
        }

    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(Utils.userId).setValue(token1);
    }

    private void notification(final String senderUiD, final String recevieverUid, final String message, final String category) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(recevieverUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(senderUiD, R.drawable.location, message + " " + category, "Seesame",
                            recevieverUid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {

                                        Log.i("ResponeMgs", response.message().toString());
                                        Toast.makeText(ChatActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                                        if (response.body().success != 1) {
                                            //    Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed!" + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();


            }
        });
    }


    private void fetchOrderDetails(String OrderId, final String btndata) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");

        reference.orderByChild("orderId").equalTo(OrderId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String orderStatus = String.valueOf(snapshot1.child("orderStatus").getValue());
                    String orderAccpeterId = String.valueOf(snapshot1.child("partnerUserId").getValue());
                    String ownerId = String.valueOf(snapshot1.child("customerUserId").getValue());

                    if (orderStatus.equalsIgnoreCase("Pending")) {

                        if ((pagedata.equalsIgnoreCase("DeliveryAgent") && btndata.equalsIgnoreCase("btnclicked"))) {
                            acceptAdsPopup();

                        }

                    } else if (orderStatus.equalsIgnoreCase("Accepted")) {

                        if (orderAccpeterId.equalsIgnoreCase(senderId)) {


                            /// the one who accpeted this order can only text messages
                            tv_orderacpt.setVisibility(View.GONE);
                            tv_ratings.setVisibility(View.GONE);
                            layout_rating.setVisibility(View.GONE);

                        } else if (ownerId.equalsIgnoreCase(Utils.userId)) {

                            tv_orderacpt.setVisibility(View.GONE);
                            tv_ratings.setVisibility(View.GONE);
                            layout_rating.setVisibility(View.GONE);
                            tv_ordrcancel.setVisibility(View.VISIBLE);
                        } else {

                            //  the one who texted messages but not accepted the oreder
                            layout_msgVw.setVisibility(View.GONE);
                            tv_orderacpt.setVisibility(View.GONE);
                            tv_ratings.setVisibility(View.GONE);
                            layout_rating.setVisibility(View.GONE);
                        }


                    } else if (orderStatus.equalsIgnoreCase("Completed")) {
                        layout_msgVw.setVisibility(View.GONE);
                        tv_orderacpt.setVisibility(View.GONE);
                        tv_ratings.setVisibility(View.GONE);
                        layout_rating.setVisibility(View.GONE);
                        Toast.makeText(getApplication(), " This order Has been compelted ", Toast.LENGTH_SHORT).show();

                    } else if (orderStatus.equalsIgnoreCase("Expired")) {

                        layout_msgVw.setVisibility(View.GONE);
                        tv_orderacpt.setVisibility(View.GONE);
                        tv_ratings.setVisibility(View.GONE);
                        layout_rating.setVisibility(View.GONE);
                        Toast.makeText(getApplication(), "Sorry . This order Has been Expired ", Toast.LENGTH_SHORT).show();
                    } else {
                        layout_msgVw.setVisibility(View.GONE);
                        tv_orderacpt.setVisibility(View.GONE);
                        tv_ratings.setVisibility(View.GONE);
                        layout_rating.setVisibility(View.GONE);
                        Toast.makeText(getApplication(), "Sorry . Ad has been accpeted by someone just now, Try some other ads", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void reverseMsg(String msg) {

        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("chats");
        String uniqueId = refernce.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("chatId", uniqueId);
        hashMap.put("sender", receiverId);
        hashMap.put("receiver", senderId);
        hashMap.put("message", msg);
        hashMap.put("orderId", orderId);
        refernce.child(uniqueId).setValue(hashMap);


    }
}