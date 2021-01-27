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

import java.util.ArrayList;
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
    private String orderId, receiverId, senderId, pagedata, agentName, orderName, cuisines, price, resturntName;
    private MessageAdapter messageAdapter;
    private boolean listflag = false;
    APIService apiService;
    private TextView tv_ordrcancel, tv_orderacpt, tv_ratings;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String userKey = "userKey";
    SharedPreferences sharedpreferences;
    FirebaseUser fuser;
    private TextView tv_welcmmsg;
    private View layout_wlcmmsg;
    HashMap<String, String> userMap;
    private ArrayList<HashMap<String, String>> userMapList;
    private boolean isUserpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final View parentLayout = findViewById(android.R.id.content);

        initView();

        userMapList = new ArrayList<HashMap<String, String>>();
        //     updateToken(FirebaseInstanceId.getInstance().getToken());

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
        cuisines = b.getString("cuisines");
        price = b.getString("price");
        resturntName = b.getString("resturntName");

        Log.i("SenderId ", senderId);
        Log.i("receiverId ", receiverId);


        if (cuisines.equalsIgnoreCase("Groceries")) {

            String pricetxt = price + "$";

            String msg = "Thanks for taking a look at my request , am looking to share " + pricetxt + " " + "for Groceries order from" + " " + resturntName + "  If you are interested please Accept the order by clicking the below  accept button";

            tv_welcmmsg.setText(msg);

        } else {
            String pricetxt = price + "$";
            String msg = "Thanks for taking a look at my request , am looking to share " + pricetxt + " " + "for food order at " + resturntName + " If you are interested please Accept the order by clicking the below  accept button";
            tv_welcmmsg.setText(msg);

        }


        //  Toast.makeText(getApplicationContext(), "PageData " + pagedata, Toast.LENGTH_SHORT).show();


        if (pagedata.equalsIgnoreCase("DeliveryAgent")) {
            this.setTitle("Chat" + "-" + b.getString("resturntName"));
            tv_orderacpt.setVisibility(View.VISIBLE);
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

     /*   if (!pagedata.equalsIgnoreCase("DeliveryAgent")) {

        }*/


        readallMessage();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //  if ((pagedata.equalsIgnoreCase("DeliveryAgent") && listflag == false)) {
               /* if ((pagedata.equalsIgnoreCase("DeliveryAgent") && Utils.chatlist)) {
                    insertdeliveryagents();
                }
*/
                if (pagedata.equalsIgnoreCase("DeliveryAgent")) {
                    //  fetchUsers();

                    if (isUserpresent) {
                        if (!text_send.getText().toString().isEmpty()) {
                            sendMsg(text_send.getText().toString().trim());
                            text_send.getText().clear();
                        }
                    } else {

                        insertdeliveryagents();
                    }

                } else {
                    if (!text_send.getText().toString().isEmpty()) {
                        sendMsg(text_send.getText().toString().trim());
                        text_send.getText().clear();
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


                acceptAdsPopup();


            }
        });


        btn_orderacpt.setOnClickListener(new View.OnClickListener() {
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
                                reference.child(orderId).child("orderStatus").setValue("Completed");
                                reference.child(orderId).child("partnerUserId").setValue(senderId);

                                deletingCategoriData(cuisines);

                            }
                        });

                snackBar.setActionTextColor(Color.RED);
                snackBar.show();


            }
        });


        tv_ratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pagedata.equalsIgnoreCase("DeliveryAgent")) {

                    intent(receiverId);

                } else {
                    intent(receiverId);

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

        //tv_welcmmsg

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

                        fetchUsers();


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
                Toast.makeText(getApplicationContext(), "Order has been succfully accepted", Toast.LENGTH_SHORT).show();
                finish();


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
                    //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
                    reference.child(categorieId).child("count").setValue(count);
                    Log.i("CountIncrese ", String.valueOf(count));

                    deletingCategoriData(cuisines);

                    //   }

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


                    if ((snapshot.child("deliveryAgentId").getValue().equals(senderId))) {

                        isUserpresent = true;
                    }

                    //  sendtextmsg(String.valueOf(snapshot.child("deliveryAgentId").getValue()));

                }


             /*   if (userMapList.size() == 0) {

                    insertdeliveryagents();
                }
*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void insertdeliveryagents() {

        Toast.makeText(getApplicationContext(), "inserted to Db ", Toast.LENGTH_SHORT).show();
        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference();
        String uniqueId = refernce.push().getKey();
        HashMap<String, String> orderChatuserList = new HashMap<>();
        orderChatuserList.put("chatId", uniqueId);
        orderChatuserList.put("orderId", orderId);
        orderChatuserList.put("deliveryAgentId", senderId);
        orderChatuserList.put("deliveryAgentName", agentName);
        orderChatuserList.put("ownerUserId", receiverId);
        orderChatuserList.put("cuisines", cuisines);
        refernce.child("OrderDeliveryAgentList").push().setValue(orderChatuserList);
        listflag = true;

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("mykey", false);
        editor.commit();

        if (!text_send.getText().toString().isEmpty()) {
            sendMsg(text_send.getText().toString().trim());
            text_send.getText().clear();
        }

    }

    private void sendtextmsg(String agentId) {


        if (agentId.equalsIgnoreCase(senderId)) {

            sendMsg(text_send.getText().toString().trim());
        } else {

            insertdeliveryagents();
        }

    }


}