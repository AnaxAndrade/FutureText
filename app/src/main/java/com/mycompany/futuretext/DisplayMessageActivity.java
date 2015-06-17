package com.mycompany.futuretext;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class DisplayMessageActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        //String messagePost = intent.getStringExtra(InputActivity.EXTRA_MESSAGEPOST);
        String messagePost = MessageLog.readFile(this.getApplicationContext(), MessageLog.STORED_MESSAGES);

        TextView textview = new TextView(this);
        textview.setTextSize(20);
        textview.setText(messagePost);
        setContentView(textview);

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
}
