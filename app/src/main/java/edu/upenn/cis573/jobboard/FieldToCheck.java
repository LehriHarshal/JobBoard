package edu.upenn.cis573.jobboard;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Chirag on 10/5/2015.
 */
public class FieldToCheck {


    int checkField(Context context,String field_value,int wrong_count)
    {
        StringBuilder signupErrors = new StringBuilder("");
        //boolean fieldError=false;
        if (field_value.length() == 0)
        {
            signupErrors.append("All fields must be at-least 4 characters. ");
            Toast.makeText(context, signupErrors.toString(),
                    Toast.LENGTH_SHORT).show();
            wrong_count++;
        }
        return wrong_count;

    }
}
