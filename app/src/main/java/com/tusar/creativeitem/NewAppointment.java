package com.tusar.creativeitem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tusar.creativeitem.helper.DatabaseHandler;
import com.tusar.creativeitem.utility.AppConfigURL;
import com.tusar.creativeitem.utility.HintSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAppointment extends AppCompatActivity {
    EditText etPatName, etMobile, etDate, etDate1;
    private DatabaseHandler db;
    private String chamber_id;
    private String key1=null;
    Spinner spinner1, spinner2,spinner3,spinner4;
    ArrayList<String> arr = new ArrayList<String>();
    ArrayAdapter<String> adapter2,adapter4;
    TabHost host;
    private String pat_id="0";
    private ArrayList<String> nameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        }

        host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        db = new DatabaseHandler(this);

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("New Patient");
        host.addTab(spec);
        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Old Patient");
        host.addTab(spec);

        for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
            host.getTabWidget().getChildAt(i).getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }

        //tab1
        etPatName = (EditText) findViewById(R.id.etPatName);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etDate = (EditText) findViewById(R.id.etDate);

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        etDate.setText(date);

        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(NewAppointment.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth+1;
                        etDate.setText(selectedday+ "/" + selectedmonth+ "/" + selectedyear);
                        updateschedule(etDate.getText().toString());

                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select appointment date");
                mDatePicker.show();

            }

        });

        updateschedule(date);
        spinner1 = (Spinner) findViewById(R.id.spinner_gender);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(new HintSpinnerAdapter(
                adapter1, R.layout.hint_row_item, this));



        //tab2
        getPatients();
        etDate1 = (EditText) findViewById(R.id.etDate1);
        spinner3 = (Spinner) findViewById(R.id.spinner_patient);

        // Creating adapter for spinner
        nameList.add("<Select a Patient>");
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameList);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(new HintSpinnerAdapter(
                adapter3, R.layout.hint_row_item2, this));
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String patientName= null;
                if(spinner3 != null && spinner3.getSelectedItem() !=null ) {
                    patientName = (String)spinner3.getSelectedItem();
                    getPatients1(patientName);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        etDate1.setText(date);

        etDate1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(NewAppointment.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth+1;
                        etDate1.setText(selectedday+ "/" + selectedmonth+ "/" + selectedyear);
                        updateschedule1(etDate1.getText().toString());

                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select appointment date");
                mDatePicker.show();

            }

        });
        updateschedule1(date);


    }//onCreate ends here

    public void getPatients(){

        RequestQueue queue = Volley.newRequestQueue(NewAppointment.this);
        String url = AppConfigURL.URL + "get_patients";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String patient_id     = jsonObject.getString("patient_id");
                                String name     = jsonObject.getString("name");

                                nameList.add(name);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast
                Toast.makeText(getApplicationContext(),"Error Response",Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // get data from sqlite
                HashMap<String, String> user = db.getUser();
                final String token = user.get("token");
                final String user_id = user.get("user_id");
                Map<String, String> params = new HashMap<>();
                params.put("auth_token", token);
                params.put("user_id", user_id);
                params.put("authenticate", "true");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void getPatients1(final String patientName){

        RequestQueue queue = Volley.newRequestQueue(NewAppointment.this);
        String url = AppConfigURL.URL + "get_patients";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String patient_id     = jsonObject.getString("patient_id");
                                String name     = jsonObject.getString("name");
                                if(name.equals(patientName)){
                                    pat_id = patient_id;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast
                Toast.makeText(getApplicationContext(),"Error Response",Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // get data from sqlite
                HashMap<String, String> user = db.getUser();
                final String token = user.get("token");
                final String user_id = user.get("user_id");
                Map<String, String> params = new HashMap<>();
                params.put("auth_token", token);
                params.put("user_id", user_id);
                params.put("authenticate", "true");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_ok:
                int current = host.getCurrentTab();

                if(String.valueOf(current).equals("0")){
                    if(etPatName.getText().toString().equals("")){
                        etPatName.setError("Field can not be blank");
                    }else if(etMobile.getText().toString().equals("")){
                        etMobile.setError("Field can not be blank");
                    }else if(etDate.getText().toString().equals("")){
                        etDate.setError("Field can not be blank");
                    }else if(spinner1.getSelectedItem() == null){
                        Toast.makeText(getApplicationContext(),"Select Gender",Toast.LENGTH_SHORT).show();
                    }else if(spinner2.getSelectedItem() == null || spinner2.getSelectedItem() == "No Schedule"){
                        Toast.makeText(getApplicationContext(),"Select Spinner or Schedule",Toast.LENGTH_SHORT).show();
                    }else{
                        String name = etPatName.getText().toString().trim();
                        String mobile = etMobile.getText().toString().trim();
                        String date = etDate.getText().toString().trim();
                        String gender = spinner1.getSelectedItem().toString().trim();
                        String schedule = spinner2.getSelectedItem().toString().trim();
                        createAppointment(name,mobile,date,gender,schedule);
                    }
                }
                else {
                    if(etDate1.getText().toString().equals("")){
                        etDate1.setError("Field can not be blank");
                    }else if(spinner3.getSelectedItem() == null){
                        Toast.makeText(getApplicationContext(),"Select Patient",Toast.LENGTH_SHORT).show();
                    }else if(spinner4.getSelectedItem() == null || spinner4.getSelectedItem() == "No Schedule"){
                        Toast.makeText(getApplicationContext(),"Select Spinner or Schedule",Toast.LENGTH_SHORT).show();
                    }else{
                        String patientName = spinner3.getSelectedItem().toString().trim();
                        getPatients1(patientName);
                        String date = etDate1.getText().toString().trim();
                        String schedule = spinner4.getSelectedItem().toString().trim();
                        createAppointment1(patientName,date,schedule);
                    }

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateschedule(String input_date){
        arr = new ArrayList<String>();
        ArrayList<HashMap<String,String>> chamber_list = db.getAllChamber();
        for(int i=0;i<chamber_list.size();i++){
            HashMap<String,String> content = new HashMap<String, String>();
            content = chamber_list.get(i);
            String status = content.get("status");
            if(status.equals("Selected")){
                chamber_id = content.get("chamber_id");
            }
        }

        ArrayList<HashMap<String,String>> schedule_list = db.getScheduleById(chamber_id);
        for(int j=0;j<schedule_list.size();j++){
            HashMap<String,String> content1 = new HashMap<String, String>();
            content1 = schedule_list.get(j);
            String key = content1.get("key");

            SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
            Date dt1= null;
            try {
                dt1 = format1.parse(input_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat format2=new SimpleDateFormat("EEEE");
            String date=format2.format(dt1);
            System.out.println("day: "+ date);

            if(date.equals("Sunday")){
                key1 = String.valueOf(0);
            }else if(date.equals("Monday")){
                key1 = String.valueOf(1);
            }else if(date.equals("Tuesday")){
                key1 = String.valueOf(2);
            }else if(date.equals("Wednesday")){
                key1 = String.valueOf(3);
            }else if(date.equals("Thursday")){
                key1 = String.valueOf(4);
            }else if(date.equals("Friday")){
                key1 = String.valueOf(5);
            }else if(date.equals("Saturday")){
                key1 = String.valueOf(6);
            }

            System.out.println("Test key1: "+key1);
            System.out.println("Test key: "+key);
            if(key1.equals(key)){
                System.out.println("Hello World");
                String morning = content1.get("morning");
                String afternoon = content1.get("afternoon");
                String evening = content1.get("evening");
                String status = content1.get("status");
                if(status.equals("closed")){
                    arr.add("No Schedule");
                }else{
                    if(morning != null){
                        arr.add(morning);
                    }
                    if(afternoon != null){
                        arr.add(afternoon);
                    }
                    if(evening != null){
                        arr.add(evening);
                    }
                }
            }
            System.out.println("Testing "+arr);

        }

        spinner2 = (Spinner) findViewById(R.id.spinner_schedule);
        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arr);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(new HintSpinnerAdapter(
                adapter2, R.layout.hint_row_item1, this));
    }
    public void updateschedule1(String input_date){

        arr = new ArrayList<String>();
        ArrayList<HashMap<String,String>> chamber_list = db.getAllChamber();
        for(int i=0;i<chamber_list.size();i++){
            HashMap<String,String> content = new HashMap<String, String>();
            content = chamber_list.get(i);
            String status = content.get("status");
            if(status.equals("Selected")){
                chamber_id = content.get("chamber_id");
            }
        }

        ArrayList<HashMap<String,String>> schedule_list = db.getScheduleById(chamber_id);
        for(int j=0;j<schedule_list.size();j++){
            HashMap<String,String> content1 = new HashMap<String, String>();
            content1 = schedule_list.get(j);
            String key = content1.get("key");

            SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
            Date dt1= null;
            try {
                dt1 = format1.parse(input_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat format2=new SimpleDateFormat("EEEE");
            String date=format2.format(dt1);
            System.out.println("day: "+ date);

            if(date.equals("Sunday")){
                key1 = String.valueOf(0);
            }else if(date.equals("Monday")){
                key1 = String.valueOf(1);
            }else if(date.equals("Tuesday")){
                key1 = String.valueOf(2);
            }else if(date.equals("Wednesday")){
                key1 = String.valueOf(3);
            }else if(date.equals("Thursday")){
                key1 = String.valueOf(4);
            }else if(date.equals("Friday")){
                key1 = String.valueOf(5);
            }else if(date.equals("Saturday")){
                key1 = String.valueOf(6);
            }

            System.out.println("Test key1: "+key1);
            System.out.println("Test key: "+key);
            if(key1.equals(key)){
                String morning = content1.get("morning");
                String afternoon = content1.get("afternoon");
                String evening = content1.get("evening");
                String status = content1.get("status");
                if(status.equals("closed")){
                    arr.add("No Schedule");
                }else{
                    if(morning != null){
                        arr.add(morning);
                    }
                    if(afternoon != null){
                        arr.add(afternoon);
                    }
                    if(evening != null){
                        arr.add(evening);
                    }
                }
            }
            System.out.println("Testing "+arr);

        }

        spinner4 = (Spinner) findViewById(R.id.spinner_schedule1);
        adapter4 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arr);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(new HintSpinnerAdapter(
                adapter4, R.layout.hint_row_item1, this));
    }

    // for edit button in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tick, menu);
        return true;
    }
    public void createAppointment(final String name, final String mobile, final String date, final String gender, final String schedule){

        RequestQueue queue = Volley.newRequestQueue(NewAppointment.this);
        String url = AppConfigURL.URL + "create_appointment";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res_new: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String status     = jsonObject.getString("status");
                                if(status.equals("failed")){
                                    String reason     = jsonObject.getString("reason");
                                    Toast.makeText(getApplicationContext(),"Sorry! "+reason,Toast.LENGTH_SHORT).show();
                                }
                                else if (status.equals("success")){
                                    Toast.makeText(getApplicationContext(),"Add Appointment Successfully!",Toast.LENGTH_SHORT).show();
                                    Intent ii = new Intent(NewAppointment.this, AppointmentActivity.class);
                                    startActivity(ii);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast
                Toast.makeText(getApplicationContext(),"Error Response",Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // get data from sqlite
                HashMap<String, String> user = db.getUser();
                final String token = user.get("token");
                final String user_id = user.get("user_id");

                Date date1 = null;
                java.text.DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    date1 = (Date)formatter.parse(date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                java.sql.Timestamp timeStampDate = new Timestamp(date1.getTime());
                long timestamp = timeStampDate.getTime()/1000;

                Map<String, String> params = new HashMap<>();
                params.put("auth_token", token);
                params.put("user_id", user_id);
                params.put("name", name);
                params.put("phone", mobile);
                params.put("gender", gender);
                params.put("chamber_id", chamber_id);
                params.put("schedule", schedule);
                params.put("timestamp", String.valueOf(timestamp));
                params.put("authenticate", "true");
                params.put("new_patient", "yes");
                System.out.println("Params: "+params);
                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void createAppointment1(final String patientName, final String date, final String schedule){

        RequestQueue queue = Volley.newRequestQueue(NewAppointment.this);
        String url = AppConfigURL.URL + "create_appointment";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res_old: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String status     = jsonObject.getString("status");
                                if(status.equals("success")){
                                    Toast.makeText(getApplicationContext(),"Add Appointment Successfully!",Toast.LENGTH_SHORT).show();
                                    Intent ii = new Intent(NewAppointment.this, AppointmentActivity.class);
                                    startActivity(ii);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast
                Toast.makeText(getApplicationContext(),"Error Response",Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // get data from sqlite
                HashMap<String, String> user = db.getUser();
                final String token = user.get("token");
                final String user_id = user.get("user_id");

                Date date1 = null;
                java.text.DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    date1 = (Date)formatter.parse(date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                java.sql.Timestamp timeStampDate = new Timestamp(date1.getTime());
                long timestamp = timeStampDate.getTime()/1000;


                Map<String, String> params = new HashMap<>();
                params.put("auth_token", token);
                params.put("user_id", user_id);
                params.put("patient_id", pat_id);
                params.put("chamber_id", chamber_id);
                params.put("schedule", schedule);
                params.put("timestamp", String.valueOf(timestamp));
                params.put("authenticate", "true");
                params.put("new_patient", "no");
                System.out.println("Params: "+params);
                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
