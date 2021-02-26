package com.seesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.screens.LoginActivity;

public class WelcomeActivity extends Activity {
    private static int SPLASH_TIME_OUT = 1000;
    SharedPreferences sharedpreferences;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    String autoStart;
    StorageReference storageReference;
    StorageReference storage;
    private Uri ImageUri;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


     /*   storage =  FirebaseStorage.getInstance().getReference();

        storage.child("Beverages.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                String a;
                Toast.makeText(getApplicationContext(),"Successs",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String a;
                Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_SHORT).show();
            }
        });
*/
      //  storageReference = storage.getReference();
   /*     databaseReference= FirebaseDatabase.getInstance().getReference("Images");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String val;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String val;
            }
        });*/

      /*  storageReference = storage.getReference().child("images");
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
        }); */

      /*  storageReference =  storage.getReference();
        storageReference.child("images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageUri = uri;
                // Got the download URL for 'users/me/profile.png'
              /*  Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                generatedFilePath = downloadUri.toString(); /// The string(file link) that you need*/
           /*   }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                String failure ;
            }
        }); */




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        }, SPLASH_TIME_OUT);



    }
}