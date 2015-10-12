package edu.upenn.cis573.jobboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HomepageActivity extends BottomMenu {
    ArrayAdapter<String> homeListAdapter;
    ArrayList<Job> jobObjects = new ArrayList<>();
    ArrayList<Job> shownObjects = new ArrayList<>();


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_homepage_view);
        super.init();
        super.enable("All");
        //Set up listview

        final ArrayList<String> jobNames = new ArrayList<String>();
        final ArrayList<String> jobDescriptions = new ArrayList<String>();

        //final List<String> jobInfo = new LinkedList<String>();


        //Query Parse
        ParseQuery<Job> query =  ParseQuery.getQuery("Job");
        query.findInBackground(new FindCallback<Job>() {
            @Override
            public void done(List<Job> objects, ParseException e) {
                for (Job o : objects) {
                    String status = o.getString("jobStatus");
                    if ((status.equals("inProgress")) || (status.equals("completed"))) {
                        //We don't want to show this to anyone
                        continue;
                    }
                    jobObjects.add(o);
                    shownObjects.add(o);
                    final String name = o.getString("jobName");
                    final String descr = o.getString("jobDescription");

                    /*String[] temp = new String[2];
                    temp[0] = name;
                    temp[1] = descr;*/

                    //Thread used to ensure list appears properly each time it is loaded
                    //Also adds each item to list
                    runOnUiThread(new Runnable() {
                        public void run() {
                            jobNames.add(name);
                            jobDescriptions.add(descr);
                            homeListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });


        ListView jobsListView = (ListView) findViewById(R.id.homeList);


        homeListAdapter = new ArrayAdapter<String>(
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

        jobsListView.setAdapter(homeListAdapter);
        jobsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        getMenuInflater().inflate(R.menu.menu_homepage, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
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




}