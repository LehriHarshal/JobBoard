package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class SearchableActivity extends Activity {

    public static int SEARCH_CATEGORY = 0;
    public static ParseGeoPoint USER_LOCATION = new ParseGeoPoint();
    ArrayAdapter<String> listAdapter;
    ArrayList<Job> jobObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        handleIntent(getIntent());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchable, menu);
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

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d("ANUPAM", "In SearchActivity: " + SEARCH_CATEGORY);
            String search = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            final ArrayList<String> jobNames = new ArrayList<String>();
            final ArrayList<String> jobDescriptions = new ArrayList<String>();
            int i = 0;

            if (SEARCH_CATEGORY == 0) {
                //Query Parse
                ParseQuery<Job> query = new ParseQuery("Job");
                query.whereContains("typeDescription", search);
                query.findInBackground(new FindCallback<Job>() {
                    @Override
                    public void done(List objects, ParseException e) {
                        for (int i = 0; i < objects.size(); i++) {
                            Job o = (Job) objects.get(i);
                            jobObjects.add(o);
                            final String name = o.getString("jobName");
                            final String descr = o.getString("jobDescription");

                            Log.v("searchable", name);

                            String[] temp = new String[2];
                            temp[0] = name;
                            temp[1] = descr;

                            //Thread used to ensure list appears properly each time it is loaded
                            //Also adds each item to list
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    jobNames.add(name);
                                    jobDescriptions.add(descr);
                                    listAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            } else if (SEARCH_CATEGORY == 1) {
                if (USER_LOCATION == null) {
                    return;
                }
                //Query Parse
                ParseQuery<Job> query = new ParseQuery("Job");
                System.out.println("\nuser location : " + USER_LOCATION);
                query.whereNear("Location", USER_LOCATION);
                query.setLimit(10);

                query.findInBackground(new FindCallback<Job>() {
                    @Override
                    public void done(List objects, ParseException e) {
                        for (int i = 0; i < objects.size(); i++) {
                            Job o = (Job) objects.get(i);
                            jobObjects.add(o);

                            final String name = o.getString("jobName");
                            final String descr = o.getString("jobDescription");

                            Log.v("searchable", name);

                            String[] temp = new String[2];
                            temp[0] = name;
                            temp[1] = descr;
                            //Thread used to ensure list appears properly each time it is loaded
                            //Also adds each item to list
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    jobNames.add(name);
                                    jobDescriptions.add(descr);
                                    listAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            } else if (SEARCH_CATEGORY == 2) {

                final List<String> userIds = new ArrayList<>();
                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("_User");
                query1.orderByDescending("userRating");
                query1.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            // The query was successful. Internet?
                            for (ParseObject object : objects) {
                                userIds.add(object.getObjectId());
                                Log.d("AnupamAlur", "UserId: " + object.getObjectId());
                            }
                        } else {
                            Log.e("AnupamAlur", "Error", e);
                        }
                    }
                });

                //Query Parse
                ParseQuery<Job> query = new ParseQuery("Job");
                query.findInBackground(new FindCallback<Job>() {
                    @Override
                    public void done(List objects, ParseException e) {
                        List<Job> jobs = objects;
                        for (String userId : userIds) {
                            for (Job job : jobs) {
                                String jobDoer = job.getString("jobDoer");
                                if (!TextUtils.isEmpty(jobDoer) && jobDoer.equals(userId)) {
                                    jobObjects.add(job);
                                    final String name = job.getString("jobName");
                                    final String descr = job.getString("jobDescription");

                                    Log.v("searchable", name);
                                    //
                                    String[] temp = new String[2];
                                    temp[0] = name;
                                    temp[1] = descr;
                                    //Thread used to ensure list appears properly each time it is loaded
                                    //Also adds each item to list
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            jobNames.add(name);
                                            jobDescriptions.add(descr);
                                            listAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }
                        jobs.removeAll(jobObjects);
                        for (Job job : jobs) {
                            final String name = job.getString("jobName");
                            final String descr = job.getString("jobDescription");
                            String[] temp = new String[2];
                            temp[0] = name;
                            temp[1] = descr;
                            //Thread used to ensure list appears properly each time it is loaded
                            //Also adds each item to list
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    jobNames.add(name);
                                    jobDescriptions.add(descr);
                                    listAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }
                });
            }

            ListView jobsListView = (ListView) findViewById(R.id.list);

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
            jobsListView.setAdapter(listAdapter);
        }
    }
}
