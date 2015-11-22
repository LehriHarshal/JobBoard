package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class JobRequestorsActivity extends BottomMenu {
    Job job;
    String jobId;
    //String jobName = "before";
    ArrayAdapter<String> requestorlistAdapter;
    List<String> requestorIds = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_requestors_list);
        super.init();
        super.enable("Home");
        Intent intent = getIntent();
        jobId = intent.getStringExtra("jobID");
        Log.v("DEBUG", "JOBID: " + jobId);

        ParseQuery<Job> query = new ParseQuery("Job");
        try {
            job = (Job) query.get(jobId);
        } catch (ParseException e) {
            Log.v("Parse Exception:", "While trying to get job");
        }

        final ArrayList<String> jobRequestors = (ArrayList<String>)job.get("jobRequestors");

        if (jobRequestors != null) {
            for (String requestor : jobRequestors) {
                Log.v("Requestors:", requestor);
                requestorIds.add(requestor);
            }
        }

        loadJobRequestors();

    }

    public void loadJobRequestors() {

        // get the list of requestors
        final ArrayList<String> userNames = new ArrayList<String>();
        final ArrayList<String> userRatings = new ArrayList<String>();

        if (requestorIds != null) {
            Log.v("DEBUG:", "our list is non null.");
            Log.v("THE LIST",requestorIds.toString());
            for (String requestor : requestorIds) {
                //Log.v("Requestors2:", requestor);
                //Query Parse for the user that requested the job, so we can display their name
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.getInBackground(requestor, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser o, ParseException e) {
                        final String username = o.getUsername();
                        final String rating = o.get("userRating").toString();
                        Log.v("Rating from Databaase",rating);
                        //Thread used to ensure list appears properly each time it is loaded
                        //Also adds each item to list
                        runOnUiThread(new Runnable() {
                            public void run() {
                                userNames.add(username);
                                Log.v("Username Added",userNames.toString());
                                //Allows rating to be shown as stars
                               if (Double.parseDouble(rating) < 1.5) {
                                    userRatings.add("*");
                                }
                                else if (Double.parseDouble(rating) < 2.5) {
                                    userRatings.add("* *");
                                }
                                else if (Double.parseDouble(rating) < 3.5) {
                                    userRatings.add("* * *");
                                }
                                else if (Double.parseDouble(rating) < 4.5) {
                                    userRatings.add("* * * *");
                                }
                                else {
                                    userRatings.add("* * * * *");
                                }

                                requestorlistAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

            final ListView requestorListview = (ListView) findViewById(R.id.requestorsList);

            requestorlistAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    userNames) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    // Must always return just a View.
                    View view = super.getView(position, convertView, parent);

                    // If you look at the android.R.layout.simple_list_item_2 source, you'll see
                    // it's a TwoLineListItem with 2 TextViews - text1 and text2.
                    //TwoLineListItem listItem = (TwoLineListItem) view;
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    text2.setTextColor(Color.parseColor("#dc4e00"));
                    text1.setTextColor(Color.parseColor("#89cede"));

                    text1.setText(userNames.get(position));
                    text1.setTextSize(25);
                    text2.setText("Rating: " + userRatings.get(position));
                    text2.setPadding(50, 0, 0, 0);
                    return view;
                }
            };

            requestorListview.setAdapter(requestorlistAdapter);
            requestorListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    //Unimplemented: Upon click, approve user
                    goToRequestorsProfile(position);
                }
            });

        }

    }

    private void goToRequestorsProfile(int position) {
        Intent intent = new Intent(this, ViewRequestorActivity.class);
        intent.putExtra("userID", requestorIds.get(position));
        intent.putExtra("jobID", jobId);
        startActivity(intent);
    }

    //go to the profile screen
    /*public void displayProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    //go to the cart screen
    public void displayCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
    // go to the job creation screen
    public void viewNotifications(View view) {
        Intent intent = new Intent(this, NotificationsPageActivity.class);
        startActivity(intent);
    }
    // go to the MyPostedJobs screen
    public void displayMyPostedJobs(View view) {
        Intent intent = new Intent(this, MyPostedJobsActivity.class);
        startActivity(intent);
    }
    //button logic to go to the homepage screen
    public void displayHomepage(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }*/
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_requestors, menu);
        return true;
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
        Intent intent = new Intent(JobRequestorsActivity.this, CurrentUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
            Intent intent = new Intent(this, HomepageActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }*/
}