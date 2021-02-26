package com.screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.Adapters.CuisinesAdpater;
import com.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
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

    //private WebView webView;
    private VideoView videoView;
    MediaController mediaController;
    private TextView tv_skip;
    private ArrayList<CuisinesData> cuisinesDataArrayList;
    private RecyclerView recyclerView_cuisinesCategorie;
    private CuisinesAdpater cuisinesAdpater;
    private CuisinesData cuisinesData;
    private ScrollView scrollView;
    private double currentlatitude, currentlongitude;
    FirebaseUser fuser;
    APIService apiService;
    private ImageView imgVw;
    private boolean location;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    private double latitude, longititude;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Email = "emailKey";
    SharedPreferences sharedpreferences;
    private static final String VIDEO_SAMPLE = "https://www.akunatech.com/image/about/akuna_video.mp4";
    // Current playback position (in milliseconds).
    private int mCurrentPosition = 0;
    private CardView layoutimg_one, layoutimg_two, layoutimg_three;
    private static int SPLASH_TIME_OUT = 9000;
    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";
    private WebView webview;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisinescategorie);
        imgVw = findViewById(R.id.imgVw);

        imgVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification();
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //  Toast.makeText(getApplicationContext(), "firebaseUser "+firebaseUser, Toast.LENGTH_SHORT).show();

        String userFirbase = String.valueOf(firebaseUser);





        layoutimg_one = findViewById(R.id.layoutimg_one);
        layoutimg_two = findViewById(R.id.layoutimg_two);
        layoutimg_three = findViewById(R.id.layoutimg_three);
        progressBar = findViewById(R.id.progressBar);

        cuisinesData = new CuisinesData();

        webview = findViewById(R.id.webview);
        webview.setBackgroundResource(R.drawable.sharebanner);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(false);
        webview.getSettings().setSupportZoom(false);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webview.loadUrl("https://www.akunatech.com/image/about/akuna_video.mp4");


        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(this);


        //  getLastLocation();

/*        Bundle b = getIntent().getExtras();
        currentlatitude = b.getDouble("lat");
        currentlongitude = b.getDouble("longi");*/

        //    count = count - 1;
/*
        int count = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
        reference.child("-MRcA_jtFt-P2Wc6-hj6").child("count").setValue(count);*/


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        initView();

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }


        //   updateToken(FirebaseInstanceId.getInstance().getToken());


        /* Method to fetch categories from db*/


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setMax(100);
                progressBar.setProgress(20);
                progressBar.setVisibility(View.VISIBLE);

                fetchCategoriValues();

            }
        }, SPLASH_TIME_OUT);


        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // startActivity(new Intent(CuisinescategorieActivity.this, MainActivity.class));
                /*Intent dashbaord = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", latitude);
                bundle.putDouble("longi", longititude);
                bundle.putString("cuisines", "Indian");
                dashbaord.putExtras(bundle);
                startActivity(dashbaord);*/

                notification();

            }
        });
          // categoireTypes();

    }


    private void initializePlayer() {
        // Show the "Buffering..." message while the video loads.
        //mBufferingTextView.setVisibility(VideoView.VISIBLE);

        // Buffer and decode the video sample.
        Uri videoUri = getMedia(VIDEO_SAMPLE);
        videoView.setVideoURI(videoUri);

        // Listener for onPrepared() event (runs after the media is prepared).
        videoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        // Hide buffering message.
                        //  mBufferingTextView.setVisibility(VideoView.INVISIBLE);

                        // Restore saved position, if available.
                        if (mCurrentPosition > 0) {
                            videoView.seekTo(mCurrentPosition);
                        } else {
                            // Skipping to 1 shows the first frame of the video.
                            videoView.seekTo(1);
                        }

                        // Start playing!
                        videoView.start();
                    }
                });

        // Listener for onCompletion() event (runs after media has finished
        // playing).
        videoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Toast.makeText(CuisinescategorieActivity.this,
                                "Playing",
                                Toast.LENGTH_SHORT).show();

                        // Return the video position to the start.
                        videoView.seekTo(0);
                    }
                });
    }


    // Release all media-related resources. In a more complicated app this
    // might involve unregistering listeners or releasing audio focus.
    private void releasePlayer() {
        //  videoView.stopPlayback();
    }

    // Get a Uri for the media sample regardless of whether that sample is
    // embedded in the app resources or available on the internet.
    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            // Media name is an external URL.
            return Uri.parse(mediaName);
        } else {
            // you can also put a video file in raw package and get file from there as shown below
            return Uri.parse("android.resource://" + getPackageName() +
                    "/raw/" + mediaName);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();
        // Load the media each time onStart() is called.
        //  initializePlayer();
    }


    @Override
    protected void onPause() {
        super.onPause();


        //  Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();

        // In Android versions less than N (7.0, API 24), onPause() is the
        // end of the visual lifecycle of the app.  Pausing the video here
        // prevents the sound from continuing to play even after the app
        // disappears.
        //
        // This is not a problem for more recent versions of Android because
        // onStop() is now the end of the visual lifecycle, and that is where
        // most of the app teardown should take place.
      /*  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause();
        }*/
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Toast.makeText(getApplicationContext(), "onStop", Toast.LENGTH_SHORT).show();


        webview.stopLoading();
        // Media playback takes a lot of resources, so everything should be
        // stopped and released at this time.
        //  releasePlayer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current playback position (in milliseconds) to the
        // instance state bundle.
//        outState.putInt(PLAYBACK_TIME, videoView.getCurrentPosition());
    }


    private void initView() {

        //    videoView = findViewById(R.id.videovw);
        tv_skip = findViewById(R.id.tv_skip);
        // scrollView = findViewById(R.id.scrollView);
        recyclerView_cuisinesCategorie = findViewById(R.id.recyclerView_cuisinesCategorie);

        cuisinesDataArrayList = new ArrayList<>();


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

                    //    if ((count != 0 && count < 0)) {

                    if (count > 0) {

                        //    Log.i("Count ", String.valueOf(count));
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

                progressBar.setVisibility(View.GONE);
                CuisinesAdpater cuisinesAdpater = new CuisinesAdpater(CuisinescategorieActivity.this, cuisinesDataArrayList, currentlatitude, currentlongitude);
                GridLayoutManager layoutManager = new GridLayoutManager(getApplication(), 2);
                recyclerView_cuisinesCategorie.setLayoutManager(layoutManager);
                recyclerView_cuisinesCategorie.setAdapter(cuisinesAdpater);
                // recyclerView_cuisinesCategorie.setVisibility(View.GONE);
                //   scrollView.setTop(300dp);
                if (cuisinesDataArrayList.size() > 2) {
                    //   tv_skip.setVisibility(View.GONE);
                    webview.setVisibility(View.GONE);
                    layoutimg_one.setVisibility(View.GONE);
                    layoutimg_two.setVisibility(View.GONE);
                    layoutimg_three.setVisibility(View.GONE);
                } else if (cuisinesDataArrayList.size() == 1) {
                    webview.stopLoading();
                    webview.setVisibility(View.GONE);
                    layoutimg_one.setVisibility(View.VISIBLE);
                    layoutimg_two.setVisibility(View.VISIBLE);
                    layoutimg_three.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });


    }


    private void categoireTypes() {


        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/testimage.jpg?alt=media&token=9e6de9b5-fddd-4751-b568-fb9065c4cb93", "Testing Order", 1, "GF"));
     //   cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/groceries.jpg?alt=media&token=873ee62c-5e15-49fa-9100-6381fcf9f77f", "Place a group Groceries", 1, "GG"));
      //  cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/Homemade%20Foods.jpg?alt=media&token=e1385348-0ef8-4c32-9a3b-c453870b2c55", "Sell your homemade delicious", 1, "HF"));
      //  cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/VideoImage.jpg?alt=media&token=da20d577-2e86-4d24-8320-b4487719e055", "Know more on what is SeeSame", 1, "Tour"));


        createCusinesDb();


       /* cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/Beverages.jpg?alt=media&token=786cccfc-428d-47fc-bb2f-14f9a7cfe8e8", "Beverages", 0, "ber"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/FastFood.jpg?alt=media&token=f8655552-4d54-4ad4-be2d-66f58661ec56", "FastFood", 0, "fstfd"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/Desserts.jpg?alt=media&token=2b760e1e-618c-4731-b82a-9b37689f59a6", "Desserts", 0, "dsrts"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/Groceries.jpg?alt=media&token=90571d68-3dac-47c8-8d41-43345a4173f3", "Groceries", 0, "grc"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/Pizza.jpg?alt=media&token=58de7954-3fb8-4ba1-8514-3ffc36a3a2b0", "Pizza and Subway", 0, "piz"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/Western.jpg?alt=media&token=2fd3e7c8-d4c0-4a81-81f0-2dadf44d0f5d", "Western", 0, "wst"));
        cuisinesDataArrayList.add(new CuisinesData("https://firebasestorage.googleapis.com/v0/b/seesame-go-dutch.appspot.com/o/Chinnes.jpg?alt=media&token=752eba5a-131e-4086-950c-f361157cc408", "Chinnes", 0, "chn"));*/

        //
        // count = count - 1;

       /* long count = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.child("-MRcA_jrtSspW0Lq-SOP").child("count").setValue(count);
        Log.i("CountIncrese ", String.valueOf(count));*/


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

        //PsJAveBgjsVGMUNgLg78fLIvGnf2
        //6foU1vRBNwhkcF5kzNMwDiCi4sH3

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo("PsJAveBgjsVGMUNgLg78fLIvGnf2"); // senderID
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data("6foU1vRBNwhkcF5kzNMwDiCi4sH3", R.drawable.location, "Bhargava has accepted your ad" + " " + "Hotel Taj", "Seesame",
                            "PsJAveBgjsVGMUNgLg78fLIvGnf2");

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {


                                        Log.i("ResponeMgs", response.message().toString());
                                        Toast.makeText(CuisinescategorieActivity.this, "Success!", Toast.LENGTH_SHORT).show();

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

                                            currentlatitude = latitude;
                                            currentlongitude = longititude;

                                            // Toast.makeText(getApplicationContext(), "Lat " + location.getLatitude(), Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(CuisinescategorieActivity.this, LocationenableActivity.class));
                //  Toast.makeText(getApplicationContext(), "Denyed by user ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}