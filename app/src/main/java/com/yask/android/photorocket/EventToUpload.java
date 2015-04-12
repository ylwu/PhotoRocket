package com.yask.android.photorocket;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.Date;

/**
 * Created by ylwu on 4/9/15.
 */

@ParseClassName("EventToUpload")
public class EventToUpload extends ParseObject{
    public static final String ID_KEY = "eventID";
    public static final String ENDTIME_KEY = "endTime";
    public static final String STARTTIME_KEY = "startTime";

    public EventToUpload(){};

    public EventToUpload(String eventID,Date startTime,Date endTime){
        put(ID_KEY,eventID);
        put(STARTTIME_KEY,startTime);
        put(ENDTIME_KEY,endTime);
    }

    public String getEventID(){
        return getString(ID_KEY);
    }

    public Date getEndTime() {
        return getDate(ENDTIME_KEY);
    }

    public Date getStartTime() {
        return getDate(STARTTIME_KEY);
    }
}
