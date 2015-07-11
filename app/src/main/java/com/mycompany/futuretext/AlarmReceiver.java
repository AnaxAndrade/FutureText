package com.mycompany.futuretext;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

public class AlarmReceiver extends BroadcastReceiver {

    //Receives alarm manager broadcast, gets message and recipient and sends SMS and creates notification
    public void onReceive(Context context, Intent intent) {

        //gets data from intent
        String message = intent.getStringExtra(InputActivity.EXTRA_MESSAGE);
        String recipient = intent.getStringExtra(InputActivity.EXTRA_RECIPIENT);
        String messagePost = intent.getStringExtra(InputActivity.EXTRA_MESSAGEPOST);
        String alarmID = intent.getStringExtra(InputActivity.EXTRA_ID);

        if (recipient.contains(",")) {
            String[] recipients = recipient.split(",");
            for(String i : recipients) {
                sendSingleSMS(i, message);
            }
        }
        else {
            sendSingleSMS(recipient, message);
        }

        notifyUser(context, messagePost, alarmID);
    }

    //Sends sms with message
    public void sendSingleSMS(String recipient, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipient, null, message, null, null);
    }

    //creates notification at the time the sms is sent
    public void notifyUser(Context context, String messagePost, String alarmID) {

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);
        notifBuilder.setSmallIcon(R.drawable.notification_template_icon_bg);
        notifBuilder.setContentTitle("FutureText Message Sent");
        notifBuilder.setContentText(messagePost);

        Intent resultIntent = new Intent(context, DisplayMessageActivity.class);

        //TaskStackBuilder makes it so that pressing back takes you back to home screen instead of navigating within the app after you enter the app thru a notification
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DisplayMessageActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notifBuilder.setContentIntent(resultPendingIntent);
        notifBuilder.setAutoCancel(true);
        notifBuilder.setVibrate(new long[]{1000});
        notifBuilder.setLights(Color.MAGENTA, 2000, 2000);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(alarmID), notifBuilder.build());
    }

}
