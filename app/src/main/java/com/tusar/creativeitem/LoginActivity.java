package com.tusar.creativeitem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.tusar.creativeitem.helper.DatabaseHandler;
import com.tusar.creativeitem.helper.SessionManager;
import com.tusar.creativeitem.utility.AppConfigURL;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Farruck Ahmed Tusar on 15-Jun-17.
 */
public class LoginActivity extends AppCompatActivity {

    EditText etEmail,etPassword;
    Button btnLogin,btnSignup;
    private DatabaseHandler db;
    private SessionManager session;
    ProgressDialog pDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        db = new DatabaseHandler(this);
        //session manager
        session = new SessionManager(getApplicationContext());

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);

        //Need to remove
        etEmail.setText("doctor@example.com");
        etPassword.setText("1234");

        if(session.isLoggedIn()){
            Intent ii = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(ii);
        }
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String InEmail = etEmail.getText().toString().trim();
                final String Inpassword = etPassword.getText().toString().trim();

                //Validation for Blank Field
                if(InEmail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Email or phone can not be blank", Toast.LENGTH_SHORT).show();
                    etEmail.setError("Field can not be blank");
                    return;
                }
                else if(Inpassword.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Password can not be blank", Toast.LENGTH_SHORT).show();
                    etPassword.setError("Field can not be blank");
                    return;
                }
                else
                {
                    if (!isNetworkAvailable()) {
                        Toast.makeText(getApplicationContext(), "Please connect to the internet!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if (!session.isLoggedIn())
                        {
                            pDialog.setMessage("Logging in..");
                            showDialog();
                            loginRequest(InEmail,Inpassword);
                        }
                        else if(session.isLoggedIn()){
                            Intent ii = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(ii);
                        }
                    }
                }
            }
        });

    } //onCreate ends here

    public void loginRequest(final String inEmail, final String inpassword){

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String url = AppConfigURL.URL + "login";
        System.out.println("url: "+url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String login_status = jsonObject.getString("login_status");
                                if(login_status.equals("failed")){
                                    hideDialog();
                                    Toast.makeText(getApplicationContext(), "User credential doesn't match", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String user_id     = jsonObject.getString("user_id");
                                    String user_type     = jsonObject.getString("user_type");
                                    String name     = jsonObject.getString("name");
                                    String email     = jsonObject.getString("email");
                                    String phone     = jsonObject.getString("phone");
                                    String chamber_id     = jsonObject.getString("chamber_id");
                                    String account_id     = jsonObject.getString("account_id");
                                    String auth_token     = jsonObject.getString("auth_token");
                                    String last_login     = jsonObject.getString("last_login");

                                    // add data into sqlite tusar
                                    db.addUser(user_id,user_type,name,email,phone,chamber_id,account_id,auth_token,last_login);

                                    //add other sqlite tables
                                    addChamber();
                                    //addPatients();
                                    session.setLogIn(true);

                                    Intent ii = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(ii);
                                    hideDialog();
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
                hideDialog();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("identifier", inEmail);
                params.put("password", inpassword);
                params.put("authenticate", "false");

                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void addPatients(){

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
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
                                String phone     = jsonObject.getString("phone");
                                String address     = jsonObject.getString("address");
                                String about     = jsonObject.getString("about");
                                String age     = jsonObject.getString("age");
                                String gender     = jsonObject.getString("gender");

                                // add data into sqlite
                                db.addPatient(patient_id,name,phone,address,about,age,gender);

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
    public void addChamber(){

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String url = AppConfigURL.URL + "get_chambers";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res11: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String chamber_id     = jsonObject.getString("chamber_id");
                                String name     = jsonObject.getString("name");
                                String address     = jsonObject.getString("address");
                                String about     = jsonObject.getString("about");
                                String account_id     = jsonObject.getString("account_id");

                                // add data into sqlite
                                db.addChamber(chamber_id,name,address,about,account_id);

                                JSONArray jsonArray1 = jsonObject.getJSONArray("schedule");
                                System.out.println(jsonArray1);
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                    String day     = jsonObject1.getString("day");
                                    String key     = jsonObject1.getString("key");
                                    String status     = jsonObject1.getString("status");
                                    String morning_open     = jsonObject1.getString("morning_open");
                                    String morning_close     = jsonObject1.getString("morning_close");
                                    String morning     = jsonObject1.getString("morning");
                                    String afternoon_open     = jsonObject1.getString("afternoon_open");
                                    String afternoon_close     = jsonObject1.getString("afternoon_close");
                                    String afternoon     = jsonObject1.getString("afternoon");
                                    String evening_open     = jsonObject1.getString("evening_open");
                                    String evening_close     = jsonObject1.getString("evening_close");
                                    String evening     = jsonObject1.getString("evening");

                                    db.addSchedule(chamber_id,day,key,status,morning_open,morning_close,morning,afternoon_open,afternoon_close,afternoon,evening_open,evening_close,evening);
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


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
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
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
