package com.seesame.ui.home;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Adapters.OrderAdpter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.seesame.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    Location currentLocation;
    private double latitude, longititude;
    LatLng latLngcurrnet = new LatLng(12.3456789, 98.7654321);
    LatLng latLngdestination = new LatLng(98.7654321, 12.3456789);
    boolean distnaceflag = false;
    private TextView tv_areaName, tv_address;
    private RecyclerView recyclerview_order;
    HashMap<String, String> orderredMap;
    private List<String> OrderList;
    private EditText edt_search_text;
    private ArrayList<HashMap<String, String>> orderedMapList;
    private OrderAdpter orderAdpter;
    private ProgressBar progressBar;
    private AutocompleteSupportFragment autocompleteFragment;
    LocationManager locationManager;
    private Double currentlatitude, currentlongitude;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        initUI(root);
        String apiKey = getString(R.string.api_key);
        Places.initialize(getActivity(), apiKey);

        Bundle b = getActivity().getIntent().getExtras();
        currentlatitude = b.getDouble("lat");
        currentlongitude = b.getDouble("longi");


        Utils.userlat = currentlatitude;
        Utils.userlang = currentlongitude;
        getAddresss(currentlatitude, currentlongitude);

        //  Toast.makeText(getActivity(), "currentlatitude " + currentlatitude, Toast.LENGTH_SHORT).show();
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

                getAddresss(latLng.latitude, latLng.longitude);


            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        edt_search_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*
                for (int i = 0; i > orderedMapList.size(); i++) {

                    String val = orderedMapList.get(i).get("customerUserId").toString();

                }*/


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

                    orderAdpter.getFilter().filter(s);

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

            }
        });


        OrderList = new ArrayList<>();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Funcation tp fetch user Current location
        //  fetchLocation();


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
        progressBar.setMax(100);
        progressBar.setProgress(20);


    }


    private void searchUsers(String s) {

      /*  orderedMapList = new ArrayList<HashMap<String, String>>();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();*/

        //  DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        //   reference.keepSynced(true);

        //   final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Orders")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderedMapList.clear();
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

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


                    if (!snapshot1.child("customerUserId").getValue().equals(Utils.userId)) {

                        orderedMapList.add(orderredMap);
                    }

                  /*  recyclerview_order.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerview_order.setHasFixedSize(true);*/
                    orderAdpter = new OrderAdpter(getActivity(), orderedMapList);
                    recyclerview_order.setAdapter(orderAdpter);
                    HomeFragment.this.notifyAll();
                    notify();
                    notifyAll();
                    orderAdpter.notifyDataSetChanged();


                }

                /*userAdapter = new UserAdapter(getContext(), mUsers, false);
                recyclerView.setAdapter(userAdapter);*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "ErrorMsg" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

   /* private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                showLocation.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
            } else {
                Toast.makeText(getActivity(), "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            // return;
        } else {

      /*      Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
            *//*    latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                showLocation.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);*//*
            } else {
              //  Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
*/


           /* Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        currentLocation.getSpeed();
                        latLngdestination = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        //  Toast.makeText(getActivity(), currentLocation.getLatitude() + " " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        Log.i("Latitude ", String.valueOf(currentLocation.getLatitude()));
                        Log.i("Longitude ", String.valueOf(currentLocation.getLongitude()));
                        latitude = currentLocation.getLatitude();
                        longititude = currentLocation.getLongitude();

                        Utils.userlat = currentLocation.getLatitude();
                        Utils.userlang = currentLocation.getLongitude();


                        getAddresss(Utils.userlat, Utils.userlang);

                    }
                }
            });*/

            //     getAddresss(Utils.userlat, Utils.userlang);

        }

    }

    private void getAddresss(double lat, double lang) {

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

            if ((addresses.get(0).getSubLocality() == null || (addresses.get(0).getSubLocality().isEmpty()))) {
                tv_areaName.setText(addresses.get(0).getAdminArea());
            } else {
                tv_areaName.setText(addresses.get(0).getSubLocality());
            }
            tv_address.setText(addresses.get(0).getAddressLine(0));

           /* Log.i("AdminArea ", addresses.get(0).getAdminArea());

            Log.i("SubadminArea ", addresses.get(0).getAdminArea());*/


            getAllOrders();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void getAllOrders() {

        orderedMapList = new ArrayList<HashMap<String, String>>();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            //   reference.orderByChild("cuisines").equalTo("Groceries").addListenerForSingleValueEvent(new ValueEventListener() {
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


                    String orderStatus = String.valueOf(snapshot1.child("orderStatus").getValue());

                    if ((!snapshot1.child("customerUserId").getValue().equals(Utils.userId))) {

                        if (orderStatus.equalsIgnoreCase("Pending")) {
                            orderedMapList.add(orderredMap);
                        }

                    }

                }

                recyclerview_order.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerview_order.setHasFixedSize(true);
                orderAdpter = new OrderAdpter(getActivity(), orderedMapList);
                recyclerview_order.setAdapter(orderAdpter);
                progressBar.setVisibility(View.GONE);

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

    @Override
    public void onPause() {
        super.onPause();

     //   Toast.makeText(getActivity(), "onPause Called ", Toast.LENGTH_SHORT).show();

    }
}