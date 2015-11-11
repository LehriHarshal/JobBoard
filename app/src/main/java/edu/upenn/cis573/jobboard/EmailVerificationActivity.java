package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;

public class EmailVerificationActivity extends Activity {

    String secretKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        getParseUserDetailsVerification();
    }


    void getParseUserDetailsVerification() {

        try {
            secretKey = ParseUser.getCurrentUser().getUsername();
            StringBuilder s=new StringBuilder();
            s.append(secretKey);
            s.reverse();
            secretKey=s.toString();
        }catch (Exception e){

        }
            Log.d("SecretKey", secretKey);
    }

    void success()
    {
        Intent intent = new Intent(EmailVerificationActivity.this, CurrentUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void verifyUser(View view)
    {
        EditText userKeyEntered=(EditText) findViewById(R.id.SecretKey);
        if(userKeyEntered.getText().toString().toLowerCase().equals(secretKey))
        {
            Toast.makeText(getApplicationContext(), "Verified Successfully", Toast.LENGTH_SHORT).show();
            ParseUser.getCurrentUser().put("ManuallyVerified", true);
            success();

        }
        else
        {
            Toast.makeText(getApplicationContext(),"Email Not Verified! Try again!",Toast.LENGTH_SHORT).show();
        }
    }
}
