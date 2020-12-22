package com.seesame.ui.Myorders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Adapters.MyorderAdpter;
import com.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seesame.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MyorderFragment extends Fragment {

    private MyordersViewModel notificationsViewModel;
    TabLayout tablyout;
    RecyclerView recyclerView_myorders;
    View layout_myorders;
    private MyorderAdpter myorderAdpter;
    private ArrayList<HashMap<String, String>> orderedMapList;
    HashMap<String, String> orderredMap;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(MyordersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        //   final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //  textView.setText(s);
            }
        });

        initView(root);


        tablyout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        layout_myorders.setVisibility(View.VISIBLE);
                        getAllOrders();
                        break;
                    case 1:
                        layout_myorders.setVisibility(View.GONE);
                        break;

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return root;
    }


    private void initView(View view) {

        tablyout = view.findViewById(R.id.tablyout);
        recyclerView_myorders = view.findViewById(R.id.recyclerView_myorders);
        layout_myorders = view.findViewById(R.id.layout_myorders);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(20);
    }


    private void getAllOrders() {

        orderedMapList = new ArrayList<HashMap<String, String>>();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
    //    reference.keepSynced(true);
        reference.orderByChild("customerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            // reference.addValueEventListener(new ValueEventListener() {
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
                    orderredMap.put("orderCompleted", String.valueOf(snapshot1.child("orderCompleted").getValue()));

                  /*  if (!snapshot1.child("customerUserId").getValue().equals(Utils.userId)) {

                        orderedMapList.add(orderredMap);
                    }*/
                    orderedMapList.add(orderredMap);
                }

                recyclerView_myorders.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_myorders.setHasFixedSize(true);
                myorderAdpter = new MyorderAdpter(getActivity(), orderedMapList);
                recyclerView_myorders.setAdapter(myorderAdpter);

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}