package com.yask.android.photorocket;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by ylwu on 3/5/15.
 */
@ParseClassName("Photo")
public class Photo extends ParseObject{

    public final static String CONTENT_KEY = "content";
    public final static String AUTHOR_KEY = "author";
    public final static String EVENT_ID_KEY = "eventID";
    public final static String IS_SAVED_INCLOUD_KEY = "isSavedInCloud";
    public final static String LOCAL_IMAGE_URI_KEY = "localImageURI";
    public Photo () {

    }

    public Photo(String eventId, String localImageURI) {
        put(EVENT_ID_KEY,eventId);
        put(LOCAL_IMAGE_URI_KEY,localImageURI);
        put(IS_SAVED_INCLOUD_KEY,false);
    }

    public byte[] getBytesData(Context cr) {
        if (getString(LOCAL_IMAGE_URI_KEY) == null){
            Log.d("parse","no local uri");
        } else {
            Bitmap imageBitmap = null;
            try {
                Log.d("Photo URI", Uri.parse(getString(LOCAL_IMAGE_URI_KEY)).toString());
                imageBitmap = MediaStore.Images.Media.getBitmap(cr.getContentResolver(),Uri.parse(getString(LOCAL_IMAGE_URI_KEY)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Convert bitmap to byte array
            int bytes = imageBitmap.getByteCount();
            ByteBuffer buffer = ByteBuffer.allocate(bytes);
            imageBitmap.copyPixelsToBuffer(buffer);
            byte[] imageArray = buffer.array();
            return imageArray;
        }
        return null;
    }

    public boolean isSavedInCloud() {
        return getBoolean(IS_SAVED_INCLOUD_KEY);
    }

    public void clearLocalURI() {
        put(LOCAL_IMAGE_URI_KEY,"");
    }

    public void setLocalURI(String uriString){
        put(LOCAL_IMAGE_URI_KEY,uriString);
    }

    public String getLocaUIRString() {
        return getString(LOCAL_IMAGE_URI_KEY);
    }

    public void upLoadedToCloud(){
        put(IS_SAVED_INCLOUD_KEY,true);
    }

    public void savedLocally() {
        put(IS_SAVED_INCLOUD_KEY,false);
    }

    public Photo(String eventID, ParseFile file){
        put(EVENT_ID_KEY,eventID);
        put(CONTENT_KEY, file);
        put(IS_SAVED_INCLOUD_KEY,true);
    }

    public void setContent(ParseFile file) {put(CONTENT_KEY,file);}

    public ParseFile getContent() {return getParseFile(CONTENT_KEY);}

    public ParseUser getAuthor() {return getParseUser(AUTHOR_KEY);}

    public String getEventID() {return getString(EVENT_ID_KEY);}

}
