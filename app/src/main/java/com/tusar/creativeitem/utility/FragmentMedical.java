package com.tusar.creativeitem.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tusar.creativeitem.R;
import com.tusar.creativeitem.helper.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentMedical extends Fragment {

    View view;
    private DatabaseHandler db;
    String patient_id;
    TextView tvBlood,tvHeight,tvWeight,tvBmi,tvBp,tvPulse,tvRespiration,tvAllergy;
    ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_medical, container, false);
        db = new DatabaseHandler(getActivity());
        pDialog = new ProgressDialog(getActivity());

        tvBlood = (TextView) view.findViewById(R.id.tvBlood);
        tvHeight = (TextView) view.findViewById(R.id.tvHeight);
        tvWeight = (TextView) view.findViewById(R.id.tvWeight);
        tvBmi = (TextView) view.findViewById(R.id.tvBmi);
        tvBp = (TextView) view.findViewById(R.id.tvBp);
        tvPulse = (TextView) view.findViewById(R.id.tvPulse);
        tvRespiration = (TextView) view.findViewById(R.id.tvRespiration);
        tvAllergy = (TextView) view.findViewById(R.id.tvAllergy);

        patient_id= getArguments().getString("patient_id");

        getMedicalInfo();
        // Inflate the layout for this fragment
        return view;
    }
    public void getMedicalInfo(){
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
                                JSONArray jsonArray1 = jsonObject.getJSONArray("medical_info");
                                System.out.println(jsonArray1);
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                    String blood_group     = jsonObject1.getString("blood_group");
                                    String height     = jsonObject1.getString("height");
                                    String weight     = jsonObject1.getString("weight");
                                    String blood_pressure     = jsonObject1.getString("blood_pressure");
                                    String pulse     = jsonObject1.getString("pulse");
                                    String bmi="";
                                    String respiration     = jsonObject1.getString("respiration");
                                    String allergy     = jsonObject1.getString("allergy");
                                    String diet     = jsonObject1.getString("diet");

                                    if(blood_group.equals("")){
                                        tvBlood.setText("N/A");
                                    }
                                    else {
                                        tvBlood.setText(blood_group);
                                    }
                                    if(height.equals("")){
                                        tvHeight.setText("N/A");
                                    }
                                    else {
                                        tvHeight.setText(height);
                                    }
                                    if(weight.equals("")){
                                        tvWeight.setText("N/A");
                                    }
                                    else {
                                        tvWeight.setText(weight);
                                    }
                                    if(blood_pressure.equals("")){
                                        tvBp.setText("N/A");
                                    }
                                    else {
                                        tvBp.setText(blood_pressure);
                                    }
                                    if(bmi.equals("")){
                                        tvBmi.setText("N/A");
                                    }
                                    else {
                                        tvBmi.setText(bmi);
                                    }
                                    if(pulse.equals("")){
                                        tvPulse.setText("N/A");
                                    }
                                    else {
                                        tvPulse.setText(pulse);
                                    }
                                    if(respiration.equals("")){
                                        tvRespiration.setText("N/A");
                                    }
                                    else {
                                        tvRespiration.setText(respiration);
                                    }
                                    if(allergy.equals("")){
                                        tvAllergy.setText("N/A");
                                    }
                                    else {
                                        tvAllergy.setText(allergy);
                                    }
                                    hideDialog();
                                }

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
