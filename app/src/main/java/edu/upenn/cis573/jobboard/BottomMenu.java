package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * Created by harshal_lehri on 10/5/15.
 */
public abstract class BottomMenu extends Activity {

    private Button home, profile, cart, my_jobs, alerts, message_alerts;
    private TextView hometext, profiletext, carttext, my_jobstext, alertstext, message_text;
    private HashMap<String,Button> map;
    private HashMap<String, TextView> text_map;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    public void init()
    {
        home = (Button)findViewById(R.id.buttonDashboard);
        profile = (Button)findViewById(R.id.buttonProfile);
        cart = (Button)findViewById(R.id.buttonCart);
        my_jobs = (Button)findViewById(R.id.buttonMyPostedJobs);
        alerts = (Button)findViewById(R.id.buttonNotification);
        message_alerts = (Button)findViewById(R.id.buttonMessages);

        hometext = (TextView)findViewById(R.id.text_dashboard);
        profiletext = (TextView)findViewById(R.id.text_profile);
        carttext = (TextView)findViewById(R.id.text_cart);
        my_jobstext = (TextView)findViewById(R.id.text_myjobs);
        alertstext = (TextView)findViewById(R.id.text_notifications);
        message_text = (TextView)findViewById(R.id.text_message_notifications);

        map = new HashMap<String,Button>();
        map.put("Home",home);
        map.put("Profile",profile);
        map.put("Cart",cart);
        map.put("My Jobs",my_jobs);
        map.put("Alerts",alerts);
        map.put("Messages",message_alerts);

        text_map = new HashMap<String,TextView>();
        text_map.put("Home",hometext);
        text_map.put("Profile",profiletext);
        text_map.put("Cart", carttext);
        text_map.put("My Jobs", my_jobstext);
        text_map.put("Alerts", alertstext);
        text_map.put("Messages", message_text);
        /*home.setEnabled(false);
        profile.setEnabled(false);
        cart.setEnabled(false);
        my_jobs.setEnabled(false);
        alerts.setEnabled(false);*/
        for(String key : map.keySet())
        {
            map.get(key).setVisibility(View.INVISIBLE);
            text_map.get(key).setVisibility(View.INVISIBLE);
        }
    }
    public void enable(String s)
    {
        if(s.equals("All"))
        {
            for(String key : map.keySet())
            {
                map.get(key).setVisibility(View.VISIBLE);
                text_map.get(key).setVisibility(View.VISIBLE);
            }
            return;
        }
        else if (map.containsKey(s))
        {
            map.get(s).setVisibility(View.VISIBLE);
            text_map.get(s).setVisibility(View.VISIBLE);
        }
    }
    public void displayProfile(View view) {
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
    }

    public void viewMessages(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

}