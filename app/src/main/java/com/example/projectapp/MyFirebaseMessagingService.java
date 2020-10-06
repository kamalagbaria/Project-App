package com.example.projectapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("TOKENFIREBASE", s);
        int x = 0;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().isEmpty())
        {
            showNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
        }
        else
        {
            //Toast.makeText(this, remoteMessage.getMessageId(), Toast.LENGTH_SHORT).show();
            Map<String, String> data = remoteMessage.getData();
            if(Objects.equals(data.get("type"), "new_answer_added"))
            {
                showNotificationNewAnswer(data);
            }
            if(Objects.equals(data.get("type"), "new_comment_added"))
            {
                showNotificationNewComment(data);
            }
        }
        //sendBroadcast(new Intent(SendNotificationBroadcastReceiver.actionQuestionAnswered));
        int x = 0;
    }

    private void showNotificationNewAnswer(Map<String, String> data)
    {
        String title = data.get("name");
        String type = data.get("type");
        String name = data.get("name");
        String notificationId = data.get("id");
        String GROUP_KEY_NEW_ANSWER = "NEW_ANSWER";

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.projectapp.NEW_ANSWER";
        int SUMMARY_ID = 0;

        Intent resultIntent = new Intent(this, QuestionDetailActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Project-App Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[] {0,1000,500,1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);

        builder.setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notifications) //change later
                .setContentTitle(name)
                .setContentText("Added new answer. Check it out!")
                .setContentInfo("Info")
                .setGroup(GROUP_KEY_NEW_ANSWER);


        Notification summaryNotification =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(title)
                        //set content text to support devices running API level < 24
                        .setContentText("You have new messages")
                        .setSmallIcon(R.drawable.notifications)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                //.setBigContentTitle("2 new messages")
                                .setSummaryText("You have new messages"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_NEW_ANSWER)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        //Todo change id to later to not override older messages
        assert notificationId != null;
        notificationManager.notify(notificationId.hashCode(), builder.build());
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }

    private void showNotificationNewComment(Map<String, String> data)
    {
        String title = data.get("name");
        String type = data.get("type");
        String name = data.get("name");
        String notificationId = data.get("id");
        String GROUP_KEY_NEW_COMMENT = "NEW_COMMENT";

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.projectapp.NEW_COMMENT";
        int SUMMARY_ID = 1;

//        Intent resultIntent = new Intent(this, QuestionDetailActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntentWithParentStack(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Project-App Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[] {0,1000,500,1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);

        builder.setAutoCancel(true)
                //.setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notifications) //change later
                .setContentTitle(name)
                .setContentText("Added new comment. Check it out!")
                .setContentInfo("Info")
                .setGroup(GROUP_KEY_NEW_COMMENT);


        Notification summaryNotification =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(title)
                        //set content text to support devices running API level < 24
                        .setContentText("You have new messages")
                        .setSmallIcon(R.drawable.notifications)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                //.setBigContentTitle("2 new messages")
                                .setSummaryText("You have new messages"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_NEW_COMMENT)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        //Todo change id to later to not override older messages
        assert notificationId != null;
        notificationManager.notify(notificationId.hashCode(), builder.build());
        notificationManager.notify(1, summaryNotification);
    }

    private void showNotification(String title, String body)
    {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.projectapp.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Project-App Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[] {0,1000,500,1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notifications) //change later
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");

        //Todo change id to later to not override older messages
        notificationManager.notify(2, builder.build());

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
