package com.tusar.creativeitem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.tusar.creativeitem.helper.SessionManager;
import com.tusar.creativeitem.utility.AppConfigURL;
import com.tusar.creativeitem.utility.CustomGridviewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    private NavigationView navigationView;
    private TextView chambarPickerTextView,txtName;
    private ImageView pickerArrow,imageCross;
    private boolean isPickerShown = false;
    private GridView gridview;
    private ArrayList<String> ChamberName = new ArrayList<String>();
    private DatabaseHandler db;
    private SessionManager session;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHandler(this);
        session = new SessionManager(getApplicationContext());
        //getChambers();

        HashMap<String, String> user = db.getUser();
        final String name = user.get("name");

        ArrayList<HashMap<String,String>> chamber_list = db.getAllChamber();

        findViewById(R.id.header).setVisibility(View.INVISIBLE);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        //declaration of headerview
        txtName = (TextView) headerView.findViewById(R.id.tvDrName);
        chambarPickerTextView = (TextView) headerView.findViewById(R.id.chamberPicker);
        RelativeLayout layout = (RelativeLayout) headerView.findViewById(R.id.relativeLayout);
        pickerArrow = (ImageView) headerView.findViewById(R.id.pickerArrow);

        //headerview actions
        txtName.setText(name);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });
        pickerArrow.setImageResource(R.drawable.ic_arrow_down);


        // grid view
        for(int i=0;i<chamber_list.size();i++){
            HashMap<String,String> content = new HashMap<String, String>();
            content = chamber_list.get(i);
            String chamberName = content.get("name");
            ChamberName.add(chamberName);

            String status = content.get("status");
            if(status.equals("Selected")){
                chambarPickerTextView.setText(chamberName +" Chamber");
            }
        }
        System.out.println("Tusar>> "+ChamberName);

        gridview = (GridView) findViewById(R.id.customgrid);
        //btnManage = (Button) findViewById(R.id.btnManage);
        gridview.setVisibility(View.INVISIBLE);
        //btnManage.setVisibility(View.INVISIBLE);
        gridview.setAdapter(new CustomGridviewAdapter(this, ChamberName));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //imageCross = (ImageView) view.findViewById(R.id.imageDel);
                if(parent.getCount()-1 == position){
                    // custom dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setView(R.layout.popup_addchamber);
                    builder.setTitle("Add new Chamber");
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    final EditText etChamberName = (EditText) dialog.findViewById(R.id.etChamberName);
                    final EditText etChamberAddress = (EditText) dialog.findViewById(R.id.etChamberAddress);
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
                            String Name, Address = null;
                            Name = etChamberName.getText().toString().trim();
                            Address = etChamberAddress.getText().toString().trim();
                            if (etChamberName.getText().toString().length() == 0) {
                                etChamberName.setError("Name cannot be blank");
                                return;
                            }
                            else if (etChamberAddress.getText().toString().length() == 0) {
                                etChamberAddress.setError("Address cannot be blank");
                                return;
                            }
                            else {
                                addChamber(Name,Address);
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }
                else {
                    String Slecteditem= ChamberName.get(+position);
                    HashMap<String, String> chamber = db.getChamberByName(Slecteditem);
                    String chamber_id = chamber.get("chamber_id");
                    db.updateChamber(chamber_id);
                    chambarPickerTextView.setText(Slecteditem+" Chambar");
                    toggleMenu();
                }
            }
        });

//        btnManage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageCross.setVisibility(View.VISIBLE);
//            }
//        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                final String appPackageName = getPackageName();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent home = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(home);
                        //finish();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_appointment:
                        Intent appoint = new Intent(getApplicationContext(), AppointmentActivity.class);
                        startActivity(appoint);
                        //finish();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_prescription:
                        Intent prescription = new Intent(getApplicationContext(), PrescriptionActivity.class);
                        startActivity(prescription);
                        //finish();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_patient:
                        Intent patient = new Intent(getApplicationContext(), PatientActivity.class);
                        startActivity(patient);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_billing:
                        Intent billing = new Intent(getApplicationContext(), BillingActivity.class);
                        startActivity(billing);
                        //finish();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_schedule:
                        Intent schedule = new Intent(getApplicationContext(), ScheduleActivity.class);
                        startActivity(schedule);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_profile:
                        Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profile);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_password:
                        Intent password = new Intent(getApplicationContext(), PasswordActivity.class);
                        startActivity(password);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        session.setLogIn(false);
                        db.deleteTables();
                        Toast.makeText(getApplicationContext(),"Logout Successful!", Toast.LENGTH_SHORT).show();
                        Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(logout);
                        drawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });

    } //onCreate ends here
    public void addChamber(final String name, final String address){

        RequestQueue queue = Volley.newRequestQueue(BaseActivity.this);
        String url = AppConfigURL.URL + "create_chamber";

        // get data from sqlite
        HashMap<String, String> user = db.getUser();
        final String token = user.get("token");
        final String user_id = user.get("user_id");
        final String account_id = user.get("account_id");

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
                                String status     = jsonObject.getString("status");
                                String chamber_id     = jsonObject.getString("chamber_id");
                                if(status.equals("success")){
                                    db.addChamber(chamber_id,name,address,"",account_id);

                                    finish();
                                    startActivity(getIntent());
                                    Toast.makeText(getApplicationContext(),"Add Chamber Successfully!",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"There is a problem while create new chamber.",Toast.LENGTH_SHORT).show();
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();
                params.put("auth_token", token);
                params.put("user_id", user_id);
                params.put("name", name);
                params.put("address", address);
                params.put("authenticate", "true");

                return params;

            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
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
    private void toggleMenu() {
        if (!isPickerShown) {
            pickerArrow.setImageResource(R.drawable.ic_arrow_up);
            setMenuItemsVisible(false);
            gridview.setVisibility(View.VISIBLE);
            //btnManage.setVisibility(View.VISIBLE);
            isPickerShown = true;
        } else {
            pickerArrow.setImageResource(R.drawable.ic_arrow_down);
            setMenuItemsVisible(true);
            gridview.setVisibility(View.INVISIBLE);
            //btnManage.setVisibility(View.INVISIBLE);
            isPickerShown = false;
        }
    }
    private void setMenuItemsVisible(boolean b) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); ++i) {
            menu.getItem(i).setVisible(b);
        }
    }

    public void getChambers(){

        RequestQueue queue = Volley.newRequestQueue(BaseActivity.this);
        String url = AppConfigURL.URL + "get_chambers";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Res chamber: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name     = jsonObject.getString("name");
                                ChamberName.add(name);
                            }

                            System.out.println("Chambers: "+ChamberName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error Response",Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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
        queue.add(stringRequest);
    }
}