package com.yask.android.photorocket;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ylwu on 3/5/15.
 */
public class EventListAdapter extends ParseQueryAdapter<Event> {

    MainActivity.MainMenuFragment main_menu_fragment;
    PastEventsActivity.PastEventsFragment past_events_fragment;

    public EventListAdapter(Context context, ParseUser user, MainActivity.MainMenuFragment fragment){
        super (context,new QueryFactory<Event>() {
            @Override
            public ParseQuery<Event> create() {
                Calendar c = Calendar.getInstance();
                Date d = c.getTime();
                ParseQuery query = new ParseQuery("Event");
                query.whereEqualTo(Event.PARTICIPANTS_KEY, ParseUser.getCurrentUser());
                query.include(Event.PARTICIPANTS_KEY);
                query.whereGreaterThanOrEqualTo(Event.ENDTIME_KEY, d);
                query.orderByAscending(Event.STARTTIME_KEY);
                //loading event from local database
                query.fromLocalDatastore();
                return query;
            }
        });

        main_menu_fragment = fragment;
    }

    public EventListAdapter(Context context, ParseUser user, PastEventsActivity.PastEventsFragment fragment){
        super (context,new QueryFactory<Event>() {
            @Override
            public ParseQuery<Event> create() {
                Calendar c = Calendar.getInstance();
                Date d = c.getTime();
                ParseQuery query = new ParseQuery("Event");
                query.whereEqualTo(Event.PARTICIPANTS_KEY, ParseUser.getCurrentUser());
                query.include(Event.PARTICIPANTS_KEY);
                query.whereLessThan(Event.ENDTIME_KEY, d);
                query.orderByDescending(Event.ENDTIME_KEY);
                //loading event from local database
                query.fromLocalDatastore();
                return query;
            }
        });

        past_events_fragment = fragment;
    }

    public EventListAdapter(Context context, ParseUser user, final boolean occuring){
        super (context,new QueryFactory<Event>() {
            @Override
            public ParseQuery<Event> create() {
                Calendar c = Calendar.getInstance();
                Date d = c.getTime();
                if (occuring){
                    ParseQuery<Event> query = ParseQuery.getQuery("Event");
                    query.whereLessThanOrEqualTo(Event.STARTTIME_KEY,d);
                    query.whereGreaterThanOrEqualTo(Event.ENDTIME_KEY,d);
                    query.whereEqualTo(Event.PARTICIPANTS_KEY, ParseUser.getCurrentUser());
                    query.include(Event.PARTICIPANTS_KEY);
                    query.fromLocalDatastore();
                    return query;
                } else {
                    ParseQuery<Event> beforeQuery = ParseQuery.getQuery("Event");
                    beforeQuery.whereLessThanOrEqualTo(Event.ENDTIME_KEY,d);
                    ParseQuery<Event> afterQuery = ParseQuery.getQuery("Event");
                    afterQuery.whereGreaterThanOrEqualTo(Event.STARTTIME_KEY,d);
                    List<ParseQuery<Event>> queries = new ArrayList<ParseQuery<Event>>();
                    queries.add(beforeQuery);
                    queries.add(afterQuery);
                    ParseQuery<Event> joinedQuery = ParseQuery.or(queries);
                    joinedQuery.whereEqualTo(Event.PARTICIPANTS_KEY, ParseUser.getCurrentUser());
                    joinedQuery.include(Event.PARTICIPANTS_KEY);
                    joinedQuery.fromLocalDatastore();
                    return joinedQuery;
                }
            }
        });
    }

    @Override
    public View getItemView(Event event, View view, ViewGroup parent){

        final Event current_event = event;
        SimpleDateFormat dateformat = new SimpleDateFormat("K:mm a, LLL dd", Locale.US);

        if (event.isOccuring()){
            view = View.inflate(getContext(), R.layout.list_view_active_event, null);
            TextView event_name_text_view = (TextView) view.findViewById(R.id.event_name_view);
            TextView event_time_view = (TextView) view.findViewById(R.id.event_time_view);
            ImageView camera_icon = (ImageView) view.findViewById(R.id.camera_button_view);

            event_name_text_view.setText(event.getEventName());
            event_time_view.setText("until " + dateformat.format(event.getEndTime())); // Set event time range


            camera_icon.setClickable(true);
            camera_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.MainMenuFragment.EVENT_ID = current_event.getObjectId(); // Set event ID
                    main_menu_fragment.takePhoto();
                }
            });

        } else {
            Calendar c = Calendar.getInstance();
            Date d = c.getTime();

            if (current_event.getEndTime().before(d)){
                SimpleDateFormat dateformatPast = new SimpleDateFormat("LLLL dd", Locale.US);
                view = View.inflate(getContext(), R.layout.list_view_event_over, null);
                TextView event_name_text_view = (TextView) view.findViewById(R.id.event_name_view);
                TextView event_time_view = (TextView) view.findViewById(R.id.event_time_view);

                event_name_text_view.setText(event.getEventName());
                event_time_view.setText(dateformatPast.format(event.getStartTime())); // Set event start date

            } else {
                view = View.inflate(getContext(), R.layout.list_view_event_inactive, null);
                TextView event_name_text_view = (TextView) view.findViewById(R.id.event_name_view);
                TextView event_time_view = (TextView) view.findViewById(R.id.event_time_view);

                event_name_text_view.setText(event.getEventName());
                event_time_view.setText(dateformat.format(event.getStartTime())); // Set event start date
            }
        }

        return view;
    };
}
