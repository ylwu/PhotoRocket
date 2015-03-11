package com.yask.android.photorocket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
                imageBitmap = decodeFile(new File(Uri.parse(getString(LOCAL_IMAGE_URI_KEY)).getPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            File f = new File(Uri.parse(getString(LOCAL_IMAGE_URI_KEY)).getPath());
            int imageLength = (int) f.length();

            InputStream is = null;
            try {
                is = new FileInputStream(f);
            } catch (FileNotFoundException e){

            }
            byte[] imageArray = new byte[imageLength];
            int bytesRead = 0;
            while (bytesRead < imageLength) {
                int n = 0;
                try {
                    n = is.read(imageArray, bytesRead, imageLength - bytesRead);
                } catch (IOException e){}
                bytesRead += n;
            }

            return imageArray;



//            //Convert bitmap to byte array
//            int bytes = imageBitmap.getByteCount();
//            ByteBuffer buffer = ByteBuffer.allocate(bytes);
//            imageBitmap.copyPixelsToBuffer(buffer);
//            buffer.rewind();
//            byte[] imageArray = buffer.array();
//            return imageArray;
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

    private Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=1200;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            Log.e("ImageResize", "Could not resize image: " + e);
        }
        return null;
    }
}
