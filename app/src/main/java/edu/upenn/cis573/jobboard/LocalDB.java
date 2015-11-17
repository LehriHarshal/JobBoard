package edu.upenn.cis573.jobboard;

import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by harshal_lehri on 11/9/15.
 */
public class LocalDB implements Serializable {

    List<String> Messages = new ArrayList<String>();
    String LastUpdated;
    private static String path = "MobileJobBoard/mobileAppMessages.ser";

    public LocalDB(List<String> Message, String LastUpdated)
    {
        Collections.copy(Message,this.Messages);
        this.LastUpdated = LastUpdated;
    }

    public LocalDB()
    {
        LastUpdated = "0/0/0";
    }
    public void save()
    {

    }

    public static boolean localDBExists()
    {
        File dbFile = new File(path);
        return dbFile.exists();
    }

    public static LocalDB getObject() {
        if (LocalDB.localDBExists()) {
            try {

                FileInputStream fileIn = new FileInputStream(path);
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
                return (LocalDB) objIn.readObject();

            } catch (Exception e) {

            }
        }
        else
        {
            return new LocalDB();
        }
        return null;
    }
}

