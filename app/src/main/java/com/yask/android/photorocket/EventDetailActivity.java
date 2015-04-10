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
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;

import java.util.List;


public class EventDetailActivity extends ActionBarActivity {

    private PhotosAdapter photosAdapter;
    private String eventID;
    private boolean isPast;
    private GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("parse activity", "create activity again");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        gridView = (GridView) findViewById(R.id.gridView2);
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Event.ID_TEXT)) {
            eventID = intent.getStringExtra(Event.ID_TEXT);
            isPast = intent.getBooleanExtra(Event.ISPAST_TEXT, false);
            Log.d("parse event is past", String.valueOf(intent.getBooleanExtra(Event.ISPAST_TEXT, false)));

        }
        //put photosAdapter in a ListView or GridView
        photosAdapter = new PhotosAdapter(this, eventID);

        photosAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Photo>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(List<Photo> photos, Exception e) {
                for (Photo p: photos){
                    Log.d("parse adpater photo id", p.getObjectId());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("parse", "detail activity destroyed");
    }

    @Override
    protected void onStart() {
        if (isPast) {
            loadPhotosFromParse(eventID);
        } else {
            gridView.setAdapter(photosAdapter);
        }
        super.onStart();
    }

    private void loadPhotosFromParse(final String eventID) {
        final ParseQuery query = new ParseQuery("Photo");
        query.whereEqualTo(Photo.EVENT_ID_KEY, eventID);
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(final List<Photo> photos, ParseException e) {
                Log.d("parse event id", eventID);
                Log.d("num of remote photos", String.valueOf(photos.size()));
                if (e == null) {
                    final ParseQuery localQuery = new ParseQuery("Photo");
                    localQuery.whereEqualTo(Photo.EVENT_ID_KEY, eventID);
                    localQuery.fromLocalDatastore();
                    localQuery.findInBackground(new FindCallback<Photo>() {
                        @Override
                        public void done(List<Photo> localPhotos, ParseException e) {
                            Log.d("parse num local photos", String.valueOf(localPhotos.size()));
                            if (photos.size() != localPhotos.size()) {
                                for (Photo p: photos){
                                    Log.d("parse fetching photo id", p.getObjectId());
                                }
                                //there are new remote photos
                                Log.d("parse", "new photos coming");
                                Photo.pinAllInBackground((List<Photo>) photos,
                                        new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Log.d("ParseEventDetail", "savedtolocal");
                                                    if (!isFinishing()) {
                                                        Log.d("ParseEventDetail", "reload");
                                                        photosAdapter.loadObjects();
                                                        gridView.setAdapter(photosAdapter);
                                                    }
                                                } else {
                                                    Log.e("parse", "error pinning photos: " + e.getMessage());
                                                }
                                            }
                                        });
                            } else {
                                photosAdapter.loadObjects();
                                gridView.setAdapter(photosAdapter);
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
