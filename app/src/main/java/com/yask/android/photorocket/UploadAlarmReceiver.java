package com.yask.android.photorocket;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class UploadAlarmReceiver extends BroadcastReceiver {
    public UploadAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String eventID = intent.getStringExtra(Event.ID_TEXT);
        Intent i = new Intent(context, UploadService.class);
        i.putExtra(Event.ID_TEXT, eventID);
        System.out.println(eventID);
        context.startService(i);
    }

    public static void  setAlarm(Context context, Date startTime, String eventID){
        Intent intent = new Intent(context.getApplicationContext() , UploadAlarmReceiver.class);
        intent.putExtra(Event.ID_TEXT, eventID);
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, startTime.getTime(), pendingIntent);
    }
}
