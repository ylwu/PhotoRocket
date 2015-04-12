package com.yask.android.photorocket;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends ActionBarActivity {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("parse","Menu Options");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_clear_local_data) {
            clearLocalData();
        }
        if (id == R.id.action_sync_events) {
            syncEventsByCurrentUser(getSupportFragmentManager().findFragmentById(R.id.container));
        }

        return super.onOptionsItemSelected(item);
    }

    public void clearLocalData() {
        Photo.unpinAllInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("parse", "cleared local data");
            }
        });
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
                                if (fragment instanceof FutureEventsFragment){
                                    ((FutureEventsFragment)fragment).eventListAdapter.loadObjects();
                                } else if (fragment instanceof  PastEventsFragment) {
                                    ((PastEventsFragment)fragment).eventListAdapter.loadObjects();
                                }

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
