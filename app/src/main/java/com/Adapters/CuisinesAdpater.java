package com.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.models.CuisinesData;
import com.screens.CuisinescategorieActivity;
import com.screens.WebViewActivity;
import com.seesame.MainActivity;
import com.seesame.R;
import com.seesame.VideoActivity;
import com.seesame.ui.Myorders.MyorderFragment;
import com.seesame.ui.PostFreeAdd.PostFreeAddFragment;
import com.seesame.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CuisinesAdpater extends RecyclerView.Adapter<CuisinesAdpater.ViewHolder> {

    private Context mContext;
    private ArrayList<CuisinesData> cuisinesDataArrayList;
    private double currentlatitude, currentlongitude;
    Activity mActivity;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private String cateGorieName;

    public CuisinesAdpater(FragmentActivity activity, ArrayList<CuisinesData> cuisinesDataArrayList, double currentlatitude, double currentlongitude) {

        this.mActivity = activity;
        this.mContext = activity;
        this.cuisinesDataArrayList = cuisinesDataArrayList;
        this.currentlatitude = currentlatitude;
        this.currentlongitude = currentlongitude;

    }

  /*  public CuisinesAdpater(CuisinescategorieActivity cuisinescategorieActivity, ArrayList<CuisinesData> cuisinesDataArrayList, double currentlatitude, double currentlongitude) {

        this.mContext = cuisinescategorieActivity;
        this.cuisinesDataArrayList = cuisinesDataArrayList;
        this.currentlatitude = currentlatitude;
        this.currentlongitude = currentlongitude;

    }*/

    @NonNull
    @Override
    public CuisinesAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.cuisines_template, parent, false);
        return new CuisinesAdpater.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CuisinesAdpater.ViewHolder holder, int position) {

        final CuisinesData cuisinesData = cuisinesDataArrayList.get(position);

      /*  String count = String.valueOf(cuisinesDataArrayList.size());
        Toast.makeText(mContext, count, Toast.LENGTH_SHORT).show();*/


      /*  if (cuisinesDataArrayList.size() > 9) {

            if (cuisinesData.getCategorieName().equalsIgnoreCase("Place a group food order") ||
                    cuisinesData.getCategorieName().contains("Place a group Groceries") ||
                    cuisinesData.getCategorieName().contains("Sell your homemade delicious") ||
                    cuisinesData.getCategorieName().contains("Know more on what is SeeSame")
            ) {

              *//*  holder.tv_cuisinesname.setVisibility(View.GONE);
                holder.imgVw_cuisines.setVisibility(View.GONE);*//*
            } else {
                holder.tv_cuisinesname.setVisibility(View.VISIBLE);
                holder.imgVw_cuisines.setVisibility(View.VISIBLE);
                holder.tv_cuisinesname.setText(cuisinesData.getCategorieName());
                Picasso.with(mContext).load(cuisinesData.getImgUrl()).fit().into(holder.imgVw_cuisines);
            }

        } else {


            holder.tv_cuisinesname.setText(cuisinesData.getCategorieName());
            Picasso.with(mContext).load(cuisinesData.getImgUrl()).fit().into(holder.imgVw_cuisines);
        }
*/


        holder.tv_cuisinesname.setText(cuisinesData.getCategorieName());
        Picasso.with(mContext).load(cuisinesData.getImgUrl()).fit().into(holder.imgVw_cuisines);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                cateGorieName = cuisinesData.getCategorieName();
                Utils.postcategorieData = cuisinesData.getCategorieName();

                if (cuisinesData.getCategorieName().equalsIgnoreCase("Know more on what is SeeSame")) {

                    //  Show a Video about app

                  /*  Intent video = new Intent(mContext, WebViewActivity.class);
                    video.putExtra("PageInfo", "AppVideo");
                    mContext.startActivity(video);*/

                    Intent videoView = new Intent(mContext, VideoActivity.class);
                    mContext.startActivity(videoView);
                    mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                } else if (cuisinesData.getCategorieName().contains("Place")) {

                    // Redirect to Post free ads

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment fragment = new PostFreeAddFragment();
                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack("Home");
                    fragmentTransaction.commit();
                    BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);
                    bottomNavigationView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
                    mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                } else {


                  // cateGorieName = "Baking";
                    orderedMapList = new ArrayList<HashMap<String, String>>();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                    reference.keepSynced(true);
                    //   reference.orderByChild("cuisines").equalTo(Utils.categoriesSelected).addListenerForSingleValueEvent(new ValueEventListener() {
                    reference.orderByChild("cuisines").equalTo(cateGorieName).addListenerForSingleValueEvent(new ValueEventListener() {
                        HashMap<String, String> orderredMap;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {


                                // OrderList
                                orderredMap = new HashMap<>();
                                orderredMap.put("orderId", String.valueOf(snapshot1.child("orderId").getValue()));
                                orderredMap.put("orderDate", String.valueOf(snapshot1.child("orderDate").getValue()));
                                orderredMap.put("orderTime", String.valueOf(snapshot1.child("orderTime").getValue()));
                                orderredMap.put("orderPrice", String.valueOf(snapshot1.child("orderPrice").getValue()));
                                orderredMap.put("deliveryPartner", String.valueOf(snapshot1.child("deliveryPartner").getValue()));
                                orderredMap.put("expireTime", String.valueOf(snapshot1.child("expireTime").getValue()));
                                orderredMap.put("cuisines", String.valueOf(snapshot1.child("cuisines").getValue()));
                                orderredMap.put("orderCategoryId", String.valueOf(snapshot1.child("orderCategoryId").getValue()));
                                orderredMap.put("serviceSelecetd", String.valueOf(snapshot1.child("serviceSelecetd").getValue()));
                                orderredMap.put("orderStatus", String.valueOf(snapshot1.child("orderStatus").getValue()));
                                orderredMap.put("customerName", String.valueOf(snapshot1.child("customerName").getValue()));
                                orderredMap.put("customerUserId", String.valueOf(snapshot1.child("customerUserId").getValue()));
                                orderredMap.put("customerUserId", String.valueOf(snapshot1.child("customerUserId").getValue()));
                                orderredMap.put("deliveryPostalCode", String.valueOf(snapshot1.child("deliveryPostalCode").getValue()));
                                orderredMap.put("deliverylatitude", String.valueOf(snapshot1.child("deliverylatitude").getValue()));
                                orderredMap.put("deliverylongititude", String.valueOf(snapshot1.child("deliverylongititude").getValue()));
                                orderredMap.put("deliveryPlaceId", String.valueOf(snapshot1.child("deliveryPlaceId").getValue()));
                                orderredMap.put("deliveryAreaname", String.valueOf(snapshot1.child("deliveryAreaname").getValue()));
                                orderredMap.put("deliveryAddress", String.valueOf(snapshot1.child("deliveryAddress").getValue()));
                                orderredMap.put("partnerName", String.valueOf(snapshot1.child("partnerName").getValue()));

                                orderredMap.put("partnerUserId", String.valueOf(snapshot1.child("partnerUserId").getValue()));
                                orderredMap.put("partnerChatId", String.valueOf(snapshot1.child("partnerChatId").getValue()));
                                orderredMap.put("partnerPostalCode", String.valueOf(snapshot1.child("partnerPostalCode").getValue()));
                                orderredMap.put("partnerlatitude", String.valueOf(snapshot1.child("partnerlatitude").getValue()));
                                orderredMap.put("partnerlongititude", String.valueOf(snapshot1.child("partnerlongititude").getValue()));
                                orderredMap.put("partnerPlaceId", String.valueOf(snapshot1.child("partnerPlaceId").getValue()));
                                orderredMap.put("partnerAreaName", String.valueOf(snapshot1.child("partnerAreaName").getValue()));
                                orderredMap.put("partnerAddress", String.valueOf(snapshot1.child("partnerAddress").getValue()));
                                orderredMap.put("orderStatusDate", String.valueOf(snapshot1.child("orderStatusDate").getValue()));
                                orderredMap.put("orderStatusTime", String.valueOf(snapshot1.child("orderStatusTime").getValue()));
                                orderredMap.put("orderCancel", String.valueOf(snapshot1.child("orderCancel").getValue()));
                                orderredMap.put("orderAccepted", String.valueOf(snapshot1.child("orderAccepted").getValue()));
                                orderredMap.put("resturntName", String.valueOf(snapshot1.child("resturntName").getValue()));
                                orderredMap.put("resturntAddress", String.valueOf(snapshot1.child("resturntAddress").getValue()));
                                orderredMap.put("resturntPostalCode", String.valueOf(snapshot1.child("resturntPostalCode").getValue()));
                                orderredMap.put("orderImg", String.valueOf(snapshot1.child("orderImg").getValue()));
                                orderredMap.put("groupData", String.valueOf(snapshot1.child("groupData").getValue()));

                                String orderStatus = String.valueOf(snapshot1.child("orderStatus").getValue());

                                if ((!snapshot1.child("customerUserId").getValue().equals(Utils.userId))) {

                                }

                                if (orderStatus.equalsIgnoreCase("Pending")) {
                                    orderedMapList.add(orderredMap);
                                }

                            }

                            if (orderedMapList.size() == 0) {


                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                //  Fragment fragment = new HomeFragment();
                                Fragment fragment = new PostFreeAddFragment();
                                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                                fragmentTransaction.addToBackStack("Home");
                                fragmentTransaction.commit();
                                BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);
                                bottomNavigationView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
                                mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);


                            } else {

                                Utils.categoriesSelected = cuisinesData.getCategorieName();
                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                Fragment fragment = new HomeFragment();
                                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                                fragmentTransaction.addToBackStack("Home");
                                fragmentTransaction.commit();
                                BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);
                                bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                                mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return cuisinesDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgVw_cuisines;
        private TextView tv_cuisinesname;
        private View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVw_cuisines = itemView.findViewById(R.id.imgVw_cuisines);
            tv_cuisinesname = itemView.findViewById(R.id.tv_cuisinesname);
            layout = itemView.findViewById(R.id.layout);

        }
    }

    private void caterGorieApiCall() {

    }
}
