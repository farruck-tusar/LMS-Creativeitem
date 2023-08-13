package com.tusar.creativeitem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tusar.creativeitem.R.id.etDate;
import static com.tusar.creativeitem.R.id.etSymptoms;


public class PrescriptionActivity extends BaseActivity{


    private TableLayout tableLayout, tableLayout1, tableLayout2, tableLayout3,tableLayoutMedicine,tableLayoutTest;
    private DatabaseHandler db;
    private EditText etPatName,etMobile,etAge,etSymptoms,etDiagnosis,etSymptoms1, etDiagnosis1;
    private Spinner spinner,spinner1;
    private ArrayList<String> nameList = new ArrayList<String>();
    private ArrayAdapter<String> dataAdapter;
    private String pat_name= null;
    private String chamber_id;
    private TabHost host;
    private String p_id="0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_prescription, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("New Patient");
        host.addTab(spec);
        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Old Patient");
        host.addTab(spec);

        for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
            host.getTabWidget().getChildAt(i).getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }


        db = new DatabaseHandler(this);


        //For tab2
        getPatients();
        spinner1 = (Spinner) findViewById(R.id.spinnerPatient);
        nameList.add("<Select a Patient>");
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(new HintSpinnerAdapter(
                dataAdapter, R.layout.hint_row_item2, this));



        ArrayList<HashMap<String,String>> chamber_list = db.getAllChamber();
        for(int i=0;i<chamber_list.size();i++){
            HashMap<String,String> content = new HashMap<String, String>();
            content = chamber_list.get(i);
            String status = content.get("status");
            if(status.equals("Selected")){
                chamber_id = content.get("chamber_id");
            }
        }

        Intent intent = getIntent();
        String condition = intent.getStringExtra("condition");
        String patient_id = intent.getStringExtra("patient_id");
        if(condition != null && !condition .isEmpty()){
            host.setCurrentTab(1);
            System.out.println("Pat id :"+patient_id);
            getPatientInfo(patient_id);
        }



        //tab1
        etPatName = (EditText) findViewById(R.id.etPatName);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etAge = (EditText) findViewById(R.id.etAge);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(new HintSpinnerAdapter(
                adapter, R.layout.hint_row_item, this));

        //Table View codes here
        tableLayout2=(TableLayout)findViewById(R.id.tableLayoutSymptoms);
        etSymptoms = (EditText) findViewById(R.id.etSymptoms);
        ImageButton btnSymptoms = (ImageButton) findViewById(R.id.btnSymptom);

        btnSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSymptoms.requestFocus();
            }
        });


        //Table View codes here
        tableLayout3=(TableLayout)findViewById(R.id.tableLayoutDiagnosis);
        etDiagnosis = (EditText) findViewById(R.id.etDiagnosis);
        ImageButton btnDiagnosis = (ImageButton) findViewById(R.id.btnDiagnosis);

        btnDiagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDiagnosis.requestFocus();
            }
        });

        //Table View codes here
        tableLayout=(TableLayout)findViewById(R.id.tableLayoutMedicine);
        ImageButton imgMedicine = (ImageButton) findViewById(R.id.btnMedicine);
        imgMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View tableRow = LayoutInflater.from(PrescriptionActivity.this).inflate(R.layout.table_medicine_row,null,false);

                final TextView medicineName  = (TextView) tableRow.findViewById(R.id.tvMedicineName);
                final TextView medicineDesc  = (TextView) tableRow.findViewById(R.id.tvMedicineDes);
                ImageButton delete = (ImageButton) tableRow.findViewById(R.id.btndelete);

                // custom dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PrescriptionActivity.this);
                builder.setView(R.layout.popup_addmedicine);
                builder.setTitle("Add Medicine");
                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText etMedicineName = (EditText) dialog.findViewById(R.id.etMedicineName);
                final EditText etMedicineDesc = (EditText) dialog.findViewById(R.id.etMedicineDesc);
                Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
                Button buttonConfirm = (Button) dialog.findViewById(R.id.buttonConfirm);

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Name, Desc = null;

                        Name = etMedicineName.getText().toString().trim();
                        Desc = etMedicineDesc.getText().toString().trim();

                        if (etMedicineName.getText().toString().length() == 0) {
                            etMedicineName.setError("Name cannot be blank");
                            return;
                        }
                        else if (etMedicineDesc.getText().toString().length() == 0) {
                            etMedicineDesc.setError("Description cannot be blank");
                            return;
                        }
                        else {

                            medicineName.setText(Name);
                            medicineDesc.setText(Desc);

                            tableLayout.addView(tableRow);
                            dialog.dismiss();
                        }
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tableLayout.removeView(tableRow);
                    }
                });
                dialog.show();
            }
        });

        //Table View codes here
        tableLayout1=(TableLayout)findViewById(R.id.tableLayoutTest);
        ImageButton imgTest = (ImageButton) findViewById(R.id.btnTest);
        imgTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View tableRow1 = LayoutInflater.from(PrescriptionActivity.this).inflate(R.layout.table_test_row,null,false);

                final TextView testName  = (TextView) tableRow1.findViewById(R.id.tvTestName);
                ImageButton delete1 = (ImageButton) tableRow1.findViewById(R.id.btndelete);

                // custom dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PrescriptionActivity.this);
                builder.setView(R.layout.popup_addtest);
                builder.setTitle("Add Test");
                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText etTestName = (EditText) dialog.findViewById(R.id.etTestName);
                Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
                Button buttonConfirm = (Button) dialog.findViewById(R.id.buttonConfirm);

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        etTestName.getText().toString().trim();

                        if (etTestName.getText().toString().length() == 0) {
                            etTestName.setError("Name cannot be blank");
                            return;
                        }
                        else {
                            testName.setText(etTestName.getText().toString().trim());
                            tableLayout1.addView(tableRow1);
                            dialog.dismiss();
                        }
                    }
                });
                delete1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tableLayout1.removeView(tableRow1);
                    }
                });


                dialog.show();
            }
        });







        //tab2


        etSymptoms1 = (EditText) findViewById(R.id.etSymptoms1);
        ImageButton btnSymptoms1 = (ImageButton) findViewById(R.id.btnSymptom1);

        btnSymptoms1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSymptoms1.requestFocus();
            }
        });

        etDiagnosis1 = (EditText) findViewById(R.id.etDiagnosis1);
        ImageButton btnDiagnosis1 = (ImageButton) findViewById(R.id.btnDiagnosis1);

        btnDiagnosis1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDiagnosis1.requestFocus();
            }
        });

        tableLayoutMedicine=(TableLayout)findViewById(R.id.tableLayoutMedicine1);
        ImageButton imgMedicine1 = (ImageButton) findViewById(R.id.btnMedicine1);
        imgMedicine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View tableRow = LayoutInflater.from(PrescriptionActivity.this).inflate(R.layout.table_medicine_row,null,false);

                final TextView medicineName  = (TextView) tableRow.findViewById(R.id.tvMedicineName);
                final TextView medicineDesc  = (TextView) tableRow.findViewById(R.id.tvMedicineDes);
                ImageButton delete = (ImageButton) tableRow.findViewById(R.id.btndelete);

                // custom dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PrescriptionActivity.this);
                builder.setView(R.layout.popup_addmedicine);
                builder.setTitle("Add Medicine");
                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText etMedicineName = (EditText) dialog.findViewById(R.id.etMedicineName);
                final EditText etMedicineDesc = (EditText) dialog.findViewById(R.id.etMedicineDesc);
                Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
                Button buttonConfirm = (Button) dialog.findViewById(R.id.buttonConfirm);

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Name, Desc = null;

                        Name = etMedicineName.getText().toString().trim();
                        Desc = etMedicineDesc.getText().toString().trim();

                        if (etMedicineName.getText().toString().length() == 0) {
                            etMedicineName.setError("Name cannot be blank");
                            return;
                        }
                        else if (etMedicineDesc.getText().toString().length() == 0) {
                            etMedicineDesc.setError("Description cannot be blank");
                            return;
                        }
                        else {

                            medicineName.setText(Name);
                            medicineDesc.setText(Desc);

                            tableLayoutMedicine.addView(tableRow);
                            dialog.dismiss();
                        }
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tableLayoutMedicine.removeView(tableRow);
                    }
                });
                dialog.show();
            }
        });

        //Table View codes here
        tableLayoutTest=(TableLayout)findViewById(R.id.tableLayoutTest1);
        ImageButton imgTest1 = (ImageButton) findViewById(R.id.btnTest1);
        imgTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View tableRow1 = LayoutInflater.from(PrescriptionActivity.this).inflate(R.layout.table_test_row,null,false);

                final TextView testName  = (TextView) tableRow1.findViewById(R.id.tvTestName);
                ImageButton delete1 = (ImageButton) tableRow1.findViewById(R.id.btndelete);

                // custom dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PrescriptionActivity.this);
                builder.setView(R.layout.popup_addtest);
                builder.setTitle("Add Test");
                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText etTestName = (EditText) dialog.findViewById(R.id.etTestName);
                Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
                Button buttonConfirm = (Button) dialog.findViewById(R.id.buttonConfirm);

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        etTestName.getText().toString().trim();

                        if (etTestName.getText().toString().length() == 0) {
                            etTestName.setError("Name cannot be blank");
                            return;
                        }
                        else {
                            testName.setText(etTestName.getText().toString().trim());
                            tableLayoutTest.addView(tableRow1);
                            dialog.dismiss();
                        }
                    }
                });
                delete1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tableLayoutTest.removeView(tableRow1);
                    }
                });


                dialog.show();
            }
        });

    }// onCreate ends here

    public void getPatients(){

        RequestQueue queue = Volley.newRequestQueue(PrescriptionActivity.this);
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

                                nameList.add(name);
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
    public void getPatientInfo(final String patient_id){

        RequestQueue queue = Volley.newRequestQueue(PrescriptionActivity.this);
        String url = AppConfigURL.URL + "get_basic_info_of_patient";
        // Request a string response from the provided URL.
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response

                        System.out.println("Response for Patient: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                pat_name = jsonObject.getString("name");
                                System.out.println("Test1: "+pat_name);
                            }
                            spinner1.setSelection(dataAdapter.getPosition(pat_name)+1);

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
                params.put("patient_id", patient_id);

                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void getPatients1(final String patientName){

        RequestQueue queue = Volley.newRequestQueue(PrescriptionActivity.this);
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
                                System.out.println("chk11: "+p_id);
                                if(name.equals(patientName)){
                                    p_id = patient_id;
                                    System.out.println("chk12: "+p_id);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                int current = host.getCurrentTab();
                if(String.valueOf(current).equals("0")){
                    if(etSymptoms.getText().toString().equals("")){
                        etSymptoms.setError("Field can not be blank");
                    }else if(etDiagnosis.getText().toString().equals("")){
                        etDiagnosis.setError("Field can not be blank");
                    }else if(spinner.getSelectedItem() == null){
                        Toast.makeText(getApplicationContext(),"Select Gender",Toast.LENGTH_SHORT).show();
                    }else {
                        String PatName = etPatName.getText().toString();
                        String Mobile = etMobile.getText().toString();
                        String Age = etAge.getText().toString();
                        String Gender = spinner.getSelectedItem().toString().trim();
                        String Symptoms = etSymptoms.getText().toString();
                        String Diagnosis = etDiagnosis.getText().toString();

                        createPrescriptionNew(PatName, Mobile, Age, Gender, Symptoms, Diagnosis);
                    }
                }
                else {
                    if(etSymptoms1.getText().toString().equals("")){
                        etSymptoms1.setError("Field can not be blank");
                    }else if(etDiagnosis1.getText().toString().equals("")){
                        etDiagnosis1.setError("Field can not be blank");
                    }else if(spinner1.getSelectedItem() == null){
                        Toast.makeText(getApplicationContext(),"Select Patient",Toast.LENGTH_SHORT).show();
                    }else {
                        String PatName1 = spinner1.getSelectedItem().toString().trim();
                        getPatients1(PatName1);
                        String Symptoms = etSymptoms1.getText().toString();
                        String Diagnosis = etDiagnosis1.getText().toString();

                        createPrescriptionOld(PatName1, Symptoms, Diagnosis);
                    }
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
    public void createPrescriptionNew(final String PatName, final String Mobile, final String Age, final String Gender, final String Symptoms, final String Diagnosis){

        RequestQueue queue = Volley.newRequestQueue(PrescriptionActivity.this);
        String url = AppConfigURL.URL + "create_prescription";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res_new_prescription: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String status     = jsonObject.getString("status");
                                if(status.equals("success")){
                                    Toast.makeText(getApplicationContext(),"Add Prescription Successfully!",Toast.LENGTH_SHORT).show();
                                    Intent ii = new Intent(PrescriptionActivity.this, PrescriptionActivity.class);
                                    startActivity(ii);
                                }
                                else {
                                    String reason     = jsonObject.getString("reason");
                                    Toast.makeText(getApplicationContext(),"Failed! " + reason,Toast.LENGTH_SHORT).show();
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
                params.put("chamber_id", chamber_id);
                params.put("name", PatName);
                params.put("phone", Mobile);
                params.put("symptom", Symptoms);
                params.put("diagnosis", Diagnosis);
                params.put("medicines", "Medicine");
                params.put("tests", "Test");
                params.put("authenticate", "true");
                params.put("new_patient", "yes");
                System.out.println("ParamsNewPrescription: "+params);
                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void createPrescriptionOld(final String PatName, final String Symptoms, final String Diagnosis){

        RequestQueue queue = Volley.newRequestQueue(PrescriptionActivity.this);
        String url = AppConfigURL.URL + "create_prescription";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //split to string from json response
                        System.out.println("Res_old_prescription: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String status     = jsonObject.getString("status");
                                if(status.equals("success")){
                                    Toast.makeText(getApplicationContext(),"Add Prescription Successfully!",Toast.LENGTH_SHORT).show();
                                    Intent ii = new Intent(PrescriptionActivity.this, PrescriptionActivity.class);
                                    startActivity(ii);
                                }
                                else {
                                    String reason     = jsonObject.getString("reason");
                                    Toast.makeText(getApplicationContext(),"Failed! " + reason,Toast.LENGTH_SHORT).show();
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
                params.put("chamber_id", chamber_id);
                params.put("patient_id", p_id);
                params.put("symptom", Symptoms);
                params.put("diagnosis", Diagnosis);
                params.put("medicines", "Medicine");
                params.put("tests", "Test");
                params.put("authenticate", "true");
                params.put("new_patient", "no");
                System.out.println("ParamsOLDPrescription: "+params);
                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
