package com.seesame.ui.Categoire;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Adapters.CuisinesAdpater;
import com.InternetCheck.CheckNetwork;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.models.CuisinesData;
import com.screens.APIService;
import com.screens.CuisinescategorieActivity;
import com.screens.LoginActivity;
import com.screens.UserListActivity;
import com.seesame.R;

import java.util.ArrayList;

public class Categories extends Fragment {

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
    private View layout_text, layout_nointernet;
    private static int SPLASH_TIME_OUT = 9000;
    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";
    private WebView webview;
    private ProgressBar progressBar;
    private ImageView img_chatIcon;
    private TextView tv_refersh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_categoires, container, false);
        initView(root);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if (CheckNetwork.isInternetAvailable(getActivity())) {

            fetchCategoriValues();

        } else {

            scrollView.setVisibility(View.GONE);
            layout_nointernet.setVisibility(View.VISIBLE);

        }

        tv_refersh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCategoriValues();
            }
        });


        img_chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatList = new Intent(getActivity(), UserListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("pageData", "Allchats");
                chatList.putExtras(bundle);
                getActivity().startActivity(chatList);
            }
        });


        return root;
    }


    private void initView(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        img_chatIcon = view.findViewById(R.id.img_chatIcon);
        layout_text = view.findViewById(R.id.layout_text);
        recyclerView_cuisinesCategorie = view.findViewById(R.id.recyclerView_cuisinesCategorie);
        layout_nointernet = view.findViewById(R.id.layout_nointernet);
        tv_refersh = view.findViewById(R.id.tv_refersh);
        scrollView = view.findViewById(R.id.scrollView);
        cuisinesDataArrayList = new ArrayList<>();
        cuisinesData = new CuisinesData();

        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(getActivity());


    }

    private void fetchCategoriValues() {

        progressBar.setMax(100);
        progressBar.setProgress(20);
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.keepSynced(true);
        //  reference.orderByChild("categorieName").equalTo("Indian").addListenerForSingleValueEvent(new ValueEventListener() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    long count = (long) snapshot1.child("count").getValue();
                    //   if (count > 0) {

                    cuisinesData = new CuisinesData();
                    cuisinesData.setImgUrl(String.valueOf(snapshot1.child("imgUrl").getValue()));
                    cuisinesData.setId(String.valueOf(snapshot1.child("id").getValue()));
                    cuisinesData.setCategorieName(String.valueOf(snapshot1.child("categorieName").getValue()));
                    cuisinesData.setCount((Long) snapshot1.child("count").getValue());
                    cuisinesDataArrayList.add(cuisinesData);

                    //   }

                }

                progressBar.setVisibility(View.GONE);
                CuisinesAdpater cuisinesAdpater = new CuisinesAdpater(getActivity(), cuisinesDataArrayList, currentlatitude, currentlongitude);
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                recyclerView_cuisinesCategorie.setLayoutManager(layoutManager);
                recyclerView_cuisinesCategorie.setAdapter(cuisinesAdpater);

                if (cuisinesDataArrayList.size() > 9) {

                    layout_text.setVisibility(View.GONE);
                    layout_nointernet.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });


    }

}

