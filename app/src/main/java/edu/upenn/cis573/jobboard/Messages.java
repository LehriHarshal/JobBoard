package edu.upenn.cis573.jobboard;

/**
 * Created by harshal_lehri on 10/30/15.
 */
import com.parse.ParseACL;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import android.location.Location;
import android.location.LocationListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ParseClassName("Messages")
public class Messages extends ParseObject {


    static ArrayList<String> messageUserNameList=new ArrayList<>();
    static ArrayList<String> messageList=new ArrayList<>();
    static ArrayList<String> messageFromUserIDList=new ArrayList<>();
    static boolean status=false;
    public Messages()
    {

    }
    public Messages(String messageFrom, String messageTo, String message)
    {
        put("Message_From",messageFrom);
        put("Message_To",messageTo);
        put("Message",message);
        put("read",false);
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
    }
}


