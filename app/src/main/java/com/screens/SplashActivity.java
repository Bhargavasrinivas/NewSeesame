package com.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seesame.AutoStartHelper;
import com.seesame.MyFirebaseMessaging;
import com.seesame.R;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 10000;
    SharedPreferences sharedpreferences;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    String autoStart;
    private Uri ImageUri;
    StorageReference storageReference;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //  super.onCreate(savedInstanceState);
        /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);*/
        //  setContentView(R.layout.activity_splash);
        //this.getSupportActionBar().hide();
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        autoStart = sharedpreferences.getString("autoStart", "");
        if (autoStart.equals("")) {
            AutoStartHelper.getInstance().getAutoStartPermission(getApplication());
        }

        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(this);

        FirebaseStorage storage = FirebaseStorage.getInstance();
     //   StorageReference storageRef = storage.getReference().child("images");
     //   storageRef.child("users/me/profile.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                ImageUri = uri;
                // Got the download URL for 'users/me/profile.png'
              //  Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
             //   generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                String failure ;
            }
        });


        // method to get the location
        //  getLastLocation();
/*        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null && pm.isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
        }
        startActivity(intent);*/


   /*     ComponentName componentName = new ComponentName(this, MyFirebaseMessaging.class);
        getPackageManager().setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

              //  startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        }, SPLASH_TIME_OUT);


    }


}