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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ViewRequestorActivity extends BottomMenu {


    boolean isJobDoer = false;
    boolean isComplete = false;
    UserJobData u = new UserJobData();
    Job job;

    String userPhone;

    int REQUEST_CODE_VENMO_APP_SWITCH = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requestor);
        super.init();
        super.enable("Home");
        Intent intent = getIntent();
        u.userId = intent.getStringExtra("userID");
        u.jobId = intent.getStringExtra("jobID");
        displayUserInfoParse();
    }

    public void displayUserInfoParse() {

        //Query the job
        ParseQuery<Job> query = new ParseQuery("Job");
        try {
            job = (Job) query.get(u.jobId);
            u.jobName = job.getString("jobName");

            if (u.userId.equals(job.get("jobDoer"))) {
                isJobDoer = true;
            }

            u.jobStatus = job.getString("jobStatus");
            if (u.jobStatus.equals("completed")) {
                isComplete = true;
            }

            if (u.jobStatus.equals("inProgress")) {
                TextView buttonID = (TextView) findViewById(R.id.moveForwardButton);
                buttonID.setText("JOB IN PROGRESS");
            }

        } catch (ParseException e) {
            Log.v("Parse Exception:", "While trying to get job");
        }

        if (isComplete){
            //Make a check mark show up as well
            TextView payNow = (TextView) findViewById(R.id.moveForwardButton);
            payNow.setText("Pay Now");
        }



        //Query Parse for the user that requested the job, so we can display their name
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(u.userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser o, ParseException e) {
                /*final String userNameParse = o.getUsername();*/
                String userName = o.getUsername();
                TextView username = (TextView) findViewById(R.id.userNameDetails);
                username.setText(userName);

                TextView jobTitle = (TextView) findViewById(R.id.title);
                jobTitle.setText(u.jobName);

                String userEmail;
                if (isJobDoer) {
                    userEmail = o.getString("email");
                    userPhone = o.getString("phone");
                } else {
                    userEmail = "**********";
                    userPhone = "**********";
                }

                TextView email = (TextView) findViewById(R.id.emailDetails);
                email.setText(userEmail);

                TextView phone = (TextView) findViewById(R.id.phoneDetail);
                phone.setText(userPhone);

            }
        });
    }

    public void moveForward(View view) {
        if (isComplete) {
            payJobDoer();
        } else if (u.jobStatus.equals("inProgress")) {

            return;
        } else {
            selectAsJobDoer();
        }
    }

    public void payJobDoer() {
        //Before paying the completer, rate how they did
        CharSequence ratings[] = new CharSequence[] {"*", "* *", "* * *", "* * * *", "* * * * *"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How would you rate this job?");
        builder.setItems(ratings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int rating) {
                //Query Parse for the user that requested the job, so we can display their name
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.getInBackground(u.userId, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser o, ParseException e) {
                        Double oldRating = Double.parseDouble(o.get("userRating").toString());

                        //update the rating to be the average of old ratings and new rating
                        oldRating = (oldRating + rating + 1.0)/2;
                        o.put("userRating", oldRating.toString());
                        o.saveInBackground();
                    }
                });

                if (rating == 0) {
                    Log.v("DEBUG:", "Bad rating.");
                }
                return;
            }
        });
        builder.show();


        Intent venmoIntent = VenmoLibrary.openVenmoPayment("2590", "Job Board", userPhone, "0", u.jobName, "pay");
        startActivityForResult(venmoIntent, REQUEST_CODE_VENMO_APP_SWITCH);

    }


    public void selectAsJobDoer() {
        //update the Job object
        job.put("jobDoer", u.userId);
        job.put("jobStatus", "inProgress");
        job.saveInBackground();
        isJobDoer = true;

        //Display the info
        displayUserInfoParse();
        TextView buttonText = (TextView) findViewById(R.id.moveForwardButton);
        buttonText.setText("In Progress.");

        String jobName = job.getString("jobName");
        String notification = "You have been selected to complete " + jobName;
        NotificationsManager.notifyUser(u.userId, notification);
        removeOtherRequesters();

    }

    private void removeOtherRequesters() {
        ParseQuery<Job> query = new ParseQuery("Job");
        try {
            job = (Job) query.get(u.jobId);
            List<String> jobRequestor = new ArrayList<String>();
            jobRequestor.add(u.userId);
            job.put("jobRequestors", jobRequestor);
        } catch (ParseException e) {
            Log.v("Parse Exception:", "While trying to get job");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_requestor, menu);
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

    //button logic to go to the homepage screen
    /*public void displayHomepage(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }*/

}