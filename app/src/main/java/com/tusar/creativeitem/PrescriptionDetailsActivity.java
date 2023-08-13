package com.tusar.creativeitem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PrescriptionDetailsActivity extends AppCompatActivity {

    private DatabaseHandler db;
    ProgressDialog pDialog;
    TextView tvName,tvEmail,tvAge,tvGender,tvDate,tvSymptoms,tvDiagnosis;
    private TableLayout tableLayout1,tableLayout2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_details);
        db = new DatabaseHandler(this);
        pDialog = new ProgressDialog(this);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        }
        Intent intent = getIntent();
        String prescription_id = intent.getStringExtra("prescription_id");

        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvAge = (TextView) findViewById(R.id.tvAge);
        tvGender = (TextView) findViewById(R.id.tvGender);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvSymptoms = (TextView) findViewById(R.id.tvSymptoms);
        tvDiagnosis = (TextView) findViewById(R.id.tvDiagnosis);
        tableLayout1=(TableLayout)findViewById(R.id.tableMedicine);
        tableLayout2=(TableLayout)findViewById(R.id.tableTest);

        getPrescriptionDetails(prescription_id);

    }//onCreate ends here

    public void getPrescriptionDetails(final String prescription_id){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConfigURL.URL + "get_prescription_details_by_id";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Response: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                long timestamp     = Long.parseLong(jsonObject.getString("timestamp"));
                                String date = getDate(timestamp*1000);
                                String patient_id = jsonObject.getString("patient_id");
                                String symptom = jsonObject.getString("symptom");
                                String diagnosis = jsonObject.getString("diagnosis");

                                getbasicinfo(patient_id);

                                if(date.equals("")){
                                    tvDate.setText("N/A");
                                }
                                else {
                                    tvDate.setText(date);
                                }
                                if(symptom.equals("")){
                                    tvSymptoms.setText("N/A");
                                }
                                else {
                                    tvSymptoms.setText(symptom);
                                }
                                if(diagnosis.equals("")){
                                    tvDiagnosis.setText("N/A");
                                }
                                else {
                                    tvDiagnosis.setText(diagnosis);
                                }

                                JSONArray jsonArray1 = jsonObject.getJSONArray("medicine");
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                    String medicine_name = jsonObject1.getString("medicine_name");
                                    String note = jsonObject1.getString("note");

                                    if(medicine_name.equals("")){
                                        View tableRow = LayoutInflater.from(PrescriptionDetailsActivity.this).inflate(R.layout.table_medicine_row1,null,false);
                                        TextView medicineName  = (TextView) tableRow.findViewById(R.id.tvMedicineName);
                                        medicineName.setText("N/A");

                                        tableLayout1.addView(tableRow);
                                    }else {
                                        View tableRow = LayoutInflater.from(PrescriptionDetailsActivity.this).inflate(R.layout.table_medicine_row1,null,false);
                                        TextView medicineName  = (TextView) tableRow.findViewById(R.id.tvMedicineName);
                                        TextView medicineDesc  = (TextView) tableRow.findViewById(R.id.tvMedicineDes);
                                        medicineName.setText(medicine_name);
                                        medicineDesc.setText(note);

                                        tableLayout1.addView(tableRow);
                                    }

                                }

                                JSONArray jsonArray2 = jsonObject.getJSONArray("test");
                                for (int j = 0; j < jsonArray2.length(); j++) {
                                    JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
                                    String test_name = jsonObject2.getString("test_name");

                                    if(test_name.equals(""))
                                    {
                                        View tableRow = LayoutInflater.from(PrescriptionDetailsActivity.this).inflate(R.layout.table_test_row1,null,false);
                                        TextView testName  = (TextView) tableRow.findViewById(R.id.tvTestName);
                                        testName.setText("N/A");

                                        tableLayout2.addView(tableRow);
                                    }else {
                                        View tableRow = LayoutInflater.from(PrescriptionDetailsActivity.this).inflate(R.layout.table_test_row1,null,false);
                                        TextView testName  = (TextView) tableRow.findViewById(R.id.tvTestName);
                                        testName.setText(test_name);

                                        tableLayout2.addView(tableRow);
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast
                hideDialog();
                Toast.makeText(getApplicationContext(),"Error Response",Toast.LENGTH_SHORT).show();

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
                params.put("prescription_id", prescription_id);
                params.put("authenticate", "true");

                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getbasicinfo(final String patient_id){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConfigURL.URL + "get_basic_info_of_patient";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Response: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String name = jsonObject.getString("name");
                                String age = jsonObject.getString("age");
                                String gender = jsonObject.getString("gender");
                                String email = "";

                                if(name.equals("")){
                                    tvName.setText("N/A");
                                }
                                else {
                                    tvName.setText(name);
                                }
                                if(age.equals("")){
                                    tvAge.setText("N/A");
                                }
                                else {
                                    tvAge.setText(age);
                                }
                                if(gender.equals("")){
                                    tvGender.setText("N/A");
                                }
                                else {
                                    tvGender.setText(gender);
                                }
                                if(email.equals("")){
                                    tvEmail.setText("N/A");
                                }
                                else {
                                    tvEmail.setText(email);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast
                hideDialog();
                Toast.makeText(getApplicationContext(),"Error Response",Toast.LENGTH_SHORT).show();

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
                params.put("patient_id", patient_id);
                params.put("authenticate", "true");

                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private String getDate(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }
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
}
