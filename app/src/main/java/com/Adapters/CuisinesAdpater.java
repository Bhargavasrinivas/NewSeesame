package com.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.models.CuisinesData;
import com.screens.CuisinescategorieActivity;
import com.seesame.MainActivity;
import com.seesame.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CuisinesAdpater extends RecyclerView.Adapter<CuisinesAdpater.ViewHolder> {

    private Context mContext;
    private ArrayList<CuisinesData> cuisinesDataArrayList;
    private double currentlatitude, currentlongitude;

    public CuisinesAdpater(CuisinescategorieActivity cuisinescategorieActivity, ArrayList<CuisinesData> cuisinesDataArrayList, double currentlatitude, double currentlongitude) {

        this.mContext = cuisinescategorieActivity;
        this.cuisinesDataArrayList = cuisinesDataArrayList;
        this.currentlatitude = currentlatitude;
        this.currentlongitude = currentlongitude;

    }

    @NonNull
    @Override
    public CuisinesAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.cuisines_template, parent, false);
        return new CuisinesAdpater.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CuisinesAdpater.ViewHolder holder, int position) {

        final CuisinesData cuisinesData = cuisinesDataArrayList.get(position);
        holder.tv_cuisinesname.setText(cuisinesData.getCategorieName());

      //  Glide.with(mContext).load(cuisinesData.getImgUrl()).into(holder.imgVw_cuisines);

        Picasso.with(mContext).load(cuisinesData.getImgUrl()).fit().into(holder.imgVw_cuisines);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent dashbaord = new Intent(mContext, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", currentlatitude);
                bundle.putDouble("longi", currentlongitude);
                bundle.putString("cuisines", cuisinesData.getCategorieName());
                dashbaord.putExtras(bundle);
                mContext.startActivity(dashbaord);


            }
        });


    }

    @Override
    public int getItemCount() {
        return cuisinesDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgVw_cuisines;
        private TextView tv_cuisinesname;
        private View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVw_cuisines = itemView.findViewById(R.id.imgVw_cuisines);
            tv_cuisinesname = itemView.findViewById(R.id.tv_cuisinesname);
            layout = itemView.findViewById(R.id.layout);

        }
    }
}
