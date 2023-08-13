package com.madlab.miniproject;

import static com.madlab.miniproject.MainActivity.NOTIFICATION_CHANNEL_ID;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class MyNotificationPublisher extends BroadcastReceiver {

    public void onReceive (Context context , Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle("Medicine Reminder");
        builder.setContentText(intent.getStringExtra("content"));
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true) ;
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(intent.getIntExtra("notificationId", 0), builder.build());
        DBHandler dbHandler = new DBHandler(context);
        dbHandler.markAsCompleted(intent.getIntExtra("requestCode", 0), intent.getStringExtra("medicineName"), intent.getStringExtra("date"), intent.getStringExtra("time"));
    }
}
