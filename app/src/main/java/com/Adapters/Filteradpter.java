package com.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.Utils;
import com.seesame.R;

import java.util.ArrayList;

public class Filteradpter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    private Context context;
    ArrayList list = new ArrayList();
    String filterValue;

    public Filteradpter(FragmentActivity activity, ArrayList filterList, String filterValue) {

        this.context = activity;
        this.list = filterList;
        this.filterValue = filterValue;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.filter_layout, null);


        TextView orderlbl = (TextView) vi.findViewById(R.id.tv_orderlbl);
        ImageView rightarrow = (ImageView) vi.findViewById(R.id.img_right);

        orderlbl.setText((CharSequence) list.get(position));

        rightarrow.setVisibility(View.INVISIBLE);

        String value = String.valueOf(list.get(position));


        if (value.equals(filterValue)) {

            rightarrow.setVisibility(View.VISIBLE);

        } /*else if (orderlbl.getText().equals(filterValue)) {
            rightarrow.setVisibility(View.VISIBLE);

        }*/


        return vi;
    }
}
