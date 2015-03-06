package com.yask.android.photorocket;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by ylwu on 3/5/15.
 */
@ParseClassName("Photo")
public class Photo extends ParseObject{

    public final static String CONTENT_KEY = "content";
    public final static String AUTHOR_KEY = "author";
    public final static String EVENT_ID_KEY = "eventID";

    public Photo() {

    }

    public Photo(String eventID, ParseFile file){
        put(EVENT_ID_KEY,eventID);
        put(CONTENT_KEY, file);
    }

    public ParseFile getConent() {return getParseFile(CONTENT_KEY);}

    public ParseUser getAuthor() {return getParseUser(AUTHOR_KEY);}

    public String getEventID() {return getString(EVENT_ID_KEY);}

}
