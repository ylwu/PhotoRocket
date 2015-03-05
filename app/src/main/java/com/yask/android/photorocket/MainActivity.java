package com.yask.android.photorocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Log.d("parse","hello");

//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.put("author", ParseUser.getCurrentUser());
//        testObject.saveInBackground();
//        Event event = new Event("foo2");
//        event.addUser(ParseUser.getCurrentUser());
//        event.saveInBackground();
    }

    private void getAllEventsByCurrentUser(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        Log.d("parse",ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("participants", ParseUser.getCurrentUser());
        query.include("participants");
        final List<Event> eventList = new ArrayList<Event>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null){
                    for (ParseObject object : parseObjects){
                        eventList.add((Event)object);
                        Log.d("parse", "add once");
                    }
                    Toast toast = Toast.makeText(getApplicationContext(),
                            String.valueOf(eventList.size()),Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Log.e("parse",e.getLocalizedMessage());
                    Log.e("parse", "cannot retrieve events");
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera){
            openCamera();
        }
        if (id == R.id.action_event_detail){
            startActivity(new Intent(this,EventDetailActivity.class));
        }
        if (id == R.id.action_newEvent){
            startActivity(new Intent(this,NewEventActivity.class));
        }
        if (id == R.id.action_sync_events) {
            getAllEventsByCurrentUser();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openCamera(){

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
