package edu.upenn.cis573.jobboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import edu.upenn.cis573.jobboard.VenmoLibrary.VenmoResponse;


public class ParseStarterProjectActivity extends Activity {

    final int REQUEST_CODE_VENMO_APP_SWITCH = 1;

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

        /*
        Intent venmoIntent = VenmoLibrary.openVenmoPayment("2590", "Job Board", "joeyraso", "0", "food", "pay");
        startActivityForResult(venmoIntent, REQUEST_CODE_VENMO_APP_SWITCH);
        */
	}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

        switch(requestCode) {
            case REQUEST_CODE_VENMO_APP_SWITCH: {
                if(resultCode == RESULT_OK) {
                    String signedrequest = data.getStringExtra("signedrequest");
                    if(signedrequest != null) {
                        VenmoResponse response = (new VenmoLibrary()).validateVenmoPaymentResponse(signedrequest, "65fmgPt5DPn2kC3bxpZhk7aZhz9bVqxx");
                        if(response.getSuccess().equals("1")) {
                            //Payment successful.  Use data from response object to display a success message
                            String note = response.getNote();
                            String amount = response.getAmount();
                        }
                    }
                    else {
                        String error_message = data.getStringExtra("error_message");
                        //An error ocurred.  Make sure to display the error_message to the user
                    }
                }
                else if(resultCode == RESULT_CANCELED) {
                    //The user cancelled the payment
                }
                break;
            }
        }
    }
}
