package edu.upenn.cis573.jobboard;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harshal_lehri on 11/9/15.
 */
public class LocalDB implements Serializable {

    Date LastUpdated;
    Map<String, List<String>> Messages;

    Map<String,String> UserIDList;
    private static String path = "mobileAppMessages.ser";

    public LocalDB()
    {
        Messages = new LinkedHashMap<String,List<String>>();
        UserIDList = new LinkedHashMap<String,String>();
        LastUpdated = null;
    }

    public void save(Context c)
    {
        try {

            FileOutputStream fileOut = c.openFileOutput(path,Context.MODE_PRIVATE);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(this);
            Log.v("Wrote to save", "wrote to save ");
            objOut.close();
            fileOut.close();

        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void reset()
    {
        this.LastUpdated = null;
        this.Messages.clear();
        this.UserIDList.clear();
    }
    public static boolean localDBExists(Context c)
    {
        File f = c.getFileStreamPath(path);
        if(f == null || !f.exists())
            return false;
        Log.v("File does not exist","File does not exist");
        return true;
    }

    public static LocalDB getObject(Context c) {
        if (LocalDB.localDBExists(c)) {
            try {
                Log.v("File does exist","File does exist");
                FileInputStream fileIn = c.openFileInput(path);
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
                LocalDB d = (LocalDB) objIn.readObject();
                objIn.close();
                fileIn.close();
                return d;

            } catch (Exception e) {

                e.printStackTrace();
                return new LocalDB();
            }
        }
        else
        {

            return new LocalDB();
        }

    }

    public void add(Map<String,List<String>> new_messages)
    {
        Messages.putAll(new_messages);
    }
}

