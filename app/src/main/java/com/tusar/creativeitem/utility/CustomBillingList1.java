package com.tusar.creativeitem.utility;

/**
 * Created by Farruck Ahmed Tusar on 11-Jul-17.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tusar.creativeitem.R;

import java.util.ArrayList;

public class CustomBillingList1 extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> name_array = new ArrayList<String>();
    private ArrayList<String> charge_array = new ArrayList<String>();

    public CustomBillingList1(Activity context, ArrayList<String> name_array, ArrayList<String> charge_array) {
        super(context, R.layout.list_single5, name_array);
        this.context = context;
        this.name_array = name_array;
        this.charge_array = charge_array;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single5, null, true);

        TextView txtName = (TextView) rowView.findViewById(R.id.txt1);
        TextView txtCharge = (TextView) rowView.findViewById(R.id.txt3);

        txtName.setText(name_array.get(position));
        txtCharge.setText(charge_array.get(position));

        return rowView;
    }
}
