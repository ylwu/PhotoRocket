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
    public static void uploadPhotosToParse(String eventID){
        ParseQuery query = new ParseQuery("Photo");
        query.whereEqualTo(Photo.EVENT_ID_KEY,eventID);
        query.whereEqualTo(Photo.IS_SAVED_INCLOUD_KEY,false);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(List<Photo> photos, ParseException e) {
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
                                photo.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null){
                                            Log.d("parse", "uploaded a photo");
                                            photo.unpinInBackground();
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
