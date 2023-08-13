package com.tusar.creativeitem;

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

import java.util.HashMap;
import java.util.Map;

public class PasswordActivity extends BaseActivity {
    private DatabaseHandler db;
    EditText oldPassword,newPassword,confirmPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_password, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(7).setChecked(true);

        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        db = new DatabaseHandler(this);

    }//onCreate end

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                String oldP = oldPassword.getText().toString();
                String newP = newPassword.getText().toString();
                String confirmP = confirmPassword.getText().toString();
                if(newP.equals(confirmP)){
                    changePassword(oldP,newP,confirmP);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Confirm Password does not match.",Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tick, menu);
        return true;
    }

    public void changePassword(final String oldP,final String newP,final String confirmP){

        RequestQueue queue = Volley.newRequestQueue(PasswordActivity.this);
        String url = AppConfigURL.URL + "update_doctor_password";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res Change Password: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String status     = jsonObject.getString("status");
                                if(status.equals("failed")){
                                    String reason     = jsonObject.getString("reason");
                                    Toast.makeText(getApplicationContext(),"Failed: "+reason,Toast.LENGTH_SHORT).show();
                                    newPassword.setText("");
                                    confirmPassword.setText("");
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Password Change Successful!",Toast.LENGTH_SHORT).show();
                                    newPassword.setText("");
                                    oldPassword.setText("");
                                    confirmPassword.setText("");
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
                params.put("authenticate", "true");
                params.put("current_password",oldP);
                params.put("new_password", newP);
                params.put("confirm_password", confirmP);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
