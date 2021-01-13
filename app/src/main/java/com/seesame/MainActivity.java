package com.seesame;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private boolean location;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager locationManager;
    private static final int REQUEST_CODE = 101;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*    To hide Header View */
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();
       /* navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) getApplicationContext());*/

        //  fetchLocation();
        //    checkLocation();
       /* if (ActivityCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            // return;
        }*/

        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
      /*  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorWhite)));*/

        //  readUsertInfo();

        //    location = isLocationEnabled();


       /* if (location) {

            Toast.makeText(getApplicationContext(), "Location enabled true ", Toast.LENGTH_SHORT).show();
            //   Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);



        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Toast.makeText(getApplicationContext(), " enabled  false", Toast.LENGTH_SHORT).show();
        }*/


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            //  getWindow().setStatusBarColor(Color.WHITE);
        }

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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


                    String userName = String.valueOf(snapshot1.child("userName").getValue());

                    Log.i("userName", userName);


                    /*edt_username.setText((CharSequence) snapshot1.child("userName").getValue());
                    edt_emailId.setText((CharSequence) snapshot1.child("mailId").getValue());
                    userId = String.valueOf(snapshot1.child("id").getValue());
                    edt_mobileno .setText((CharSequence) snapshot1.child("mobileNo").getValue());
                    dbUserName = String.valueOf(snapshot1.child("userName").getValue());*/

                }

                //    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Error" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private BottomNavigationView.OnNavigationItemReselectedListener onNavigationItemReselectedListener
            = new BottomNavigationView.OnNavigationItemReselectedListener() {

        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {


            Toast.makeText(MainActivity.this, "Reselected", Toast.LENGTH_SHORT).show();


        }
    };

/*navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.action_one:
                    // Switch to page one
                    fragment = FragmentA.newInstance();
                    break;
                case R.id.action_two:
                    // Switch to page two
                    fragment = FragmentB.newInstance();
                    break;
                case R.id.action_three:
                    // Switch to page three
                    fragment = FragmentC.newInstance();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment,"TAG").commit();
            return true;
        }
    });*/


    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //   ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            // return;
        }

    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



  /*  @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }*/


    private void getAddresss(double lat, double lang) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
          /*  String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            String data = addresses.get(0).getSubLocality();
            String data2 = addresses.get(0).getSubAdminArea();*/


            //     Toast.makeText(getApplicationContext(), "Address " + addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();

            if ((addresses.get(0).getSubLocality() == null || (addresses.get(0).getSubLocality().isEmpty()))) {
                //  tv_areaName.setText(addresses.get(0).getAdminArea());
            } else {
                //   tv_areaName.setText(addresses.get(0).getSubLocality());
            }
            // tv_address.setText(addresses.get(0).getAddressLine(0));

           /* Log.i("AdminArea ", addresses.get(0).getAdminArea());

            Log.i("SubadminArea ", addresses.get(0).getAdminArea());*/


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /*public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                locationMode = Settings.Secure.getInt(getApplication().getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }*/
}