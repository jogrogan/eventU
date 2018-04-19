package com.eventu;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Handles app notifications
 */
public class NotificationIntentService extends IntentService {

    public NotificationIntentService() {
        super("com.eventu.NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                intent.getStringExtra("channel"))
                .setContentTitle(intent.getStringExtra("Event"))
                .setContentText(intent.getStringExtra("Location"))
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                NotificationChannel channel = new NotificationChannel(
                        intent.getStringExtra("channel"),
                        "channel", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("description");
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0, mBuilder.build());
        }
    }
}