package com.Adapters;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.models.RateModel;
import com.screens.UserrateListActivity;
import com.seesame.R;

import java.util.ArrayList;

public class UserRatelistAdpater extends RecyclerView.Adapter<UserRatelistAdpater.ViewHolder> {


    private ArrayList<RateModel> userrateList = new ArrayList<RateModel>();
    private Context mcontext;
    private RateModel rateModel;

    public UserRatelistAdpater(UserrateListActivity userrateListActivity, ArrayList<RateModel> ratingList) {

        this.mcontext = userrateListActivity;
        this.userrateList = ratingList;
    }

    @NonNull
    @Override
    public UserRatelistAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.userrate_template, parent, false);
        return new UserRatelistAdpater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRatelistAdpater.ViewHolder holder, int position) {

        try {
            RateModel rateModel = userrateList.get(position);

            holder.tv_commnets.setText(rateModel.getComments().toString());
          //  holder.ratingBar.setNumStars((int) rateModel.getRateCount());
            float ratings = rateModel.getRateCount();

            if (ratings == 0) {
               // holder.ratingBar.setVisibility(View.GONE);
                holder.ratingBar.setNumStars(1);
            } else {
                holder.ratingBar.setVisibility(View.VISIBLE);
                holder.ratingBar.setNumStars((int) rateModel.getRateCount());
            }

        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        int count =  userrateList.size();
        return userrateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RatingBar ratingBar;
        private TextView tv_commnets;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tv_commnets = itemView.findViewById(R.id.tv_commnets);

        }
    }
}
