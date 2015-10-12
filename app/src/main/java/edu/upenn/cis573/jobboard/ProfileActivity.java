package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;


public class ProfileActivity extends BottomMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_tab_view);

        //Displays profile information about the user
        displayUserDetails();
        super.init();
        super.enable("All");
        //Calls the logout method once the user presses the "Logout" button
        Button logOutBut = (Button) findViewById(R.id.moveForwardButton);
        logOutBut.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                logoutUser();
                //Starts CurrentUserActivity, which routes user once logged out
                Intent intent = new Intent(ProfileActivity.this, CurrentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    public void displayUserDetails() {
        TextView username = (TextView) findViewById(R.id.usernameTextView);
        username.append(ParseUser.getCurrentUser().getUsername());

        TextView email = (TextView) findViewById(R.id.emailTextView);
        email.append(ParseUser.getCurrentUser().getEmail());

        TextView phone = (TextView) findViewById(R.id.phoneNumberTextView);
        phone.append(ParseUser.getCurrentUser().get("phone").toString());
    }

    public static void logoutUser() {
        //Parse method to log out by removing CurrentUser
        ParseUser.logOut();
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