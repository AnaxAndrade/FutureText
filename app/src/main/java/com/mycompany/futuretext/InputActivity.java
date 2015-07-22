package com.mycompany.futuretext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.view.View;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.graphics.drawable.Drawable;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import android.widget.TimePicker;
import android.widget.Toast;
import android.database.Cursor;
import android.text.style.ImageSpan;
import android.text.SpannableString;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

public class InputActivity extends ActionBarActivity {

    Context context;

    public final static String EXTRA_MESSAGE = "com.mycompany.futuretext.MESSAGE";
    public final static String EXTRA_MESSAGEPOST = "com.mycompany.futuretext.MESSAGEPOST";
    public final static String EXTRA_RECIPIENT = "com.mycompany.futuretext.RECIPIENT";
    public final static String EXTRA_DATEANDTIME = "com.mycompany.futuretext.DATEANDTIME";
    public final static String EXTRA_ID = "com.mycompany.futuretext.MESSAGEID";

    //request codes
    static final int PICK_CONTACT_REQUEST = 1;
    static final int PICK_ATTACHMENT_REQUEST = 2;

    EditText getRecipient;
    EditText getDate;
    EditText getTime;
    EditText getMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        findViewsById();

        setTime();
        setDate();
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

    private void findViewsById() {

        getRecipient = (EditText) findViewById(R.id.get_recipients);

        getTime = (EditText) findViewById(R.id.time);
        getTime.setInputType(InputType.TYPE_NULL);

        getDate = (EditText) findViewById(R.id.date);
        getDate.setInputType(InputType.TYPE_NULL);

        getMessage = (EditText) findViewById(R.id.get_message);
    }

    //called when user clicks send later button
    public void sendDelayedMessage(View view) {

        //gets data from input fields
        String recipient = getRecipient.getText().toString();
        String message = getMessage.getText().toString();
        String time = getTime.getText().toString();
        String date = getDate.getText().toString();
        String messagePost = ("To: " + recipient + "\n" + message + "\n" + time + " " + date);

        //parses date input
        String[] dateValues = date.split("/");
        int month = Integer.parseInt(dateValues[0]) - 1;
        int day = Integer.parseInt(dateValues[1]);
        int year = Integer.parseInt(dateValues[2]);

        //parses time input
        String[] timeValues = time.split(":");
        int hour = Integer.parseInt(timeValues[0]);
        int minute = Integer.parseInt(timeValues[1]);

        Calendar c = Calendar.getInstance();

        //creates unique id for the alarm manager so separate messages stay separate
        String alarmIDString = new StringBuilder().append(c.get(Calendar.HOUR)).append(c.get(Calendar.MINUTE)).append(c.get(Calendar.SECOND)).append(c.get(Calendar.MILLISECOND)).toString();
        int alarmID = Integer.parseInt(alarmIDString);

        //sets calendar to parsed time input
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);

        Calendar now = Calendar.getInstance();
        //this if stops the method if the time set is in the past
        if(c.before(now)){

            Toast.makeText(this,("You're using FutureText" + "\n" + "Please pick a time in the future!"), Toast.LENGTH_LONG).show();
            return;//breaks out of method
        }

        //writes message post to archive file
        MessageLog.toFile(this.getApplicationContext(), MessageLog.STORED_MESSAGES, messagePost);

        Intent messageIntent = new Intent(this, AlarmReceiver.class);
        messageIntent.putExtra(EXTRA_MESSAGE, message);
        messageIntent.putExtra(EXTRA_RECIPIENT, recipient);
        messageIntent.putExtra(EXTRA_DATEANDTIME, time);
        messageIntent.putExtra(EXTRA_ID, alarmIDString);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmID, messageIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent); //schedules alarm manager for preset time via Calendar c

        Toast.makeText(this,("text scheduled for " + time + " " + date), Toast.LENGTH_LONG).show();
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

                if (getRecipient.getText().toString().matches("")) {
                    getRecipient.setText(number);
                }
                else {
                    getRecipient.append("," + number);
                }

                cursor.close();
            }
        }

        /*if (requestCode == PICK_ATTACHMENT_REQUEST) {
            if (resultCode == RESULT_OK) {

                try {

                    Uri attachmentUri = data.getData();
                    InputStream inputStream = context.getContentResolver().openInputStream(attachmentUri);
                    Drawable drawable = Drawable.createFromStream(inputStream, attachmentUri.toString());
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    SpannableString s = new SpannableString("abc\n");
                    s.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    getMessage.setText(s);

                }
                catch (FileNotFoundException e){
                    e.printStackTrace();
                }

            }
        }*/
    }

    //Everything below this comment is for showing the TimePickerDialog and getting it's time into the time EditText
    TimePickerDialog timePickerDialog;

    private void setTime() {

        Calendar c = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(this, new OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int hourOfTheDay, int minute) {

                if (minute < 10) {
                    String minuteString = "0" + Integer.toString(minute);
                    getTime.setText(Integer.toString(hourOfTheDay) + ":" + minuteString);
                }
                else {
                    getTime.setText(Integer.toString(hourOfTheDay) + ":" + Integer.toString(minute));
                }
            }

        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
    }

    public void showTimeDialog(View view) {
        timePickerDialog.show();
    }

    //Everything below this comment is for showing DatePickerDialog and getting its date into the date EditText
    DatePickerDialog datePickerDialog;

    private void setDate() {

        Calendar c = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                getDate.setText(Integer.toString(monthOfYear + 1) + "/" + Integer.toString(dayOfMonth) + "/" + Integer.toString(year));
            }

        }, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.YEAR));
    }

    public void showDateDialog(View view) {

        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

//    public void pickAttachment(View view) {
//
//        Intent getAttachmentIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        getAttachmentIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        getAttachmentIntent.setType("*/*");
//        startActivityForResult(getAttachmentIntent, PICK_ATTACHMENT_REQUEST);
//
//    }
}