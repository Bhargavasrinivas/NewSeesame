package com.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Adapters.AutoScrollPagerAdapter;
import com.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private String registeredMailId;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Email = "emailKey";
    SharedPreferences sharedpreferences;
    private LocationManager locationManager;
    private static final int REQUEST_CODE = 101;
    private boolean location;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    private double latitude, longititude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().hide();

        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(this);

        getLastLocation();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("mykey",true);
        editor.commit();

      /*  progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(20);
*/

        //    requestPermissions();


       /* location = isLocationEnabled();


        if (location) {

            Toast.makeText(getApplicationContext(), "Location enabled true ", Toast.LENGTH_SHORT).show();
            //   Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);



        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Toast.makeText(getApplicationContext(), " enabled  false", Toast.LENGTH_SHORT).show();
        }

*/


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


    public void tv_termscondition(View view) {

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


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

      /*  firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();*/


        if (currentUser != null) {
            Log.d(TAG, "Currently Signed in: " + currentUser.getEmail());
            //     showToastMessage("Currently Logged in: " + currentUser.getEmail());

            //    Log.i("UserName ", currentUser.getDisplayName());
            Log.i("ImageURl ", String.valueOf(currentUser.getPhotoUrl()));
            Log.i("Email ", currentUser.getEmail());
            Log.i("UserId ", currentUser.getUid());

            Utils.userId = currentUser.getUid();
            Utils.userName = currentUser.getDisplayName();
            Utils.mailId = currentUser.getEmail();
            Utils.userprofilepic = String.valueOf(currentUser.getPhotoUrl());
            Utils.mobileNo = "";

           /* String s = "Bhargavags@gmail.com";
            String s1 = s.substring(s.indexOf("@") - 15);
            s1.trim();
            Log.i("trimchar ", s1);*/


            Intent main = new Intent(LoginActivity.this, CuisinescategorieActivity.class);
            Bundle bundle = new Bundle();
            bundle.putDouble("lat", latitude);
            bundle.putDouble("longi", longititude);
            main.putExtras(bundle);
            startActivity(main);

            //startActivity(new Intent(LoginActivity.this, MainActivity.class));


        }
        String mailId = sharedpreferences.getString(Email, "");
        if (mailId != null) {

            readUsertInfo(mailId);

        }

    }
    //  Funcation to store User info in UserSignup table


    private void registerUser(final String userName, final String email, String password, final String imgUrl) {

        registeredMailId = email;


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            final String userId = current_user.getUid();

                            //  String token_id = FirebaseInstanceId.getInstance().getToken();

                            dbrefernce = FirebaseDatabase.getInstance().getReference("userSignup").child(userId);
                            Utils.userId = userId;
                            HashMap<String, String> orderMap = new HashMap<>();
                            orderMap.put("id", userId);
                            orderMap.put("userName", userName);
                            orderMap.put("mailId", email);
                            orderMap.put("password", "Seesame@123$");
                            orderMap.put("imgUrl", imgUrl);
                            orderMap.put("mobileNo", "");

                            dbrefernce.setValue(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        //  Toast.makeText(getApplicationContext(), "Registered Succesfully", Toast.LENGTH_SHORT).show();
                                        progressHUD.dismiss();
                                        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        Intent main = new Intent(LoginActivity.this, CuisinescategorieActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putDouble("lat", latitude);
                                        bundle.putDouble("longi", longititude);
                                        main.putExtras(bundle);
                                        startActivity(main);


                                    } else {

                                        //  Toast.makeText(getApplicationContext(), "Registered Unsuccesfully", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });


                        } else {

                         /*   startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();*/

                            readUsertInfo(registeredMailId);

                            //    login();

                        }


                    }
                });


    }


    private void readUsertInfo(String mailId) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
        reference.keepSynced(true);

        //  reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {

        reference.orderByChild("mailId").equalTo(mailId).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {


                    String userId = String.valueOf(snapshot1.child("id").getValue());

                    Utils.userId = userId;
                    Utils.userName = String.valueOf(snapshot1.child("id").getValue());
                    Utils.mailId = String.valueOf(snapshot1.child("mailId").getValue());

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Email, Utils.mailId);
                   // editor.putBoolean("key",true);
                    editor.commit();


                   /* Utils.userprofilepic = String.valueOf(snapshot1.child("id").getValue());
                    Utils.mobileNo = "";*/

                    String usr = userId;

                    //    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                   /* Intent main = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", latitude);
                    bundle.putDouble("longi", longititude);
                    main.putExtras(bundle);
                    startActivity(main);*/

                    Intent main = new Intent(LoginActivity.this, CuisinescategorieActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", latitude);
                    bundle.putDouble("longi", longititude);
                    main.putExtras(bundle);
                    startActivity(main);







                    //   login();

                   /* edt_username.setText((CharSequence) snapshot1.child("userName").getValue());
                    edt_emailId.setText((CharSequence) snapshot1.child("mailId").getValue());
                    userId = String.valueOf(snapshot1.child("id").getValue());
                    edt_mobileno .setText((CharSequence) snapshot1.child("mobileNo").getValue());
                    dbUserName = String.valueOf(snapshot1.child("userName").getValue());

                    img_mobile.setVisibility(View.GONE);
                    img_uname.setVisibility(View.GONE);
                    //  Log.i("UserId ", String.valueOf(snapshot1.child("id").getValue()));

                    if ((!snapshot1.child("imgUrl").getValue().toString().isEmpty() || snapshot1.child("imgUrl").getValue().toString() != null)) {
                        Glide.with(getActivity()).load(snapshot1.child("imgUrl").getValue().toString()).into(user_profilepic);
                    }*/
                }

                //    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Error" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient
                        .getLastLocation()
                        .addOnCompleteListener(
                                new OnCompleteListener<Location>() {

                                    @Override
                                    public void onComplete(
                                            @NonNull Task<Location> task) {
                                        Location location = task.getResult();
                                        if (location == null) {
                                            requestNewLocationData();
                                        } else {
                                          /*  latTextView
                                                    .setText(
                                                            location
                                                                    .getLatitude()
                                                                    + "");
                                            lonTextView
                                                    .setText(
                                                            location
                                                                    .getLongitude()
                                                                    + "");*/

                                            latitude = location.getLatitude();
                                            longititude = location.getLongitude();
                                          //  Toast.makeText(getApplicationContext(), "Lat " + location.getLatitude(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
            } else {
                //   Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest
                = new LocationRequest();
        LocationCallback mLocationCallback = new LocationCallback();
        mLocationRequest.setPriority(
                LocationRequest
                        .PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(this);

        mFusedLocationClient
                .requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper());
    }

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED

                && ActivityCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission
                                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        /* ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission
                        .ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        */
    }


    // method to requestfor permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager
                locationManager
                = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        return locationManager
                .isProviderEnabled(
                        LocationManager.GPS_PROVIDER)
                || locationManager
                .isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager
                    .PERMISSION_GRANTED) {

                getLastLocation();
            } else {
                startActivity(new Intent(LoginActivity.this, LocationenableActivity.class));
              //  Toast.makeText(getApplicationContext(), "Denyed by user ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        } /*else {

            startActivity(new Intent(LoginActivity.this, LocationenableActivity.class));

          //  Toast.makeText(getApplicationContext(), "Ask for permission ", Toast.LENGTH_SHORT).show();
          //  requestPermissions();
        *//*    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);*//*
            //

        }*/
    }


}
