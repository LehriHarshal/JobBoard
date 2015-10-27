package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.barcode.Barcode;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JobCreationActivity extends Activity{

    EditText jobNameTextObject;
    EditText jobDescriptionTextObject;
    EditText startDateTextObject;
    EditText endDateTextObject;
    EditText jobLocationTextObject;

    String startDate="";
    String endDate="";


    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String latitude;
    String longitude;

    static double lat=0;
    static double lon=0;

    int clicked_button_id;
    int year,day,month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_creation_view);
        setDate();
        jobNameTextObject = (EditText) findViewById(R.id.creationName);
        jobDescriptionTextObject = (EditText) findViewById(R.id.creationDescription);
    }

    protected  void setDate()
    {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        String current_date = month+"/"+day+"/"+year;
        Button start_date = (Button)findViewById(R.id.creation_START_Date_Button);
        start_date.setText(current_date);
        Button end_date = (Button)findViewById(R.id.creation_END_Date_Button);
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

    //go to the sign up screen
    public void submitJob(View view) {
        String jobName = jobNameTextObject.getText().toString().trim();
        String jobDescription = jobDescriptionTextObject.getText().toString().trim();
        //endDateTextObject.getText().toString().trim();


        //all fields must be filled in for the sign up to work


        // By Chirag M. Shah
        FieldToCheck fieldToCheck_obj=new FieldToCheck();
        int wrong_count=0;
        wrong_count=fieldToCheck_obj.checkField(this, jobName,wrong_count);
        if(wrong_count!=0)
            return;
        wrong_count=fieldToCheck_obj.checkField(this, jobDescription,wrong_count);
        if(wrong_count!=0)
            return;
        Log.v("Start date", startDate + " End Date " + endDate);
        wrong_count=fieldToCheck_obj.checkField(this, startDate,wrong_count);
        if(wrong_count!=0)
            return;
        wrong_count=fieldToCheck_obj.checkField(this, endDate,wrong_count);
        if(wrong_count!=0)
            return;
        //displays the fieldErrors using Toast (taught in HW2)
        // This is shifted ot the Field to check method.




        final Job newJob = new Job(jobName, jobDescription, startDate, endDate,Double.toString(lat),Double.toString(lon));
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
    public  void getLocation(View view)
    {
        Log.v("Here","Here");
        Intent intent = new Intent(this, MapsActivity.class);
        //intent.putExtra("Latitude",latitude);
        //intent.putExtra("Longitude", longitude);
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

    public Dialog openDateDialog(View view)
    {
        Button b = (Button)view;
        clicked_button_id = b.getId();
        DatePickerDialog d = new DatePickerDialog(this,dateListener,year,month,day);
        d.show();
        return d;
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

        public  void onDateSet(DatePicker v, int year, int month , int day) {

            if(clicked_button_id == R.id.creation_START_Date_Button) {
                startDate = month+"/"+day+"/"+year;
                Button b = (Button)findViewById(clicked_button_id);
                b.setText(startDate);
            }
            else
            {
                endDate = month+"/"+day+"/"+year;
                Button b = (Button)findViewById(clicked_button_id);
                b.setText(endDate);
            }


        }
    };




}
