package com.seesame.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.screens.LoginActivity;
import com.screens.WebViewActivity;
import com.seesame.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    private ListView listView;
    private Button btn_logout;
    private EditText edt_mobileno, edt_emailId, edt_username;
    private CircleImageView user_profilepic;
    private ConstraintLayout constraintLayout;
    private View layout_progressbar;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    String[] mobileArray = {"Help", "Privacy Policy", "Terms of Service"};

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(20);


        initUi(root);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.row_item, R.id.label, mobileArray);
        listView.setAdapter(adapter);
        constraintLayout = root.findViewById(R.id.constraintLayout);

        /*   Funcation call to fetch user details */

        readUsertInfo();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                        getString(R.string.logoutmsg), Snackbar.LENGTH_LONG)
                        .setAction("NO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setAction("YES", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                FirebaseAuth.getInstance().signOut();


                            }
                        });

                snackBar.setActionTextColor(Color.RED);


                snackBar.show();

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


    public void initUi(View view) {

        btn_logout = view.findViewById(R.id.btn_logout);
        listView = view.findViewById(R.id.listView);
        edt_mobileno = view.findViewById(R.id.edt_mobileno);
        edt_emailId = view.findViewById(R.id.edt_emailId);
        user_profilepic = view.findViewById(R.id.user_profilepic);
        edt_username = view.findViewById(R.id.edt_username);
        //   layout_progressbar = view.findViewById(R.id.layout_progressbar);

    }


    private void readUsertInfo() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSignup");
        reference.keepSynced(true);

        reference.orderByChild("id").equalTo(Utils.userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    edt_username.setText((CharSequence) snapshot1.child("userName").getValue());
                    edt_emailId.setText((CharSequence) snapshot1.child("mailId").getValue());

                    if ((!snapshot1.child("imgUrl").getValue().toString().isEmpty() || snapshot1.child("imgUrl").getValue().toString() != null)) {
                        Glide.with(getActivity()).load(snapshot1.child("imgUrl").getValue().toString()).into(user_profilepic);
                    }
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Error" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}




      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           // getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            getActivity().getWindow().setStatusBarColor(R.color.top_grdcolor);
        }*/

//getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
