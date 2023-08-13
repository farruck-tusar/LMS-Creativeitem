package com.tusar.creativeitem;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.tusar.creativeitem.helper.DatabaseHandler;
import com.tusar.creativeitem.utility.FragmentAppointment;
import com.tusar.creativeitem.utility.FragmentBasic;
import com.tusar.creativeitem.utility.FragmentBilling;
import com.tusar.creativeitem.utility.FragmentMedical;
import com.tusar.creativeitem.utility.FragmentPrescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatientDetailsActivity extends AppCompatActivity{
    Fragment fragment;
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        }
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String patient_id = intent.getStringExtra("patient_id");
        this.setTitle(name);

        db = new DatabaseHandler(this);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Basic Information");
        categories.add("Medical Information");
        categories.add("Appointment Histoy");
        categories.add("Prescription History");
        categories.add("Billing History");



        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                if(item.equals("Basic Information")){
                    fragment = new FragmentBasic();
                    // pass data to fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("patient_id", patient_id);
                    fragment.setArguments(bundle);
                }
                else if (item.equals("Medical Information")){
                    fragment = new FragmentMedical();
                    // pass data to fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("patient_id", patient_id);
                    fragment.setArguments(bundle);
                }
                else if (item.equals("Appointment Histoy")){
                    fragment = new FragmentAppointment();
                    // pass data to fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("patient_id", patient_id);
                    fragment.setArguments(bundle);
                }
                else if (item.equals("Prescription History")){
                    fragment = new FragmentPrescription();
                    // pass data to fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("patient_id", patient_id);
                    fragment.setArguments(bundle);
                }
                else if (item.equals("Billing History")){
                    fragment = new FragmentBilling();
                    // pass data to fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("patient_id", patient_id);
                    fragment.setArguments(bundle);
                }
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_place,fragment);
                ft.commit();

                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }//onCreate ends here

    // for back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
     //for edit button in action bar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_edit, menu);
//        return true;
//    }

}
