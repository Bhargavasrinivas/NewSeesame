package com.seesame.ui.profile;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Adapters.UserRatelistAdpater;
import com.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.models.RateModel;
import com.screens.LoginActivity;
import com.screens.UserrateListActivity;
import com.screens.WebViewActivity;
import com.seesame.R;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.screens.LoginActivity.Email;


public class ProfileFragment extends Fragment {


    private ListView listView;
    private Button btn_logout;
    private EditText edt_mobileno, edt_emailId, edt_username;
    private CircleImageView user_profilepic;
    private ConstraintLayout constraintLayout;
    private View layout_progressbar;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private ImageView img_mobile, img_uname;
    private RatingBar rating_bar;
    String[] mobileArray = {"Ratings", "Contact Us", "FAQ's", "Privacy Policy", "Terms of Service"};
    private String userId, dbUserName;
    boolean onloadflag = false, datachangeflag = false;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    private RateModel rateModel;
    private ArrayList<RateModel> ratingList = new ArrayList<>();
    private long ttlRateCount = 0;
    private float ttlcountRate;

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        //   progressBar = root.findViewById(R.id.progressBar);
        //progressBar.setMax(100);
        //  progressBar.setProgress(20);
        ratingList = new ArrayList<RateModel>();
        initUi(root);
        //  rating_bar.setNumStars((int) 2.5);

        final SharedPreferences sharedpreferences;
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.row_item, R.id.label, mobileArray);
        listView.setAdapter(adapter);
        constraintLayout = root.findViewById(R.id.constraintLayout);

        /*   Funcation call to fetch user details */

        readUsertInfo();


        img_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
                //  reference.child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
                reference.child(Utils.userId).child("mobileNo").setValue(edt_mobileno.getText().toString().trim());
                Toast.makeText(getActivity(), "Mobile Number updated succesfully", Toast.LENGTH_SHORT).show();
                edt_mobileno.clearFocus();
                img_mobile.setVisibility(View.GONE);
            }
        });

        img_uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
                reference.child(Utils.userId).child("userName").setValue(edt_username.getText().toString().trim());
                Toast.makeText(getActivity(), "Name updated succesfully", Toast.LENGTH_SHORT).show();
                edt_username.clearFocus();
                img_uname.setVisibility(View.GONE);

            }
        });


        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //    Toast.makeText(getActivity(), "Want change name", Toast.LENGTH_SHORT).show();

                datachangeflag = true;


                //   btn_logout.setText("UPDATE");

            }

            @Override
            public void afterTextChanged(Editable s) {

                //  Toast.makeText(getActivity(), "Want change name", Toast.LENGTH_SHORT).show();

                Log.i("UserNameEdt", s.toString());


                if (Utils.userName.equalsIgnoreCase(s.toString())) {
                    img_uname.setVisibility(View.GONE);
                } else {
                    img_uname.setVisibility(View.VISIBLE);

                }


            }
        });


        edt_mobileno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                img_mobile.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (btn_logout.getText().equals("UPDATE")) {

                    String val = Utils.userId;

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
                    //   reference.child(Utils.userId/Utils.userId).setValue("8892575075");reference
                    reference.child(Utils.userId).child("mobileNo").setValue(edt_username.getText().toString().trim());

                } else {


                    logoutPopup();


                 /*   Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getString(R.string.logoutmsg), Snackbar.LENGTH_LONG)
                            .setAction("NO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .setAction("YES", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //     FirebaseAuth.getInstance().signOut();


                                    if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                                        *//*  ((ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();*//* // note: it has a return value!

                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.remove(Email);
                                        editor.commit();

                                        deleteCache(getActivity());
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_HOME);
                                        startActivity(intent);
                                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));


                                    } else {

                                    }

                                }
                            });

                    snackBar.setActionTextColor(Color.RED);
                    snackBar.show();
*/

                }


            }
        });


        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String value = (String) parent.getItemAtPosition(position);
                        Intent policy = new Intent(getActivity(), WebViewActivity.class);
                        policy.putExtra("PageInfo", value);
                        startActivity(policy);

                        // Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                    }
                });


        return root;

    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void initUi(View view) {

        btn_logout = view.findViewById(R.id.btn_logout);
        listView = view.findViewById(R.id.listView);
        edt_mobileno = view.findViewById(R.id.edt_mobileno);
        edt_emailId = view.findViewById(R.id.edt_emailId);
        user_profilepic = view.findViewById(R.id.user_profilepic);
        edt_username = view.findViewById(R.id.edt_username);
        img_mobile = view.findViewById(R.id.img_mobile);
        img_uname = view.findViewById(R.id.img_uname);
        rating_bar = view.findViewById(R.id.rating_bar);
        //   layout_progressbar = view.findViewById(R.id.layout_progressbar);
        img_mobile.setVisibility(View.GONE);
        img_uname.setVisibility(View.GONE);


    }


    private void readUsertInfo() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
        reference.keepSynced(true);

        //  reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {

        reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    edt_username.setText((CharSequence) snapshot1.child("userName").getValue());
                    edt_emailId.setText((CharSequence) snapshot1.child("mailId").getValue());
                    userId = String.valueOf(snapshot1.child("id").getValue());
                    edt_mobileno.setText((CharSequence) snapshot1.child("mobileNo").getValue());
                    dbUserName = String.valueOf(snapshot1.child("userName").getValue());

                    img_mobile.setVisibility(View.GONE);
                    img_uname.setVisibility(View.GONE);
                    //  Log.i("UserId ", String.valueOf(snapshot1.child("id").getValue()));

                    if ((!snapshot1.child("imgUrl").getValue().toString().isEmpty() || snapshot1.child("imgUrl").getValue().toString() != null)) {
                        Glide.with(getActivity()).load(snapshot1.child("imgUrl").getValue().toString()).into(user_profilepic);
                    }

                    rateCount();
                }

                //    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Error" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void rateCount() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ratings");
        reference.keepSynced(true);

        reference.orderByChild("userId").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    rateModel = new RateModel();
                    long ratings = (long) snapshot1.child("rateCount").getValue();
                    rateModel.setRateCount((int) ratings);
                    ratingList.add(rateModel);

                    long count = (long) snapshot1.child("rateCount").getValue();

                    ttlRateCount = ttlRateCount + count;


                }
                ttlcountRate = ttlRateCount;

                if (ratingList.size() == 0) {
                    rating_bar.setVisibility(View.GONE);
                } else {


                    float ratettlCount = (ttlcountRate / ratingList.size());
                    // rating_bar.setNumStars(ratettlCount);
                    rating_bar.setRating(ratettlCount);

                    Log.i("ttlRateCount ", String.valueOf(ratettlCount));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });


    }


    private void logoutPopup() {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.custom_popup, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.setView(deleteDialogView);

        deleteDialogView.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic

                if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {


                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.remove(Email);
                    editor.commit();

                    deleteCache(getActivity());
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }

            }
        });
        deleteDialogView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();


    }

}









      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           // getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            getActivity().getWindow().setStatusBarColor(R.color.top_grdcolor);
        }*/

//getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
