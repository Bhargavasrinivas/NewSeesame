package com.seesame.ui.PostFreeAdd;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.InternetCheck.CheckNetwork;
import com.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.screens.CuisinescategorieActivity;
import com.screens.UserListActivity;
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
    private Spinner spinner_expiretime, spinner_deliverypartner, spinner_cuisines, spinner_groups;
    List<String> delveryPartnerList, timeList, cuisinesList, groupList;
    private AutocompleteSupportFragment autocompleteFragment, autocomplete_pickupfragment;
    private TextView tv_areaName,tv_refersh, tv_address, tv_pickupareaName, tv_pickupaddress, tv_chsfav, tv_cuisineslbl, tv_deliverypartnerlbl, tv_pricelbl;
    private View layout_pickuplocation, layout_resturntlocation, layout_restaurant, layout_cuisines, layout_quantity,
            layout_allchats, layout_deliverypartner, layout_prodctdscrp,layout_nointernet,layout_main;
    private EditText edtTxt_foodPrice, edt_prdctcomnts;
    private Button btn_placeorder;
    private ArrayAdapter deliverypartneradpter;
    private ImageView img_chatIcon;
    private EditText favTextInput, pickupTextInput, edt_comments, edtTxt_quntity;
    private String completeaddressrestunrt, completeaddresslocation;
    private int check = 0;
    private AutoCompleteTextView autoCompleteTextView;
    private String expiryTime = "a", deliverypartner, cuisines, resturntName, locationName, userAddress, placeId, resturntPostalCode, locationpostalCode, currentDate, currentTime,
            userName, userId, pickupAreaName, pickupAddress, orderDateTime, spinnerGroups;
    private ProgressBar progressBar;
    BottomNavigationView navigation;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(PostFreeAddViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initUI(root);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpledf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderDateTime = simpledf.format(cal.getTime());


        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //  textView.setText(s);
            }
        });



        if (!CheckNetwork.isInternetAvailable(getActivity())) {

            layout_main.setVisibility(View.GONE);
            layout_nointernet.setVisibility(View.VISIBLE);
        }




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

        SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("hh:mm a");
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getActivity());


        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        View favView = autocompleteFragment.getView();
        favTextInput = favView.findViewById(R.id.places_autocomplete_search_input);
        favTextInput.setTextSize(15.5f);
        //  Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"font/Poppins.xml");
        final Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.poppins);
        favTextInput.setTypeface(typeface);
        favTextInput.setTextColor(Color.parseColor("#4c4c4c"));
        favTextInput.setHintTextColor(Color.parseColor("#4c4c4c"));
        ImageView favSearch = favView.findViewById(R.id.places_autocomplete_search_button);
        favSearch.setImageResource(R.drawable.search_icon);

        autocompleteFragment.setHint("e.g. resturntant or hotel");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                btn_placeorder.setEnabled(false);
                btn_placeorder.setBackgroundResource(R.drawable.btngreyout_gradient);


                resturntName = place.getName();
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


                img_chatIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //  Toast.makeText(getActivity(), "Chaticon ", Toast.LENGTH_SHORT).show();

                     /*   Intent chatList = new Intent(getActivity(), UserListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("pageData","Allchats");
                        chatList.putExtras(bundle);
                        getActivity().startActivity(chatList);
*/
                    }
                });


                getAddresssbyLatlong(latitude, longitutude, "resturntant");
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        layout_allchats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatList = new Intent(getActivity(), UserListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("pageData", "Allchats");
                chatList.putExtras(bundle);
                getActivity().startActivity(chatList);
            }
        });


        autocomplete_pickupfragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_pickupfragment);

        autocomplete_pickupfragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG));

        View pickUpView = autocomplete_pickupfragment.getView();

        pickupTextInput = pickUpView.findViewById(R.id.places_autocomplete_search_input);
        pickupTextInput.setTextSize(15.5f);
        pickupTextInput.setTextColor(Color.parseColor("#4c4c4c"));
        pickupTextInput.setHintTextColor(Color.parseColor("#4c4c4c"));
        pickupTextInput.setTypeface(typeface);
        ImageView pickupSearch = pickUpView.findViewById(R.id.places_autocomplete_search_button);
        pickupSearch.setImageResource(R.drawable.search_icon);

        autocomplete_pickupfragment.setHint("e.g.Block or Building");

        autocomplete_pickupfragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                btn_placeorder.setEnabled(false);
                btn_placeorder.setBackgroundResource(R.drawable.btngreyout_gradient);
                //   layout_pickuplocation.setVisibility(View.VISIBLE);
                LatLng latLng = place.getLatLng();
                String mStringLatitude = String.valueOf(latLng.latitude);
                String mStringLongitude = String.valueOf(latLng.longitude);
                locationName = place.getName();

             /*   double pickuplatitude, pickuplatitude;
                pickuplatitude = Double.parseDouble(mStringLatitude);
                pickuplatitude = Double.parseDouble(mStringLongitude);*/

                Utils.userlat = Double.parseDouble(mStringLatitude);
                Utils.userlang = Double.parseDouble(mStringLongitude);

                getAddresssbyLatlong(Utils.userlat, Utils.userlang, "location");
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


        btn_placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckNetwork.isInternetAvailable(getActivity())) {
                    if (cuisines.contains("Choose cuisines")) {
                        Toast.makeText(getActivity(), "Please choose cuisines", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ((spinnerGroups.contains("Group Food")) && tv_areaName.getText().toString().isEmpty()) {

                        Toast.makeText(getActivity(), "Please choose your  Favourite Resturnt", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ((spinnerGroups.contains("Group Food")) && favTextInput.getText().toString().isEmpty()) {

                        Toast.makeText(getActivity(), "Please choose your Favouirute  Resturnt", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (((pickupAreaName == null || pickupAreaName.isEmpty()) && pickupAddress == null)) {

                        Toast.makeText(getActivity(), "Please choose your Meetup Location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (edtTxt_foodPrice.getText().toString().isEmpty()) {

                        Toast.makeText(getActivity(), "Please enter Price ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (expiryTime.contains("Choose expriry time")) {
                        Toast.makeText(getActivity(), "Please choose Expiry Time ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (deliverypartner.contains("Choose delivery partner")) {
                        Toast.makeText(getActivity(), "Please choose Delivery Partner", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ((pickupTextInput.getText().toString().isEmpty())) {

                        Toast.makeText(getActivity(), "Please choose your meetup location ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Conditions for Home made Products

                    if ((spinnerGroups.contains("Sell Home Made Products") && edtTxt_quntity.getText().toString().isEmpty())) {

                        Toast.makeText(getActivity(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    readUsertInfo();
                }else {
                    layout_main.setVisibility(View.GONE);
                    layout_nointernet.setVisibility(View.VISIBLE);
                }



            }
        });


        expiryTime = "a";
        spinner_expiretime.setOnItemSelectedListener(this);
        spinner_cuisines.setOnItemSelectedListener(this);
        spinner_deliverypartner.setOnItemSelectedListener(this);
        spinner_groups.setOnItemSelectedListener(this);

        timeList = new ArrayList<>();
        //  timeList.add("Choose expriry time");
        timeList.add("15 mins");
        timeList.add("30 mins");
        timeList.add("45 mins");
        timeList.add("60 mins");
        timeList.add("1 Day");
        timeList.add("3 Day");
        timeList.add("5 Day");
        timeList.add("7 Day");

        delveryPartnerList = new ArrayList<>();
        //  delveryPartnerList.add("Choose delivery partner");
        delveryPartnerList.add("A");
        delveryPartnerList.add("B");
        delveryPartnerList.add("C");
        delveryPartnerList.add("D");


        cuisinesList = new ArrayList<>();
        //cuisinesList.add("Choose cuisines");
        cuisinesList.add("Beverages");
        cuisinesList.add("Chinnes");
        cuisinesList.add("Indian");
        cuisinesList.add("FastFood");
        cuisinesList.add("Desserts");
        cuisinesList.add("Pizza and Subway");
        cuisinesList.add("Western");

        groupList = new ArrayList<>();
        groupList.add("Group Food");
        groupList.add("Group Groceries");
        groupList.add("Sell Home Made Products");

        ArrayAdapter groupAdpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, groupList);
        groupAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_groups.setAdapter(groupAdpter);

        ArrayAdapter timedpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, timeList);
        timedpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_expiretime.setAdapter(timedpter);

        ArrayAdapter cuisinesdpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, cuisinesList);
        cuisinesdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_cuisines.setAdapter(cuisinesdpter);


        deliverypartneradpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, delveryPartnerList);
        deliverypartneradpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_deliverypartner.setAdapter(deliverypartneradpter);

        changUIData();
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
        layout_pickuplocation = view.findViewById(R.id.layout_pickuplocation);
        layout_resturntlocation = view.findViewById(R.id.layout_resturntlocation);
        tv_pickupareaName = view.findViewById(R.id.tv_pickupareaName);
        tv_pickupaddress = view.findViewById(R.id.tv_pickupaddress);
        layout_restaurant = view.findViewById(R.id.layout_restaurant);
        tv_chsfav = view.findViewById(R.id.tv_chsfav);
        spinner_groups = view.findViewById(R.id.spinner_groups);
        tv_cuisineslbl = view.findViewById(R.id.tv_cuisineslbl);
        layout_cuisines = view.findViewById(R.id.layout_cuisines);
        img_chatIcon = view.findViewById(R.id.img_chatIcon);
        layout_allchats = view.findViewById(R.id.layout_allchats);
        layout_deliverypartner = view.findViewById(R.id.layout_deliverypartner);
        tv_deliverypartnerlbl = view.findViewById(R.id.tv_deliverypartnerlbl);
        tv_pricelbl = view.findViewById(R.id.tv_pricelbl);
        layout_prodctdscrp = view.findViewById(R.id.layout_prodctdscrp);
        edt_prdctcomnts = view.findViewById(R.id.edt_prdctcomnts);
        layout_quantity = view.findViewById(R.id.layout_quantity);
        edtTxt_quntity = view.findViewById(R.id.edtTxt_quntity);
        layout_main = view.findViewById(R.id.layout_main);
        layout_nointernet = view.findViewById(R.id.layout_nointernet);
        tv_refersh = view.findViewById(R.id.tv_refersh);
    }

    private void getAddresssbyLatlong(double lat, double lang, String data) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
           /* String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            String locality = addresses.get(0).getSubLocality();
            String data2 = addresses.get(0).getSubAdminArea();*/

            progressBar.setMax(100);
            progressBar.setProgress(20);
            progressBar.setVisibility(View.VISIBLE);
            if (data.contains("location")) {


                tv_pickupareaName.setText(addresses.get(0).getSubLocality());
                tv_pickupaddress.setText(addresses.get(0).getAddressLine(0));
                pickupAreaName = addresses.get(0).getSubLocality();
                pickupAddress = addresses.get(0).getAddressLine(0);
                locationpostalCode = addresses.get(0).getPostalCode();

                completeaddresslocation = locationName + " " + pickupAddress;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pickupTextInput.setText(completeaddresslocation);

                        progressBar.setVisibility(View.GONE);
                        btn_placeorder.setEnabled(true);
                        btn_placeorder.setBackgroundResource(R.drawable.btn_gradient);

                    }
                }, 3000);


            } else {


                //   tv_areaName.setText(addresses.get(0).getSubLocality());
                tv_areaName.setText(resturntName);
                tv_address.setText(addresses.get(0).getAddressLine(0));
                //resturntName = addresses.get(0).getSubLocality();
                userAddress = addresses.get(0).getAddressLine(0);
                resturntPostalCode = addresses.get(0).getPostalCode();
                // postalCode = addresses.get(0).getPostalCode();

                completeaddressrestunrt = resturntName + " " + userAddress;


                //   autocompleteFragment.setText(completeadds);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        favTextInput.setText(completeaddressrestunrt);
                        progressBar.setVisibility(View.GONE);
                        btn_placeorder.setEnabled(true);
                        btn_placeorder.setBackgroundResource(R.drawable.btn_gradient);
                    }
                }, 3000);


            }

           /* addresses.get(0).getLongitude();
            addresses.get(0).getLatitude();*/


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void placeOrderApiCall() {

        progressBar.setMax(100);
        progressBar.setProgress(20);

        //  dbrefernce = FirebaseDatabase.getInstance().getReference("userSignup").child(userId);
        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("Orders");
        String uniqueId = refernce.push().getKey();

        String price = edtTxt_foodPrice.getText().toString();

        HashMap<String, String> orderMap = new HashMap<>();
        orderMap.put("customerName", userName);
        orderMap.put("customerUserId", userId);
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
        orderMap.put("customerChatId", "");
        orderMap.put("deliveryPostalCode", locationpostalCode);
        orderMap.put("deliverylatitude", String.valueOf(Utils.userlat));
        orderMap.put("deliverylongititude", String.valueOf(Utils.userlang));
        orderMap.put("deliveryPlaceId", "");
        orderMap.put("deliveryAreaname", pickupAreaName);
        orderMap.put("deliveryAddress", pickupAddress);
        orderMap.put("partnerName", "");
        orderMap.put("partnerUserId", "");
        orderMap.put("partnerChatId", "");
        orderMap.put("partnerPostalCode", "");
        orderMap.put("partnerlatitude", "");
        orderMap.put("partnerlongititude", "");
        orderMap.put("partnerPlaceId", "");
        orderMap.put("partnerAreaName", "");
        orderMap.put("partnerAddress", "");
        orderMap.put("orderStatusDate", orderDateTime);
        orderMap.put("orderStatusTime", currentTime);
        orderMap.put("orderCancel", "");
        orderMap.put("orderAccepted", "");
        orderMap.put("orderCompleted", "");
        orderMap.put("ownerRating", "false");
        orderMap.put("partnerRating", "false");
        orderMap.put("productDesciption", edt_prdctcomnts.getText().toString().trim());
        orderMap.put("groupData", spinnerGroups);
        orderMap.put("orderQuantity", edtTxt_quntity.getText().toString().trim());

        if (cuisines.contains("Groceries")) {
            orderMap.put("resturntName", deliverypartner);
            orderMap.put("resturntAddress", userAddress);
            orderMap.put("resturntPostalCode", "");
        } else if ((cuisines.contains("Baking") || cuisines.contains("Home Made Foods") || (cuisines.contains("Curry and Powder")))) {

            //  String restruntName = spinnerGroups + " " + cuisines;
            String restruntName = "Homemade" + "-" + cuisines;
            orderMap.put("resturntName", restruntName);
            orderMap.put("resturntAddress", userAddress);
            orderMap.put("resturntPostalCode", "");
            cuisines = "Sell your homemade delicacy";
            orderMap.put("cuisines", cuisines);

        } else {
            orderMap.put("resturntName", resturntName);
            orderMap.put("resturntAddress", userAddress);
            orderMap.put("resturntPostalCode", "");
        }

        if (cuisines.equalsIgnoreCase("Indian")) {

            orderMap.put("orderImg", Utils.Indian);
        } else if (cuisines.equalsIgnoreCase("Beverages")) {

            orderMap.put("orderImg", Utils.Beverages);

        } else if (cuisines.equalsIgnoreCase("FastFood")) {

            orderMap.put("orderImg", Utils.FastFood);

        } else if (cuisines.equalsIgnoreCase("Desserts")) {

            orderMap.put("orderImg", Utils.Desserts);

        } else if (cuisines.equalsIgnoreCase("Groceries")) {

            orderMap.put("orderImg", Utils.Groceries);

        } else if (cuisines.equalsIgnoreCase("Pizza and Subway")) {

            orderMap.put("orderImg", Utils.Pizz);

        } else if (cuisines.equalsIgnoreCase("Western")) {
            orderMap.put("orderImg", Utils.Western);
        } else if ((cuisines.contains("Baking") || cuisines.contains("Home Made Foods") || (cuisines.contains("Curry and Powder")))) {
            orderMap.put("orderImg", Utils.homeMade);
        } else {
            orderMap.put("orderImg", Utils.Chinnes);
        }


        refernce.child(uniqueId).setValue(orderMap);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Add's sucessfully posted ", Toast.LENGTH_SHORT).show();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MyorderFragment myorderFragment = new MyorderFragment();
        fragmentTransaction.replace(R.id.nav_host_fragment, myorderFragment);
        fragmentTransaction.commit();
        BottomNavigationView bottomNavigationView = (getActivity()).findViewById(R.id.nav_view);
        bottomNavigationView.getMenu().findItem(R.id.navigation_notifications).setChecked(true);


        // insertCategoriData(cuisines);


        //  NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.mobile_navigation);
        //  navigationView.getMenu().getItem(2).setChecked(true);
        // navigationView.setCheckedItem(R.id.navigation_notifications);

    }

    private void readUsertInfo() {

        String id = Utils.userId;

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
        //  reference.keepSynced(true);

        //ref.orderByChild('user/id1').equalTo(true)
        reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    userName = String.valueOf(snapshot1.child("userName").getValue());
                    userId = String.valueOf(snapshot1.child("id").getValue());
                    Utils.userId = String.valueOf(snapshot1.child("id").getValue());


                    placeOrderApiCall();

                }

                //    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Error" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }



    /*  All Spinner Click listner */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spinner_expiretime) {
            expiryTime = String.valueOf(parent.getItemAtPosition(position));
            return;
        }

        if (parent.getId() == R.id.spinner_deliverypartner) {
            deliverypartner = String.valueOf(parent.getItemAtPosition(position));

            Log.i("SpinnerValue ", deliverypartner);

            return;
        }

        /* Condition to stop preloading of spinner value */

        if (++check > 1) {
            if (parent.getId() == R.id.spinner_groups) {
                spinnerGroups = String.valueOf(parent.getItemAtPosition(position));

                Log.i("spinnerGroups ", spinnerGroups);
                spinner_deliverypartner.setSelection(0);
                spinner_expiretime.setSelection(0);
                layout_restaurant.setVisibility(View.GONE);
                tv_chsfav.setVisibility(View.GONE);

                if (spinnerGroups.equalsIgnoreCase("Group Groceries")) {
                    layout_prodctdscrp.setVisibility(View.GONE);
                    layout_cuisines.setVisibility(View.GONE);
                    tv_cuisineslbl.setVisibility(View.GONE);
                    layout_deliverypartner.setVisibility(View.VISIBLE);
                    tv_deliverypartnerlbl.setVisibility(View.VISIBLE);
                    spinnerdelivertpartnerGroceries();
                    cuisines = "Groceries";
                    layout_quantity.setVisibility(View.GONE);
                    edtTxt_quntity.setVisibility(View.GONE);
                  /*  tv_pricelbl.setText("Set Pice");
                    edtTxt_foodPrice.setHint("Price")*/
                    ;

                } else if (spinnerGroups.equalsIgnoreCase("Sell Home Made Products")) {

              /*  layout_cuisines.setVisibility(View.GONE);
                tv_cuisineslbl.setVisibility(View.GONE);
                cuisines = "Sell Homemade Products";*/
                    layout_prodctdscrp.setVisibility(View.VISIBLE);
                    layout_cuisines.setVisibility(View.VISIBLE);
                    tv_cuisineslbl.setVisibility(View.VISIBLE);
                    tv_cuisineslbl.setText("Choose Products");
                    layout_deliverypartner.setVisibility(View.GONE);
                    tv_deliverypartnerlbl.setVisibility(View.GONE);
                    cuisinesList.clear();
                    cuisinesList.add("Baking");
                    cuisinesList.add("Home Made Foods");
                    cuisinesList.add("Curry and Powder");
                    ArrayAdapter cuisinesdpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, cuisinesList);
                    cuisinesdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_cuisines.setAdapter(cuisinesdpter);
                    layout_restaurant.setVisibility(View.GONE);
                    tv_chsfav.setVisibility(View.GONE);
                    layout_quantity.setVisibility(View.VISIBLE);
                    edtTxt_quntity.setVisibility(View.VISIBLE);


                } else {
                    layout_prodctdscrp.setVisibility(View.GONE);

                    cuisinesList.clear();
                    tv_cuisineslbl.setText("Choose cuisines");
                    //  cuisinesList.add("Choose cuisines");
                    cuisinesList.add("Beverages");
                    cuisinesList.add("Chinnes");
                    cuisinesList.add("Indian");
                    cuisinesList.add("FastFood");
                    cuisinesList.add("Desserts");
                    cuisinesList.add("Pizza and Subway");
                    cuisinesList.add("Western");
                    ArrayAdapter cuisinesdpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, cuisinesList);
                    cuisinesdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_cuisines.setAdapter(cuisinesdpter);
                    layout_cuisines.setVisibility(View.VISIBLE);
                    tv_cuisineslbl.setVisibility(View.VISIBLE);
                    spinnerdelivertPartnerFood();
                    layout_deliverypartner.setVisibility(View.VISIBLE);
                    tv_deliverypartnerlbl.setVisibility(View.VISIBLE);
                    layout_quantity.setVisibility(View.GONE);
                    edtTxt_quntity.setVisibility(View.GONE);
                }

                return;
            }


            if (parent.getId() == R.id.spinner_cuisines) {
                cuisines = String.valueOf(parent.getItemAtPosition(position));


                spinner_deliverypartner.setSelection(0);
                spinner_expiretime.setSelection(0);

                if (cuisines.equalsIgnoreCase("Groceries")) {
                    spinnerdelivertpartnerGroceries();
                } else if ((cuisines.contains("Baking") || cuisines.contains("Home Made Foods") || cuisines.contains("Curry and Powder"))) {

                } else {

                    spinnerdelivertPartnerFood();
                }


                return;
            }

        }


    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }


    private void insertCategoriData(final String categorieName) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.keepSynced(true);
        reference.orderByChild("categorieName").equalTo(categorieName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    //   edt_username.setText((CharSequence) snapshot1.child("userName").getValue());

                    long count = (long) snapshot1.child("count").getValue();

                    String categorieId = (String) snapshot1.child("id").getValue();

                    Log.i("Count ", String.valueOf(count));


                    //  if (count == 0) {

                 /*   DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
                    reference.child(Utils.userId).child("userName").setValue(edt_username.getText().toString().trim());*/

                    count = count + 1;
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
                    //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
                    reference.child(categorieId).child("count").setValue(count);
                    Log.i("CountIncrese ", String.valueOf(count));
                    //   }
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    MyorderFragment myorderFragment = new MyorderFragment();
                    fragmentTransaction.replace(R.id.nav_host_fragment, myorderFragment);
                    fragmentTransaction.commit();
                    BottomNavigationView bottomNavigationView = (getActivity()).findViewById(R.id.nav_view);
                    bottomNavigationView.getMenu().findItem(R.id.navigation_notifications).setChecked(true);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void spinnerdelivertpartnerGroceries() {
        delveryPartnerList.clear();
        delveryPartnerList.add("Red mart");
        delveryPartnerList.add("Fair prirce");
        deliverypartneradpter.notifyDataSetChanged();
        layout_restaurant.setVisibility(View.GONE);
        tv_chsfav.setVisibility(View.GONE);
        deliverypartner = "Red mart";
    }

    private void spinnerdelivertPartnerFood() {
        delveryPartnerList.clear();
        //    delveryPartnerList.add("Choose delivery partner");
        delveryPartnerList.add("A");
        delveryPartnerList.add("B");
        delveryPartnerList.add("C");
        delveryPartnerList.add("D");
        // spinner_deliverypartner.setSelection(1);
        deliverypartneradpter.notifyDataSetChanged();
        layout_restaurant.setVisibility(View.VISIBLE);
        tv_chsfav.setVisibility(View.VISIBLE);
        deliverypartner = "A";
    }


    private void changUIData() {

        if ((Utils.postcategorieData.contains("Groceries") || Utils.postcategorieData.contains("Group Groceries"))) {

            layout_cuisines.setVisibility(View.GONE);
            tv_cuisineslbl.setVisibility(View.GONE);
            // spinner_cuisines.setVisibility(View.GONE);
            spinnerdelivertpartnerGroceries();
            spinnerGroups = "Group Groceries";
            cuisines = "Groceries";
            spinner_groups.setSelection(1);

        } else if (Utils.postcategorieData.equalsIgnoreCase("FastFood")) {
            spinner_groups.setSelection(0);
            spinnerGroups = "Group Food";
            spinner_cuisines.setSelection(3);
        } else if (Utils.postcategorieData.equalsIgnoreCase("Beverages")) {
            spinner_cuisines.setSelection(0);
            cuisines = "Beverages";
            spinner_groups.setSelection(0);
            spinnerGroups = "Group Food";
        } else if (Utils.postcategorieData.equalsIgnoreCase("Chinese")) {
            spinner_cuisines.setSelection(1);
            cuisines = "Chinnes";
            spinner_groups.setSelection(0);
            spinnerGroups = "Group Food";
        } else if (Utils.postcategorieData.equalsIgnoreCase("Indian")) {
            spinner_cuisines.setSelection(2);
            cuisines = "Indian";
            spinner_groups.setSelection(0);
            spinnerGroups = "Group Food";
        } else if (Utils.postcategorieData.equalsIgnoreCase("Desserts")) {
            spinner_cuisines.setSelection(4);
            cuisines = "Desserts";
            spinner_groups.setSelection(0);
            spinnerGroups = "Group Food";
        } else if (Utils.postcategorieData.equalsIgnoreCase("Pizza and Subway")) {
            spinner_cuisines.setSelection(5);
            cuisines = "Pizz and Subway";
            spinner_groups.setSelection(0);
            spinnerGroups = "Group Food";
        } else if (Utils.postcategorieData.equalsIgnoreCase("Western")) {
            spinner_cuisines.setSelection(6);
            cuisines = "Western";
            spinner_groups.setSelection(0);
            spinnerGroups = "Group Food";
        } else if (Utils.postcategorieData.equalsIgnoreCase("Sell your homemade delicacy")) {

            spinnerGroups = "Sell Home Made Products";
            cuisines = "Baking";
            deliverypartner = "A";
            spinner_groups.setSelection(2);
            layout_restaurant.setVisibility(View.GONE);
            tv_chsfav.setVisibility(View.GONE);
            layout_deliverypartner.setVisibility(View.GONE);
            tv_deliverypartnerlbl.setVisibility(View.GONE);
            layout_quantity.setVisibility(View.VISIBLE);
            edtTxt_quntity.setVisibility(View.VISIBLE);
            cuisinesList.clear();
            cuisinesList.add("Baking");
            cuisinesList.add("Home Made Foods");
            cuisinesList.add("Curry and Powder");
            ArrayAdapter cuisinesdpter = new ArrayAdapter(getActivity(), R.layout.spinner_item, cuisinesList);
            cuisinesdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_cuisines.setAdapter(cuisinesdpter);
        }


    }

}