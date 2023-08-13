package com.tusar.creativeitem;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tusar.creativeitem.helper.DatabaseHandler;
import com.tusar.creativeitem.utility.CustomPatientList;
import com.tusar.creativeitem.utility.CustomScheduleList;
import com.tusar.creativeitem.utility.HintSpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleActivity extends BaseActivity {
    private com.github.clans.fab.FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab3;
    private com.github.clans.fab.FloatingActionButton fab4;

    private DatabaseHandler db;
    private ListView list;
    private String time;

    ArrayList<String> day_array = new ArrayList<String>();
    ArrayList<String> time_array = new ArrayList<String>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_schedule, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(5).setChecked(true);

        db = new DatabaseHandler(this);
        menuRed = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.menu_red);
        fab1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab4);

        menuRed.setClosedOnTouchOutside(true);


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScheduleActivity.this, NewAppointment.class);
                startActivity(i);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScheduleActivity.this, PrescriptionActivity.class);
                startActivity(i);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScheduleActivity.this, NewPatient.class);
                startActivity(i);
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScheduleActivity.this, NewBilling.class);
                startActivity(i);
            }
        });

        ArrayList<HashMap<String,String>> chamber_list;
        chamber_list = db.getAllChamber();

        for(int i=0;i<chamber_list.size();i++){
            HashMap<String,String> content = new HashMap<String, String>();
            content = chamber_list.get(i);
            String status = content.get("status");
            if(status.equals("Selected")){
                String chamber_id = content.get("chamber_id");

                ArrayList<HashMap<String,String>> schedule_list = db.getScheduleById(chamber_id);
                if(schedule_list.size()==0){
                    TextView tvSchedule = (TextView) findViewById(R.id.tvSchedule);
                    tvSchedule.setText("Not Scheduled yet");
                    tvSchedule.setVisibility(View.VISIBLE);

                }
                for(int j=0;j<schedule_list.size();j++){
                    HashMap<String,String> content1 = new HashMap<String, String>();
                    content1 = schedule_list.get(j);
                    System.out.println("Check ....."+content1);
                    String day = content1.get("day");
                    day_array.add(day);

                    String status1 = content1.get("status");
                    if(status1.equals("closed")){
                        time_array.add("Closed");
                    }
                    else {
                        String morning = content1.get("morning");
                        String morning_open = content1.get("morning_open");
                        String morning_close = content1.get("morning_close");

                        String afternoon = content1.get("afternoon");
                        String afternoon_open = content1.get("afternoon_open");
                        String afternoon_close = content1.get("afternoon_close");

                        String evening = content1.get("evening");
                        String evening_open = content1.get("evening_open");
                        String evening_close = content1.get("evening_close");

                        if(morning != "" && afternoon != "" && evening != ""){
                            time = morning + "\n" + afternoon + "\n" + evening;
                        }

                        time_array.add(time);
                    }
                }
            }
        }
        //listview
        CustomScheduleList adapter = new
                CustomScheduleList(ScheduleActivity.this, day_array, time_array);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // custom dialog
                TextView v = (TextView) view.findViewById(R.id.txt1);
                //Toast.makeText(getApplicationContext(), "selected Item Name is " + v.getText(), Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
                builder.setView(R.layout.popup_schedule);
                builder.setTitle("Select " + v.getText() + " schedule");
                final AlertDialog dialog = builder.create();
                dialog.show();

                Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
                Button buttonConfirm = (Button) dialog.findViewById(R.id.buttonConfirm);

                Spinner spinner1 = (Spinner) dialog.findViewById(R.id.spinner1);
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(ScheduleActivity.this,
                        R.array.morning, android.R.layout.simple_spinner_item);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(new HintSpinnerAdapter(adapter1, R.layout.hint_row_item3, ScheduleActivity.this));

                Spinner spinner2 = (Spinner) dialog.findViewById(R.id.spinner2);
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(ScheduleActivity.this,
                        R.array.morning, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(new HintSpinnerAdapter(adapter2, R.layout.hint_row_item3, ScheduleActivity.this));

                Spinner spinner3 = (Spinner) dialog.findViewById(R.id.spinner3);
                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(ScheduleActivity.this,
                        R.array.afternoon, android.R.layout.simple_spinner_item);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(new HintSpinnerAdapter(adapter3, R.layout.hint_row_item3, ScheduleActivity.this));

                Spinner spinner4 = (Spinner) dialog.findViewById(R.id.spinner4);
                ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(ScheduleActivity.this,
                        R.array.afternoon, android.R.layout.simple_spinner_item);
                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner4.setAdapter(new HintSpinnerAdapter(adapter4, R.layout.hint_row_item3, ScheduleActivity.this));

                Spinner spinner5 = (Spinner) dialog.findViewById(R.id.spinner5);
                ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(ScheduleActivity.this,
                        R.array.evening, android.R.layout.simple_spinner_item);
                adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner5.setAdapter(new HintSpinnerAdapter(adapter5, R.layout.hint_row_item3, ScheduleActivity.this));

                Spinner spinner6 = (Spinner) dialog.findViewById(R.id.spinner6);
                ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(ScheduleActivity.this,
                        R.array.evening, android.R.layout.simple_spinner_item);
                adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner6.setAdapter(new HintSpinnerAdapter(adapter6, R.layout.hint_row_item3, ScheduleActivity.this));

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                dialog.show();
            }
        });
    }
}
