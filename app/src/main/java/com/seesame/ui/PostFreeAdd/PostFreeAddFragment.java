package com.seesame.ui.PostFreeAdd;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seesame.R;
import com.seesame.ui.Myorders.MyorderFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class PostFreeAddFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private PostFreeAddViewModel dashboardViewModel;
    private Spinner spinner_expiretime, spinner_deliverypartner, spinner_cuisines;
    List<String> delveryPartnerList, timeList, cuisinesList;
    private AutocompleteSupportFragment autocompleteFragment;
    private TextView tv_areaName, tv_address;
    private EditText edtTxt_foodPrice;
    private Button btn_placeorder;
    private String expiryTime = "a", deliverypartner, cuisines, areaName, userAddress, placeId, postalCode, currentDate, currentTime,
            userName, userId;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(PostFreeAddViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initUI(root);

        readUsertInfo();

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //  textView.setText(s);
            }
        });

        getAddresssbyLatlong(Utils.userlat, Utils.userlang);

        String apiKey = getString(R.string.api_key);
        Places.initialize(getActivity(), apiKey);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(c.getTime());

        SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm:ss");
        currentTime = timeFormate.format(new Date());

        try {

            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
            Date currentT = format.parse(currentTime);
            Date OldT = format.parse("10:17:38");
            long differncetime = currentT.getTime() - OldT.getTime();
            int mins = (int) (differncetime / (1000 * 60)) % 60;
            Log.i("ExpireTime ", String.valueOf(mins));


        } catch (Exception e) {

        }














       /* SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDatenew = dateFormat.format(cal.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.i("DateFormate ", formattedDatenew);*/


        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("hh:mm a");
        Log.i("timedataformata ", String.valueOf(simpleDateFormattime));

        //   Log.i("currentDate", String.valueOf(currentTime));

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


                Log.i("WebUrl ", String.valueOf(place.getWebsiteUri()));


                LatLng latLng = place.getLatLng();
                String mStringLatitude = String.valueOf(latLng.latitude);
                String mStringLongitude = String.valueOf(latLng.longitude);

                Log.i("Latitude ", mStringLatitude);

                Log.i("Longitutude ", mStringLongitude);


                double latitude, longitutude;
                latitude = Double.parseDouble(mStringLatitude);
                longitutude = Double.parseDouble(mStringLongitude);

                Utils.userlat = latitude;
                Utils.userlang = longitutude;
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

             /*   if (TextUtils.isEmpty(edtTxt_foodPrice.getText().toString())) {

                  //  Toast.makeText(getActivity(), "Please enter price ", Toast.LENGTH_SHORT).show();
                    placeOrderApiCall();

                }*/

                placeOrderApiCall();


            }
        });


        //   spinner_expiretime.setEnabled(false);
        expiryTime = "a";

        //  spinner_expiretime.setOnItemClickListener((AdapterView.OnItemClickListener) getActivity());
        // spinner_expiretime.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) getActivity());
        spinner_expiretime.setOnItemSelectedListener(this);
        spinner_cuisines.setOnItemSelectedListener(this);
        spinner_deliverypartner.setOnItemSelectedListener(this);






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

/*

        spinner_expiretime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/


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
        progressBar = view.findViewById(R.id.progressBar);
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

            areaName = addresses.get(0).getSubLocality();
            userAddress = addresses.get(0).getAddressLine(0);
            postalCode = addresses.get(0).getPostalCode();




           /* addresses.get(0).getLongitude();
            addresses.get(0).getLatitude();*/


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void placeOrderApiCall() {

        progressBar.setMax(100);
        progressBar.setProgress(20);


        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference();
        String uniqueId = refernce.push().getKey();

        HashMap<String, String> orderMap = new HashMap<>();
        orderMap.put("orderId", uniqueId);
        orderMap.put("orderDate", currentDate);
        orderMap.put("orderTime", currentTime);
        orderMap.put("orderPrice", edtTxt_foodPrice.getText().toString());
        orderMap.put("deliveryPartner", deliverypartner);
        orderMap.put("expireTime", expiryTime);
        orderMap.put("cuisines", cuisines);
        orderMap.put("orderCategoryId", "1");
        orderMap.put("serviceSelecetd", deliverypartner);
        orderMap.put("orderStatus", "Pending");
        orderMap.put("customerName", userName);
        orderMap.put("customerUserId", userId);
        orderMap.put("customerChatId", "");
        orderMap.put("deliveryPostalCode", postalCode);
        orderMap.put("deliverylatitude", String.valueOf(Utils.userlat));
        orderMap.put("deliverylongititude", String.valueOf(Utils.userlang));
        orderMap.put("deliveryPlaceId", "");
        orderMap.put("deliveryAreaname", areaName);
        orderMap.put("deliveryAddress", userAddress);
        orderMap.put("partnerName", "");
        orderMap.put("partnerUserId", "");
        orderMap.put("partnerChatId", "");
        orderMap.put("partnerPostalCode", "");
        orderMap.put("partnerlatitude", "");
        orderMap.put("partnerlongititude", "");
        orderMap.put("partnerPlaceId", "");
        orderMap.put("partnerAreaName", "");
        orderMap.put("partnerAddress", "");
        orderMap.put("orderStatusDate", currentDate);
        orderMap.put("orderStatusTime", currentTime);
        orderMap.put("orderCancel", "");
        orderMap.put("orderAccepted", "");
        orderMap.put("orderCompleted", "");
        refernce.child("Orders").push().setValue(orderMap);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Add's sucessfully posted ", Toast.LENGTH_SHORT).show();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MyorderFragment NAME = new MyorderFragment();
        fragmentTransaction.replace(R.id.nav_host_fragment, NAME);
        fragmentTransaction.commit();


    }

    private void readUsertInfo() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
        //  reference.keepSynced(true);

        //ref.orderByChild('user/id1').equalTo(true)
        reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {


                /*    edt_username.setText((CharSequence) snapshot1.child("userName").getValue());
                    edt_emailId.setText((CharSequence) snapshot1.child("mailId").getValue());*/

                    //  Log.i("UserId", String.valueOf(snapshot1.child("id").getValue()));
                    userName = String.valueOf(snapshot1.child("userName").getValue());
                    userId = String.valueOf(snapshot1.child("id").getValue());

                    Utils.userId = String.valueOf(snapshot1.child("id").getValue());


                   /* if ((!snapshot1.child("imgUrl").getValue().toString().isEmpty() || snapshot1.child("imgUrl").getValue().toString() != null)) {
                        Glide.with(getActivity()).load(snapshot1.child("imgUrl").getValue().toString()).into(user_profilepic);
                    }*/


                }

                //    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Error" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spinner_expiretime) {
            expiryTime = String.valueOf(parent.getItemAtPosition(position));
            return;
        }

        if (parent.getId() == R.id.spinner_deliverypartner) {
            deliverypartner = String.valueOf(parent.getItemAtPosition(position));
            return;
        }
        if (parent.getId() == R.id.spinner_cuisines) {
            cuisines = String.valueOf(parent.getItemAtPosition(position));
            return;
        }


    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}