package com.screens;

import android.os.Bundle;

import com.Adapters.UserListadapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.models.User;
import com.seesame.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private UserListadapter userListadapter;
    EditText search_users;
    private List<User> mUsers;
    private RecyclerView recyclervw_userlist;
    private ArrayList<HashMap<String, String>> userMapList;
    private String orderId, orderName, price, resturntName, cuisines, pageData;
    HashMap<String, String> userMap;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();

        // Intent info = getIntent();
      /*  Bundle b = getIntent().getExtras();

        orderId = b.getString("orderId");
        receiverId = b.getString("receiverId");
        senderId = b.getString("senderId");
        pagedata = b.getString("pageData");
*/

        Bundle b = getIntent().getExtras();
        orderId = b.getString("orderId");
        orderName = b.getString("OrderName");
        price = b.getString("price");
        resturntName = b.getString("resturntName");
        cuisines = b.getString("cuisines");
        pageData = b.getString("pageData");


        userMapList = new ArrayList<HashMap<String, String>>();
        mUsers = new ArrayList<>();


        if (pageData.equalsIgnoreCase("pageData")) {
            getAllchatUsers();

        } else {
            readallUsers(orderId);
        }


    }

    private void readallUsers(String orderId) {
        progressBar.setMax(100);
        progressBar.setProgress(20);
        progressBar.setVisibility(View.VISIBLE);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OrderDeliveryAgentList");
        reference.keepSynced(true);
        //  Log.i("MyId ", firebaseUser.getUid());
        reference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {

            // reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if (search_users.getText().toString().equals("")) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                /*    User user = snapshot.getValue(User.class);
                    Log.i("UserId ", user.getId());
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                    */
                    userMap = new HashMap<>();
                    userMap.put("chatId", String.valueOf(snapshot.child("chatId").getValue()));
                    userMap.put("deliveryAgentId", String.valueOf(snapshot.child("deliveryAgentId").getValue()));
                    userMap.put("deliveryAgentName", String.valueOf(snapshot.child("deliveryAgentName").getValue()));
                    userMap.put("orderId", String.valueOf(snapshot.child("orderId").getValue()));
                    userMap.put("orderName", orderName);


                    userMapList.add(userMap);


                    //     orderredMap.put("orderId", String.valueOf(snapshot1.child("orderId").getValue()));

                }


                  /*  recyclervw.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclervw.setHasFixedSize(true);*/

                recyclervw_userlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclervw_userlist.setHasFixedSize(true);
                userListadapter = new UserListadapter(UserListActivity.this, userMapList, false, price, cuisines, orderName);
                recyclervw_userlist.setAdapter(userListadapter);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getAllchatUsers() {

        progressBar.setMax(100);
        progressBar.setProgress(20);
        progressBar.setVisibility(View.VISIBLE);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OrderDeliveryAgentList");
        reference.keepSynced(true);
        //  Log.i("MyId ", firebaseUser.getUid());
        reference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {

            // reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if (search_users.getText().toString().equals("")) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                /*    User user = snapshot.getValue(User.class);
                    Log.i("UserId ", user.getId());
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                    */
                    userMap = new HashMap<>();
                    userMap.put("chatId", String.valueOf(snapshot.child("chatId").getValue()));
                    userMap.put("deliveryAgentId", String.valueOf(snapshot.child("deliveryAgentId").getValue()));
                    userMap.put("deliveryAgentName", String.valueOf(snapshot.child("deliveryAgentName").getValue()));
                    userMap.put("orderId", String.valueOf(snapshot.child("orderId").getValue()));
                    userMap.put("orderName", orderName);


                    userMapList.add(userMap);


                    //     orderredMap.put("orderId", String.valueOf(snapshot1.child("orderId").getValue()));

                }


                  /*  recyclervw.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclervw.setHasFixedSize(true);*/

                recyclervw_userlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclervw_userlist.setHasFixedSize(true);
                userListadapter = new UserListadapter(UserListActivity.this, userMapList, false, price, cuisines, orderName);
                recyclervw_userlist.setAdapter(userListadapter);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {

        recyclervw_userlist = findViewById(R.id.recyclervw_userlist);
        progressBar = findViewById(R.id.progressBar);
    }

}