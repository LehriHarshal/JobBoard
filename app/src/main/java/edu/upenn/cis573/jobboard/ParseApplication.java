package edu.upenn.cis573.jobboard;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        //New Parse account login
        Parse.initialize(this, "xiuEk1zSlac6EuvH5kYZoQllnjOuZz36XFElcVQ2", "J73rHlo4AG3Skw3y5tQH7IPwf2ghBIJbAxzjyUdt");
        //Old Parse Login
        //Parse.initialize(this, "rm3H0T94rlsEFyfEEojuSv29XSWrPmmzfLVCWsFb", "9tRjp0biQVhmD8nDSSKEq0jgLx6WSemFP1fOgsbc");
        ParseObject.registerSubclass(Job.class);
        ParseObject.registerSubclass(Messages.class);
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        //defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
