package edu.upenn.cis573.jobboard;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ChatActivity extends AppCompatActivity {


    String current_user;
    String messageFromUserID;
    ArrayList<String> messageUserNameList=new ArrayList<>();
    ArrayList<String> messageList=new ArrayList<>();
    ListView messageListView;
    ArrayList<String> messageFromUserIDList=new ArrayList<>();
    ArrayAdapter<String> messageListAdapter;
    String currentUserId;
    HashMap<String,List<String>> savedChats= new HashMap<>();
    ArrayList<String> users_chatted_with =new ArrayList<>();
    boolean status=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Log.d("Number of Elements1", Integer.toString(messageList.size()));
        try {
            current_user = ParseUser.getCurrentUser().getUsername();
            currentUserId = ParseUser.getCurrentUser().getObjectId();
        } catch (Exception e) {

        }

        if(Messages.status==false) {
            doStuff();
        }
        Log.d("Number of Elements2", Integer.toString(messageList.size()));

        Log.d("Number of Elements chat",messageList.toString());
        messageListView = (ListView) findViewById(R.id.incoming_message_list);

        messageListAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                Messages.messageUserNameList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // Must always return just a View.
                View view = super.getView(position, convertView, parent);

                // If you look at the android.R.layout.simple_list_item_2 source, you'll see
                // it's a TwoLineListItem with 2 TextViews - text1 and text2.
                //TwoLineListItem listItem = (TwoLineListItem) view;
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(Color.parseColor("#dc4e00"));

                text1.setTextColor(Color.parseColor("#89cede"));
                text1.setSingleLine(false);
                text1.setMaxWidth(10);
                text1.setText(Messages.messageUserNameList.get(position));
                //text2.setText(messageList.get(0));
                text2.setPadding(50, 0, 0, 0);
                return view;
            }
        };

        messageListView.setAdapter(messageListAdapter);
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
                intent.putExtra("MessageFrom", currentUserId);
                intent.putExtra("MessageTo", Messages.messageFromUserIDList.get(position));
                startActivity(intent);
            }
        });

    }


    void doStuff()
    {
        Log.d("Size_ML: ", Integer.toString(messageUserNameList.size()));
        //if(messageUserNameList.size()!=0)
        //{
        //    return;
        //}
        status=true;


        ParseQuery<ParseObject> query;
        LocalDB db = LocalDB.getObject();
        messageList =  (ArrayList<String>)db.Messages;

        if(db.LastUpdated.equals("0/0/0")) {
            query= ParseQuery.getQuery("Messages");
        }
        else
        {
            query = ParseQuery.getQuery("Messages").whereGreaterThan("CreatedAt",db.LastUpdated);
        }

        ParseQuery<ParseUser> user = ParseUser.getQuery();
        //messageList = new ArrayList<String>();
        //messageFromUserIDList = new ArrayList<String>();
        //messageUserNameList = new ArrayList<String>();

        List<ParseObject> messageObjectsFromDatabase = null;


        try {
            messageObjectsFromDatabase = query.find();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG);
        }


        if (messageObjectsFromDatabase != null) {
            HashSet<String> uniqueMessages = new HashSet<String>();
            for (ParseObject m : messageObjectsFromDatabase) {
                final String messageFrom = m.getString("Message_From");
                final String messageTo = m.getString("Message_To");

                if ((!messageTo.equals(currentUserId)) && (!messageFrom.equals(currentUserId))) {
                    //We don't want to show this to anyone
                    continue;
                }

                //Thread used to ensure list appears properly each time it is loaded
                //Also adds each item to list

                String messageUserName = null;
                try {
                    if(messageTo.equals(currentUserId)) {
                        messageUserName = user.get(messageFrom).getUsername();
                        messageFromUserID = user.get(messageFrom).getObjectId();
                        users_chatted_with.add(messageFrom);
                    }
                    else {
                        messageUserName = user.get(messageTo).getUsername();
                        messageFromUserID = user.get(messageTo).getObjectId();
                        users_chatted_with.add(messageTo);
                    }
                }catch(Exception e)
                {

                }

                String userAddedToList = messageUserName;
                if(!uniqueMessages.contains(messageFromUserID)) {
                    messageUserNameList.add(userAddedToList);
                    //messageList.add(message);
                    messageFromUserIDList.add(messageFromUserID);
                    Log.v("Message From User",messageFromUserID);
                    uniqueMessages.add(messageFromUserID);
                }

            }

        }
        Messages.messageFromUserIDList=messageFromUserIDList;
        Messages.messageList=messageList;
        Messages.messageUserNameList=messageUserNameList;
        Messages.status=true;
    }

    public static void logoutUser() {
        //Parse method to log out by removing CurrentUser
        ParseUser.logOut();
    }

    public void onBackPressed()
    {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }
}
