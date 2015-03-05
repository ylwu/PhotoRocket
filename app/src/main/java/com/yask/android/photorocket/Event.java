package com.yask.android.photorocket;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by ylwu on 3/4/15.
 */
@ParseClassName("Event")
public class Event extends ParseObject {
    public Event(){

    }
    public Event(String name){
        put("name",name);
        ArrayList<ParseUser> participants = new ArrayList<ParseUser>();
        participants.add(ParseUser.getCurrentUser());
        put("participants",participants);
    }

    public void addUser(ParseUser user){
        ArrayList<ParseUser> participants = (ArrayList) get("participants");
        participants.add(user);
        put("participants",participants);
    }
}
