package com.tusar.creativeitem;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {
    private DatabaseHandler db;
    EditText etName,etPhone,etEmail,etDegree;
    ImageButton imgName,imgPhone,imgEmail,imgDegree;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_profile, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(6).setChecked(true);

        TextView tvName = (TextView) findViewById(R.id.tvDrName);

        etName = (EditText) findViewById(R.id.etDocName);
        etPhone = (EditText) findViewById(R.id.etDocMobile);
        etEmail = (EditText) findViewById(R.id.etDocEmail);
        etDegree = (EditText) findViewById(R.id.etDocDegree);

        imgName = (ImageButton) findViewById(R.id.imgDocName);
        imgPhone = (ImageButton) findViewById(R.id.imgDocPhone);
        imgEmail = (ImageButton) findViewById(R.id.imgDocEmail);
        imgDegree = (ImageButton) findViewById(R.id.imgDocDegree);

        db = new DatabaseHandler(this);
        // get data from sqlite
        HashMap<String, String> user = db.getUser();
        final String name = user.get("name");
        final String phone = user.get("phone");
        final String email = user.get("email");

        tvName.setText(name);

        etName.setText(name);
        etPhone.setText(phone);
        etEmail.setText(email);
        etDegree.setText("MBBS,FRCS,FCPS(London),Department Head(Dhaka Medical College)");

        imgName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("");
                etName.requestFocus();
                etName.setCursorVisible(true);

            }
        });
        imgPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPhone.setText("");
                etPhone.requestFocus();
                etPhone.setCursorVisible(true);
            }
        });
        imgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.setText("");
                etEmail.requestFocus();
                etEmail.setCursorVisible(true);
            }
        });
        imgDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDegree.setText("");
                etDegree.requestFocus();
                etDegree.setCursorVisible(true);
            }
        });
    }//onCreate end

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                if(etName.getText().toString().equals("")){
                    etName.setError("Field can not be blank");
                }else if(etPhone.getText().toString().equals("")){
                    etPhone.setError("Field can not be blank");
                }else if(etEmail.getText().toString().equals("")){
                    etEmail.setError("Field can not be blank");
                }else if(etDegree.getText().toString().equals("")){
                    etDegree.setError("Field can not be blank");
                }else{
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String degree = etDegree.getText().toString().trim();

                    //call server
                    updateProfile(name,phone,email,degree);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateProfile(final String name, final String phone, final String email, final String degree){

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        String url = AppConfigURL.URL + "update_doctor_profile";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response_update: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String status     = jsonObject.getString("status");
                                if(status.equals("success")){
                                    String user_id1     = jsonObject.getString("user_id");
                                    Toast.makeText(getApplicationContext(),"Update Profile Successfully!",Toast.LENGTH_SHORT).show();
                                    db.updateUser(user_id1,name,phone,email);
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                                else if(status.equals("failed")){
                                    String reason     = jsonObject.getString("reason");
                                    Toast.makeText(getApplicationContext(),"Sorry! "+reason,Toast.LENGTH_SHORT).show();
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
                params.put("phone", phone);
                params.put("email", email);
                params.put("degree", degree);
                params.put("authenticate", "true");
                System.out.println("Params: "+params);
                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tick, menu);
        return true;
    }
}
