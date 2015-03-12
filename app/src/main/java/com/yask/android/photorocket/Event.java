package com.yask.android.photorocket;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ylwu on 3/4/15.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String NAME_KEY = "name";
    public static final String PARTICIPANTS_KEY = "participants";
    public static final String STARTTIME_KEY = "startTime";
    public static final String ENDTIME_KEY = "endTime";
    public static final String ID_TEXT = "eventID";
    public static final String ISOCCURING_TEXT = "isOccuring";

    public Event(){

    }

    public Event(String name,Date startTime, Date endTime){
        put(NAME_KEY,name);
        ArrayList<ParseUser> participants = new ArrayList<ParseUser>();
        participants.add(ParseUser.getCurrentUser());
        put(PARTICIPANTS_KEY,participants);
        put(STARTTIME_KEY,startTime);
        put(ENDTIME_KEY,endTime);
    }

    public boolean isOccuring(){
        Calendar c = Calendar.getInstance();
        Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        startTime.setTime(getDate(STARTTIME_KEY));
        endTime.setTime(getDate(ENDTIME_KEY));
        return c.after(startTime) && c.before(endTime);
    }

    public Date getStartTime() {return getDate(STARTTIME_KEY);}

    public Date getEndTime() {return getDate(ENDTIME_KEY);}

    public List<ParseUser> getParticipants() {
        List<ParseUser> participants = new ArrayList<ParseUser>();
        for (Object user: getList(PARTICIPANTS_KEY)){
            participants.add((ParseUser)user);
        }
        return participants;
    }

    public void addParticipant(ParseUser user){
        ArrayList<ParseUser> participants = (ArrayList) get(PARTICIPANTS_KEY);
        participants.add(user);
        put(PARTICIPANTS_KEY,participants);
    }

    public String getEventName(){
        return getString(NAME_KEY);
    }
}
