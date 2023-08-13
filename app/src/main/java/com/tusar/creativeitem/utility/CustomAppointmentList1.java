package com.tusar.creativeitem.utility;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tusar.creativeitem.R;

import java.util.ArrayList;

public class CustomAppointmentList1 extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> date_array = new ArrayList<String>();
    private ArrayList<String> visit_array = new ArrayList<String>();

    public CustomAppointmentList1(Activity context, ArrayList<String> date_array, ArrayList<String> visit_array) {
        super(context, R.layout.list_single6, date_array);
        this.context = context;
        this.date_array = date_array;
        this.visit_array = visit_array;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single6, null, true);

        TextView txtDate = (TextView) rowView.findViewById(R.id.txt1);
        ImageView img = (ImageView) rowView.findViewById(R.id.img);

        txtDate.setText(date_array.get(position));
        if(visit_array.get(position).equals("1")){
            img.setImageResource(R.drawable.ic_check_circle_active);
        }
        else{
            img.setImageResource(R.drawable.ic_check_circle);
        }
        return rowView;
    }
}
