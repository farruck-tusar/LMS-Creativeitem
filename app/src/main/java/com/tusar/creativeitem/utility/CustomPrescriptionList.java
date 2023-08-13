package com.tusar.creativeitem.utility;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tusar.creativeitem.R;

import java.util.ArrayList;

public class CustomPrescriptionList extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> date_array = new ArrayList<String>();

    public CustomPrescriptionList(Activity context, ArrayList<String> date_array) {
        super(context, R.layout.list_single3, date_array);
        this.context = context;
        this.date_array = date_array;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single3, null, true);

        TextView txtDate = (TextView) rowView.findViewById(R.id.txt1);

        txtDate.setText(date_array.get(position));

        return rowView;
    }
}
