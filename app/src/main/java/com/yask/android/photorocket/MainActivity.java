package com.yask.android.photorocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    public static final String ACTION_RESET = "photorocket.mainactivity.RESET";

    public BroadcastReceiver resetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            refreshFragment(fragment);
        }
    };

    public void refreshFragment(Fragment fragment){
        if (fragment instanceof FutureEventsFragment){
            ((FutureEventsFragment)fragment).eventListAdapter.loadObjects();
        } else if (fragment instanceof  PastEventsFragment) {
            ((PastEventsFragment)fragment).eventListAdapter.loadObjects();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FutureEventsFragment futureEventsFragment = new FutureEventsFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, futureEventsFragment)
                    .commit();
        }
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // used magic number 15 to get rid of prefix
            joinEvent(intent.getDataString().substring(15), futureEventsFragment);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(resetReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(resetReceiver,new IntentFilter(ACTION_RESET));
    }

    protected void savePhotoLocally(final String eventID, String locaImageURI){
        Photo photo = new Photo(eventID,locaImageURI);
        photo.pinInBackground();
    }

    public void joinEvent(String eventID, final FutureEventsFragment fragment) {
        ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.getInBackground(eventID,new GetCallback<Event>() {
            @Override
            public void done(final Event event, ParseException e) {
                if (e == null) {
                    event.addParticipant(ParseUser.getCurrentUser());
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("parse user", "succesfully add participant");
                                NotificationAlarmReceiver.setAlarm(getApplicationContext(), event.getStartTime());
                                UploadAlarmReceiver.setAlarm(getApplicationContext(), event.getEndTime(), event.getObjectId());
                                syncEventsByCurrentUser(fragment);
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

    /*
        Download future events from cloud and save to local database
     */
    public void syncEventsByCurrentUser(final Fragment fragment){
        ParseQuery<Event> query = new ParseQuery<Event>("Event");
        query.whereEqualTo("participants", ParseUser.getCurrentUser());
        query.include("participants");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null){
                    ParseObject.pinAllInBackground(events, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                refreshFragment(fragment);
                            } else {
                                Log.e("parse MainActivity", e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    Log.e("parse", e.getLocalizedMessage());
                    Log.e("parse", "cannot retrieve events");
                }
            }
        });
    }
}
