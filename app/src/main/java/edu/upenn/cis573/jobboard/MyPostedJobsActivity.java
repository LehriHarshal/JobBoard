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


public class MyPostedJobsActivity extends BottomMenu {
    //List<String> myPostedJobs;
    ArrayAdapter<String> listAdapter;
    ArrayList<Job> jobObjects = new ArrayList<>();
    //ArrayList<Job> shownObjects = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posted_jobs);
        super.init();
        super.enable("All");
        final ArrayList<String> jobNames = new ArrayList<String>();
        final ArrayList<String> jobDescriptions = new ArrayList<>();

        //List of IDS for all the jobs
        List<String> myPostedJobs = ParseUser.getCurrentUser().getList("myPostedJobs");
        if (myPostedJobs != null){
            for (String jobId : myPostedJobs) {
                //Query Parse
                ParseQuery<Job> query = new ParseQuery("Job");
                query.getInBackground(jobId, new GetCallback<Job>() {
                    @Override
                    public void done(final Job o, ParseException e) {
                        if (o == null) return;
                        addvalues(jobNames,jobDescriptions,o);
                    }


                });
            }

        }

        ListView postedJobsListview = (ListView) findViewById(R.id.postedJobsList);

        listAdapter = new ArrayAdapter<String>(
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

        postedJobsListview.setAdapter(listAdapter);
        postedJobsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                showDoerList(position);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_details, menu);
        return true;
    }

    public void addvalues(final ArrayList<String>jobNames,final ArrayList<String>jobDescriptions,final Job o)
    {
        //Thread used to ensure list appears properly each time it is loaded
        //Also adds each item to list
        runOnUiThread(new Runnable() {
            public void run() {
                final String name = o.getJobName();
                jobObjects.add(o);
                jobNames.add(name);
                jobDescriptions.add(o.getString("jobDescription"));
                listAdapter.notifyDataSetChanged();
            }
        });

    }









    //clicking a job in my jobs shows the list of people who requested your job
    public void showDoerList(int position) {
        String id = jobObjects.get(position).getObjectId();
        Intent intent = new Intent(this, JobRequestorsActivity.class);
        intent.putExtra("jobID", id);
        startActivity(intent);
    }

    //go to the profile screen
   /* public void displayProfile(View view) {
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

    //button logic to create a new job
    public void createJob(View view) {
        Intent intent = new Intent(this, JobCreationActivity.class);
        startActivity(intent);
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
        Intent intent = new Intent(MyPostedJobsActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    
}