package com.tusar.creativeitem.utility;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tusar.creativeitem.LoginActivity;
import com.tusar.creativeitem.R;
import com.tusar.creativeitem.helper.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentBasic extends Fragment {
    View view;
    private DatabaseHandler db;
    String patient_id;
    TextView tvName,tvEmail,tvAge,tvGender,tvMobile;

    ProgressDialog pDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        pDialog = new ProgressDialog(getActivity());
        view = inflater.inflate(R.layout.fragment_basic, container, false);

        tvName = (TextView) view.findViewById(R.id.tvName);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvAge = (TextView) view.findViewById(R.id.tvAge);
        tvGender = (TextView) view.findViewById(R.id.tvGender);
        tvMobile = (TextView) view.findViewById(R.id.tvMobile);

        patient_id= getArguments().getString("patient_id");

        getBasicInfo();

        // Inflate the layout for this fragment
        return view;
    }
    public void getBasicInfo(){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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

                                String patient_id     = jsonObject.getString("patient_id");
                                String name     = jsonObject.getString("name");
                                String phone     = jsonObject.getString("phone");
                                String email = "";
                                String address     = jsonObject.getString("address");
                                String about     = jsonObject.getString("about");
                                String age     = jsonObject.getString("age");
                                String gender     = jsonObject.getString("gender");

                                if(name.equals("")){
                                    tvName.setText("N/A");
                                }
                                else{tvName.setText(name);}
                                if(email.equals("")){
                                    tvEmail.setText("N/A");
                                }
                                else{tvEmail.setText(email);}
                                if(age.equals("")){
                                    tvAge.setText("N/A");
                                }
                                else{tvAge.setText(age);}
                                if(gender.equals("")){
                                    tvGender.setText("N/A");
                                }
                                else{tvGender.setText(gender);}
                                if(phone.equals("")){
                                    tvMobile.setText("N/A");
                                }
                                else{tvMobile.setText(phone);}

                                hideDialog();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideDialog();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast
                hideDialog();
                Toast.makeText(getActivity(),"Error Response",Toast.LENGTH_LONG).show();

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
}
