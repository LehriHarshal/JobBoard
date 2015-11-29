package edu.upenn.cis573.jobboard;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

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
    ArrayList<String> messageBackgroundColor = null;
    HashMap<String, List <String>>savedRes = new HashMap<>();
    List<String> message_temp=new ArrayList<>();
    HashMap< String, Boolean> status = new HashMap<>();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        message_from_user = intent.getStringExtra("MessageFrom");
        message_to_user = intent.getStringExtra("MessageTo");
        setContentView(R.layout.activity_messaging);

        refreshChat();

    }

    private void refreshChat() {

        if(status.get(message_to_user)!=null) {
            if (status.get(message_to_user) == true) {
                return;
            }
        }
        ParseQuery<ParseUser> user = ParseUser.getQuery();
        try {
            messageFromUserName = user.get(message_from_user).getUsername();
            messageToUserName = user.get(message_to_user).getUsername();
            current_user = ParseUser.getCurrentUser().getUsername();
            Log.v("Usernames", messageFromUserName+" "+messageToUserName);
        } catch (Exception e) {

        }
        ParseQuery<ParseObject> query;
        query= ParseQuery.getQuery("Messages");


        messageList = new ArrayList<String>();


        List<ParseObject> messageObjectsFromDatabase = null;
        messageListView = (ListView) findViewById(R.id.message_list);
        try {
            messageObjectsFromDatabase = query.find();
        } catch (Exception e) {

        }



        if (messageObjectsFromDatabase != null) {

            for (ParseObject m : messageObjectsFromDatabase) {

                final String messageFrom = m.getString("Message_From");
                final String messageTo = m.getString("Message_To");
                final String message_from_database = m.getString("Message");
                final String createdAt=m.getString("createdAt");


                if (!((messageFrom.equals(message_from_user)) && (messageTo.equals(message_to_user)) || (messageFrom.equals(message_to_user)) && (messageTo.equals(message_from_user))))

                {
                    //We don't want to show this to anyone
                    continue;
                }



                Log.v("Message",messageFrom+" "+messageTo+" "+message_from_database);
                //Thread used to ensure list appears properly each time it is loaded
                //Also adds each item to list
                // save results before printing
                runOnUiThread(new Runnable() {
                    public void run() {

                        if (messageFrom.equals(message_from_user)){
                            messageList.add(messageFromUserName + " says :\n" + message_from_database);
                            message_temp.add(messageFromUserName + " says :\n" + message_from_database);
                        }
                        else
                            messageList.add(messageToUserName + " says :\n" + message_from_database);
                            message_temp.add(messageFromUserName + " says :\n" + message_from_database);

                    }
                });
            }

        }
        status.put(message_to_user,true);
        savedRes.put(message_to_user,message_temp);

                messageListAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                messageList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // Must always return just a View.
                View view = super.getView(position, convertView, parent);

                // If you look at the android.R.layout.simple_list_item_2 source, you'll see
                // it's a TwoLineListItem with 2 TextViews - text1 and text2.
                //TwoLineListItem listItem = (TwoLineListItem) view;
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                //text2.setEllipsize(TextUtils.TruncateAt.END);
                //text2.setGravity(Gravity.RIGHT);
                //text2.setTextColor(Color.parseColor("#dc4e00"));

                text1.setTextColor(Color.parseColor("#89cede"));
                text1.setSingleLine(false);
                text1.setMaxWidth(10);
                text1.setText(messageList.get(position));
                text2.setPadding(50, 0, 0, 0);
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

    public void sendMessage(View view)
    {

        EditText message_text = (EditText)findViewById(R.id.message_edit_text);
        message = String.valueOf(message_text.getText());

        //Messages m = new Messages(message_from_user,message_to_user,message);
        //m.saveInBackground();
        if(message.equals(""))
        {
            return;
        }
        ParseObject messageObject = ParseObject.create("Messages");
        messageObject.put("Message_From", message_from_user); //string
        messageObject.put("Message_To", message_to_user); //integer
        messageObject.put("Message", message); //variable
        messageObject.put("read",true); // readvariable to be true
        ParseACL acl = new ParseACL();
        ParseQuery<ParseUser> queryParseUser = ParseUser.getQuery();
        ParseUser sender = null;
        ParseUser receiver = null;
        try {
            sender = queryParseUser.get(message_from_user);
            receiver = queryParseUser.get(message_to_user);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Error in fetching User",Toast.LENGTH_SHORT);
        }
        acl.setReadAccess(sender, true);
        acl.setReadAccess(receiver, true);
        messageObject.setACL(acl);
        messageObject.saveInBackground();
        message_text.setText("");
        messageList.add(messageFromUserName + " says :\n" + message);
        messageListAdapter.notifyDataSetChanged();
        //temp.add("messageFromUserName + \" says :\\n\" + message");
        message_temp.add(messageFromUserName + " says :\n" + message);
        savedRes.put(message_to_user,message_temp);
    }


    // Have to Edit
    public void refreshMessages(View view)
    {
        Log.d("Here","Refreshing");
        Toast.makeText(getApplicationContext(),"Messages Refreshed",Toast.LENGTH_SHORT).show();
        refreshChat();
    }
}
