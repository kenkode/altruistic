package com.kenkode.altruistic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
        @Override
        public void onMessageReceived(final RemoteMessage remoteMessage) {
            if(remoteMessage.getData() != null) {
                String title = "Altruistic";
                String message = "The current bus is full! Please wait for the next bus...";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendArrivedNotificationAPI26(title, message);
                }
                sendArrivedNotification(title, message);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void sendArrivedNotificationAPI26(String title, String body) {
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),
                    0, new Intent(), PendingIntent.FLAG_ONE_SHOT);

            NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
            Notification.Builder builder = notificationHelper.getNotification(title,body,pendingIntent);
            notificationHelper.getManager().notify(1, builder.build());

        }

        private void sendArrivedNotification(String title, String body) {
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),
                    0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

            builder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager)getBaseContext().getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
        }
    }