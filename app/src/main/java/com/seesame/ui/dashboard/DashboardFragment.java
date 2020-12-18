package com.seesame.ui.dashboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.seesame.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class DashboardFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private DashboardViewModel dashboardViewModel;
    private Spinner spinner_expiretime, spinner_deliverypartner, spinner_cuisines;
    List<String> delveryPartnerList, timeList, cuisinesList;
    private AutocompleteSupportFragment autocompleteFragment;
    private TextView tv_areaName, tv_address;
    private EditText edtTxt_foodPrice;
    private Button btn_placeorder;
    private String expiryTime = "a", deliverypartner, cuisines;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initUI(root);

        int initialposition = spinner_expiretime.getSelectedItemPosition();
        spinner_expiretime.setSelection(initialposition, false);

        //  selectionCurrent = spinner_expiretime.getSelectedItemPosition();
        // final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //  textView.setText(s);
            }
        });

        String apiKey = getString(R.string.api_key);
        Places.initialize(getActivity(), apiKey);


      /*  Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/epimodem.ttf");
        ((EditText)placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTypeface(face);*/

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getActivity());


        //    ((EditText)autocompleteFragment.getView().findViewById(R.id.autocomplete_fragment)).setTextSize(10.0f);
/*
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/epimodem.ttf");
        ((EditText)placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTypeface(face);*/


        /*EditText etPlace = (EditText)autocompleteFragment.getView().findViewById(R.id.autocomplete_fragment);
        etPlace.setHint("Type your address");
        etPlace.setHintTextColor(R.color.colorRed);*/

        // autocompleteFragment.getView().setVisibility(View.INVISIBLE);
        //  autocompleteFragment.getView().setB

        // autocompleteFragment.setText("Choose your favourite");

        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setHint("Choose your favourite");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                LatLng latLng = place.getLatLng();
                String mStringLatitude = String.valueOf(latLng.latitude);
                String mStringLongitude = String.valueOf(latLng.longitude);

                Log.i("Latitude ", mStringLatitude);

                Log.i("Longitutude ", mStringLongitude);

                double latitude, longitutude;
                latitude = Double.parseDouble(mStringLatitude);
                longitutude = Double.parseDouble(mStringLongitude);

                //   getAddresssbyLatlong(latitude, longitutude);

                //var latitude = place.geometry.location.lat();
                getAddresssbyLatlong(latitude, longitutude);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });



       /* View fView = autocompleteFragment.getView();
        EditText etTextInput = fView.findViewById(R.id.autocomplete_fragment);
        etTextInput.setTextColor(Color.WHITE);
        etTextInput.setHintTextColor(Color.WHITE);
        etTextInput.setTextSize(12.5f);*/


        btn_placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edtTxt_foodPrice.getText().toString())) {

                    Toast.makeText(getActivity(), "Please enter price ", Toast.LENGTH_SHORT).show();
                }

            }
        });


        spinner_expiretime.setOnItemClickListener((AdapterView.OnItemClickListener) getActivity());


      /*  spinner_expiretime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                Toast.makeText(getActivity(), parent.toString(), Toast.LENGTH_SHORT).show();




*//*
                if (selectionCurrent != position){
                    // Your code here
                    selectionCurrent = position;

                    Toast.makeText(getActivity(), selectionCurrent, Toast.LENGTH_SHORT).show();
                }*//*

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        timeList = new ArrayList<>();
        timeList.add("Choose expriry time in min");
        timeList.add("30");
        timeList.add("31");
        timeList.add("32");
        timeList.add("33");
        timeList.add("34");
        timeList.add("35");
        timeList.add("36");
        timeList.add("37");
        timeList.add("38");
        timeList.add("39");
        timeList.add("40");
        timeList.add("41");
        timeList.add("42");
        timeList.add("43");
        timeList.add("44");
        timeList.add("45");
        timeList.add("46");
        timeList.add("47");
        timeList.add("48");
        timeList.add("49");
        timeList.add("50");
        timeList.add("51");
        timeList.add("52");
        timeList.add("53");
        timeList.add("54");
        timeList.add("55");
        timeList.add("56");
        timeList.add("57");
        timeList.add("58");
        timeList.add("59");
        timeList.add("60");


        delveryPartnerList = new ArrayList<>();
        delveryPartnerList.add("Choose delivery partner");
        delveryPartnerList.add("A");
        delveryPartnerList.add("B");
        delveryPartnerList.add("C");
        delveryPartnerList.add("D");


        cuisinesList = new ArrayList<>();
        cuisinesList.add("Choose cuisines");
        cuisinesList.add("Western");
        cuisinesList.add("Indian");
        cuisinesList.add("Chinnes");
        cuisinesList.add("FastFood");
        cuisinesList.add("Beverages");
        cuisinesList.add("Desserts");
        cuisinesList.add("Pizza and Subway");
        cuisinesList.add("Red Mart");
        cuisinesList.add("Fair Price");


        ArrayAdapter timedpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, timeList);
        timedpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_expiretime.setAdapter(timedpter);

        ArrayAdapter cuisinesdpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, cuisinesList);
        cuisinesdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_cuisines.setAdapter(cuisinesdpter);


        ArrayAdapter deliverypartneradpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, delveryPartnerList);
        deliverypartneradpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_deliverypartner.setAdapter(deliverypartneradpter);


        return root;
    }


    private void initUI(View view) {

        tv_areaName = view.findViewById(R.id.tv_areaName);
        tv_address = view.findViewById(R.id.tv_address);
        spinner_expiretime = view.findViewById(R.id.spinner_expiretime);
        spinner_deliverypartner = view.findViewById(R.id.spinner_deliverypartner);
        spinner_cuisines = view.findViewById(R.id.spinner_cuisines);
        edtTxt_foodPrice = view.findViewById(R.id.edtTxt_foodPrice);
        btn_placeorder = view.findViewById(R.id.btn_placeorder);
    }

    private void getAddresssbyLatlong(double lat, double lang) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}