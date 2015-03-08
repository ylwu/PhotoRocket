package com.yask.android.photorocket;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ylwu on 3/7/15.
 */
public class Utils {

    public static final String MORE_THAN_ONE_EVENT_ERROR = "more than one event";

    /*
        WARNING: getEventIDForTime and getEventIDForNow needs to be called in background thread.
        Calling in on mainthread will cause an error.
        Example: mainActivity.CheckEventTask

     */
    public static String getEventIDForTime(Date d){
        List<Event> eventList = new ArrayList<Event>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereLessThanOrEqualTo(Event.STARTTIME_KEY,d);
        query.whereGreaterThanOrEqualTo(Event.ENDTIME_KEY,d);
        try {
            List<ParseObject> objectList = query.find();
            for (ParseObject parseObject: objectList){
                eventList.add((Event)parseObject);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (eventList.size() == 0){
            return null;
        } else if (eventList.size() ==1){
            Event event = (Event)eventList.get(0);
            return event.getObjectId();
        } else {
            return String.valueOf(eventList.size());
        }
    }

    public static final String getEventIDForNow(){
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        return getEventIDForTime(d);
    }
}
