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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.models.Chat;
import com.seesame.Client;
import com.seesame.Data;
import com.seesame.MyResponse;
import com.seesame.R;
import com.seesame.Sender;
import com.seesame.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {


    private ImageButton btn_send, btn_orderacpt;
    private RecyclerView recyclervw;
    private EditText text_send;
    List<Chat> mchat;
    private DatabaseReference dbrefernce;
    private String orderId, receiverId, senderId, pagedata, agentName, orderName;
    private MessageAdapter messageAdapter;
    private boolean listflag = false;
    APIService apiService;
    private TextView tv_ordrcancel, tv_orderacpt;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String userKey = "userKey";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final View parentLayout = findViewById(android.R.id.content);

        initView();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Utils.chatlist = sharedpreferences.getBoolean("mykey", true);
        Toast.makeText(getApplicationContext(), "chatList " + Utils.chatlist, Toast.LENGTH_SHORT).show();

        try {

            String CurrentString = Utils.mailId;
            String[] separated = CurrentString.split("@");
            agentName = separated[0];


        } catch (Exception e) {


        }

        Bundle b = getIntent().getExtras();
        orderId = b.getString("orderId");
        receiverId = b.getString("receiverId");
        senderId = b.getString("senderId");
        pagedata = b.getString("pageData");
        orderName = b.getString("orderName");


        if (pagedata.equalsIgnoreCase("DeliveryAgent")) {
            this.setTitle("Chat" + "-" + b.getString("resturntName"));
        } else {

            this.setTitle("Chat" + "-" + orderName);
        }

        if (!pagedata.equalsIgnoreCase("DeliveryAgent")) {

            btn_orderacpt.setVisibility(View.GONE);
        }

        //    bundle.putString("pageData", "DeliveryAgent");

        readallMessage();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //  if ((pagedata.equalsIgnoreCase("DeliveryAgent") && listflag == false)) {
                if ((pagedata.equalsIgnoreCase("DeliveryAgent") && Utils.chatlist)) {

                    Toast.makeText(getApplicationContext(), "inserted to Db ", Toast.LENGTH_SHORT).show();
                    DatabaseReference refernce = FirebaseDatabase.getInstance().getReference();
                    String uniqueId = refernce.push().getKey();
                    HashMap<String, String> orderChatuserList = new HashMap<>();
                    orderChatuserList.put("chatId", uniqueId);
                    orderChatuserList.put("orderId", orderId);
                    orderChatuserList.put("deliveryAgentId", senderId);
                    orderChatuserList.put("deliveryAgentName", agentName);
                    refernce.child("OrderDeliveryAgentList").push().setValue(orderChatuserList);
                    listflag = true;

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("mykey", false);
                    editor.commit();

                }

                if (!text_send.getText().toString().isEmpty()) {
                    sendMsg(text_send.getText().toString().trim());
                    text_send.getText().clear();
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

                Snackbar snackBar = Snackbar.make(parentLayout.findViewById(android.R.id.content),
                        getString(R.string.acctporder), Snackbar.LENGTH_LONG)
                        .setAction("NO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setAction("YES", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                                reference.child(orderId).child("orderStatus").setValue("Accepted");
                                reference.child(orderId).child("partnerUserId").setValue(senderId);

                            }
                        });

                snackBar.setActionTextColor(Color.RED);
                snackBar.show();


            }
        });


        btn_orderacpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change the Order Status to Completed
              /*  DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                reference.child(orderId).child("orderStatus").setValue("Completed");
                reference.child(orderId).child("partnerUserId").setValue(senderId);*/
                //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());

              /*  reference.child(orderId).child("partnerlatitude").setValue("Completed");
                reference.child(orderId).child("partnerlongititude").setValue("Completed");
                reference.child(orderId).child("partnerAreaName").setValue("Completed");
                reference.child(orderId).child("partnerAddress").setValue("Completed");
                reference.child(orderId).child("orderAccepted").setValue("Completed");*/


          /*      final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                final AlertDialog alert = builder.create();
                builder.setTitle("Are you sure want accept this  Free add?")
                        .setCancelable(false)

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.dismiss();

                            }
                        })
                        .setPositiveButton("Go", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                                reference.child(orderId).child("orderStatus").setValue("Completed");
                                reference.child(orderId).child("partnerUserId").setValue(senderId);
                            }
                        });


                alert.show();*/

             /*   Snackbar snackBar = Snackbar.make(getApplicationContext().findViewById(android.R.id.content),
                        getString(R.string.noOrders), Snackbar.LENGTH_LONG);
                snackBar.show();*/

                //   Toast.makeText(getApplication(), "Clicked on Me ", Toast.LENGTH_SHORT).show();
                Snackbar snackBar = Snackbar.make(parentLayout.findViewById(android.R.id.content),
                        getString(R.string.acctporder), Snackbar.LENGTH_LONG)
                        .setAction("NO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setAction("YES", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                                reference.child(orderId).child("orderStatus").setValue("Completed");
                                reference.child(orderId).child("partnerUserId").setValue(senderId);

                            }
                        });

                snackBar.setActionTextColor(Color.RED);
                snackBar.show();


            }
        });

    }


    private void initView() {

        btn_send = findViewById(R.id.btn_send);
        recyclervw = findViewById(R.id.recyclervw);
        text_send = findViewById(R.id.text_send);
        btn_orderacpt = findViewById(R.id.btn_orderacpt);
        tv_ordrcancel = findViewById(R.id.tv_ordrcancel);
        tv_orderacpt = findViewById(R.id.tv_orderacpt);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


    }


    private void sendMsg(String msg) {


      /*  fuser =  FirebaseAuth.getInstance().getCurrentUser();
        dbrefernce = FirebaseDatabase.getInstance().getReference(User)*/

        //  DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("Orders");

        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("chats");
        String uniqueId = refernce.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("chatId", uniqueId);
        hashMap.put("sender", senderId);
        hashMap.put("receiver", receiverId);
        hashMap.put("message", msg);
        hashMap.put("orderId", orderId);

        refernce.child(uniqueId).setValue(hashMap);

      /*  hashMap.put("sender", Util.senderId);  // Gan
        hashMap.put("receiver", Util.receiverId);*/


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
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Messages " + databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();


            }
        });


    }

    private void notification() {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo("6foU1vRBNwhkcF5kzNMwDiCi4sH3");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data("PsJAveBgjsVGMUNgLg78fLIvGnf2", R.mipmap.ic_launcher, "Seesame" + ": " + "Welcome Mesage", "Seesame",
                            "6foU1vRBNwhkcF5kzNMwDiCi4sH3");

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {


                                        Log.i("ResponeMgs ", response.message().toString());


                                        if (response.body().success != 1) {
                                            //  Toast.makeText(LoginActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

}