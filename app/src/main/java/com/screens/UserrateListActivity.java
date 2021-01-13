package com.screens;

import android.os.Bundle;

import com.Adapters.UserRatelistAdpater;
import com.Utils;
import com.bumptech.glide.Glide;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.models.RateModel;
import com.seesame.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserrateListActivity extends AppCompatActivity {

    private CircleImageView user_profilepic;
    private TextView tv_username, tv_nocomments;
    private RecyclerView recyclervw_userratelist;
    private ArrayList<RateModel> ratingList = new ArrayList<>();
    private RateModel rateModel;
    private UserRatelistAdpater userRatelistAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userrate_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ratingList = new ArrayList<RateModel>();
        initView();
        readUsertInfo();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initView() {

        tv_username = findViewById(R.id.tv_username);
        user_profilepic = findViewById(R.id.user_profilepic);
        tv_nocomments = findViewById(R.id.tv_nocomments);
        recyclervw_userratelist = findViewById(R.id.recyclervw_userratelist);
    }


    private void readUsertInfo() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
        reference.keepSynced(true);

        //  reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {

        reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    /*edt_username.setText((CharSequence) snapshot1.child("userName").getValue());
                    edt_emailId.setText((CharSequence) snapshot1.child("mailId").getValue());
                    userId = String.valueOf(snapshot1.child("id").getValue());
                    edt_mobileno.setText((CharSequence) snapshot1.child("mobileNo").getValue());
                    dbUserName = String.valueOf(snapshot1.child("userName").getValue());\
                    img_mobile.setVisibility(View.GONE);
                    img_uname.setVisibility(View.GONE);*/

                    tv_username.setText((CharSequence) snapshot1.child("userName").getValue());

                   /* if ((!snapshot1.child("imgUrl").getValue().toString().isEmpty() || snapshot1.child("imgUrl").getValue().toString() != null)) {
                        Glide.with(getApplication()).load(snapshot1.child("imgUrl").getValue().toString()).into(user_profilepic);
                    }*/
                }


                fetchRatings();
                //    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplication(), "Error" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void fetchRatings() {


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ratings");
        reference.keepSynced(true);

        reference.orderByChild("userId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    rateModel = new RateModel();
                    long ratings = (long) snapshot1.child("rateCount").getValue();
                    rateModel.setComments(String.valueOf(snapshot1.child("comments").getValue()));
                    rateModel.setRateCount((int) ratings);
                    ratingList.add(rateModel);
                    String commnets = String.valueOf(snapshot1.child("comments").getValue());


                    Log.i("ratings ", String.valueOf(ratings));

                }

                if(ratingList.size() == 0){

                 tv_nocomments.setVisibility(View.VISIBLE);

                }else {
                    tv_nocomments.setVisibility(View.GONE);
                    recyclervw_userratelist.setLayoutManager(new LinearLayoutManager(getApplication()));
                    recyclervw_userratelist.setHasFixedSize(true);
                    userRatelistAdpater = new UserRatelistAdpater(UserrateListActivity.this, ratingList);
                    recyclervw_userratelist.setAdapter(userRatelistAdpater);
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplication(), "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });


    }


}