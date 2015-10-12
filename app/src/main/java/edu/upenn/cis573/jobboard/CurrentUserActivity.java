package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * Created by mjrudin on 4/2/15.
 */
public class CurrentUserActivity extends Activity {

    //Used by sign up, sign in, and log out activity to route users to different views if they are
    //logged in or not
    public CurrentUserActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if Parse has a user logged in, take us to the HomepageActivity
        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, HomepageActivity.class));
        } else {
            //if Parse has no user logged in, take us to the SignInActivity
            startActivity(new Intent(this, SignInActivity.class));
        }
    }
}