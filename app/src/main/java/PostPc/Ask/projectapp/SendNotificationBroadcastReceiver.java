package PostPc.Ask.projectapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class SendNotificationBroadcastReceiver extends BroadcastReceiver
{
    public static final String actionQuestionAnswered = "QUESTION_ANSWERED";
    public static final String actionQuestionRated = "QUESTION_RATED";
    public static final String actionUserName = "USER_NAME";

    private static final String CHANNEL_ID = "APP_PROJECT";
    private static final String NOTIFICATION_TITLE = "PROJECT_APP";

    private static final String groupQuestionAnswered = "GROUP_QUESTION_ANSWERED";

    private static final String questionAnswered = "answered one of your questions. Check it out";
    private static final String questionRated = "rated one of your questions. Check it out";

    private static final int ANSWERED_QUESTION_ID = 1;
    private static final int RATED_QUESTION_ID = 2;



    private String userName;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();
        assert action!= null;

        this.context = context;
        if(action.equals(actionQuestionAnswered))
        {
            //this.createNotificationChannel();
            this.displayNotificationAnswered();
        }

        if(action.equals(actionQuestionRated))
        {
            //this.createNotificationChannel();
            this.displayNotificationRated();
        }

    }



    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_TITLE, importance);
            channel.setDescription(NOTIFICATION_TITLE);
            NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void displayNotificationAnswered()
    {


        Notification builder = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(this.userName + " " + questionAnswered)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setGroup(groupQuestionAnswered)
                .setGroupSummary(true)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(ANSWERED_QUESTION_ID, builder);
        }
    }

    private void displayNotificationRated()
    {
    }

}
