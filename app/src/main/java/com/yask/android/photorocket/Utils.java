package com.yask.android.photorocket;

import android.util.Log;

import com.parse.FindCallback;
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

    public static String getEventIDForTime(Date d){
        final List<Event> eventList = new ArrayList<Event>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
//        query.whereGreaterThanOrEqualTo(Event.STARTTIME_KEY,d);
//        query.whereLessThanOrEqualTo(Event.ENDTIME_KEY,d);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null){
                    for (ParseObject object : parseObjects){
                        eventList.add((Event)object);
                        Log.d("parse", "add new event");
                    }
                } else {
                    Log.e("parse",e.getLocalizedMessage());
                    Log.e("parse", "cannot retrieve events");
                }

            }
        });
        if (eventList.size() == 0){
            return null;
        } else if (eventList.size() ==1){
            Event event = (Event)eventList.get(0);
            return event.getObjectId();
        } else {
            return MORE_THAN_ONE_EVENT_ERROR;
        }
    }

    public static final String getEventIDForNow(){
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        return getEventIDForTime(d);
    }
}
