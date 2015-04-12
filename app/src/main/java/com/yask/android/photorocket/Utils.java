package com.yask.android.photorocket;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/**
 * Created by ylwu on 3/7/15.
 */
public class Utils {
    public static void uploadPhotosToParse(final String eventID){
        ParseQuery query = new ParseQuery("Photo");
        query.whereEqualTo(Photo.EVENT_ID_KEY,eventID);
        query.whereEqualTo(Photo.IS_SAVED_INCLOUD_KEY,false);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(List<Photo> photos, ParseException e) {
                ParseQuery<EventToUpload> uploadParseQuery = new ParseQuery<EventToUpload>("EventToUpload");
                uploadParseQuery.whereEqualTo(EventToUpload.ID_KEY,eventID);
                uploadParseQuery.fromLocalDatastore();
                uploadParseQuery.findInBackground(new FindCallback<EventToUpload>() {
                    @Override
                    public void done(List<EventToUpload> eventToUploads, ParseException e) {
                        for (EventToUpload eventToUpload: eventToUploads){
                            Log.d("parse delete toupload", eventID);
                            eventToUpload.unpinInBackground();
                        }
                    }
                });

                for (final Photo photo : photos){
                    final String uriString = photo.getLocaURIString();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    final ParseFile photoFile = new ParseFile("IMG" + timeStamp + ".jpg", photo.getBytesData());
                    photoFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                photo.clearLocalURI();
                                photo.setContent(photoFile);
                                photo.upLoadedToCloud();
                                photo.saveEventually(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null){
                                            Log.d("parse", "uploaded a photo");
                                        } else {
                                            Log.e("parse", "cannot upload the photo");
                                            photo.setLocalURI(uriString);
                                            photo.savedLocally();
                                        }
                                    }
                                });
                            } else {
                                Log.e("parse", "cannot save ParseFile");
                            }
                        }
                    });
                }
            }
        });
    }
}
