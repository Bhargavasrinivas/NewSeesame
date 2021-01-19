package com.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import com.Adapters.CuisinesAdpater;
import com.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.models.CuisinesData;
import com.seesame.Client;
import com.seesame.Data;
import com.seesame.MainActivity;
import com.seesame.MyResponse;
import com.seesame.R;
import com.seesame.Sender;
import com.seesame.Token;
import com.seesame.ui.Myorders.MyorderFragment;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CuisinescategorieActivity extends AppCompatActivity {

    //  private WebView webView;
    private VideoView videoView;
    MediaController mediaController;
    private Object Drawable;
    private TextView tv_skip;
    private ArrayList<CuisinesData> cuisinesDataArrayList;
    private RecyclerView recyclerView_cuisinesCategorie;
    private CuisinesAdpater cuisinesAdpater;
    private CuisinesData cuisinesData;
    private ScrollView scrollView;
    private double currentlatitude, currentlongitude;
    FirebaseUser fuser;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisinescategorie);
 /*       Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


        Bundle b = getIntent().getExtras();
        currentlatitude = b.getDouble("lat");
        currentlongitude = b.getDouble("longi");

        //    count = count - 1;

        int count = 0;
/*
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
        reference.child("-MRJV30mPjqfN1hxeSG").child("count").setValue(count);*/
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        initView();
      /*  MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);*/

        videoView.setMediaController(new MediaController(getApplication()));
        Uri uri = Uri.parse("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                videoView.start();
                videoView.requestFocus();
            }

        });

        videoView.setVideoURI(uri);

        ///  notification();

        // updateToken(FirebaseInstanceId.getInstance().getToken());

      //  fetchCategoriValues();

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // startActivity(new Intent(CuisinescategorieActivity.this, MainActivity.class));
                Intent dashbaord = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", currentlatitude);
                bundle.putDouble("longi", currentlongitude);
                bundle.putString("cuisines", "Indian");
                dashbaord.putExtras(bundle);
                startActivity(dashbaord);

            }
        });


    }


    private void initView() {

        videoView = findViewById(R.id.videovw);
        tv_skip = findViewById(R.id.tv_skip);
        // scrollView = findViewById(R.id.scrollView);
        recyclerView_cuisinesCategorie = findViewById(R.id.recyclerView_cuisinesCategorie);
        //  videoView.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        //videoView.setVideoURI(Uri.parse("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4"));





       /* mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();
        videoView.start();*

       //https://youtu.be/6OEl3hhkV_E

        Uri uri = Uri.parse("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        videoView.setVideoURI(uri);
        //  videoView.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        //  videoView.start();

        cuisinesDataArrayList = new ArrayList<>();

        /*videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                Log.i("ErrorVideo", mp);
                return true;
            }
        });*/


     /*   Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4", MediaStore.Images.Thumbnails.MINI_KIND);
        BitmapDrawable bitmapD = new BitmapDrawable(thumbnail);
        //  videoView.setBackground(Drawable ,bitmapD);
        videoView.setBackground(bitmapD);*/






    /*   videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        /*videoView.setMediaController(mediaController);
                        mediaController.setAnchorView(videoView);*/

        //    Uri uri = Uri.parse("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        //   videoView.setMediaController(new MediaController(getApplication()));
        //    videoView.setVideoURI(uri);
        //  videoView.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        // videoView.requestFocus();
        //  videoView.start();


            /*        }
                });*/

      /*
            }
        });  */

        //  videoView.start();

      /*  webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("https://www.youtube.com");*/

/*
        BitmapDrawable bitmapDrawable = new BitmapDrawable(String.valueOf(R.drawable.sharebanner));
        videoView.setBackgroundDrawable(bitmapDrawable);*/


    }


    private void createCusinesDb() {


        for (int i = 0; i < cuisinesDataArrayList.size(); i++) {

            DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("Categories");
            String uniqueId = refernce.push().getKey();
            cuisinesData.setCategorieName(cuisinesDataArrayList.get(i).getCategorieName());
            cuisinesData.setCount(cuisinesDataArrayList.get(i).getCount());
            cuisinesData.setId(uniqueId);
            cuisinesData.setImgUrl(cuisinesDataArrayList.get(i).getImgUrl());
            refernce.child(uniqueId).setValue(cuisinesData);
        }


        //refernce.setValue(cuisinesDataArrayList);

    }


    private void fetchCategoriValues() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.keepSynced(true);
        //  reference.orderByChild("categorieName").equalTo("Indian").addListenerForSingleValueEvent(new ValueEventListener() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    long count = (long) snapshot1.child("count").getValue();
                    //   String categorieId = (String) snapshot1.child("id").getValue();
                    Log.i("Count ", String.valueOf(count));
                    if (count != 0) {
                        cuisinesData = new CuisinesData();
                       /* count = count + 1;
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
                        //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
                        reference.child(categorieId).child("count").setValue(count);
                        Log.i("CountIncrese ", String.valueOf(count));*/

                        cuisinesData.setImgUrl(String.valueOf(snapshot1.child("imgUrl").getValue()));
                        cuisinesData.setId(String.valueOf(snapshot1.child("id").getValue()));
                        cuisinesData.setCategorieName(String.valueOf(snapshot1.child("categorieName").getValue()));
                        cuisinesData.setCount((Long) snapshot1.child("count").getValue());
                        cuisinesDataArrayList.add(cuisinesData);
                        // cuisinesDataArrayList.add(snapshot1.child("count").getValue());


                    }

                }

                if (cuisinesDataArrayList.size() > 0) {

                    videoView.setVisibility(View.INVISIBLE);
                }


                CuisinesAdpater cuisinesAdpater = new CuisinesAdpater(CuisinescategorieActivity.this, cuisinesDataArrayList, currentlatitude, currentlongitude);
                GridLayoutManager layoutManager = new GridLayoutManager(getApplication(), 2);
                recyclerView_cuisinesCategorie.setLayoutManager(layoutManager);
                recyclerView_cuisinesCategorie.setAdapter(cuisinesAdpater);
                // recyclerView_cuisinesCategorie.setVisibility(View.GONE);
                //   scrollView.setTop(300dp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void categoireTypes() {

        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/hotlethree.jpg?alt=media&token=82f60ce1-7a8b-4dad-a74d-067998370983", "Indian", 0, "ind"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/hotlethree.jpg?alt=media&token=82f60ce1-7a8b-4dad-a74d-067998370983", "Beverages", 0, "ber"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/hotlethree.jpg?alt=media&token=82f60ce1-7a8b-4dad-a74d-067998370983", "FastFood", 0, "ind"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/hotlethree.jpg?alt=media&token=82f60ce1-7a8b-4dad-a74d-067998370983", "Desserts", 0, "ber"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/hotlethree.jpg?alt=media&token=82f60ce1-7a8b-4dad-a74d-067998370983", "Groceries", 0, "ind"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/hotlethree.jpg?alt=media&token=82f60ce1-7a8b-4dad-a74d-067998370983", "Pizza and Subway", 0, "ber"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/hotlethree.jpg?alt=media&token=82f60ce1-7a8b-4dad-a74d-067998370983", "Western", 0, "ber"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/hotlethree.jpg?alt=media&token=82f60ce1-7a8b-4dad-a74d-067998370983", "Chinnes", 0, "ber"));

        createCusinesDb();


    }

    private void deletingCategoriData() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.keepSynced(true);
        reference.orderByChild("categorieName").equalTo("Groceries").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    //   edt_username.setText((CharSequence) snapshot1.child("userName").getValue());

                    long count = (long) snapshot1.child("count").getValue();

                    String categorieId = (String) snapshot1.child("id").getValue();

                    Log.i("Count ", String.valueOf(count));


                    //  if (count == 0) {

                    count = 0;
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
                    //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
                    reference.child(categorieId).child("count").setValue(count);
                    Log.i("CountIncrese ", String.valueOf(count));
                    //   }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(Utils.userId).setValue(token1);
    }

    //PsJAveBgjsVGMUNgLg78fLIvGnf2

    ///6foU1vRBNwhkcF5kzNMwDiCi4sH3

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


                                        Log.i("ResponeMgs", response.message().toString());


                                        if (response.body().success != 1) {
                                            Toast.makeText(CuisinescategorieActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(CuisinescategorieActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CuisinescategorieActivity.this, "Failed!" + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();


            }
        });
    }

}