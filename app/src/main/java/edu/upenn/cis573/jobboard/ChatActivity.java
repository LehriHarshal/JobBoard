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
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    String current_user;
    String messageFromUserID;
    ArrayList<String> messageUserNameList;
    Map<String,List<String>> messageMap;
    Map<String,String> userIdMap;

    ListView messageListView;
    ArrayList<String> messageFromUserIDList;
    ArrayAdapter<String> messageListAdapter;
    String currentUserId;
    LocalDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        try {
            current_user = ParseUser.getCurrentUser().getUsername();
            Log.v("Current User",current_user);
            currentUserId = ParseUser.getCurrentUser().getObjectId();
        } catch (Exception e) {

        }


        ParseQuery<ParseObject> query;

        db = LocalDB.getObject(getApplicationContext());

        messageMap =  db.Messages;
        userIdMap = db.UserIDList;

        if(db.LastUpdated == null) {

            query= ParseQuery.getQuery("Messages").orderByAscending("createdAt");
            Log.v("New Query","New Query");
        }
        else
        {
            query = ParseQuery.getQuery("Messages").whereGreaterThan("createdAt",db.LastUpdated).orderByAscending("createdAt");
            Log.v("Update Query","Update Query");
        }
        ParseQuery<ParseUser> user = ParseUser.getQuery();

        messageFromUserIDList = new ArrayList<String>();
        messageUserNameList = new ArrayList<String>();

        List<ParseObject> messageObjectsFromDatabase = null;
        messageListView = (ListView) findViewById(R.id.incoming_message_list);

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
                final String message = m.getString("Message");

                db.LastUpdated = m.getCreatedAt();

                Log.v("Dates",db.LastUpdated.toString());

                String messageFromUserName = null;
                String messageToUserName = null;
                try {
                    messageFromUserName = user.get(messageFrom).getUsername();
                    messageToUserName = user.get(messageTo).getUsername();
                } catch (Exception e) {

                }

                //if ((!messageTo.equals(currentUserId)) && (!messageFrom.equals(currentUserId))) {
                    //We don't want to show this to anyone
                    //continue;
                //}
                //Thread used to ensure list appears properly each time it is loaded
                //Also adds each item to list
                String messageUserName = null;
                //String messageToUserName = null;
                String messageUserNameID = null;
                try {
                    if(messageTo.equals(currentUserId)) {
                        messageUserName = user.get(messageFrom).getUsername();
                        messageUserNameID = messageFrom;
                        //messageFromUserID = user.get(messageFrom).getObjectId();
                    }
                    else {
                        messageUserName = user.get(messageTo).getUsername();
                        messageUserNameID = messageTo;
                        //messageToUserName = user.get(messageTo).getUsername();
                        //messageFromUserID = user.get(messageTo).getObjectId();
                    }
                }catch(Exception e)
                {
                    Log.v("Exception", "Exception");
                }

                String userAddedToList = messageUserName;
                String message_to_display = messageFromUserName +" says:\n\n"+message;

                if(messageMap.containsKey(messageUserName))
                  messageMap.get(messageUserName).add(message_to_display);
                else
                {
                    List<String> user_messages = new ArrayList<String>();
                    user_messages.add(message_to_display);
                    messageMap.put(messageUserName, user_messages);
                    userIdMap.put(messageUserName,messageUserNameID);
                    //messageFromUserIDList.add(messageUserNameID);
                }

            }

        }

        messageUserNameList.addAll(messageMap.keySet());
        messageFromUserIDList.addAll(userIdMap.values());

        db.save(this);
        messageListAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                messageUserNameList) {

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
                text1.setText(messageUserNameList.get(position));
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
                intent.putExtra("MessageTo", messageFromUserIDList.get(position));
                intent.putExtra("LocalDB",db);
                List<String> messages_from_user = messageMap.get(messageUserNameList.get(position));
                intent.putStringArrayListExtra("Messages", (ArrayList<String>) messages_from_user);

                startActivity(intent);
            }
        });

    }
    public static void logoutUser() {
        //Parse method to log out by removing CurrentUser
        ParseUser.logOut();
    }
}
