package edu.upenn.cis573.jobboard;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by chirags on 10/31/15.
 */

@ParseClassName("ProfilePhoto")
public class ProfilePhoto extends com.parse.ParseObject{

    public ProfilePhoto()
    {

    }

    public ProfilePhoto(ParseFile parsefile)
    {
        Log.d("DEbugging", "Debugging");
    //        put("image", parsefile);
        put("picStatus", true);
        put("photo", parsefile);

        saveInBackground();
        returnPhoto(parsefile);
      //  Log.d("Value of picstatus", get("picstatus").toString());
    }


    void returnPhoto(ParseFile parseFile)
    {
        ProfileActivity.photo=parseFile;
    }

}
