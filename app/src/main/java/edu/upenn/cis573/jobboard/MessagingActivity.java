package edu.upenn.cis573.jobboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class MessagingActivity extends AppCompatActivity {

    String message_from_user;
    String message_to_user;
    String message;
    String messageFromUserName;
    String messageToUserName;
    String current_user;
    ArrayList<String> messageList = null;
    ListView messageListView;
    ArrayAdapter<String> messageListAdapter;
    ArrayList<String> message_list;
    LocalDB db;
    UpdateMessages messageUpdater;
    Bitmap ToUserImage = null;
    Bitmap FromUserImage = null;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        message_from_user = intent.getStringExtra("MessageFrom");
        message_to_user = intent.getStringExtra("MessageTo");
        message_list = intent.getStringArrayListExtra("Messages");
        db = (LocalDB) intent.getSerializableExtra("LocalDB");
        setContentView(R.layout.activity_messaging);
        messageFromUserName = ParseUser.getCurrentUser().getUsername();
        ParseFile pfrom = (ParseFile) ParseUser.getCurrentUser().get("photo");
        try {
            byte[] data = pfrom.getData();
            if (data == null)
                FromUserImage = null;
            else
                FromUserImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {

        }

        try {
            messageToUserName = ParseUser.getQuery().get(message_to_user).getUsername();
            ParseFile tfrom = (ParseFile) ParseUser.getQuery().get(message_to_user).get("photo");
            byte[] data = tfrom.getData();
            if (data == null)
                ToUserImage = null;
            else
                ToUserImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {

        }
        messageList = new ArrayList<String>();
        refreshChat();

        /*messageUpdater = new UpdateMessages();
        while(true) {
            messageUpdater.execute("Starting");
            try {
                messageUpdater.get();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }*/



    }

    private void refreshChat() {

        messageListView = (ListView) findViewById(R.id.message_list);
        messageListAdapter = new ArrayAdapter<String>(
                this,
                R.layout.chat_list,
                android.R.id.text1,
                message_list) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // Must always return just a View.
                //View view = super.getView(position, convertView, parent);
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.chat_list, null);
                // If you look at the android.R.layout.simple_list_item_2 source, you'll see
                // it's a TwoLineListItem with 2 TextViews - text1 and text2.
                //TwoLineListItem listItem = (TwoLineListItem) view;
                TextView text1 = (TextView) view.findViewById(R.id.message_from_text);
                TextView text2 = (TextView) view.findViewById(R.id.message_to_text);

                ImageView image1 = (ImageView) view.findViewById(R.id.from_image);
                ImageView image2 = (ImageView) view.findViewById(R.id.to_image);
                //text2.setEllipsize(TextUtils.TruncateAt.END);
                //text2.setGravity(Gravity.RIGHT);

                text2.setTextColor(Color.parseColor("#dc4e00"));
                text1.setTextColor(Color.parseColor("#89cede"));
                text1.setTextSize(15);
                text2.setTextSize(15);
                String message = message_list.get(position);
                //Log.v("Messages",message);
                if (message.contains(messageToUserName)) {
                    text1.setText(message);

                    if (ToUserImage != null)
                        image1.setImageBitmap(ToUserImage);
                    else
                        image1.setImageResource(R.drawable.profile_icon);
                } else {


                    text2.setText(message);
                    if (FromUserImage != null)
                        image2.setImageBitmap(FromUserImage);
                    else
                        image2.setImageResource(R.drawable.profile_icon);

                }

                return view;
            }
        };

        messageListView.setAdapter(messageListAdapter);
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            }
        });

        messageListView.setStackFromBottom(true);
    }

    public void sendMessage(View view) {

        EditText message_text = (EditText) findViewById(R.id.message_edit_text);
        message = String.valueOf(message_text.getText());

        //Messages m = new Messages(message_from_user,message_to_user,message);
        //m.saveInBackground();
        if (message.equals("")) {
            return;
        }
        ParseObject messageObject = ParseObject.create("Messages");
        messageObject.put("Message_From", message_from_user); //string
        messageObject.put("Message_To", message_to_user); //integer
        messageObject.put("Message", message); //variable
        ParseACL acl = new ParseACL();
        ParseQuery<ParseUser> queryParseUser = ParseUser.getQuery();
        ParseUser sender = null;
        ParseUser receiver = null;
        String receiver_user_name = null;
        try {
            sender = queryParseUser.get(message_from_user);
            receiver = queryParseUser.get(message_to_user);
            receiver_user_name = queryParseUser.get(message_to_user).getUsername();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error in fetching User", Toast.LENGTH_SHORT);
        }
        acl.setReadAccess(sender, true);
        acl.setReadAccess(receiver, true);
        messageObject.setACL(acl);
        messageObject.saveInBackground();
        message_text.setText("");
        message_list.add(messageFromUserName + " says :\n\n" + message);
        db.Messages.get(receiver_user_name).add(messageFromUserName + " says :\n" + message);
        db.LastUpdated = new Date();
        db.save(this);
        messageListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        db.save(this);
        finish();
        return;
    }


    public void refreshMessagesAsync()
    {
        ParseQuery<ParseObject> query;
        Log.v("LasUpdated",db.LastUpdated.toString());

        //DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        //Date d = df.parse(db.LastUpdated);
        query = ParseQuery.getQuery("Messages").whereGreaterThan("createdAt", db.LastUpdated).orderByAscending("createdAt");
        query = query.whereEqualTo("Message_To", message_from_user);
        query = query.whereEqualTo("Message_From", message_to_user);

        ParseQuery<ParseUser> user = ParseUser.getQuery();


        List<ParseObject> messageObjectsFromDatabase = null;
        try {
            messageObjectsFromDatabase = query.find();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
        }


        if (messageObjectsFromDatabase != null) {
            for (ParseObject m : messageObjectsFromDatabase) {
                final String messageFrom = m.getString("Message_From");
                final String messageTo = m.getString("Message_To");
                final String message = m.getString("Message");

                //Log.v("Dates",message+" "+messageFrom+" "+messageTo+" "+db.LastUpdated+" "+date);
                db.LastUpdated = m.getCreatedAt();


                String messageFromUserName = null;
                String messageToUserName = null;
                try {
                    messageFromUserName = user.get(messageFrom).getUsername();
                    messageToUserName = user.get(messageTo).getUsername();
                } catch (Exception e) {

                }

                message_list.add(messageFromUserName +" says:\n\n"+message);
                db.Messages.get(messageFromUserName).add(message);
            }

            messageListAdapter.notifyDataSetChanged();
            db.save(this);
            //Toast.makeText(getApplicationContext(),"Messages Refreshed",Toast.LENGTH_LONG).show();
        }

        //Looper.loop();
    }


    // Have to Edit
    public void refreshMessages(View view) {
        ParseQuery<ParseObject> query;
        Log.v("LasUpdated",db.LastUpdated.toString());

        //DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        //Date d = df.parse(db.LastUpdated);
        query = ParseQuery.getQuery("Messages").whereGreaterThan("createdAt", db.LastUpdated).orderByAscending("createdAt");
        query = query.whereEqualTo("Message_To", message_from_user);
        query = query.whereEqualTo("Message_From", message_to_user);

        ParseQuery<ParseUser> user = ParseUser.getQuery();


        List<ParseObject> messageObjectsFromDatabase = null;
        try {
            messageObjectsFromDatabase = query.find();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
        }


        if (messageObjectsFromDatabase != null) {
            for (ParseObject m : messageObjectsFromDatabase) {
                final String messageFrom = m.getString("Message_From");
                final String messageTo = m.getString("Message_To");
                final String message = m.getString("Message");

                //Log.v("Dates",message+" "+messageFrom+" "+messageTo+" "+db.LastUpdated+" "+date);
                db.LastUpdated = m.getCreatedAt();


                String messageFromUserName = null;
                String messageToUserName = null;
                try {
                    messageFromUserName = user.get(messageFrom).getUsername();
                    messageToUserName = user.get(messageTo).getUsername();
                } catch (Exception e) {

                }

                message_list.add(messageFromUserName +" says:\n\n"+message);
                db.Messages.get(messageFromUserName).add(message);
            }

            messageListAdapter.notifyDataSetChanged();
            db.save(this);
            Toast.makeText(getApplicationContext(),"Messages Refreshed",Toast.LENGTH_LONG).show();
        }


    }

    private class UpdateMessages extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object... info) {

            Log.v("Doing things","Doing things in the background");

            refreshMessagesAsync();
            try{
                Thread.sleep(1000);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Object o) {
            messageListAdapter.notifyDataSetChanged();
        }
    }
}
