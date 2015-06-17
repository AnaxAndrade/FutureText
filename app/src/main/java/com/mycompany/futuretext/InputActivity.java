package com.mycompany.futuretext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.net.Uri;
//import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.view.View;
//import java.util.Date;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Locale;
//import android.app.ActivityManager;
import android.widget.Toast;

public class InputActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.futuretext.MESSAGE";
    public final static String EXTRA_MESSAGEPOST = "com.mycompany.futuretext.MESSAGEPOST";
    public final static String EXTRA_RECIPIENT = "com.mycompany.futuretext.RECIPIENT";
    public final static String EXTRA_DATEANDTIME = "com.mycompany.futuretext.DATEANDTIME";


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

    //called when the user clicks the send button
    public void sendMessage (View view) {

        EditText getRecipient = (EditText) findViewById(R.id.get_recipients);
        String recipient = getRecipient.getText().toString();
        EditText getMessage = (EditText) findViewById(R.id.get_message);
        String message = getMessage.getText().toString();

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGEPOST, "To: " + recipient + "\n" + message);
        startActivity(intent);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipient, null, message, null, null);

    }

    /*Broadcasts to AlarmReceiver to send SMS and passes and Intent to DisplayMessageActivity to show what
    will be sent and when*/
    //called when user clicks send later button
    public void sendDelayedMessage(View view) {

        EditText getRecipient = (EditText) findViewById(R.id.get_recipients);
        String recipient = getRecipient.getText().toString();
        EditText getMessage = (EditText) findViewById(R.id.get_message);
        String message = getMessage.getText().toString();
        EditText getDateAndTime = (EditText) findViewById(R.id.get_date_and_time);
        String when = getDateAndTime.getText().toString();
        String messagePost = ("To: " + recipient + "\n" + message + "\n" + when);

        MessageLog.toFile(this.getApplicationContext(), MessageLog.STORED_MESSAGES, messagePost);



        char[] hourChars = new char[2];
        char[] minuteChars = new char[2];
        when.getChars(0,2,hourChars,0);
        when.getChars(3, when.length(), minuteChars, 0);
        String hourString =  new StringBuilder().append(hourChars[0]).append(hourChars[1]).toString();
        String minuteString = new StringBuilder().append(minuteChars[0]).append(minuteChars[1]).toString();
        int hour = Integer.parseInt(hourString);
        int minute = Integer.parseInt(minuteString);



        Calendar c = Calendar.getInstance();

        String alarmIDString = new StringBuilder().append(c.get(Calendar.HOUR)).append(c.get(Calendar.MINUTE)).append(c.get(Calendar.SECOND)).append(c.get(Calendar.MILLISECOND)).toString();
        int alarmID = Integer.parseInt(alarmIDString);

        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(this, AlarmReceiver.class);
        //intent.setAction("com.mycompany.futuretext.InputActivity");
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_RECIPIENT, recipient);
        intent.putExtra(EXTRA_DATEANDTIME, when);


        PendingIntent alarmIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmID, intent, PendingIntent.FLAG_ONE_SHOT);



        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent);

        Toast.makeText(this,("text scheduled for " + hourString + ":" + minuteString), Toast.LENGTH_LONG).show();
        Toast.makeText(this, alarmIDString, Toast.LENGTH_LONG).show();

    }

}
