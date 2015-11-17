package edu.upenn.cis573.jobboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BottomMenu {
    //Used for clicking of jobs and displaying their info
    ArrayList<Job> jobObjects = new ArrayList<>();
    ArrayAdapter<String> cartListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        super.init();
        super.enable("All");
        final ListView cartListview = (ListView) findViewById(R.id.list);
        final ArrayList<String> jobNames = new ArrayList<String>();
        final ArrayList<String> jobDescriptions = new ArrayList<>();

        //List of IDS for all the jobs
        final String userID= ParseUser.getCurrentUser().getObjectId();
        List<String> myRequestedJobs = ParseUser.getCurrentUser().getList("myRequestedJobs");
        if (myRequestedJobs != null ) {
            for (final String jobId : myRequestedJobs) {
                //Query Parse
                ParseQuery<Job> query = new ParseQuery("Job");
                query.getInBackground(jobId, new GetCallback<Job>() {
                    @Override
                    public void done(final Job o, ParseException e) {
                        if(o.get("JobDoer") == null || o.get("JobDoer").equals(userID)) {
                            jobObjects.add(o);
                            final String name = o.getJobName();

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    jobNames.add(name);
                                    jobDescriptions.add(o.getString("jobDescription"));
                                    cartListAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                    }
                });

            }
        }

        cartListAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                jobNames) {

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

                text1.setText(jobNames.get(position));
                text1.setTextSize(25);
                text2.setText(jobDescriptions.get(position));
                text2.setPadding(50, 0, 0, 0);
                return view;
            }
        };

        cartListview.setAdapter(cartListAdapter);
        cartListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                openJob(position);
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
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

    //go to the JobDetailsActivity
    public void openJob(int position) {
        String id = jobObjects.get(position).getObjectId();
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra("jobID", id);
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
    public  void logoutUser() {
        //Parse method to log out by removing CurrentUser
        ParseUser.logOut();
        Intent intent = new Intent(CartActivity.this, CurrentUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}