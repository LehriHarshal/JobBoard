package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class JobDetailsActivity extends BottomMenu {
    Job job=new Job();
    String doer;
    String doerUsername;
    String posterID;
    boolean isJobDoer = false;
    boolean isJobPoster = false;

    String userId = ParseUser.getCurrentUser().getObjectId();
    String posterName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        super.init();
        super.enable("Home");
        Intent intent = getIntent();
        job.jobId = intent.getStringExtra("jobID");
        Log.v("Inside Job,",job+" "+job.jobId);
        Log.v("DEBUG:", "commencing.");
        //Query Parse
        ParseQuery<Job> query = new ParseQuery("Job");
        query.getInBackground(job.jobId, new GetCallback<Job>() {
            @Override
            public void done(Job o, ParseException e) {
                job = o;
                TextView jobNameTextObject = (TextView) findViewById(R.id.detailsName);
                TextView jobDescriptionTextObject = (TextView) findViewById(R.id.detailsDescription);
                TextView startDateTextObject = (TextView) findViewById(R.id.detailsStartDate);
                TextView endDateTextObject = (TextView) findViewById(R.id.detailsEndDate);
                TextView spinnerTextValue = (TextView) findViewById(R.id.spinnerTextValue);

                spinnerTextValue.setText(o.getTypeDescription());
                jobNameTextObject.setText(o.getJobName());
                jobDescriptionTextObject.setText(o.getJobDescription());
                startDateTextObject.setText(o.getStartDate());
                endDateTextObject.setText(o.getEndDate());
            }
        });


        ParseQuery<Job> query2 = new ParseQuery("Job");
        try {
            String id = job.jobId;
            job = query2.get(job.jobId);
            job.jobId = id;
            doer = job.getString("jobDoer");
            posterID = job.getString("jobPoster");
            job.jobName = job.getString("jobName");
            Log.v("Values in try block", job.jobId+" "+doer+" "+posterID+" "+job.jobName);
            if (userId.equals(posterID)) {
                isJobPoster = true;

            }
            if(doer!=null && doer.equals(userId))
            {
                isJobDoer = true;
            }
        } catch (Exception e) {
            Log.v("Parse Exception:", "While trying to get job");
        }

        TextView buttonTitle = (TextView) findViewById(R.id.Request);
        if (isJobDoer) {
            //This job belongs to them.
                buttonTitle.setText("Completed?");
                buttonTitle.setEnabled(true);
                Button sendbutton = (Button)findViewById(R.id.contactJobPosterButton);
                sendbutton.setVisibility(View.VISIBLE);

            isJobDoer();
        }
        else {
            if(isJobPoster)
            {
                if(doer == null) {
                    buttonTitle.setText("Unassigned");
                    buttonTitle.setEnabled(false);
                    Button sendbutton = (Button) findViewById(R.id.contactJobPosterButton);
                    sendbutton.setVisibility(View.INVISIBLE);
                }
            }
            else {
                buttonTitle.setText("Request Job");
                buttonTitle.setEnabled(true);
                Button sendbutton = (Button) findViewById(R.id.contactJobPosterButton);
                sendbutton.setVisibility(View.VISIBLE);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_details, menu);
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

    public void updateJob(View view) {
        if (isJobDoer) {
            jobCompleted();
        } else {
            jobRequesting();
        }
    }

    private void jobCompleted() {
        //Query Parse
        if(doer==null)
        {
            Toast.makeText(getApplicationContext(),"This Job has not been Assigned to anyone yet",Toast.LENGTH_LONG).show();
            return;
        }
        ParseQuery<Job> query = new ParseQuery("Job");
        query.getInBackground(job.jobId, new GetCallback<Job>() {
            @Override
            public void done(Job o, ParseException e) {
                o.put("jobStatus", "completed");
                o.saveInBackground();
            }
        });

        //Find the job doer.
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(doer, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser o, ParseException e) {
                String email = o.getString("email");
                TextView emailText = (TextView) findViewById(R.id.contactInfo);
                emailText.setText(email);

                String phone = o.getString("phone");
                TextView phoneText = (TextView) findViewById(R.id.userPhoneNumber);
                phoneText.setText(phone);



            }
        });

        String message = doerUsername + " has completed task: " + job.jobName;
        NotificationsManager.notifyUser(posterID, message);

        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

    private void isJobDoer() {
        //Find the job in question
        ParseQuery<Job> query = new ParseQuery("Job");
        query.getInBackground(job.jobId, new GetCallback<Job>() {
            @Override
            public void done(Job o, ParseException e) {

                //dummy value, if it was real - would still work.
                //User permissions prohibited development
                ParseGeoPoint location = new ParseGeoPoint(40.0, 75.2);
                TextView locationText = (TextView) findViewById(R.id.userLocation);
                String lat = "" + location.getLatitude();
                lat = lat.substring(0, 4);
                String longitude = "" + location.getLongitude();
                longitude = longitude.substring(0, 4);
                locationText.setText(lat + ", " + longitude);

                String posterID = o.getJobPoster();
                //Find the job poster.
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.getInBackground(userId, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser o, ParseException e) {
                        String email = o.getString("email");
                        TextView emailText = (TextView) findViewById(R.id.contactInfo);
                        emailText.setText(email);

                        String phone = o.getString("phone");
                        TextView phoneText = (TextView) findViewById(R.id.userPhoneNumber);
                        phoneText.setText(phone);

                        doerUsername = o.getString("username");
                    }
                });

            }
        });

    }

    private void jobRequesting() {
        //Allow the user to request this job
        //Update both the User and the Jobs
        addJobToMyRequested(); //current user gets this job added to requests

        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }


    private void addJobToMyRequested() {
        //Updates myRequestedJobs list in the User
        List<String> myRequestedJobs = ParseUser.getCurrentUser().getList("myRequestedJobs");
        if (myRequestedJobs == null) {
            ParseUser.getCurrentUser().put("myRequestedJobs", new ArrayList<String>());
            myRequestedJobs = ParseUser.getCurrentUser().getList("myRequestedJobs");
        }
        myRequestedJobs.add(job.jobId);
        Log.v("My Requested Jobs",myRequestedJobs.toString());
        ParseUser.getCurrentUser().saveInBackground();

        //Updates jobRequestors list in the Job
        List<String> currRequestors = job.getList("jobRequestors");
        if (currRequestors == null) {
            job.put("jobRequestors", new ArrayList<String>());
            currRequestors = job.getList("jobRequestors");
        }
        currRequestors.add(ParseUser.getCurrentUser().getObjectId());
        job.put("jobRequestors", currRequestors);
        //Log.v("DEBUG", "ADDED!");
        job.saveInBackground();
        return;
    }

    public void requestJobDetails(View view) {
        //Appends job poster email address, so the user can ask for additional job details before
        //requesting the job
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(job.getJobPoster(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser o, ParseException e) {
                String email = o.getEmail();
                TextView emailField = (TextView) findViewById(R.id.contactInfo);
                emailField.setText(email);
            }
        });

    }

    public void openMessaging(View view)
    {
        if(!isJobDoer){
            Intent intent = new Intent(this, MessagingActivity.class);
            intent.putExtra("MessageFrom",userId);
            intent.putExtra("MessageTo",posterID);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No one", Toast.LENGTH_LONG).show();
        }
    }


    public void follow(View view)
    {
        List<String> myFollowings = ParseUser.getCurrentUser().getList("myFollowings");
        if (myFollowings == null) {
            ParseUser.getCurrentUser().put("myFollowings", new ArrayList<String>());
            myFollowings = ParseUser.getCurrentUser().getList("myFollowings");
        }
        if(!myFollowings.contains(posterID))
            myFollowings.add(posterID);
        ParseUser.getCurrentUser().put("myFollowings", myFollowings);
        ParseUser.getCurrentUser().saveInBackground();
        Log.v("Following List", myFollowings.toString());
        if(myFollowings.contains(posterID))
            Toast.makeText(getApplicationContext(),"Already Following",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"Now Following",Toast.LENGTH_SHORT).show();
        return;
        /*if (followings == null)
            followings = new ArrayList<String>();
        if (followings.contains(posterID)) {
            Toast.makeText(getApplicationContext(), "Already Following", Toast.LENGTH_LONG);
            return;
        }
        followings.add(posterID);


        Log.v("Followings",followings.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    current_user.save();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error in Following", Toast.LENGTH_LONG);
                }
                Toast.makeText(getApplication(), "Now Following", Toast.LENGTH_SHORT);
            }
        });*/


    }
    //button logic to go to the homepage screen
    /*public void displayHomepage(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }*/


}
