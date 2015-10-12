package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


/**
 * Created by mjrudin on 4/2/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


/**
 * Created by mjrudin on 4/2/15.
 */
public class SignUpActivity extends Activity {

    EditText usernameTextObject;
    EditText passwordTextObject;
    EditText emailTextObject;
    EditText phoneTextObject;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_view);

        //read in the username, password, email, and phone from layout
        usernameTextObject = (EditText) findViewById(R.id.username_edit);
        passwordTextObject = (EditText) findViewById(R.id.password_edit);
        emailTextObject = (EditText) findViewById(R.id.email_edit);
        phoneTextObject = (EditText) findViewById(R.id.phone_edit);

        //Calls the signup method once the user presses the "Sign Up" button
        Button signUpButton = (Button) findViewById(R.id.signupButton2);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signupUser();
            }
        });
    }

    protected void signupUser() {
        String username = usernameTextObject.getText().toString().trim();
        String password = passwordTextObject.getText().toString().trim();
        String email = emailTextObject.getText().toString().trim();
        String phone = phoneTextObject.getText().toString().trim();

        //all fields must be filled in for the sign up to work
        StringBuilder signupErrors = new StringBuilder("");
        boolean fieldError = false;
        if (username.length() < 4) {
            signupErrors.append("Username must be 4 characters. ");
            fieldError = true;
        }
        if (password.length() < 4) {
            signupErrors.append("Password must be 4 characters. ");
            fieldError = true;
        }
        if (email.length() == 0) {
            signupErrors.append("You must enter an email address. ");
            fieldError = true;
        }
        if (phone.length() == 0) {
            signupErrors.append("You must enter a phone number. ");
            fieldError = true;
        }

        //displays the fieldErrors using Toast (taught in HW2)
        if (fieldError) {
            Toast.makeText(SignUpActivity.this, signupErrors.toString(),
                    Toast.LENGTH_SHORT).show();
            //We must breakout of the signUpUser() method if errors exist
            return;
        }


        //Now, we use Parse to create users
        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.put("phone", phone);

        //This is a Parse method to sign up a user
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Start an intent for the CurrentUserActivity, which routes user if logged in
                    Intent intent = new Intent(SignUpActivity.this, CurrentUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    //If e is not null, there is an error which we display
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}