package ca.mcgill.ecse321.eventregistration;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ca.mcgill.ecse321.eventregistration.controller.EventRegistrationController;
import ca.mcgill.ecse321.eventregistration.controller.InvalidInputException;
import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Participant;
import ca.mcgill.ecse321.eventregistration.model.RegistrationManager;
import ca.mcgill.ecse321.eventregistration.persistence.PersistenceXStream;

public class MainActivity extends AppCompatActivity {

    private RegistrationManager rm = null;
    private String fileName;
    String error = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fileName = getFilesDir().getAbsolutePath() + "/eventregistration.xml";
        rm = PersistenceXStream.initializeModelManager(fileName);
        refreshData();
    }

    private void refreshData() {
        TextView tv = (TextView) findViewById(R.id.newparticipant_name);
        TextView tvEvent = (TextView) findViewById(R.id.newevent_name);
        tv.setText("");
        tvEvent.setText("");

        // Initialize the data in the participant spinner
        Spinner spinnerParticipant = (Spinner) findViewById(R.id.participantspinner);
        Spinner spinnerEvent = (Spinner) findViewById(R.id.eventspinner);
        ArrayAdapter<CharSequence> participantAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        participantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> eventAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (Participant p: rm.getParticipants() ) {
            participantAdapter.add(p.getName());
        }
        spinnerParticipant.setAdapter(participantAdapter);

        for (Event e: rm.getEvents() ) {
            eventAdapter.add(e.getName());
        }
        spinnerEvent.setAdapter(eventAdapter);

    }

    public void addParticipant(View v) {
        TextView tv = (TextView) findViewById(R.id.newparticipant_name);
        TextView tvError = (TextView) findViewById(R.id.errorMessage);
        EventRegistrationController pc = new EventRegistrationController(rm);
        try {
            pc.createParticipant(tv.getText().toString());
        } catch (InvalidInputException e) {
            error = e.getMessage();
        }
        if (tv.getText().toString().equals("")){
            tvError.setText("Participant name cannot be empty!");
            refreshData();
        }
        else {
            tvError.setText("");
        }
        refreshData();
    }

    public void addEvent (View v) {
        TextView tvName = (TextView) findViewById(R.id.newevent_name);
        TextView tvDate = (TextView) findViewById(R.id.newevent_date);
        TextView tvStartTime = (TextView) findViewById(R.id.newevent_starttime);
        TextView tvEndTime = (TextView) findViewById(R.id.newevent_endtime);
        TextView tvError = (TextView) findViewById(R.id.errorMessage);

        Bundle bundle1 = getDateFromLabel(tvDate.getText());
        Bundle bundle2 = getTimeFromLabel(tvStartTime.getText());
        Bundle bundle3 = getTimeFromLabel(tvEndTime.getText());

        Time startTime = new Time(bundle2.getInt("hour"),bundle2.getInt("minute"),0);
        Time endTime = new Time(bundle3.getInt("hour"),bundle3.getInt("minute"),0);

        java.sql.Date sqlDate = new Date(bundle1.getInt("year"),bundle1.getInt("month"),bundle1.getInt("day"));

        EventRegistrationController pc = new EventRegistrationController(rm);
        try {
            pc.createEvent(tvName.getText().toString(), sqlDate , startTime , endTime);
        } catch (InvalidInputException e) {
            error = e.getMessage();
        }
        if (tvName.getText().toString().equals("") && startTime.getTime() > endTime.getTime()){
            tvError.setText("Event name cannot be empty! Event end time cannot be before event start time!");
        }
        else if (tvName.getText().toString().equals("")){
            tvError.setText("Event name cannot be empty!");
        }
        else if (startTime.getTime() > endTime.getTime()){
            tvError.setText("Event end time cannot be before event start time!");
        }
        else{
            tvError.setText("");
        }
        refreshData();
    }

    public void register (View v) {
        Spinner spinnerParticipant = (Spinner) findViewById(R.id.participantspinner);
        Spinner spinnerEvent = (Spinner) findViewById(R.id.eventspinner);

        //Participant participant = (Participant) spinnerParticipant.getSelectedItem();
        //Event event = (Event) spinnerEvent.getSelectedItem();

        TextView tv = (TextView) findViewById(R.id.newparticipant_name);
        EventRegistrationController pc = new EventRegistrationController(rm);


        int participantIndex = spinnerParticipant.getSelectedItemPosition();
        int eventIndex = spinnerEvent.getSelectedItemPosition();
        Participant participant = rm.getParticipants().get(participantIndex);
        Event event = rm.getEvents().get(eventIndex);

        try {
            pc.register(participant,event);
        } catch (InvalidInputException e) {
            error = e.getMessage();
        }
        refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getDateFromLabel(tf.getText());
        args.putInt("id", v.getId());

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getDateFromLabel(tf.getText());
        args.putInt("id", v.getId());

        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private Bundle getTimeFromLabel(CharSequence text) {
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split(":");
        int hour = 12;
        int minute = 0;

        if (comps.length == 2) {
            hour = Integer.parseInt(comps[0]);
            minute = Integer.parseInt(comps[1]);
        }

        rtn.putInt("hour", hour);
        rtn.putInt("minute", minute);

        return rtn;
    }

    private Bundle getDateFromLabel(CharSequence text) {
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split("-");
        int day = 1;
        int month = 1;
        int year = 1;

        if (comps.length == 3) {
            day = Integer.parseInt(comps[0]);
            month = Integer.parseInt(comps[1]);
            year = Integer.parseInt(comps[2]);
        }

        rtn.putInt("day", day);
        rtn.putInt("month", month-1);
        rtn.putInt("year", year);

        return rtn;
    }

    public void setTime(int id, int h, int m) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d:%02d", h, m));
    }

    public void setDate(int id, int d, int m, int y) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d-%02d-%04d", d, m + 1, y));
    }
}
