package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JobCreationActivity extends Activity {

    static double lat = 0;
    static double lon = 0;
    public int numberselected = 0;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    EditText jobNameTextObject;
    EditText jobDescriptionTextObject;
    EditText startDateTextObject;
    EditText endDateTextObject;
    EditText jobLocationTextObject;
    Spinner typeDescriptionInt;
    String startDate = "";
    String endDate = "";
    String latitude;
    String longitude;
    int clicked_button_id;
    int year, day, month;
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker v, int year, int month, int day) {

            if (clicked_button_id == R.id.creation_START_Date_Button) {
                startDate = (month + 1) + "" + "/" + day + "/" + year;
                Button b = (Button) findViewById(clicked_button_id);
                b.setText(startDate);
            } else {
                endDate = (month + 1) + "" + "/" + day + "/" + year;
                Button b = (Button) findViewById(clicked_button_id);
                b.setText(endDate);
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_creation_view);
        setDate();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.TypeDeclarations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        typeDescriptionInt = (Spinner) findViewById(R.id.spinner);
        jobNameTextObject = (EditText) findViewById(R.id.creationName);
        jobDescriptionTextObject = (EditText) findViewById(R.id.creationDescription);

    }

    protected void setDate() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH) + 1;
        String current_date = month + "/" + day + "/" + year;
        Button start_date = (Button) findViewById(R.id.creation_START_Date_Button);
        start_date.setText(current_date);
        Button end_date = (Button) findViewById(R.id.creation_END_Date_Button);
        end_date.setText(current_date);
        startDate = current_date;
        endDate = current_date;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_creation, menu);
        return true;
    }



    //go to the sign up screen
    public void submitJob(View view) {
        String jobName = jobNameTextObject.getText().toString().trim();
        String jobDescription = jobDescriptionTextObject.getText().toString().trim();
        String typeDescription = typeDescriptionInt.getSelectedItem().toString().trim();
        //endDateTextObject.getText().toString().trim();


        //all fields must be filled in for the sign up to work


        FieldToCheck fieldToCheck_obj = new FieldToCheck();
        int wrong_count = 0;
        wrong_count = fieldToCheck_obj.checkField(this, jobName, wrong_count);
        if (wrong_count != 0)
            return;
        wrong_count = fieldToCheck_obj.checkField(this, jobDescription, wrong_count);
        if (wrong_count != 0)
            return;
        Log.v("Start date", startDate + " End Date " + endDate);
        wrong_count = fieldToCheck_obj.checkField(this, startDate, wrong_count);
        if (wrong_count != 0)
            return;
        wrong_count = fieldToCheck_obj.checkField(this, endDate, wrong_count);
        if (wrong_count != 0)
            return;

        //displays the fieldErrors using Toast (taught in HW2)
        // This is shifted ot the Field to check method.


       /* Criteria criteria = new Criteria();
        try {
            if (criteria != null) {
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null) {
                    latitude = Double.toString(location.getLatitude());
                    longitude = Double.toString(location.getLongitude());

                }
            }
        } catch (SecurityException s) {
            Toast.makeText(getApplicationContext(), "Kindly Switch on Location Settings", Toast.LENGTH_SHORT);
        }*/


        final Job newJob = new Job(jobName, jobDescription, startDate, endDate, Double.toString(lat), Double.toString(lon), typeDescription);
        //Job jObj=new Job(latitude,longitude);


        //Ensures all users can see jobs posted by other users
        ParseACL acl = newJob.getACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);

        newJob.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //if e is null, no errors, so retrieve jobID program
                    // send the new job's job Id to addJobToMyPostedJobs
                    String jobId = newJob.getObjectId();
                    addJobToMyPostedJobs(jobId);
                }
            }
        });


        Intent intent = new Intent(this, MyPostedJobsActivity.class);
        startActivity(intent);
    }

    // Call back for the Location
    public void getLocation(View view) {
        Log.v("Here", "Here");
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Latitude", latitude);
        intent.putExtra("Longitude", longitude);
        startActivity(intent);
    }

    // add the user id of the new job to a "MyPostedJobs"
    private void addJobToMyPostedJobs(String jobId) {
        //Updates myPostedJobs list in the User
        List<String> myPostedJobs = ParseUser.getCurrentUser().getList("myPostedJobs");
        if (myPostedJobs == null) {
            ParseUser.getCurrentUser().put("myPostedJobs", new ArrayList<String>());
            myPostedJobs = ParseUser.getCurrentUser().getList("myPostedJobs");
        }

        myPostedJobs.add(jobId);
        ParseUser.getCurrentUser().put("myPostedJobs", myPostedJobs);
        ParseUser.getCurrentUser().saveInBackground();

        return;
    }

    //private void getLocation(View view) {
    //    return;
    //}

    public Dialog openDateDialog(View view) {
        Button b = (Button) view;
        clicked_button_id = b.getId();
        DatePickerDialog d = new DatePickerDialog(this, dateListener, year, month, day);
        d.show();
        return d;
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            //       Object store_selection =  parent.getItemAtPosition(pos);
            //     Log.v("ANUPAM",store_selection.toString());

            JobCreationActivity.this.numberselected = pos + 1;


        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.action_logout){
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public  void logoutUser() {
        //Parse method to log out by removing CurrentUser
        ParseUser.logOut();
        Intent intent = new Intent(JobCreationActivity.this, CurrentUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
