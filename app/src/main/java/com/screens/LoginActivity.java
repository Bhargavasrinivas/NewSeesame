package com.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Adapters.AutoScrollPagerAdapter;
import com.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.seesame.MainActivity;
import com.seesame.R;

import java.util.HashMap;

import okhttp3.internal.Util;

public class LoginActivity extends AppCompatActivity {
    private static final int AUTO_SCROLL_THRESHOLD_IN_MILLI = 1000;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1001;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private static final String EMAIL = "email";
    private DatabaseReference dbrefernce;
    private View layout_google, layout_twitter, layout_facebook;
    //CallbackManager callbackManager;
    FirebaseUser fuser;
    private ProgressBar progressBar;
    private KProgressHUD progressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().hide();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(20);


        layout_twitter = findViewById(R.id.layout_twitter);
        layout_facebook = findViewById(R.id.layout_facebook);
        layout_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progressHUD = KProgressHUD.create(LoginActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait loging in ")
                        .setMaxProgress(100)
                        .show();
                progressHUD.setProgress(90);

            }
        });

        layout_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressHUD.dismiss();

            }
        });

      /*  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorWhite)));*/


        layout_google = findViewById(R.id.layout_google);

        AutoScrollPagerAdapter autoScrollPagerAdapter = new AutoScrollPagerAdapter(getSupportFragmentManager());
        AutoScrollViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(autoScrollPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // start auto scroll
        viewPager.startAutoScroll();
        // set auto scroll time in mili
        viewPager.setInterval(AUTO_SCROLL_THRESHOLD_IN_MILLI);
        // enable recycling using true
        viewPager.setCycle(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            getWindow().setStatusBarColor(Color.WHITE);
        }

        configureGoogleClient();

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Sign In
                //
                signInToGoogle();
            }
        });


        layout_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                signInToGoogle();
            }
        });

    }

    public void tv_termscond(View view) {

        // startActivity(new Intent(LoginActivity.this, WebViewActivity.class));
        Intent terms = new Intent(LoginActivity.this, WebViewActivity.class);
        terms.putExtra("PageInfo", "Terms of Service");
        startActivity(terms);


    }


    public void tv_policy(View view) {
        //  startActivity(new Intent(LoginActivity.this, WebViewActivity.class));
        Intent policy = new Intent(LoginActivity.this, WebViewActivity.class);
        policy.putExtra("PageInfo", "Privacy Policy");
        startActivity(policy);


    }


    public void signInToGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @SuppressLint("ResourceAsColor")
    private void configureGoogleClient() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                //907898291765-6pokcirq82p5m9buclmc7aieuslrrhtn.apps.googleusercontent.com
                .requestIdToken(getString(R.string.default_web_client_id))
                // .requestIdToken("115208236281-2h73u2devfl11p2hnpq6ad18892riiop.apps.googleusercontent.co")


                // .requestIdToken("350517955200-faectofs3u2dod9ncof498mu3gpqdi5s.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setBackgroundColor(R.color.google_color);
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {


            progressHUD = KProgressHUD.create(LoginActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setMaxProgress(100)
                    .show();
            progressHUD.setProgress(90);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
              /*  Log.i("Name ", account.getDisplayName());
                Log.i("ImageURl ", String.valueOf(account.getPhotoUrl()));
                Log.i("Email ", account.getEmail());*/
                //  Log.i("Mobile ", account.get);

                //   showToastMessage("Google Sign in Succeeded");
                //   firebaseAuthWithGoogle(account);

                // user Signup funcation call
                registerUser(account.getDisplayName(), account.getEmail(), "Akunatech@123$", String.valueOf(account.getPhotoUrl()));


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // showToastMessage("Google Sign in Failed " + e);
            }
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Currently Signed in: " + currentUser.getEmail());
            //     showToastMessage("Currently Logged in: " + currentUser.getEmail());

            Log.i("UserName ", currentUser.getDisplayName());
            Log.i("ImageURl ", String.valueOf(currentUser.getPhotoUrl()));
            Log.i("Email ", currentUser.getEmail());
            Log.i("UserId ", currentUser.getUid());

            Utils.userId = currentUser.getUid();
            Utils.userName = currentUser.getDisplayName();
            Utils.mailId = currentUser.getEmail();
            Utils.userprofilepic = String.valueOf(currentUser.getPhotoUrl());
            Utils.mobileNo = "";


            startActivity(new Intent(LoginActivity.this, MainActivity.class));


        }
    }
    //  Funcation to store User info in UserSignup table


    private void registerUser(final String userName, final String email, String password, final String imgUrl) {


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            final String userId = current_user.getUid();

                            //  String token_id = FirebaseInstanceId.getInstance().getToken();

                            dbrefernce = FirebaseDatabase.getInstance().getReference("userSignup").child(userId);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("id", userId);
                            userMap.put("userName", userName);
                            userMap.put("mailId", email);
                            userMap.put("password", "Seesame@123$");
                            userMap.put("imgUrl", imgUrl);
                            userMap.put("mobileNo", "");

                            dbrefernce.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        //  Toast.makeText(getApplicationContext(), "Registered Succesfully", Toast.LENGTH_SHORT).show();
                                        progressHUD.dismiss();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));


                                    } else {

                                        //  Toast.makeText(getApplicationContext(), "Registered Unsuccesfully", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });


                        } else {


                            Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });


    }


}
