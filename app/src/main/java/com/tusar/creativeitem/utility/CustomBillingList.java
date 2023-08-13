package com.tusar.creativeitem.utility;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tusar.creativeitem.R;

import java.util.ArrayList;

public class CustomBillingList extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> date_array = new ArrayList<String>();
    private ArrayList<String> charge_array = new ArrayList<String>();

    public CustomBillingList(Activity context, ArrayList<String> date_array, ArrayList<String> charge_array) {
        super(context, R.layout.list_single4, date_array);
        this.context = context;
        this.date_array = date_array;
        this.charge_array = charge_array;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single4, null, true);

        TextView txtDate = (TextView) rowView.findViewById(R.id.txt1);
        TextView txtCharge = (TextView) rowView.findViewById(R.id.txt2);

        txtDate.setText(date_array.get(position));
        txtCharge.setText(charge_array.get(position));

        return rowView;
    }
}
