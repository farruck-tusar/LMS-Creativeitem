package com.tusar.creativeitem;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewBilling extends AppCompatActivity {

    EditText etAmount,etDate,etNote;
    Spinner spinner1;
    private String chamber_id;
    private DatabaseHandler db;
    private ArrayList<String> nameList = new ArrayList<String>();
    private String pat_id="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_billing);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        }
        db = new DatabaseHandler(this);

        etAmount = (EditText) findViewById(R.id.etAmount);
        etDate = (EditText) findViewById(R.id.etDate);
        etNote = (EditText) findViewById(R.id.etNote);

        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(NewBilling.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth+1;
                        etDate.setText(selectedday+ "/" + selectedmonth+ "/" + selectedyear);


                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select appointment date");
                mDatePicker.show();

            }

        });

        getPatients();

        nameList.add("<Select a Patient>");
        spinner1 = (Spinner) findViewById(R.id.spinner_patient);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(new HintSpinnerAdapter(
                adapter1, R.layout.hint_row_item2, this));
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String patientName= null;
                if(spinner1 != null && spinner1.getSelectedItem() !=null ) {
                    patientName = (String)spinner1.getSelectedItem();
                    getPatients1(patientName);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }//onCreate ends

    public void getPatients(){

        RequestQueue queue = Volley.newRequestQueue(NewBilling.this);
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

        RequestQueue queue = Volley.newRequestQueue(NewBilling.this);
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

    // for back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_ok:
                if(etAmount.getText().toString().equals("")){
                    etAmount.setError("Field can not be blank");
                }else if(etDate.getText().toString().equals("")){
                    etDate.setError("Field can not be blank");
                }else if(etNote.getText().toString().equals("")){
                    etNote.setError("Field can not be blank");
                }else if(spinner1.getSelectedItem() == null){
                    Toast.makeText(getApplicationContext(),"Select Patient",Toast.LENGTH_SHORT).show();
                }else{
                    String amount = etAmount.getText().toString().trim();
                    String date = etDate.getText().toString().trim();
                    String note = etNote.getText().toString().trim();

                    //call server
                    System.out.println("Chk date: "+date);
                    creteBilling(amount,date,note);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // for edit button in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tick, menu);
        return true;
    }

    public void creteBilling(final String amount, final String date, final String note){

        RequestQueue queue = Volley.newRequestQueue(NewBilling.this);
        String url = AppConfigURL.URL + "create_invoice";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Response_billing_new: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String status     = jsonObject.getString("status");
                                if(status.equals("success")){
                                    Toast.makeText(getApplicationContext(),"Add Billing info Successfully!",Toast.LENGTH_SHORT).show();
                                    Intent ii = new Intent(NewBilling.this, BillingActivity.class);
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

                System.out.println("Date passed "+date);
                Date date1 = null;
                java.text.DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    date1 = (Date)formatter.parse(date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                java.sql.Timestamp timeStampDate = new Timestamp(date1.getTime());
                long timestamp = timeStampDate.getTime()/1000;

                ArrayList<HashMap<String,String>> chamber_list = db.getAllChamber();
                for(int i=0;i<chamber_list.size();i++){
                    HashMap<String,String> content = new HashMap<String, String>();
                    content = chamber_list.get(i);
                    String status = content.get("status");
                    if(status.equals("Selected")){
                        chamber_id = content.get("chamber_id");
                    }
                }

                Map<String, String> params = new HashMap<>();
                params.put("auth_token", token);
                params.put("user_id", user_id);
                params.put("patient_id", pat_id);
                params.put("chamber_id", chamber_id);
                params.put("charge", amount);
                params.put("code", note);
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
