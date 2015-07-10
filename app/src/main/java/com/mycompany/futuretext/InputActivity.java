package com.mycompany.futuretext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.view.View;
import java.util.Calendar;
import android.widget.Toast;
import android.database.Cursor;

public class InputActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.futuretext.MESSAGE";
    public final static String EXTRA_MESSAGEPOST = "com.mycompany.futuretext.MESSAGEPOST";
    public final static String EXTRA_RECIPIENT = "com.mycompany.futuretext.RECIPIENT";
    public final static String EXTRA_DATEANDTIME = "com.mycompany.futuretext.DATEANDTIME";

    static final int PICK_CONTACT_REQUEST = 1; //request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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
        if (id == R.id.to_messagearchives) {
            Intent intent = new Intent(getApplicationContext(), DisplayMessageActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //called when user clicks send later button
    public void sendDelayedMessage(View view) {

        //gets data from input fields
        EditText getRecipient = (EditText) findViewById(R.id.get_recipients);
        String recipient = getRecipient.getText().toString();
        EditText getMessage = (EditText) findViewById(R.id.get_message);
        String message = getMessage.getText().toString();
        EditText getDateAndTime = (EditText) findViewById(R.id.get_date_and_time);
        String when = getDateAndTime.getText().toString();
        String messagePost = ("To: " + recipient + "\n" + message + "\n" + when);

        //writes message post to archive file
        MessageLog.toFile(this.getApplicationContext(), MessageLog.STORED_MESSAGES, messagePost);

        //parses time input
        char[] hourChars = new char[2];
        char[] minuteChars = new char[2];
        when.getChars(0,2,hourChars,0);
        when.getChars(3, when.length(), minuteChars, 0);
        String hourString =  new StringBuilder().append(hourChars[0]).append(hourChars[1]).toString();
        String minuteString = new StringBuilder().append(minuteChars[0]).append(minuteChars[1]).toString();
        int hour = Integer.parseInt(hourString);
        int minute = Integer.parseInt(minuteString);

        Calendar c = Calendar.getInstance();
        //creates unique id for the alarm manager so separate messages stay separate
        String alarmIDString = new StringBuilder().append(c.get(Calendar.HOUR)).append(c.get(Calendar.MINUTE)).append(c.get(Calendar.SECOND)).append(c.get(Calendar.MILLISECOND)).toString();
        int alarmID = Integer.parseInt(alarmIDString);

        //sets calendar to parsed time input
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);

        Intent messageIntent = new Intent(this, AlarmReceiver.class);
        messageIntent.putExtra(EXTRA_MESSAGE, message);
        messageIntent.putExtra(EXTRA_RECIPIENT, recipient);
        messageIntent.putExtra(EXTRA_DATEANDTIME, when);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmID, messageIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent); //schedules alarm manager for preset time via Calendar c

        Toast.makeText(this,("text scheduled for " + hourString + ":" + minuteString), Toast.LENGTH_LONG).show();
    }

    //Allows user to access contacts to choose a phone number
    public void pickContact(View view) {

        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);//only contacts with phone #s
        startActivityForResult(pickContactIntent,PICK_CONTACT_REQUEST);
    }

    //gets data from pickContactIntent, gets number from it and writes in number to get_recipients edittext
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check request code.
        if (requestCode == PICK_CONTACT_REQUEST) {
            //check to see if request was successful
            if (resultCode == RESULT_OK){

                Uri contactUri = data.getData();

                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                cursor.moveToFirst();
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                EditText getRecipient = (EditText) findViewById(R.id.get_recipients);
                getRecipient.setText(number);
                cursor.close();

            }
        }
    }
}
