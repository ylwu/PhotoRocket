package com.yask.android.photorocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class EventDetailActivity extends ActionBarActivity {

    public static String TEST_EVENT_ID = "yorLShkZPR";

    private PhotosAdapter photosAdapter;
    private String eventID;
    private boolean isPast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("parse activity", "create activity again");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        GridView gridView = (GridView) findViewById(R.id.gridView2);
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Event.ID_TEXT)) {
            eventID = intent.getStringExtra(Event.ID_TEXT);
            isPast = intent.getBooleanExtra(Event.ISPAST_TEXT, false);
            Log.d("parse event is past", String.valueOf(intent.getBooleanExtra(Event.ISPAST_TEXT, false)));

        }
        //put photosAdapter in a ListView or GridView
        photosAdapter = new PhotosAdapter(this, eventID);
        gridView.setAdapter(photosAdapter);
    }

    @Override
    protected void onStart() {
        if (isPast) loadPhotosFromParse(eventID);
        super.onStart();
    }

    private void loadPhotosFromParse(String eventID) {
        final ParseQuery query = new ParseQuery("Photo");
        query.whereEqualTo(Photo.EVENT_ID_KEY, eventID);
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(final List<Photo> photos, ParseException e) {
                if (e == null) {
                    query.fromLocalDatastore();
                    query.findInBackground(new FindCallback<Photo>() {
                        @Override
                        public void done(List<Photo> localPhotos, ParseException e) {
                            Log.d("parse num photos", String.valueOf(photos.size()));
                            if (photos.size() != localPhotos.size()) {
                                //there are new remote photos
                                Log.d("parse", "new photos coming");
                                ParseObject.pinAllInBackground((List<Photo>) photos,
                                        new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Log.d("ParseEventDetail", "savedtolocal");
                                                    if (!isFinishing()) {
                                                        photosAdapter.loadObjects();
                                                    }
                                                } else {
                                                    Log.e("parse", "error pinning photos: " + e.getMessage());
                                                }
                                            }
                                        });
                            }
                        }
                    });


                } else {
                    Log.e("parse", "LoadFromParse: Error finding photos: " + e.getMessage());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_load) {
            loadPhotosFromParse(eventID);
        }
        if (id == R.id.action_upload) {
            Utils.uploadPhotosToParse(eventID);
        }

        return super.onOptionsItemSelected(item);
    }

}
