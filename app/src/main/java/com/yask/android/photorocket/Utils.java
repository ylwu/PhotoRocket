package com.yask.android.photorocket;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
        WARNING: getEventIDForTime, eventExistInTimeRange and getEventIDForNow needs to be called in background thread.
        Calling in on mainthread will cause Networkonmainthread error. The reason is that making the query
        in parse is a network event and may take long, so the UI shouldn't be frozen while waiting for
        the result to come back.
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

    public static void joinEvent(String eventID) {
        Log.d("parse event search", eventID);
        ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.getInBackground(eventID,new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    event.addParticipant(ParseUser.getCurrentUser());
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("parse user", "succesfully add participant");
                            } else {
                                Log.e("parse user", e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    Log.e("parse event search", e.getLocalizedMessage());
                }
            }
        });
    }

    public static boolean eventExistInTimeRange(Date startTime, Date endTime){
        List<Event> eventList = new ArrayList<Event>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereLessThanOrEqualTo(Event.STARTTIME_KEY,endTime);
        query.whereGreaterThanOrEqualTo(Event.ENDTIME_KEY,startTime);
        try {
            List<ParseObject> objectList = query.find();
            return  objectList.size() != 0;
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    public static final String getEventIDForNow(){
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        return getEventIDForTime(d);
    }
}
