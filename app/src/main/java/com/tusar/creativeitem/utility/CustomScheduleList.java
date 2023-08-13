package com.tusar.creativeitem.utility;

/**
 * Created by Farruck Ahmed Tusar on 20-Jun-17.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tusar.creativeitem.R;

import java.util.ArrayList;

public class CustomScheduleList extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> day_array = new ArrayList<String>();
    private ArrayList<String> time_array = new ArrayList<String>();

    public CustomScheduleList(Activity context, ArrayList<String> day_array, ArrayList<String> time_array) {
        super(context, R.layout.list_single1, day_array);
        this.context = context;
        this.day_array = day_array;
        this.time_array = time_array;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single1, null, true);

        TextView day = (TextView) rowView.findViewById(R.id.txt1);
        TextView time = (TextView) rowView.findViewById(R.id.txt2);

        day.setText(day_array.get(position));
        time.setText(time_array.get(position));

        return rowView;
    }
}
