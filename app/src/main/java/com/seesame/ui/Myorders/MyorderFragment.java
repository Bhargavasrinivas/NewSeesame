package com.seesame.ui.Myorders;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Adapters.CompletedOrderAdpter;
import com.Adapters.Filteradpter;
import com.Adapters.MyorderAdpter;
import com.InternetCheck.CheckNetwork;
import com.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.screens.LoginActivity;
import com.screens.UserListActivity;
import com.seesame.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MyorderFragment extends Fragment {

    private MyordersViewModel notificationsViewModel;
    TabLayout tablyout;
    RecyclerView recyclerView_myorders, recyclerView_ordersCompleted;
    View layout_myorders, layout_noorders;
    private MyorderAdpter myorderAdpter;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private ArrayList<HashMap<String, String>> orderedcompletedMapList;
    private ArrayList<HashMap<String, String>> orderedacceptedList;
    HashMap<String, String> orderredMap;
    private CompletedOrderAdpter completedOrderAdpter;
    private ProgressBar progressBar;
    private TextView tv_noorder, tv_notxt, tv_refersh;
    private ImageView img_norders, img_filter, img_chats;
    private Spinner orderSpinner;
    List<String> orderList;
    private ArrayAdapter orderAdpter;
    private String spinerValue;
    private Button btn_pending, btn_accepted, btn_expired, btn_cancelled;
    private Switch switch_cmpltordr;
    private Boolean switchcompleteordr = false;
    private View layout_nointernet, layout_main;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(MyordersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        setHasOptionsMenu(true);
        orderedacceptedList = new ArrayList<HashMap<String, String>>();
        orderedMapList = new ArrayList<HashMap<String, String>>();
        orderedcompletedMapList = new ArrayList<HashMap<String, String>>();
        initView(root);
        Utils.myorderfilterValue = "All Orders";

     /*   if (CheckNetwork.isInternetAvailable(getActivity())) {
            getAllMyOrders("Pending");
        } else {
            layout_main.setVisibility(View.GONE);
            layout_nointernet.setVisibility(View.VISIBLE);
        }*/
        checkinternetConnection("Pending");


        tv_refersh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkinternetConnection("Pending");
            }
        });

        img_chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatList = new Intent(getActivity(), UserListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("pageData", "Allchats");
                chatList.putExtras(bundle);
                getActivity().startActivity(chatList);

            }
        });
        btn_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if(switchcompleteordr){
                    switchcompleteordr = false;
                    switch_cmpltordr.setChecked(false);
                }else {
                    btnStatus("Pending");
                }

            }
        });

        btn_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if(switchcompleteordr){
                    switchcompleteordr = false;
                    switch_cmpltordr.setChecked(false);
                }else {
                    btnStatus("Accepted");
                }



            }
        });

        btn_expired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if(switchcompleteordr){
                    switchcompleteordr = false;
                    switch_cmpltordr.setChecked(false);
                }else {
                    btnStatus("Expired");
                }

            }
        });

        btn_cancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if(switchcompleteordr){
                    switchcompleteordr = false;
                    switch_cmpltordr.setChecked(false);
                }else {
                    btnStatus("Cancelled");
                }

            }
        });

        switch_cmpltordr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    greybtnStatus();
                    getAllMyOrders("Completed");
                    switchcompleteordr = true;
                    recyclerView_myorders.setVisibility(View.GONE);
                    recyclerView_ordersCompleted.setVisibility(View.VISIBLE);

                } else {
                    btnStatus("Pending");
                    switchcompleteordr = false;
                    recyclerView_myorders.setVisibility(View.VISIBLE);
                    recyclerView_ordersCompleted.setVisibility(View.GONE);
                }

            }
        });


        return root;
    }


    private void initView(View view) {

        tablyout = view.findViewById(R.id.tablyout);
        recyclerView_myorders = view.findViewById(R.id.recyclerView_myorders);
        recyclerView_ordersCompleted = view.findViewById(R.id.recyclerView_ordersCompleted);
        layout_myorders = view.findViewById(R.id.layout_myorders);
        tv_noorder = view.findViewById(R.id.tv_noorder);
        img_norders = view.findViewById(R.id.img_norders);
        layout_noorders = view.findViewById(R.id.layout_noorders);
        progressBar = view.findViewById(R.id.progressBar);
        img_filter = view.findViewById(R.id.img_filter);
        img_chats = view.findViewById(R.id.img_chats);
        btn_pending = view.findViewById(R.id.btn_pending);
        btn_accepted = view.findViewById(R.id.btn_accepted);
        btn_expired = view.findViewById(R.id.btn_expired);
        btn_cancelled = view.findViewById(R.id.btn_cancelled);
        switch_cmpltordr = view.findViewById(R.id.switch_cmpltordr);
        layout_nointernet = view.findViewById(R.id.layout_nointernet);
        layout_main = view.findViewById(R.id.layout_main);
        tv_refersh = view.findViewById(R.id.tv_refersh);

        //   orderSpinner = view.findViewById(R.id.orderSpinner);
      /*  orderList = new ArrayList<>();
        orderList.add("My Orders");
        orderList.add("Accpeted Orders");
        ArrayAdapter orderAdpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, orderList);
        orderAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderAdpter);*/

        progressBar.setMax(100);
        progressBar.setProgress(20);
    }

    private void getAllMyOrders(final String btninfo) {


        orderedMapList.clear();
        orderedcompletedMapList.clear();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.orderByChild("customerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            //  reference.orderByChild("customerUserId").equalTo(Utils.userId).orderByChild("partnerUserId").equalTo("").addListenerForSingleValueEvent(new ValueEventListener() {
            // reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                progressBar.setVisibility(View.GONE);
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

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

                    orderredMap.put("resturntName", String.valueOf(snapshot1.child("resturntName").getValue()));
                    orderredMap.put("resturntAddress", String.valueOf(snapshot1.child("resturntAddress").getValue()));
                    orderredMap.put("resturntPostalCode", String.valueOf(snapshot1.child("resturntPostalCode").getValue()));

                    orderredMap.put("ownerRating", String.valueOf(snapshot1.child("ownerRating").getValue()));
                    orderredMap.put("partnerRating", String.valueOf(snapshot1.child("partnerRating").getValue()));
                    orderredMap.put("orderImg", String.valueOf(snapshot1.child("orderImg").getValue()));
                    orderredMap.put("groupData", String.valueOf(snapshot1.child("groupData").getValue()));
                    orderredMap.put("orderQuantity", String.valueOf(snapshot1.child("orderQuantity").getValue()));


                    String status = String.valueOf(snapshot1.child("orderStatus").getValue());

                    String partnerUserId = String.valueOf(snapshot1.child("partnerUserId").getValue());

                    if (status.equalsIgnoreCase(btninfo)) {
                        layout_noorders.setVisibility(View.GONE);
                        tv_noorder.setVisibility(View.GONE);


                        if (btninfo.equalsIgnoreCase("Completed")) {
                            orderedcompletedMapList.add(orderredMap);
                        } else {

                            String ordrSts = String.valueOf(snapshot1.child("orderStatus").getValue());
                            if (ordrSts.equalsIgnoreCase(btninfo)) {
                                orderedMapList.add(orderredMap);
                            }

                        }

                    }
                }

                if (btninfo.equalsIgnoreCase("Completed")) {

                    if (orderedcompletedMapList.size() == 0) {
                        tv_noorder.setVisibility(View.VISIBLE);
                        layout_noorders.setVisibility(View.GONE);
                    } else {
                        tv_noorder.setVisibility(View.VISIBLE);
                    }

                }

                if (orderedMapList.size() == 0) {

                    if (btninfo.equalsIgnoreCase("Completed")) {
                        tv_noorder.setVisibility(View.VISIBLE);

                    } else {
                        layout_noorders.setVisibility(View.VISIBLE);
                        tv_noorder.setVisibility(View.GONE);
                    }

                }

                Collections.reverse(orderedMapList);
                recyclerView_myorders.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_myorders.setHasFixedSize(true);
                myorderAdpter = new MyorderAdpter(getActivity(), orderedMapList, btninfo);
                recyclerView_myorders.setAdapter(myorderAdpter);


                if (btninfo.equalsIgnoreCase("Completed")) {

                    myCompletedOrders();
                    /*Collections.reverse(orderedcompletedMapList);
                    recyclerView_ordersCompleted.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView_ordersCompleted.setHasFixedSize(true);
                    completedOrderAdpter = new CompletedOrderAdpter(getActivity(), orderedcompletedMapList);
                    recyclerView_ordersCompleted.setAdapter(completedOrderAdpter);*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void acceptedOrders() {

        orderedacceptedList.clear();
        orderedcompletedMapList.clear();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        //    reference.keepSynced(true);

        //  ref.child("users").orderByChild("profile/email").equalTo(email);
        //reference.child("Orders").orderByChild("partnerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
        //reference.orderByChild("customerUserId/partnerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
        reference.orderByChild("customerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {

            //  reference.orderByChild("customerUserId").equalTo(Utils.userId).orderByChild("partnerUserId").equalTo("").addListenerForSingleValueEvent(new ValueEventListener() {
            // reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                progressBar.setVisibility(View.GONE);
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

                    orderredMap.put("resturntName", String.valueOf(snapshot1.child("resturntName").getValue()));
                    orderredMap.put("resturntAddress", String.valueOf(snapshot1.child("resturntAddress").getValue()));
                    orderredMap.put("resturntPostalCode", String.valueOf(snapshot1.child("resturntPostalCode").getValue()));
                    orderredMap.put("ownerRating", String.valueOf(snapshot1.child("ownerRating").getValue()));
                    orderredMap.put("partnerRating", String.valueOf(snapshot1.child("partnerRating").getValue()));
                    orderredMap.put("orderImg", String.valueOf(snapshot1.child("orderImg").getValue()));
                    orderredMap.put("groupData", String.valueOf(snapshot1.child("groupData").getValue()));
                    orderredMap.put("orderQuantity", String.valueOf(snapshot1.child("orderQuantity").getValue()));
                    String status = String.valueOf(snapshot1.child("orderStatus").getValue());


/*
                    if (status.equalsIgnoreCase("Completed")) {
                        orderedcompletedMapList.add(orderredMap);
                    }*/

                    if (status.equalsIgnoreCase("Accepted")) {
                        //   orderedcompletedMapList.add(orderredMap);
                        orderedacceptedList.add(orderredMap);
                    }

                }

                if (orderedacceptedList.size() == 0) {
                    layout_noorders.setVisibility(View.VISIBLE);

                } else {
                    layout_noorders.setVisibility(View.GONE);
                }

                myaccpetedOrdes();

              /*  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView_myorders.setLayoutManager(linearLayoutManager);*/

               /* recyclerView_myorders.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_myorders.setHasFixedSize(true);
                myorderAdpter = new MyorderAdpter(getActivity(), orderedacceptedList, "Accepted");
                recyclerView_myorders.setAdapter(myorderAdpter);

                //  recyclerView_ordersCompleted.setLayoutManager(linearLayoutManager);
                recyclerView_ordersCompleted.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_ordersCompleted.setHasFixedSize(true);
                completedOrderAdpter = new CompletedOrderAdpter(getActivity(), orderedcompletedMapList);
                recyclerView_ordersCompleted.setAdapter(completedOrderAdpter);*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void btnStatus(String btndata) {


        if (switchcompleteordr) {
            switchcompleteordr = false;
            switch_cmpltordr.setChecked(false);
        }

        greybtnStatus();
        tv_noorder.setVisibility(View.GONE);

        if (btndata.equalsIgnoreCase("Pending")) {

            btn_pending.setTextColor(Color.parseColor("#FFFFFF"));
            btn_pending.setBackgroundResource(R.drawable.btnsmall_bg);
            // getAllMyOrders("Pending");
            checkinternetConnection("Pending");

            return;

        } else if (btndata.equalsIgnoreCase("Accepted")) {

            btn_accepted.setTextColor(Color.parseColor("#FFFFFF"));
            btn_accepted.setBackgroundResource(R.drawable.btnsmall_bg);
            // getAllMyOrders("Accepted");
            acceptedOrders();
            return;
        } else if (btndata.equalsIgnoreCase("Expired")) {
            btn_expired.setTextColor(Color.parseColor("#FFFFFF"));
            btn_expired.setBackgroundResource(R.drawable.btnsmall_bg);
            // getAllMyOrders("Expired");
            checkinternetConnection("Expired");
            return;
        } else {
            btn_cancelled.setTextColor(Color.parseColor("#FFFFFF"));
            btn_cancelled.setBackgroundResource(R.drawable.btnsmall_bg);
            //  getAllMyOrders("Cancelled");
            checkinternetConnection("Cancelled");
            return;
        }


    }

    private void greybtnStatus() {
        btn_accepted.setBackgroundResource(R.drawable.btnsmall_grey);
        btn_expired.setBackgroundResource(R.drawable.btnsmall_grey);
        btn_cancelled.setBackgroundResource(R.drawable.btnsmall_grey);
        btn_pending.setBackgroundResource(R.drawable.btnsmall_grey);
        btn_accepted.setTextColor(Color.parseColor("#4c4c4c"));
        btn_expired.setTextColor(Color.parseColor("#4c4c4c"));
        btn_cancelled.setTextColor(Color.parseColor("#4c4c4c"));
        btn_pending.setTextColor(Color.parseColor("#4c4c4c"));
    }

    private void myaccpetedOrdes() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        //    reference.keepSynced(true);

        //  ref.child("users").orderByChild("profile/email").equalTo(email);
        //reference.child("Orders").orderByChild("partnerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
        //reference.orderByChild("customerUserId/partnerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
        reference.orderByChild("partnerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {

            //  reference.orderByChild("customerUserId").equalTo(Utils.userId).orderByChild("partnerUserId").equalTo("").addListenerForSingleValueEvent(new ValueEventListener() {
            // reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                progressBar.setVisibility(View.GONE);
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

                    orderredMap.put("resturntName", String.valueOf(snapshot1.child("resturntName").getValue()));
                    orderredMap.put("resturntAddress", String.valueOf(snapshot1.child("resturntAddress").getValue()));
                    orderredMap.put("resturntPostalCode", String.valueOf(snapshot1.child("resturntPostalCode").getValue()));
                    orderredMap.put("ownerRating", String.valueOf(snapshot1.child("ownerRating").getValue()));
                    orderredMap.put("partnerRating", String.valueOf(snapshot1.child("partnerRating").getValue()));
                    orderredMap.put("orderImg", String.valueOf(snapshot1.child("orderImg").getValue()));
                    orderredMap.put("groupData", String.valueOf(snapshot1.child("groupData").getValue()));
                    orderredMap.put("orderQuantity", String.valueOf(snapshot1.child("orderQuantity").getValue()));
                    String status = String.valueOf(snapshot1.child("orderStatus").getValue());


/*
                    if (status.equalsIgnoreCase("Completed")) {
                        orderedcompletedMapList.add(orderredMap);
                    }*/

                    if (status.equalsIgnoreCase("Accepted")) {
                        //   orderedcompletedMapList.add(orderredMap);
                        orderedacceptedList.add(orderredMap);
                    }

                }

                if (orderedacceptedList.size() == 0) {
                    layout_noorders.setVisibility(View.VISIBLE);

                } else {
                    layout_noorders.setVisibility(View.GONE);
                }


              /*  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView_myorders.setLayoutManager(linearLayoutManager);*/

                recyclerView_myorders.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_myorders.setHasFixedSize(true);
                myorderAdpter = new MyorderAdpter(getActivity(), orderedacceptedList, "Accepted");
                recyclerView_myorders.setAdapter(myorderAdpter);

                //  recyclerView_ordersCompleted.setLayoutManager(linearLayoutManager);
                recyclerView_ordersCompleted.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_ordersCompleted.setHasFixedSize(true);
                completedOrderAdpter = new CompletedOrderAdpter(getActivity(), orderedcompletedMapList);
                recyclerView_ordersCompleted.setAdapter(completedOrderAdpter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void myCompletedOrders() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.orderByChild("partnerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            //  reference.orderByChild("customerUserId").equalTo(Utils.userId).orderByChild("partnerUserId").equalTo("").addListenerForSingleValueEvent(new ValueEventListener() {
            // reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                progressBar.setVisibility(View.GONE);
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

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

                    orderredMap.put("resturntName", String.valueOf(snapshot1.child("resturntName").getValue()));
                    orderredMap.put("resturntAddress", String.valueOf(snapshot1.child("resturntAddress").getValue()));
                    orderredMap.put("resturntPostalCode", String.valueOf(snapshot1.child("resturntPostalCode").getValue()));

                    orderredMap.put("ownerRating", String.valueOf(snapshot1.child("ownerRating").getValue()));
                    orderredMap.put("partnerRating", String.valueOf(snapshot1.child("partnerRating").getValue()));
                    orderredMap.put("orderImg", String.valueOf(snapshot1.child("orderImg").getValue()));
                    orderredMap.put("groupData", String.valueOf(snapshot1.child("groupData").getValue()));
                    orderredMap.put("orderQuantity", String.valueOf(snapshot1.child("orderQuantity").getValue()));

                    String orderStatus = String.valueOf(snapshot1.child("orderStatus").getValue());

                    if(orderStatus.equalsIgnoreCase("Completed")){
                        orderedcompletedMapList.add(orderredMap);
                    }

                }

               /* if (orderedMapList.size() == 0) {

                    if (btninfo.equalsIgnoreCase("Completed")) {
                        tv_noorder.setVisibility(View.VISIBLE);

                    } else {
                        layout_noorders.setVisibility(View.VISIBLE);
                        tv_noorder.setVisibility(View.GONE);
                    }

                }*/

                if (orderedcompletedMapList.size() == 0) {
                    tv_noorder.setVisibility(View.VISIBLE);
                } else {
                    tv_noorder.setVisibility(View.GONE);
                }

                Collections.reverse(orderedcompletedMapList);
                recyclerView_ordersCompleted.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_ordersCompleted.setHasFixedSize(true);
                completedOrderAdpter = new CompletedOrderAdpter(getActivity(), orderedcompletedMapList);
                recyclerView_ordersCompleted.setAdapter(completedOrderAdpter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkinternetConnection(String status) {

        if (CheckNetwork.isInternetAvailable(getActivity())) {
            getAllMyOrders(status);
        } else {
            layout_main.setVisibility(View.GONE);
            layout_nointernet.setVisibility(View.VISIBLE);
        }
    }


}