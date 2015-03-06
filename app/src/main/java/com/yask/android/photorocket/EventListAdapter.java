package com.yask.android.photorocket;

import android.content.Context;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by ylwu on 3/5/15.
 */
public class EventListAdapter extends ParseQueryAdapter<Event> {

    public EventListAdapter(Context context, ParseUser user){
        super (context,new QueryFactory<Event>() {
            @Override
            public ParseQuery<Event> create() {
                ParseQuery query = new ParseQuery("Event");
                query.whereEqualTo("participants", ParseUser.getCurrentUser());
                query.include("participants");
                return query;
            }
        });
    }
}
