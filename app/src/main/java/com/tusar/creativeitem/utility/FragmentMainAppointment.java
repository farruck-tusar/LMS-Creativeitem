package com.tusar.creativeitem.utility;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import com.tusar.creativeitem.AppointmentActivity;
import com.tusar.creativeitem.R;
import com.tusar.creativeitem.helper.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FragmentMainAppointment extends Fragment {
    View view;
    private DatabaseHandler db;
    String chamber_id,timestamp;
    ProgressDialog pDialog;
    private ListView list;
    private TextView tvAppointment;

    ArrayList<String> name_array = new ArrayList<String>();
    ArrayList<String> count_array = new ArrayList<String>();
    ArrayList<String> phone_array = new ArrayList<String>();
    ArrayList<String> patient_id_array = new ArrayList<String>();
    ArrayList<String> visit_array = new ArrayList<String>();
    ArrayList<String> appointment_id_array = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        pDialog = new ProgressDialog(getActivity());
        view = inflater.inflate(R.layout.fragment_main_appointment, container, false);

        chamber_id= getArguments().getString("chamber_id");
        timestamp= getArguments().getString("timestamp");

        list=(ListView) view.findViewById(R.id.listAppointment);
        tvAppointment=(TextView) view.findViewById(R.id.tvAppointment);

        getAllAppointments(chamber_id, Long.parseLong(timestamp));
        return view;
    }

    public void getAllAppointments(final String chamber_id, final long timestamp){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConfigURL.URL + "get_appointments";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Response All Appointment: "+response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONArray jsonArray1 = jsonObject.getJSONArray("patient");
                                System.out.println(jsonArray1);
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                    String name     = jsonObject1.getString("name");
                                    String phone     = jsonObject1.getString("phone");
                                    String patient_id     = jsonObject1.getString("patient_id");
                                    count_array.add(String.valueOf(i+1));
                                    name_array.add(name);
                                    phone_array.add(phone);
                                    patient_id_array.add(patient_id);
                                }
                                String visit = jsonObject.getString("is_visited");
                                String appointment_id = jsonObject.getString("appointment_id");
                                visit_array.add(visit);
                                appointment_id_array.add(appointment_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (name_array.size()== 0){
                            tvAppointment.setVisibility(View.VISIBLE);
                            hideDialog();
                        }
                        else
                        {
                            System.out.println("count :"+count_array);
                            CustomAppointmentList adapter = new CustomAppointmentList(getActivity(), name_array,count_array, phone_array, visit_array,patient_id_array,appointment_id_array);
                            list.setAdapter(adapter);
                            hideDialog();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error Toast

                hideDialog();
                Toast.makeText(getActivity(),"Check Internet Connection or Response Error!",Toast.LENGTH_SHORT).show();
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
                System.out.println("params >>> "+params);
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
