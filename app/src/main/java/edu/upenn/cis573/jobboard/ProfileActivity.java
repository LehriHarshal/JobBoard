package edu.upenn.cis573.jobboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class ProfileActivity extends BottomMenu {


    static final int REQUEST_IMAGE_CAPTURE = 1;
     static ParseFile photo=null;
    static Drawable image=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_tab_view);

        //Displays profile information about the user
        displayUserDetails();
        super.init();
        super.enable("All");
        //Calls the logout method once the user presses the "Logout" button
        Button logOutBut = (Button) findViewById(R.id.moveForwardButton);
        logOutBut.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                logoutUser();
                //Starts CurrentUserActivity, which routes user once logged out
                Intent intent = new Intent(ProfileActivity.this, CurrentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void displayUserDetails() {
        TextView username = (TextView) findViewById(R.id.usernameTextView);
        username.append(ParseUser.getCurrentUser().getUsername());

        TextView email = (TextView) findViewById(R.id.emailTextView);
        email.append(ParseUser.getCurrentUser().getEmail());

        TextView phone = (TextView) findViewById(R.id.phoneNumberTextView);
        phone.append(ParseUser.getCurrentUser().get("phone").toString());


            if (ParseUser.getCurrentUser().getBoolean("picStatus") == true)
            {
                ParseFile p = ParseUser.getCurrentUser().getParseFile("photo");
                Drawable d=ProfileActivity.image;
                try {
                    byte picBytes[] = p.getData();
                    Log.d("Size of array", Integer.toString(picBytes.length));
                    Bitmap img=BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length);
                    d = new BitmapDrawable(getResources(),img);
                }
                catch(Exception e)
                {
                    Log.d("Error","Dynamically not being able to fetch");
                }


                ImageView i = (ImageView) findViewById(R.id.imageView);
                i.setBackground(d);
            }


    }

    public static void logoutUser() {
        //Parse method to log out by removing CurrentUser
        ParseUser.logOut();
    }

    //go to the profile screen
    /*public void displayProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    //go to the cart screen
    public void displayCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
    // go to the job creation screen
    public void viewNotifications(View view) {
        Intent intent = new Intent(this, NotificationsPageActivity.class);
        startActivity(intent);
    }
    // go to the MyPostedJobs screen
    public void displayMyPostedJobs(View view) {
        Intent intent = new Intent(this, MyPostedJobsActivity.class);
        startActivity(intent);
    }
    //button logic to go to the homepage screen
    public void displayHomepage(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }*/


    public void profilePhotoOnClick(View view) {
        //Log.v("Debug","Debug");
        //Toast.makeText(getApplicationContext(),"Debug",Toast.LENGTH_SHORT);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            //}
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView i=(ImageView)findViewById(R.id.imageView);
            Drawable d = new BitmapDrawable(getResources(),imageBitmap);
            image=d;
            Toast.makeText(getApplicationContext(), "Image Captured", Toast.LENGTH_SHORT).show();
            i.setBackground(d);


            try {

                Log.d("1", "1");
                //FileOutputStream fos = new FileOutputStream(f);
                Log.d("2", "1");
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                Log.d("6", "1");
                ParseFile parsefile=new ParseFile(bos.toByteArray());
                parsefile.saveInBackground();

                ParseUser.getCurrentUser().put("picStatus", true);
                ParseUser.getCurrentUser().put("photo", parsefile);
                ParseUser.getCurrentUser().saveInBackground();
                final ProfilePhoto p=new ProfilePhoto(parsefile);

                Toast.makeText(getApplicationContext(), "Image Saved to Database", Toast.LENGTH_SHORT).show();
                Log.d("8", "1");
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), " Unable to save to Database", Toast.LENGTH_SHORT).show();

            }
        }
    }




}