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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
    private TextView tv_noorder;
    private ImageView img_norders, img_filter, img_chats;
    private Spinner orderSpinner;
    List<String> orderList;
    private ArrayAdapter orderAdpter;
    private String spinerValue;


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
        getAllMyOrders();


        tablyout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        //   orderSpinner.setVisibility(View.VISIBLE);
                        img_filter.setVisibility(View.VISIBLE);
                        tv_noorder.setVisibility(View.GONE);
                        layout_myorders.setVisibility(View.VISIBLE);
                        recyclerView_myorders.setVisibility(View.VISIBLE);
                        recyclerView_ordersCompleted.setVisibility(View.GONE);

                        //   getAllMyOrders();
                        if (((orderedMapList == null || orderedMapList.size() == 0) && (orderedacceptedList.size() == 0 || orderedacceptedList == null))) {

                            layout_noorders.setVisibility(View.VISIBLE);

                        }
                        break;
                    case 1:
                        //   getAllMyOrders();
                        //  orderSpinner.setVisibility(View.GONE);
                        img_filter.setVisibility(View.GONE);
                        layout_myorders.setVisibility(View.VISIBLE);
                        recyclerView_myorders.setVisibility(View.GONE);
                        recyclerView_ordersCompleted.setVisibility(View.VISIBLE);
                        layout_noorders.setVisibility(View.GONE);

                        if (orderedcompletedMapList.size() == 0) {

                            tv_noorder.setVisibility(View.VISIBLE);

                        }
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

        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.popup_layout, null);
                // create the popup window
                /*int width = LinearLayout.LayoutParams.WRAP_CONTENT;.
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;*/

                int width = 500;
                int height = 400;

                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.RIGHT, 60, -700);

                ListView listView;
                listView = popupView.findViewById(R.id.listView);
                ArrayList filterList = new ArrayList();
                filterList.add("All Orders");
                filterList.add("Accepted Orders");


                String pageData = "myOrders";
                Filteradpter filteradpter = new Filteradpter(getActivity(), filterList, Utils.myorderfilterValue, pageData);
                listView.setAdapter(filteradpter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String value = String.valueOf(parent.getItemAtPosition(position));
                        popupWindow.dismiss();

                        if (value.equalsIgnoreCase("0")) {
                            Utils.myorderfilterValue = "All Orders";
                            getAllMyOrders();

                        } else {
                            Utils.myorderfilterValue = "Accepted Orders";
                            acceptedOrders();
                        }

                    }
                });


                popupView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {


                        popupWindow.dismiss();
                        return true;
                    }
                });


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

    private void getAllMyOrders() {

        orderedMapList.clear();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        //    reference.keepSynced(true);
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


                  /*  if (!snapshot1.child("customerUserId").getValue().equals(Utils.userId)) {
                       orderedcompletedMapList
                        orderedMapList.add(orderredMap);
                    }*/

                    //partnerUserId


                    String status = String.valueOf(snapshot1.child("orderStatus").getValue());

                    String partnerUserId = String.valueOf(snapshot1.child("partnerUserId").getValue());

                    if (status.equalsIgnoreCase("Completed")) {
                        orderedcompletedMapList.add(orderredMap);
                    } else {
                        orderedMapList.add(orderredMap);

                      /*  if(partnerUserId !=null){
                            orderedMapList.add(orderredMap);
                        }*/


                    }


                }

                if (orderedMapList.size() == 0) {

                 /*   Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getString(R.string.noOrders), Snackbar.LENGTH_LONG);
                    snackBar.show();*/

                    layout_noorders.setVisibility(View.VISIBLE);
                }


                recyclerView_myorders.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_myorders.setHasFixedSize(true);
                myorderAdpter = new MyorderAdpter(getActivity(), orderedMapList);
                recyclerView_myorders.setAdapter(myorderAdpter);


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

  /* @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String value = String.valueOf(parent.getItemAtPosition(position));

        Log.i("Value", value);


        if (value.equalsIgnoreCase("My Orders")) {
            getAllMyOrders();
        } else {
            acceptedOrders();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/

    private void acceptedOrders() {

        orderedacceptedList.clear();
        //  orderedcompletedMapList = new ArrayList<HashMap<String, String>>();


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        //    reference.keepSynced(true);
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




                  /*  if (!snapshot1.child("customerUserId").getValue().equals(Utils.userId)) {
                       orderedcompletedMapList
                        orderedMapList.add(orderredMap);
                    }*/

                    //partnerUserId


                    String status = String.valueOf(snapshot1.child("orderStatus").getValue());

                    // String partnerUserId = String.valueOf(snapshot1.child("partnerUserId").getValue());

                    if (status.equalsIgnoreCase("Accepted")) {
                        //   orderedcompletedMapList.add(orderredMap);
                        orderedacceptedList.add(orderredMap);
                    } else {
                        // orderedacceptedList.add(orderredMap);

                    }


                }

                if (orderedMapList.size() == 0) {

                 /*   Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getString(R.string.noOrders), Snackbar.LENGTH_LONG);
                    snackBar.show();*/

                    layout_noorders.setVisibility(View.VISIBLE);
                }


                recyclerView_myorders.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView_myorders.setHasFixedSize(true);
                myorderAdpter = new MyorderAdpter(getActivity(), orderedacceptedList);
                recyclerView_myorders.setAdapter(myorderAdpter);


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


}