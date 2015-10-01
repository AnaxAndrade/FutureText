package com.mycompany.futuretext;

import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;

public class DisplayMessageActivity extends ActionBarActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message);
        listView = (ListView) findViewById(R.id.archive);

        String messagePost = MessageLog.readFile(this.getApplicationContext(), MessageLog.STORED_MESSAGES);
        String[] messages = messagePost.split("To: ");

        String[] temp = new String[messages.length];

        for (int i = 0; i < messages.length ; i++) {

            temp[i] = messages[messages.length - (i + 1)];
        }

        messages = temp;

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, messages);
        listView.setAdapter(adapter);

        //Will show a delete dialog without any functionality
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                DialogFragment fragment = DeleteDialogFragment.newInstance(R.string.delete_dialog);
//                fragment.show(getFragmentManager(), "dialog");
//            }
//        });

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

//  All this will be used to delete pending messages
//    public void showDeleteDialog(View view) {
//
//        DialogFragment fragment = DeleteDialogFragment.newInstance(R.string.delete_dialog);
//        fragment.show(getFragmentManager(), "dialog");
//    }
//
//    public void deletePendingMessage() {
//
//    }
//
//    public void dontDeletePendingMessage() {
//
//    }
}
