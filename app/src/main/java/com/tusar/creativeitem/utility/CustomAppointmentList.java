package com.tusar.creativeitem.utility;

/**
 * Created by Farruck Ahmed Tusar on 20-Jun-17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import com.tusar.creativeitem.PatientActivity;
import com.tusar.creativeitem.PatientDetailsActivity;
import com.tusar.creativeitem.PrescriptionActivity;
import com.tusar.creativeitem.R;
import com.tusar.creativeitem.helper.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomAppointmentList extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> name_array = new ArrayList<String>();
    private ArrayList<String> count_array = new ArrayList<String>();
    private ArrayList<String> phone_array = new ArrayList<String>();
    private ArrayList<String> visit_array = new ArrayList<String>();
    private ArrayList<String> appointment_id_array = new ArrayList<String>();
    private ArrayList<String> patient_id_array = new ArrayList<String>();
    private ProgressDialog pDialog;
    private DatabaseHandler db;
    String is_visited;
    CharSequence[] items;
    public CustomAppointmentList(Activity context, ArrayList<String> name_array, ArrayList<String> count_array, ArrayList<String> phone_array, ArrayList<String> visit_array, ArrayList<String> patient_id_array, ArrayList<String> appointment_id_array) {
        super(context, R.layout.list_single2, name_array);

        this.context = context;
        db = new DatabaseHandler(context);
        pDialog = new ProgressDialog(context);
        this.name_array = name_array;
        this.count_array = count_array;
        this.phone_array = phone_array;
        this.visit_array = visit_array;
        this.patient_id_array = patient_id_array;
        this.appointment_id_array = appointment_id_array;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single2, null, true);

        TextView txt = (TextView) rowView.findViewById(R.id.txt1);
        TextView txtName = (TextView) rowView.findViewById(R.id.txt2);
        TextView txtPhone = (TextView) rowView.findViewById(R.id.txt3);
        final ImageButton imgMore = (ImageButton) rowView.findViewById(R.id.imgMore);
        final ImageButton imgCheck = (ImageButton) rowView.findViewById(R.id.imgCheck);

        txt.setText(count_array.get(position));
        txtName.setText(name_array.get(position));
        txtPhone.setText(phone_array.get(position));
        if(visit_array.get(position).equals("1")){
            imgCheck.setImageResource(R.drawable.ic_check_circle_active);
        }

        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visit_array.get(position).equals("1")){
                    items = new CharSequence[]{"Manage Profile", "Manage Prescription", "Mark as not visited"};
                }
                else{
                    items = new CharSequence[]{"Manage Profile", "Manage Prescription", "Mark as visited"};
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if(item == 0) {
                            Intent i = new Intent(context, PatientDetailsActivity.class);
                            i.putExtra("name", name_array.get(position));
                            i.putExtra("patient_id", patient_id_array.get(position));
                            context.startActivity(i);
                        } else if(item == 1) {
                            Intent ii = new Intent(context, PrescriptionActivity.class);
                            ii.putExtra("condition","tab2");
                            ii.putExtra("patient_id", patient_id_array.get(position));
                            context.startActivity(ii);

                        } else if(item == 2) {
                            if(visit_array.get(position).equals("1")){
                                is_visited = "0";
                            }
                            else {
                                is_visited = "1";
                            }
                            update_appointment_visit_status(appointment_id_array.get(position),is_visited);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                dialog.show();


//                //Creating the instance of PopupMenu
//                PopupMenu popup = new PopupMenu(context, imgMore);
//                //Inflating the Popup using xml file
//                popup.getMenuInflater().inflate(R.menu.popup_memu, popup.getMenu());
//
//                //registering popup with OnMenuItemClickListener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(context,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
//
//                popup.show();//showing popup menu
            }
        });
        return rowView;
    }
    public void update_appointment_visit_status(final String appointment_id, final String is_visited){

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = AppConfigURL.URL + "update_appointment_visit_status";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Response: "+response);
                        if(response.equals("success")){
                            Intent ii = new Intent (getContext(), AppointmentActivity.class);
                            context.startActivity(ii);
                            Toast.makeText(getContext(),"Visit status updated successfully.",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(),"There is a problem while updating",Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error Response",Toast.LENGTH_LONG).show();
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
                params.put("appointment_id", appointment_id);
                params.put("is_visited", is_visited);
                params.put("authenticate", "true");
                System.out.println("params >>> "+params);
                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
