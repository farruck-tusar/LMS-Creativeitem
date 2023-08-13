package com.tusar.creativeitem.utility;
/**
 * Created by Farruck Ahmed Tusar on 11-Jul-17.
 */
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.tusar.creativeitem.R;
import com.tusar.creativeitem.helper.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FragmentAppointment extends Fragment {
    View view;
    private DatabaseHandler db;
    private String patient_id;
    ProgressDialog pDialog;
    private TextView tvAppoint1;
    private ListView list;

    ArrayList<String> date_array = new ArrayList<String>();
    ArrayList<String> visit_array = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        pDialog = new ProgressDialog(getActivity());
        view = inflater.inflate(R.layout.fragment_appointment, container, false);

        patient_id= getArguments().getString("patient_id");
        getAppointment(patient_id);
        list=(ListView) view.findViewById(R.id.listAppointment);
        tvAppoint1=(TextView) view.findViewById(R.id.tvAppoint1);
        return  view;
    }
    public void getAppointment(final String patient_id){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConfigURL.URL + "get_appointment_history_of_patient";
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
                                String visit = jsonObject.getString("is_visited");

                                date_array.add(date);
                                visit_array.add(visit);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (date_array.size()== 0){
                            tvAppoint1.setVisibility(View.VISIBLE);
                            hideDialog();
                        }
                        else{
                            CustomAppointmentList1 adapter = new
                                    CustomAppointmentList1(getActivity(), date_array, visit_array);
                            list.setAdapter(adapter);

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
        String date = android.text.format.DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
