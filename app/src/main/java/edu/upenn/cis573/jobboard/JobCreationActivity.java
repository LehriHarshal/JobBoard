package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class JobCreationActivity extends Activity {

    EditText jobNameTextObject;
    EditText jobDescriptionTextObject;
    EditText startDateTextObject;
    EditText endDateTextObject;
    //EditText jobLocationTextObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_creation_view);

        jobNameTextObject = (EditText) findViewById(R.id.creationName);
        jobDescriptionTextObject = (EditText) findViewById(R.id.creationDescription);
        startDateTextObject = (EditText) findViewById(R.id.creationStartDate);
        endDateTextObject = (EditText) findViewById(R.id.creationEndDate);
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
        String startDate = startDateTextObject.getText().toString().trim();
        String endDate = endDateTextObject.getText().toString().trim();

        //all fields must be filled in for the sign up to work
        StringBuilder signupErrors = new StringBuilder("");
        boolean fieldError = false;
        if (jobName.length() == 0) {
            signupErrors.append("Username must be 4 characters. ");
            fieldError = true;
        }
        if (jobDescription.length() == 0) {
            signupErrors.append("Password must be 4 characters. ");
            fieldError = true;
        }
        if (startDate.length() == 0) {
            signupErrors.append("You must enter an email address. ");
            fieldError = true;
        }
        if (endDate.length() == 0) {
            signupErrors.append("You must enter a phone number. ");
            fieldError = true;
        }

        //displays the fieldErrors using Toast (taught in HW2)
        if (fieldError) {
            Toast.makeText(this, signupErrors.toString(),
                    Toast.LENGTH_SHORT).show();
            //We must breakout of the signUpUser() method if errors exist
            return;
        }

        final Job newJob = new Job(jobName, jobDescription, startDate, endDate);

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
    /*private void getLocation(View view) {
        return;
    }*/


}