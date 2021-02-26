package com.seesame.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Adapters.CompletedOrderAdpter;
import com.Adapters.Filteradpter;
import com.Adapters.MyorderAdpter;
import com.Adapters.OrderAdpter;
import com.Adapters.Refresh;
import com.InternetCheck.CheckNetwork;
import com.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.screens.UserListActivity;
import com.seesame.R;
import com.seesame.ui.Myorders.MyorderFragment;
import com.seesame.ui.PostFreeAdd.PostFreeAddFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment implements Refresh {

    private HomeViewModel homeViewModel;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    Location currentLocation;
    private double latitude, longititude;
    LatLng latLngcurrnet = new LatLng(12.3456789, 98.7654321);
    LatLng latLngdestination = new LatLng(98.7654321, 12.3456789);
    boolean distnaceflag = false;
    private TextView tv_areaName, tv_address, tv_refersh;
    private RecyclerView recyclerview_order;
    HashMap<String, String> orderredMap;
    private List<String> OrderList;
    private EditText edt_search_text;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private ArrayList<HashMap<String, String>> myOrderList;
    private OrderAdpter orderAdpter;
    private ProgressBar progressBar;
    private AutocompleteSupportFragment autocompleteFragment;
    LocationManager locationManager;
    private Double currentlatitude, currentlongitude;
    private String cuisines;
    private ImageView img_filter, newchat;
    private Button btn_allorders, btn_myoreders;
    private View layout_main, layout_nointernet, layout_noorders;
    private Refresh refreshlistner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        orderedMapList = new ArrayList<HashMap<String, String>>();
        myOrderList = new ArrayList<HashMap<String, String>>();

        initUI(root);
        String apiKey = getString(R.string.api_key);
        Places.initialize(getActivity(), apiKey);
        Utils.filterValue = "All Orders";

        Bundle b = getActivity().getIntent().getExtras();
        currentlatitude = b.getDouble("lat");
        currentlongitude = b.getDouble("longi");
        cuisines = b.getString("cuisines");
        refreshlistner  = new Refresh() {
            @Override
            public void yourDesiredMethod() {

               // Toast.makeText(getActivity(),"Toast Called",Toast.LENGTH_SHORT).show();
                orderAdpter.notifyDataSetChanged();
            }
        };

        /*  Funcation to fetch orders based on Categories*/

        checkInterNetconnection();

      /*  Utils.userChanegedlat = Utils.userlat;
        Utils.userChanegedlang = Utils.userlang;
*/

        tv_refersh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInterNetconnection();
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
                String[] mobileArray = {"All Orders", "My Order"};
                ArrayList filterList = new ArrayList();
                filterList.add("All Orders");
                filterList.add("My Orders");

                String pageData = "Dashboard";
                Filteradpter filteradpter = new Filteradpter(getActivity(), filterList, Utils.filterValue, pageData);
                listView.setAdapter(filteradpter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String value = String.valueOf(parent.getItemAtPosition(position));
                        popupWindow.dismiss();

                        if (value.equalsIgnoreCase("0")) {
                            Utils.filterValue = "All Orders";
                            Utils.categoriesSelected = "Allorders";
                            getAllOrders("None");

                        } else {
                            Utils.filterValue = "My Orders";
                            getAllMyOrders();
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


        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {


                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                Log.i("WebUrl ", String.valueOf(place.getWebsiteUri()));
                LatLng latLng = place.getLatLng();
                String mStringLatitude = String.valueOf(latLng.latitude);
                String mStringLongitude = String.valueOf(latLng.longitude);


                Log.i("Latitude ", mStringLatitude);
                Log.i("Longitutude ", mStringLongitude);

                Utils.userlat = latLng.latitude;
                Utils.userlang = latLng.longitude;

                Utils.userChanegedlat = latLng.latitude;
                Utils.userChanegedlang = latLng.longitude;

                getAddresss(latLng.latitude, latLng.longitude, "Change");


            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        edt_search_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        edt_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // searchUsers(s.toString().toLowerCase());
                //  adapter.getFilter().filter(newText);

                try {



                   /*
                    if (ActivityCompat.checkSelfPermission(
                            getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                        return;
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                        Toast.makeText(getActivity(), "Location not set", Toast.LENGTH_SHORT).show();
                    }

*/


                } catch (Exception e) {


                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                orderAdpter.getFilter().filter(s);
            }
        });


        OrderList = new ArrayList<>();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Funcation tp fetch user Current location
        //  fetchLocation();


        newchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent chatList = new Intent(getActivity(), UserListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("pageData", "Allchats");
                chatList.putExtras(bundle);
                getActivity().startActivity(chatList);

            }
        });


        /* Button All Orders and MyOrders btn clickEvent */

        btn_allorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_myoreders.setBackgroundResource(R.drawable.btnsmall_grey);
                btn_myoreders.setTextColor(Color.parseColor("#4c4c4c"));
                btn_allorders.setTextColor(Color.parseColor("#FFFFFF"));
                btn_allorders.setBackgroundResource(R.drawable.btnsmall_bg);
                Utils.filterValue = "All Orders";
                Utils.categoriesSelected = "Allorders";
                getAllOrders("None");


            }
        });

        btn_myoreders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_myoreders.setTextColor(Color.parseColor("#FFFFFF"));
                btn_myoreders.setBackgroundResource(R.drawable.btnsmall_bg);
                btn_allorders.setBackgroundResource(R.drawable.btnsmall_grey);
                btn_allorders.setTextColor(Color.parseColor("#4c4c4c"));
                Utils.filterValue = "My Orders";
                getAllMyOrders();

            }
        });


        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        //  Toast.makeText(getActivity(), "onStart Called ", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }


    private void initUI(View view) {


        tv_areaName = view.findViewById(R.id.tv_areaName);
        tv_address = view.findViewById(R.id.tv_address);
        recyclerview_order = view.findViewById(R.id.recyclerview_order);
        edt_search_text = view.findViewById(R.id.edt_search_text);
        progressBar = view.findViewById(R.id.progressBar);
        img_filter = view.findViewById(R.id.img_filter);
        newchat = view.findViewById(R.id.newchat);
        btn_allorders = view.findViewById(R.id.btn_allorders);
        btn_myoreders = view.findViewById(R.id.btn_myoreders);
        layout_main = view.findViewById(R.id.layout_main);
        layout_nointernet = view.findViewById(R.id.layout_nointernet);
        tv_refersh = view.findViewById(R.id.tv_refersh);
        layout_noorders = view.findViewById(R.id.layout_noorders);
        progressBar.setMax(100);
        progressBar.setProgress(20);


    }

    public void someMethod() {

    }

    private void getAddresss(double lat, double lang, String info) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
          /*  String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            String data = addresses.get(0).getSubLocality();
            String data2 = addresses.get(0).getSubAdminArea();*/

            String addressline = addresses.get(0).getAddressLine(0);
            String locality = addresses.get(0).getSubLocality();


            if (locality == null) {

                tv_areaName.setText(addressline);
            } else {

                tv_areaName.setText(locality + " " + addressline);
            }

            tv_address.setText(addresses.get(0).getAddressLine(0));


            getAllOrders(info);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void getAllOrders(final String info) {

        orderedMapList.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.keepSynced(true);

        if (Utils.categoriesSelected.equalsIgnoreCase("Allorders")) {

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                //  reference.orderByChild("orderStatus").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

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
                        orderredMap.put("resturntName", String.valueOf(snapshot1.child("resturntName").getValue()));
                        orderredMap.put("resturntAddress", String.valueOf(snapshot1.child("resturntAddress").getValue()));
                        orderredMap.put("resturntPostalCode", String.valueOf(snapshot1.child("resturntPostalCode").getValue()));
                        orderredMap.put("groupData", String.valueOf(snapshot1.child("groupData").getValue()));
                        orderredMap.put("orderImg", String.valueOf(snapshot1.child("orderImg").getValue()));
                        orderredMap.put("orderQuantity", String.valueOf(snapshot1.child("orderQuantity").getValue()));
                        String orderStatus = String.valueOf(snapshot1.child("orderStatus").getValue());

                        if (orderStatus.equalsIgnoreCase("Pending")) {
                            orderedMapList.add(orderredMap);
                        }


                    }


                    if (orderedMapList.size() == 0) {
                        layout_noorders.setVisibility(View.VISIBLE);
                    } else {
                        layout_noorders.setVisibility(View.GONE);
                    }

                    Collections.reverse(orderedMapList);
                    recyclerview_order.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerview_order.setHasFixedSize(true);
                    orderAdpter = new OrderAdpter(getActivity(), orderedMapList, info, refreshlistner);
                    recyclerview_order.setAdapter(orderAdpter);
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            //  Utils.categoriesSelected = "Baking";

            reference.orderByChild("cuisines").equalTo(Utils.categoriesSelected).addListenerForSingleValueEvent(new ValueEventListener() {


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
                        layout_noorders.setVisibility(View.VISIBLE);
                    } else {
                        layout_noorders.setVisibility(View.GONE);
                    }
                    Collections.reverse(orderedMapList);
                    recyclerview_order.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerview_order.setHasFixedSize(true);
                    orderAdpter = new OrderAdpter(getActivity(), orderedMapList, info,refreshlistner);
                    recyclerview_order.setAdapter(orderAdpter);


                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


    private void getAllMyOrders() {


        myOrderList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        //    reference.keepSynced(true);
        reference.orderByChild("customerUserId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            //  reference.orderByChild("customerUserId").equalTo(Utils.userId).orderByChild("partnerUserId").equalTo("").addListenerForSingleValueEvent(new ValueEventListener() {


            // reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // progressBar.setVisibility(View.GONE);
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

                    if (status.equalsIgnoreCase("Completed")) {

                    } else {
                        myOrderList.add(orderredMap);


                    }


                }

                if (myOrderList.size() == 0) {
                    layout_noorders.setVisibility(View.VISIBLE);
                } else {
                    layout_noorders.setVisibility(View.GONE);
                }

                Collections.reverse(myOrderList);
                recyclerview_order.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerview_order.setHasFixedSize(true);
                orderAdpter = new OrderAdpter(getActivity(), myOrderList, "None",refreshlistner);
                recyclerview_order.setAdapter(orderAdpter);
                orderAdpter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        //  Toast.makeText(getActivity(), "onResume Called ", Toast.LENGTH_SHORT).show();
    }

    private void greybtnStatus() {
        btn_myoreders.setBackgroundResource(R.drawable.btnsmall_grey);
        btn_myoreders.setTextColor(Color.parseColor("#4c4c4c"));
    }


    private void checkInterNetconnection() {
        if (CheckNetwork.isInternetAvailable(getActivity())) {

            getAddresss(Utils.userlat, Utils.userlang, "None");
            layout_nointernet.setVisibility(View.GONE);
        } else {

            layout_main.setVisibility(View.GONE);
            layout_nointernet.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void yourDesiredMethod() {

        Toast.makeText(getActivity(), "Method called ", Toast.LENGTH_SHORT).show();

    }
}