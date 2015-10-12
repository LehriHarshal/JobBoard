package edu.upenn.cis573.jobboard;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Anjali on 4/30/15.
 */
public class NotificationsManager {

    /*
        This method adds a notification to users set of notifications
        It takes in a userId as a String, and the notifications message
        as a String.
     */
    public static void notifyUser(String userId, String msg) {
        final String message = msg;
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser o, ParseException e) {
                ArrayList<String> notifications = (ArrayList<String>)o.get("notifications");
                if (notifications == null) {
                    notifications = new ArrayList<String>();
                }
                notifications.add(message);
                o.put("notifications", notifications);
                o.saveInBackground();
            }
        });
    }


}