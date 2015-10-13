package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.app.job.JobInfo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.lang.String;


public class MyPostedJobsActivity extends BottomMenu {
    //List<String> myPostedJobs;
    ArrayAdapter<String> postedlistAdapter;
    ArrayList<Job> jobObjects = new ArrayList<>();
    //ArrayList<Job> shownObjects = new ArrayList<>();
      JobInfo j1=new JobInfo();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posted_jobs);
        super.init();
        super.enable("All");



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
                        final String name = o.getJobName();
                        jobObjects.add(o);
                        //shownObjects.add(o);

                        //Thread used to ensure list appears properly each time it is loaded
                        //Also adds each item to list
                        runOnUiThread(new Runnable() {
                            public void run() {
                                j1.addName(name);
                                j1.addDescription(o.getString("jobDescription"));
                                postedlistAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                });
            }

        }

        ListView postedJobsListview = (ListView) findViewById(R.id.postedJobsList);

        postedlistAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                j1.jobNames) {

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

                text1.setText(j1.geName(position));
                text1.setTextSize(25);
                text2.setText(j1.getDescription(position));
                text2.setPadding(50, 0, 0, 0);
                return view;
            }
        };

        postedJobsListview.setAdapter(postedlistAdapter);
        postedJobsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                showDoerList(position);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}