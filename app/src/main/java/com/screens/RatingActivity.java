package com.screens;

import android.os.Bundle;

import com.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.seesame.ui.home.HomeFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingActivity extends AppCompatActivity {

    private TextView tv_ratingtxt, tv_username;
    private Button btn_sbmt;
    private CircleImageView user_profilepic;
    private RatingBar ratingBar;
    private String ratetxt, myUserId, deliveryUserId, orderId, userName, commnets, userRole;
    private RateModel rateModel;
    private EditText edt_comments;
    private int rateValue;
    private ProgressBar progressBar;
    private float ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();


        String userId = Utils.userId;

        Bundle b = getIntent().getExtras();
        myUserId = b.getString("myuserId");
        deliveryUserId = b.getString("deliverusrId");
        orderId = b.getString("orderId");



        userRole = b.getString("userRole");


        // Db Call to read user info from user id
        readUsertInfo();

        ratingBar.setRating(5.0f);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (rating < 1.0f) {
                    ratingBar.setRating(1.0f);
                    ratings = 1;
                    return;
                }

                int ratigbar = (int) ratingBar.getRating();
                float rateinFloat = rating;


                if (ratingBar.getRating() == 1) {

                    tv_ratingtxt.setText("Tell us, What went wrong?");
                    //   commnets = "Tell us, What went wrong?";
                    edt_comments.setText("Tell us, What went wrong?");
                    ratings = 1;
                    return;

                }
                if (ratingBar.getRating() == 2) {

                    tv_ratingtxt.setText("What needs to be improved?");
                    // commnets = "Needs to be improved?";
                    edt_comments.setText("What needs to be improved?");
                    ratings = 2;
                    return;

                }
                if (ratingBar.getRating() == 3) {

                    tv_ratingtxt.setText("What coluld be done better");
                    //  commnets = "coluld be done better";
                    edt_comments.setText("What coluld be done better");
                    ratings = 3;
                    return;

                }
                if (ratingBar.getRating() == 4) {

                    tv_ratingtxt.setText("You are  Perfect");
                    edt_comments.setText("You are  Perfect");
                    //   commnets = "You are  Perfect";
                    ratings = 4;
                    return;

                }
                if (ratingBar.getRating() == 5) {

                    tv_ratingtxt.setText("You are Awesome");
                    edt_comments.setText("You are Awesome");
                    //  commnets = "You are Awesome ";
                    ratings = 5;
                    return;

                }


            }
        });


    }

    private void readUsertInfo() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
        reference.keepSynced(true);

        //  reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {

        reference.orderByChild("id").equalTo(deliveryUserId).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                   /* edt_username.setText((CharSequence) snapshot1.child("userName").getValue());
                    edt_emailId.setText((CharSequence) snapshot1.child("mailId").getValue());
                    userId = String.valueOf(snapshot1.child("id").getValue());
                    edt_mobileno.setText((CharSequence) snapshot1.child("mobileNo").getValue());
                    dbUserName = String.valueOf(snapshot1.child("userName").getValue());*/
                    userName = String.valueOf(snapshot1.child("userName").getValue());
                    tv_username.setText(String.valueOf(snapshot1.child("userName").getValue()));
                    if ((!snapshot1.child("imgUrl").getValue().toString().isEmpty() || snapshot1.child("imgUrl").getValue().toString() != null)) {
                        Glide.with(getApplication()).load(snapshot1.child("imgUrl").getValue().toString()).into(user_profilepic);
                    }
                }

                progressBar.setVisibility(View.GONE);

                //    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Error" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initView() {

        tv_ratingtxt = findViewById(R.id.tv_ratingtxt);
        edt_comments = findViewById(R.id.edt_comments);
        btn_sbmt = findViewById(R.id.btn_sbmt);
        ratingBar = findViewById(R.id.ratingBar);
        user_profilepic = findViewById(R.id.user_profilepic);
        tv_username = findViewById(R.id.tv_username);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(20);
        progressBar.setVisibility(View.VISIBLE);
    }


    public void onClick_btnrating(View view) {

        /*String rating = "Rating is :" + ratingBar.getRating();
        int rateValue = (int) ratingBar.getRating();*/
        progressBar.setVisibility(View.VISIBLE);
        // int rateValue = (int) ratingBar.getRating();

        float rate = ratingBar.getRating();

        Log.i("ratingVlaue ", String.valueOf(ratings));

        insertingtoRateTable(ratings);

    }


    private void insertingtoRateTable(float rateval) {

      /*  myUserId = b.getString("myuserId");
        deliveryUserId = b.getString("deliverusrId");
        orderId = b.getString("orderId");*/

        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("Ratings");
        String rateId = refernce.push().getKey();

        rateModel = new RateModel();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        if (userRole.equalsIgnoreCase("owner")) {
            reference.child(orderId).child("partnerRating").setValue("true");
            rateModel.setCommentorId(myUserId);
            rateModel.setUserId(deliveryUserId);
        } else {
            rateModel.setCommentorId(deliveryUserId);
            rateModel.setUserId(myUserId);
            reference.child(orderId).child("ownerRating").setValue("true");
        }

        rateModel.setRateid(rateId);
        rateModel.setComments(edt_comments.getText().toString().trim());
        rateModel.setRateCount(rateval);
        rateModel.setUserName(userName);
        rateModel.setOrderId(orderId);
        refernce.child(rateId).setValue(rateModel);
        Toast.makeText(getApplicationContext(), "Rating succesfully posted", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        finish();


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
    public void onBackPressed(){
        finish();
      /*  backpress = (backpress + 1);
        Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

        if (backpress>1) {
            this.finish();
        }*/
    }

}