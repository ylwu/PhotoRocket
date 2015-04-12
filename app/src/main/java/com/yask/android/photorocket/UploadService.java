package com.yask.android.photorocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UploadService extends Service {
    public UploadService() {
        super();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        String eventID = intent.getStringExtra(Event.ID_TEXT);
        System.out.println("trying to upload stuff from upload service");
        Utils.uploadPhotosToParse(eventID);
        System.out.println("Uploaded Stuff");
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

   @Override
   public void onCreate(){
       System.out.println("I should sync stuff here");
   }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("parse service", "destroyed");
    }
}
