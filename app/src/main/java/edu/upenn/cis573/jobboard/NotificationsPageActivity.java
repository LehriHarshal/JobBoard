package edu.upenn.cis573.jobboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class NotificationsPageActivity extends BottomMenu {

    ArrayAdapter<String> notificationsListAdapter;
    String userId = ParseUser.getCurrentUser().getObjectId().toString();
    ArrayList<String> notifications = new ArrayList<String>();
    ArrayList<String> jobIDList = new ArrayList<String>();
    int max_Limit_Follow_Notifications = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifications = new ArrayList<String>();
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        List<String> currNotification;
        try {
            currNotification = (List<String>)(userQuery.get(userId)).get("notifications");

            if(currNotification != null)
            {
                for(String s : currNotification)
                {
                    notifications.add(s);
                }
            }
        }catch (Exception e)
        {
            Log.v("Parse ERROR","INSIDE CATCH");
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> followings = currentUser.getList("myFollowings");
        ParseQuery<ParseObject> followings_job = (ParseQuery.getQuery("Job"));//.whereContainedIn("JobPoster", followings);
        followings_job = followings_job.addDescendingOrder("createdAt");
        ParseQuery<ParseUser> users= ParseUser.getQuery();

        if(followings_job != null)
        {
            List<ParseObject> job_list = null;
            try {
                job_list = followings_job.find();
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Error when retrieving Jobs",Toast.LENGTH_SHORT).show();
            }

            if(job_list != null)
            {
                int counter = 0;
                for(ParseObject o : job_list)
                {
                    if(counter >= max_Limit_Follow_Notifications)
                        break;

                    try {
                        String userID = o.getString("jobPoster");
                        if(!followings.contains(userID))
                            continue;
                        counter++;
                        String userName = users.get(userID).getUsername();
                        String jobId = o.getObjectId();
                        String jobName = o.getString("jobName");
                        String message = userName+" has posted a new Job : "+jobName;
                        notifications.add(message);
                        jobIDList.add(jobId);
                        Log.v("User",userId+" "+userName+" "+jobId+" "+jobName);
                    }
                    catch(Exception e)
                    {
                    }
                }
            }
        }

        setContentView(R.layout.activity_notifications_page);
        super.init();
        super.enable("All");
        updateNotificationsList();
    }

    private void updateNotificationsList() {
        /*notifications = new ArrayList<String>();
        //query to find notifications
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser o, ParseException e) {
                List<String> currNotifications = (List<String>) o.get("notifications");
                Log.v("Length of Noti List",currNotifications.toString());
                if (currNotifications != null) {
                    for (String s: currNotifications) {
                        notifications.add(s);
                    }
                }
            }
        });*/
        //set up the listview to display notifications
        ListView notificationsListView = (ListView) findViewById(R.id.notificationsList);
        notificationsListAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                notifications) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
                textView.setTextColor(Color.parseColor("#89cede"));
                String message = notifications.get(position);
                if(message.contains("has posted a new Job"))
                {
                    String message1[] = message.split(":");
                    textView.setText(message1[0]);
                    textView.setTextSize(20);
                    textView2.setText(message1[1]);
                }
                else {
                    textView.setText(notifications.get(position));
                    textView.setTextSize(20);
                }

                return view;
            }
        };

        notificationsListView.setAdapter(notificationsListAdapter);
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (notifications.get(position).contains("has posted a new Job"))
                    openJob(position);
                else
                    optionToDelete(position);
            }
        });
    }

    private void openJob(int position)
    {
        String id = jobIDList.get(position);
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra("jobID", id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications_page, menu);
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

    private void optionToDelete(final int position) {
        CharSequence options[] = new CharSequence[] {"Delete", "Keep"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Would you like to delete this notification?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    deleteJob(position);
                }
            }
        });
        builder.show();

    }
    //give the user the option of deleting this job
    public void deleteJob(final int position) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        try {
            ParseUser user = (ParseUser) userQuery.get(userId);
            List<String> userNotifications = (List<String>) user.get("notifications");
            userNotifications.remove(position);
            user.put("notifications", userNotifications);
            user.save();
        } catch (ParseException e) {
            Log.v("There is no user as", "");
        }

        //now we want to refresh our page
        updateNotificationsList();
    }

    public  void logoutUser() {
        //Parse method to log out by removing CurrentUser
        ParseUser.logOut();
        Intent intent = new Intent(NotificationsPageActivity.this, CurrentUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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