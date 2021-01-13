package com.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.seesame.R;

public class LocationenableActivity extends AppCompatActivity {
    int PERMISSION_ID = 44;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationenable);

        // this.setTitle("");
    }


    public void btn_allowlocation(View view) {


        requestPermissions();

    }

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

    @Override
    public void onBackPressed() {

     /*   AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to exit from the application?");
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();*/

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit from application", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                // Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finish();

            }
        }, 2000);


    }

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

                //    Toast.makeText(getApplicationContext(), " Location Allowed ", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(LocationenableActivity.this, LoginActivity.class));


            } else {
                // startActivity(new Intent(LoginActivity.this, LocationenableActivity.class));
                Toast.makeText(getApplicationContext(), "Please allow location permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION,
                        Manifest.permission
                                .ACCESS_FINE_LOCATION},
                PERMISSION_ID);
    }
}