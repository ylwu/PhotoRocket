package com.yask.android.photorocket;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UploadService extends Service {
    public UploadService() {
        super();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        String eventID = intent.getStringExtra("eventID");
        System.out.println("trying to upload stuff from upload service");
        Utils.uploadPhotosToParse(eventID);
        System.out.println("Uploaded Stuff");
        return START_STICKY;
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


}
