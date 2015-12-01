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
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ViewRequestorActivity extends BottomMenu {

    public RatingBar ratingBar;
    boolean isJobDoer = false;
    boolean isComplete = false;
    UserJobData u = new UserJobData();
    Job job;
    AlertDialog.Builder builder;
    String userPhone;

    int REQUEST_CODE_VENMO_APP_SWITCH = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requestor);
        super.init();
        super.enable("Home");

        CharSequence ratings[] = new CharSequence[]{"*", "* *", "* * *", "* * * *", "* * * * *"};



        LinearLayout l = (LinearLayout)getLayoutInflater().inflate(R.layout.ratings_job,null);
        ratingBar = (RatingBar)l.findViewById(R.id.ratingBar_Job);
        //float r=ratingBar.getRating();
        ratingBar.setNumStars(5);
        //addListenerOnRatingBar();
        ratingBar.setMax(5);

        //ratingBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //ratingBar.setOnRatingBarChangeListener(new RatingBar.addListenerOnRatingBar()){
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(final RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                float r = ratingBar.getRating();
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.getInBackground(u.userId, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser o, ParseException e) {
                        float r = ratingBar.getRating();
                        if (r == 0) {
                            Log.v("DEBUG:", "Bad rating.");
                            return;
                        }

                        Double oldRating = Double.parseDouble(o.get("userRating").toString());

                        //update the rating to be the average of old ratings and new rating
                        oldRating = (oldRating + r + 1.0) / 2;
                        o.put("userRating", oldRating.toString());
                        try {
                            o.save();
                        }catch (Exception exc)
                        {

                        }

                        Log.v("HERE WE ARE RATING",oldRating+"");
                        //builder.setCancelable(true);
                        if (VenmoLibrary.isVenmoInstalled(getApplicationContext())) {
                            Intent venmoIntent = VenmoLibrary.openVenmoPayment("3164", "Mobile JobBoard", userPhone, "0", u.jobName, "pay");
                            startActivityForResult(venmoIntent, REQUEST_CODE_VENMO_APP_SWITCH);
                        } else {
                            setContentView(R.layout.venmo_webview);
                            WebView myWebView = (WebView) findViewById(R.id.venmo_wv);
                            myWebView.loadUrl("http://www.venmo.com");

                        }

                    }
                });

           /* {
            @Override
            public void onClick(RatingBar ratingBar, final int rating) {
                //Query Parse for the user that requested the job, so we can display their name
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.getInBackground(u.userId, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser o, ParseException e) {
                        float r=ratingBar.getRating();
                        Double oldRating = Double.parseDouble(o.get("userRating").toString());

                        //update the rating to be the average of old ratings and new rating
                        oldRating = (oldRating + rating + 1.0)/2;
                        o.put("userRating", oldRating.toString());
                        o.saveInBackground();
                    }
                });*/

                /*if (r == 0) {
                    Log.v("DEBUG:", "Bad rating.");
                }
                return;*/
            }

        });

        builder = new AlertDialog.Builder(this);
        builder.setTitle("How would you rate this job?");
        builder.setView(l);
        builder.setCancelable(true);




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

        builder.show();
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



    //button logic to go to the homepage screen
    /*public void displayHomepage(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }*/
    public void addListenerOnRatingBar() {

        //ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar= new  RatingBar(this);


        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {



            }
        });
    }
}