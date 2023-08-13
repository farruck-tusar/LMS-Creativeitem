package com.tusar.creativeitem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
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
import com.tusar.creativeitem.utility.CustomAppointmentList;
import com.tusar.creativeitem.utility.CustomBillingList1;
import com.tusar.creativeitem.utility.CustomPatientList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity {

    private TextView mTextMessage;
    private int var1,var2,var3,var4 = 0;
    private TextView tvPat, tvAppoint, tvChamber, tvBill;

    private int chamber_id = 0;

    private com.github.clans.fab.FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab3;
    private com.github.clans.fab.FloatingActionButton fab4;

    private DatabaseHandler db;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);



        db = new DatabaseHandler(this);
        pDialog = new ProgressDialog(this);

        mTextMessage = (TextView) findViewById(R.id.message);

        tvPat = (TextView) findViewById(R.id.registered_patient);
        tvAppoint = (TextView) findViewById(R.id.app_today);
        tvChamber = (TextView) findViewById(R.id.active_chamber);
        tvBill = (TextView) findViewById(R.id.bill_today);

        // get data from sqlite
        HashMap<String, String> user = db.getUser();
        final String name = user.get("name");
        mTextMessage.setText("Welcome, "+ name);


        ArrayList<HashMap<String,String>> chamber_list;
        chamber_list = db.getAllChamber();
        var3 = chamber_list.size();
        tvChamber.setText(""+var3);

        for(int i=0;i<chamber_list.size();i++){
            HashMap<String,String> content = new HashMap<String, String>();
            content = chamber_list.get(i);
            String status = content.get("status");
            if(status.equals("Selected")){
                chamber_id = Integer.parseInt(content.get("chamber_id"));
            }
        }
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

        getPatients();
        getAllAppointments(String.valueOf(chamber_id), today_timestamp);
        getAllBilling(String.valueOf(chamber_id), today_timestamp);

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
                Intent i = new Intent(MainActivity.this, NewAppointment.class);
                startActivity(i);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PrescriptionActivity.class);
                startActivity(i);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewPatient.class);
                startActivity(i);
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewBilling.class);
                startActivity(i);
            }
        });



    }//onCreate ends here

    public void getPatients(){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = AppConfigURL.URL + "get_patients";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Res1: "+response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            System.out.println("Print 1: "+ jsonArray.length());
                            var1 = jsonArray.length();
                            tvPat.setText(""+var1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideDialog();
                        }

                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error Response from server",Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
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
        queue.add(stringRequest);
    }
    public void getAllAppointments(final String chamber_id, final long timestamp){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = AppConfigURL.URL + "get_appointments";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res 2: "+response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            System.out.println("Print 2: "+ jsonArray.length());
                            var2 = jsonArray.length();
                            tvAppoint.setText(""+var2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideDialog();
                        }
                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast

                hideDialog();
                Toast.makeText(getApplicationContext(),"Check Internet Connection or Response Error!",Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> user = db.getUser();
                final String token = user.get("token");
                final String user_id = user.get("user_id");

                Map<String, String> params = new HashMap<>();
                params.put("auth_token", token);
                params.put("user_id", user_id);
                params.put("chamber_id", chamber_id);
                params.put("timestamp", String.valueOf(timestamp));
                params.put("authenticate", "true");
                System.out.println("params >>> "+params);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getAllBilling(final String chamber_id, final long timestamp){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = AppConfigURL.URL + "get_invoices";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Billing: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int temp = Integer.parseInt(jsonObject.getString("charge"));
                                var4 = var4 +temp;
                                tvBill.setText(""+var4);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideDialog();
                        }
                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast
                Toast.makeText(getApplicationContext(),"Check Internet Connection or Response Error!",Toast.LENGTH_SHORT).show();
                hideDialog();
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
                params.put("chamber_id", chamber_id);
                params.put("timestamp", String.valueOf(timestamp));
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
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Do you want to close the application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
