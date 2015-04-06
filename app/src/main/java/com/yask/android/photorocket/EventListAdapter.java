package com.yask.android.photorocket;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ylwu on 3/5/15.
 */
public class EventListAdapter extends ParseQueryAdapter<Event> {

    Fragment eventFragment;

    public EventListAdapter(Context context, Fragment fragment, final boolean isPast){
        super (context,new QueryFactory<Event>() {
            @Override
            public ParseQuery<Event> create() {
                Calendar c = Calendar.getInstance();
                Date d = c.getTime();
                ParseQuery query = new ParseQuery("Event");
                query.whereEqualTo(Event.PARTICIPANTS_KEY, ParseUser.getCurrentUser());
                query.include(Event.PARTICIPANTS_KEY);
                if (isPast){
                    query.whereLessThan(Event.ENDTIME_KEY, d);
                    query.orderByDescending(Event.ENDTIME_KEY);
                } else {
                    query.whereGreaterThanOrEqualTo(Event.ENDTIME_KEY, d);
                    query.orderByAscending(Event.STARTTIME_KEY);

                }
                //loading event from local database
                query.fromLocalDatastore();
                return query;
            }
        });
        eventFragment = fragment;
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
                    //TODO: fix following line
                    FutureEventsFragment.EVENT_ID = current_event.getObjectId(); // Set event ID
                    ((FutureEventsFragment)eventFragment).takePhoto();
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
