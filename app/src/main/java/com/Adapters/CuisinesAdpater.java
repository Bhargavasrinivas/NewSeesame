package com.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seesame.R;

public class CuisinesAdpater extends RecyclerView.Adapter<CuisinesAdpater.ViewHolder> {


    @NonNull
    @Override
    public CuisinesAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CuisinesAdpater.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgVw_cuisines;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVw_cuisines =  itemView.findViewById(R.id.imgVw_cuisines);

        }
    }
}
