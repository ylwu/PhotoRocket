package com.yask.android.photorocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.GridView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class EventDetailActivity extends ActionBarActivity {

    private PhotosAdapter photosAdapter;
    private String eventName;
    private String eventID;
    private boolean isPast;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        gridView = (GridView) findViewById(R.id.gridView2);
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Event.ID_TEXT)) {
            eventID = intent.getStringExtra(Event.ID_TEXT);
            isPast = intent.getBooleanExtra(Event.ISPAST_TEXT, false);
            eventName = intent.getStringExtra(Event.NAME_KEY);
        }
        photosAdapter = new PhotosAdapter(this, eventID);
        setTitle(eventName);
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
                if (e == null) {
                    final ParseQuery localQuery = new ParseQuery("Photo");
                    localQuery.whereEqualTo(Photo.EVENT_ID_KEY, eventID);
                    localQuery.fromLocalDatastore();
                    localQuery.findInBackground(new FindCallback<Photo>() {
                        @Override
                        public void done(List<Photo> localPhotos, ParseException e) {
                            if (photos.size() != localPhotos.size()) {
                                //there are new remote photos
                                Photo.pinAllInBackground((List<Photo>) photos,
                                        new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    if (!isFinishing()) {
                                                        photosAdapter.loadObjects();
                                                        gridView.setAdapter(photosAdapter);
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                photosAdapter.loadObjects();
                                gridView.setAdapter(photosAdapter);
                            }
                        }
                    });


                }
            }
        });
    }

}
