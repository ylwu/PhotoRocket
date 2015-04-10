package com.yask.android.photorocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UploadAtBootReceiver extends BroadcastReceiver {
    public UploadAtBootReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Calendar c = Calendar.getInstance();
            Date d = c.getTime();
            Log.d("parse check at upload", "start");
            uploadOldEventsPhotos(d);
            setUploadAlarms(context,d);
            setNotificationAlarms(context,d);
        }
    }

    public void uploadOldEventsPhotos(Date d){
        ParseQuery<EventToUpload> query = new ParseQuery<EventToUpload>("EventToUpload");
        query.whereLessThanOrEqualTo(EventToUpload.ENDTIME_KEY, d);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<EventToUpload>() {
            @Override
            public void done(List<EventToUpload> eventToUploads, ParseException e) {
                for (EventToUpload eventToUpload: eventToUploads){
                    Log.d("parse upload", eventToUpload.getEventID());
                    Utils.uploadPhotosToParse(eventToUpload.getEventID());
                }
            }
        });
    }

    public void setUploadAlarms(final Context context,Date d){
        ParseQuery<EventToUpload> query = new ParseQuery<EventToUpload>("EventToUpload");
        query.whereGreaterThan(EventToUpload.ENDTIME_KEY, d);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<EventToUpload>() {
            @Override
            public void done(List<EventToUpload> eventToUploads, ParseException e) {
                for (EventToUpload eventToUpload: eventToUploads){
                    Log.d("parse setUploadAlarms", eventToUpload.getEventID());
                    UploadAlarmReceiver.setAlarm(context, eventToUpload.getEndTime(),eventToUpload.getEventID());
                }
            }
        });
    }

    public void setNotificationAlarms(final Context context,Date d){
        ParseQuery<EventToUpload> query = new ParseQuery<EventToUpload>("EventToUpload");
        query.whereGreaterThan(EventToUpload.STARTTIME_KEY, d);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<EventToUpload>() {
            @Override
            public void done(List<EventToUpload> eventToUploads, ParseException e) {
                for (EventToUpload eventToUpload: eventToUploads){
                    Log.d("parse setNotifAlarms", eventToUpload.getEventID());
                    NotificationAlarmReceiver.setAlarm(context, eventToUpload.getStartTime());
                }
            }
        });
    }
}
