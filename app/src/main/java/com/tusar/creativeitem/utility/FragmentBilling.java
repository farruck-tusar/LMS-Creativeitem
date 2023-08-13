package com.tusar.creativeitem.utility;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
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
import com.tusar.creativeitem.BillingDetailsActivity;
import com.tusar.creativeitem.PrescriptionDetailsActivity;
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

public class FragmentBilling extends Fragment {
    View view;
    private DatabaseHandler db;
    String patient_id;
    ProgressDialog pDialog;
    private ListView list;
    private TextView tvbilling;

    ArrayList<String> date_array = new ArrayList<String>();
    ArrayList<String> charge_array = new ArrayList<String>();
    ArrayList<String> invoice_id_array = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        pDialog = new ProgressDialog(getActivity());
        view = inflater.inflate(R.layout.fragment_billing, container, false);

        patient_id= getArguments().getString("patient_id");

        getBillingInfo(patient_id);

        list=(ListView) view.findViewById(R.id.listBilling);
        tvbilling=(TextView) view.findViewById(R.id.tvbilling);
        // Inflate the layout for this fragment
        return view;
    }

    public void getBillingInfo(final String patient_id){
        pDialog.setMessage("Loading...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConfigURL.URL + "get_invoices_of_patient";
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

                                String charge = jsonObject.getString("charge");
                                String invoice_id = jsonObject.getString("invoice_id");
                                long timestamp     = Long.parseLong(jsonObject.getString("timestamp"));
                                String date = getDate(timestamp*1000);

                                date_array.add(date);
                                charge_array.add(charge + "৳");
                                invoice_id_array.add(invoice_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (date_array.size()== 0){
                            tvbilling.setVisibility(View.VISIBLE);
                            hideDialog();
                        }
                        else {
                            CustomBillingList adapter = new
                                    CustomBillingList(getActivity(), date_array, charge_array);
                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    Intent i = new Intent(getActivity(), BillingDetailsActivity.class);
                                    i.putExtra("invoice_id", invoice_id_array.get(position));
                                    startActivity(i);
                                }
                            });
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
    private String getDate(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
