package com.tusar.creativeitem;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tusar.creativeitem.helper.DatabaseHandler;
import com.tusar.creativeitem.utility.FragmentMainBilling;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BillingActivity extends BaseActivity{

    private com.github.clans.fab.FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab3;
    private com.github.clans.fab.FloatingActionButton fab4;

    Fragment fragment;
    private DatabaseHandler db;
    private TextView tvdatePicker;
    private String chamber_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_billing, contentFrameLayout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(4).setChecked(true);

        db = new DatabaseHandler(this);

        //Floating nav button
        menuRed = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.menu_red);
        fab1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab4);

        menuRed.setClosedOnTouchOutside(true);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BillingActivity.this, NewAppointment.class);
                startActivity(i);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BillingActivity.this, PrescriptionActivity.class);
                startActivity(i);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BillingActivity.this, NewPatient.class);
                startActivity(i);
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BillingActivity.this, NewBilling.class);
                startActivity(i);
            }
        });

        ArrayList<HashMap<String,String>> chamber_list;
        chamber_list = db.getAllChamber();

        for(int i=0;i<chamber_list.size();i++){
            HashMap<String,String> content = new HashMap<String, String>();
            content = chamber_list.get(i);
            String status = content.get("status");
            if(status.equals("Selected")){
                chamber_id = content.get("chamber_id");
            }
        }

        tvdatePicker = (TextView) findViewById(R.id.datePicker);
        tvdatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);
                fragment = new FragmentMainBilling();
                DatePickerDialog mDatePicker=new DatePickerDialog(BillingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        selectedmonth = selectedmonth+1;
                        tvdatePicker.setText(selectedday+ "/" + selectedmonth+ "/" + selectedyear);

                        Date date = null;
                        java.text.DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            date = (Date)formatter.parse(tvdatePicker.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
                        long today_timestamp = timeStampDate.getTime()/1000;

                        Bundle bundle = new Bundle();
                        bundle.putString("chamber_id", chamber_id);
                        bundle.putString("timestamp", String.valueOf(today_timestamp));
                        fragment.setArguments(bundle);
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment_place1,fragment);
                        ft.commit();

                    }
                },mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });

        fragment = new FragmentMainBilling();
        //initially sent today's date for billing list
        //today timestamp
        String today = (DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString());
        Date date = null;
        java.text.DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = (Date)formatter.parse(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
        long today_timestamp = timeStampDate.getTime()/1000;

        Bundle bundle = new Bundle();
        bundle.putString("chamber_id", chamber_id);
        bundle.putString("timestamp", String.valueOf(today_timestamp));
        fragment.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_place1,fragment);
        ft.commit();

    } //onCreate ends here
}
