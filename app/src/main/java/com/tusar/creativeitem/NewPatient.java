package com.tusar.creativeitem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.tusar.creativeitem.utility.HintSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewPatient extends AppCompatActivity {

    EditText etname,etMobile,etAge,etAddress;
    Spinner spinner1;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        }

        db = new DatabaseHandler(this);

        etname = (EditText) findViewById(R.id.etPatName);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etAge = (EditText) findViewById(R.id.etAge);
        etAddress = (EditText) findViewById(R.id.etAddress);
        spinner1 = (Spinner) findViewById(R.id.spinner_gender);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(new HintSpinnerAdapter(
                adapter1, R.layout.hint_row_item, this));


    }//onCreate ends

    public void createPatient(final String name, final String mobile, final String address, final String age, final String gender){

        RequestQueue queue = Volley.newRequestQueue(NewPatient.this);
        String url = AppConfigURL.URL + "create_patient";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Respone for Create Patient: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String status     = jsonObject.getString("status");
                                if(status.equals("success")){
                                    Toast.makeText(getApplicationContext(),"Add Patient Successfully!",Toast.LENGTH_SHORT).show();
                                    Intent ii = new Intent(NewPatient.this, PatientActivity.class);
                                    startActivity(ii);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"There is a problem while create new patient.",Toast.LENGTH_SHORT).show();
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
                params.put("name", name);
                params.put("phone", mobile);
                params.put("address", address);
                params.put("age", age);
                params.put("gender", gender);
                params.put("authenticate", "true");

                System.out.println("Sent data for new patient: " + params);
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
                if(etname.getText().toString().equals("")){
                    etname.setError("Field can not be blank");
                }else if(etMobile.getText().toString().equals("")){
                    etMobile.setError("Field can not be blank");
                }else if(spinner1.getSelectedItem() == null){
                    Toast.makeText(getApplicationContext(),"Select Gender",Toast.LENGTH_SHORT).show();
                }else{
                    String name = etname.getText().toString().trim();
                    String mobile = etMobile.getText().toString().trim();
                    String address = etAddress.getText().toString().trim();
                    String age = etAge.getText().toString().trim();
                    String gender = spinner1.getSelectedItem().toString().trim();
                    createPatient(name,mobile,address,age,gender);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // for tick button in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tick, menu);
        return true;
    }
}
