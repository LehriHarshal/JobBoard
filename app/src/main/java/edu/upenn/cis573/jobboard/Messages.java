package edu.upenn.cis573.jobboard;

/**
 * Created by harshal_lehri on 10/30/15.
 */
import com.parse.ParseACL;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import android.location.Location;
import android.location.LocationListener;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Messages")
public class Messages extends ParseObject {

    public Messages()
    {

    }
    public Messages(String messageFrom, String messageTo, String message)
    {
        put("Message_From",messageFrom);
        put("Message_To",messageTo);
        put("Message",message);
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
    }
}


