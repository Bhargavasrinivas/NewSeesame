package com.seesame.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.seesame.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //  final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //   textView.setText(s);
            }
        });




        initUI(root);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Funcation tp fetch user Current location
        fetchLocation();



        return root;
    }


    private void initUI(View view) {


        tv_areaName = view.findViewById(R.id.tv_areaName);
        tv_address = view.findViewById(R.id.tv_address);


    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
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


                    getAddresss();


/*
                    latitude = currentLocation.getLatitude();
                    longititude = currentLocation.getLongitude();

                    Log.i("latitude", String.valueOf(latitude));
                    Log.i("longititude", String.valueOf(longititude));*/



                /*    if (pageData.equalsIgnoreCase("true")) {

                        latitude = Util.userlatitude;
                        longititude = Util.userlangitude;

                    } else {
                        Util.userlatitude = currentLocation.getLatitude();
                        Util.userlangitude = currentLocation.getLongitude();
                        latitude = currentLocation.getLatitude();
                        longititude = currentLocation.getLongitude();
                    }

*/
                    //  getAddresss();

                   /* SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);*/
                }
            }
        });
    }

    private void getAddresss() {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longititude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            String data = addresses.get(0).getSubLocality();
            String data2 = addresses.get(0).getSubAdminArea();

            tv_areaName.setText(addresses.get(0).getSubLocality());
            tv_address.setText(addresses.get(0).getAddressLine(0));

           /* addresses.get(0).getLongitude();
            addresses.get(0).getLatitude();*/


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}