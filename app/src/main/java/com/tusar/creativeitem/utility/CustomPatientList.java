package com.tusar.creativeitem.utility;

/**
 * Created by Farruck Ahmed Tusar on 20-Jun-17.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tusar.creativeitem.R;

import java.util.ArrayList;

public class CustomPatientList extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> name_array = new ArrayList<String>();
    private ArrayList<String> phone_array = new ArrayList<String>();

    public CustomPatientList(Activity context, ArrayList<String> name_array, ArrayList<String> phone_array) {
        super(context, R.layout.list_single, name_array);
        this.context = context;
        this.name_array = name_array;
        this.phone_array = phone_array;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        TextView txtName = (TextView) rowView.findViewById(R.id.txt1);
        TextView txtPhone = (TextView) rowView.findViewById(R.id.txt2);

        txtName.setText(name_array.get(position));
        txtPhone.setText(phone_array.get(position));

        return rowView;
    }
}
