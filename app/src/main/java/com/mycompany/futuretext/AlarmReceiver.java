package com.mycompany.futuretext;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class AlarmReceiver extends BroadcastReceiver {

    //Receives alarm manager broadcast, gets message and recipient and sends SMS
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra(InputActivity.EXTRA_MESSAGE);
        String recipient = intent.getStringExtra(InputActivity.EXTRA_RECIPIENT);

        //Sends sms of message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipient, null, message, null, null);

    }

}
