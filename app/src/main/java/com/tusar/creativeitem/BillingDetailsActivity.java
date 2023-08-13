package com.tusar.creativeitem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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

public class BillingDetailsActivity extends AppCompatActivity {
    private DatabaseHandler db;
    ProgressDialog pDialog;
    TextView tvAmount,tvDate,tvName,tvMobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_details);

        db = new DatabaseHandler(this);
        pDialog = new ProgressDialog(this);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        }
        Intent intent = getIntent();
        String invoice_id = intent.getStringExtra("invoice_id");

        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvName = (TextView) findViewById(R.id.tvName);
        tvMobile = (TextView) findViewById(R.id.tvMobile);

        getBillingDetails(invoice_id);

    }//onCreate ends

    public void getBillingDetails(final String invoice_id){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConfigURL.URL + "get_invoice_info";
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
                                String charge = jsonObject.getString("charge");
                                String patient_name = jsonObject.getString("patient_name");
                                String mobile_num = "";

                                if(date.equals("")){
                                    tvDate.setText("N/A");
                                }
                                else {
                                    tvDate.setText(date);
                                }
                                if(charge.equals("")){
                                    tvAmount.setText("N/A");
                                }
                                else {
                                    tvAmount.setText(charge);
                                }
                                if(patient_name.equals("")){
                                    tvName.setText("N/A");
                                }
                                else {
                                    tvName.setText(patient_name);
                                }
                                if(mobile_num.equals("")){
                                    tvMobile.setText("N/A");
                                }
                                else {
                                    tvMobile.setText(mobile_num);
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
                Toast.makeText(getApplicationContext(),"Response Error from Server!",Toast.LENGTH_SHORT).show();

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
                params.put("invoice_id", invoice_id);
                params.put("authenticate", "true");
                System.out.println("Params1: "+params);
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
