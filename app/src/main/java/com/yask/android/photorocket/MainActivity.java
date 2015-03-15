package com.yask.android.photorocket;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainMenuFragment())
                    .commit();
        }
        Log.e("parse", "HELLO");


//        Uncomment this to add a new event
//        Calendar c = Calendar.getInstance();
//        Date now = c.getTime();
//        c.roll(Calendar.HOUR,2);
//        Date twoHoursLater = c.getTime();
//        Event event = new Event("Celtics Game",now,twoHoursLater);
//        event.addParticipant(ParseUser.getCurrentUser());
//        event.saveInBackground();
    }

    /*
        Download events from cloud and save to local database
     */
    private void syncEventsByCurrentUser(){
        ParseQuery<Event> query = new ParseQuery<Event>("Event");
        Log.d("parse",ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("participants", ParseUser.getCurrentUser());
        query.include("participants");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null){
                    ParseObject.pinAllInBackground(events,new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                Log.d("parse MainActivity", "synced events");
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_event_detail){
            startActivity(new Intent(this, EventDetailActivity.class));
        }
        if (id == R.id.action_newEvent){
            startActivity(new Intent(this,NewEventActivity.class));
        }
        if (id == R.id.action_sync_events) {
            syncEventsByCurrentUser();
        }
        if (id == R.id.action_save_photo) {
            new FetchAndSavePhotoLocallyTask().execute();
        }
        if (id == R.id.action_clear_local_data) {
            clearLocalData();
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

    //Helper function to save photo
    protected void savePhoto(final String eventID, byte[] imageData) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final ParseFile photoFile = new ParseFile("IMG" + timeStamp + ".jpg", imageData);
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Photo photo = new Photo(eventID, photoFile);
                photo.saveInBackground();

            }
        });
    }

    protected void savePhotoLocally(final String eventID, String locaImageURI){
        Photo photo = new Photo(eventID,locaImageURI);
        photo.pinInBackground();
    }


    /*
        This is an asyncTask that checks if there is an event at a certain time. It calls
        Utils.getEventIDForTime in background. It is a good model to follow for any tasks that need
        to get current eventID first, since that needs to be done in background.
     */

    class CheckEventTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTimeInMillis(0);
            cal.set(2015, Calendar.MARCH, 6, 2, 9, 0);

            Date d = cal.getTime();
            Log.d("parseDate",d.toString());
            Log.d("parse","test test");
            return Utils.getEventIDForTime(d);
        }

        @Override
        protected void onPostExecute(String eventId) {
            String toastString;
            if (eventId == null){
                toastString = "No Event";
            } else if (eventId == Utils.MORE_THAN_ONE_EVENT_ERROR){
                toastString = Utils.MORE_THAN_ONE_EVENT_ERROR;
            } else {
                toastString = eventId;
            }
            Toast toast = Toast.makeText(getApplicationContext(),toastString,Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //This is just for testing purpose, don't touch it
    class FetchAndSavePhotoLocallyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                savephotoLocally();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /*
        Testing function to fill database, testing purpose as well
     */
    public void savephotoLocally() throws IOException {

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://www.theinquirer.net/IMG/726/297726/apple-watch-sports-band-white-540x334.png?1410346517");
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        int imageLength = (int)(entity.getContentLength());
        InputStream is = entity.getContent();

        byte[] imageBlob = new byte[imageLength];
        int bytesRead = 0;
        while (bytesRead < imageLength) {
            int n = is.read(imageBlob, bytesRead, imageLength - bytesRead);
            if (n <= 0)
                ; // do some error handling
            bytesRead += n;
        }
        final ParseFile photoFile = new ParseFile("iWatch.jpg", imageBlob);
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Photo photo = new Photo(EventDetailActivity.TEST_EVENT_ID,photoFile);
                    photo.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("parse", "saved locally");
                            } else {
                                Log.e("parse", e.getLocalizedMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainMenuFragment extends Fragment {

        private EventListAdapter eventListAdapter;

        private static final String APP_NAME = "PhotoRocket";
        private static final int MEDIA_TYPE_IMAGE = 1;
        private static final int MEDIA_TYPE_VIDEO = 2;
        private static final int CAPTURE_IMAGE_REQUEST_CODE = 1993;
        public static String EVENT_ID;
        private Uri imageUri;
        private boolean isPast;

        public MainMenuFragment() {

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            eventListAdapter = new EventListAdapter(this.getActivity(), MainMenuFragment.this, false);
            eventListAdapter.setTextKey(Event.NAME_KEY);
            final ListView eventListView = (ListView) rootView.findViewById(R.id.listview_main);
            if (eventListView == null){
                Log.d("parse", "listView null");
            }
            eventListView.setAdapter(eventListAdapter);

            eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event event = eventListAdapter.getItem(position);
                    EVENT_ID = event.getObjectId();
                    Intent intent = new Intent(view.getContext(),EventDetailActivity.class)
                            .putExtra(Event.ID_TEXT,event.getObjectId()).putExtra(Event.ISFUTURE_TEXT,event.isFuture());
                    startActivity(intent);
                }
            });

            ImageButton add_new_event_button = (ImageButton) rootView.findViewById(R.id.add_new_event_button);

            add_new_event_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    startActivity(new Intent(v.getContext(), NewEventActivity.class));
                }
            });

            ImageButton past_event_button = (ImageButton) rootView.findViewById(R.id.history_button);

            past_event_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                // Create new fragment and transaction
                Fragment newFragment = new PastEventsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                }
            });

            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAPTURE_IMAGE_REQUEST_CODE){
                if (resultCode == RESULT_OK){
                    // Image captured and saved
                    Toast.makeText(getActivity(), "Image saved!", Toast.LENGTH_LONG).show();

                    // Retrieve imagfe uri
                    Uri current_image_uri = imageUri;

                    // Save to local Parse database
                    ((MainActivity) getActivity()).savePhotoLocally(EVENT_ID, current_image_uri.toString());
                    Log.d("saved photo", current_image_uri.toString());

                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            eventListAdapter.loadObjects();
        }

        public void takePhoto(){
            Intent open_camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageUri = Uri.fromFile(createImageFolder(MEDIA_TYPE_IMAGE));
            open_camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(open_camera_intent, CAPTURE_IMAGE_REQUEST_CODE);
        }

        private static File createImageFolder(int type){

            // Create file for saving images
            File photo_rocket_dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_NAME);

            // Create storage directory if it does not exist
            if (!photo_rocket_dir.exists()){
                if (!photo_rocket_dir.mkdirs()){
                    Log.d(APP_NAME, "failed to create directory");
                }
            }

            // Create media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = null;

            if (type == MEDIA_TYPE_IMAGE){
                mediaFile = new File(photo_rocket_dir.getPath() + File.separator + "IMG" + timeStamp + ".jpg");
            }

            return mediaFile;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PastEventsFragment extends Fragment {

        private EventListAdapter eventListAdapter;

        public PastEventsFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_past_events, container, false);
            eventListAdapter = new EventListAdapter(this.getActivity(),PastEventsFragment.this,true);
            eventListAdapter.setTextKey(Event.NAME_KEY);
            final ListView eventListView = (ListView) rootView.findViewById(R.id.listview_past_events);
            if (eventListView == null){
                Log.d("parse", "listView null");
            }
            eventListView.setAdapter(eventListAdapter);

            eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event event = eventListAdapter.getItem(position);
                    Intent intent = new Intent(view.getContext(),EventDetailActivity.class)
                            .putExtra(Event.ID_TEXT,event.getObjectId()).putExtra(Event.ISFUTURE_TEXT,event.isFuture());
                    startActivity(intent);
                }
            });

            ImageButton back_button = (ImageButton) rootView.findViewById(R.id.back_button);
            back_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Fragment newFragment = new MainMenuFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, newFragment);
                    transaction.commit();
                }
            });

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            eventListAdapter.loadObjects();
        }
    }
}
