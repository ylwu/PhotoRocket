package com.yask.android.photorocket;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("LOGGED OUTPUT: Alarm broadcast received");
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("PhotoRocket Event")
                        .setSmallIcon(R.drawable.camera_icon)
                        .setContentText("Tap to take pictures");
        int mNotificationId = 002;
        Intent resultIntent = new Intent(context, MainActivity.class);
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public static void  setAlarm(Context context, Date startTime){
        Intent intent = new Intent(context.getApplicationContext() , AlarmReceiver.class);
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExact(AlarmManager.RTC, startTime.getTime(), pendingIntent);
    }
}
