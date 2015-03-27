package com.yask.android.photorocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class EventDetailActivity extends ActionBarActivity {

    public static String TEST_EVENT_ID = "yorLShkZPR";

    private PhotosAdapter photosAdapter;
    private String eventID;
    private boolean isFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().add(R.id.container, (Fragment) new DetailFragment()).commit();
        }
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Event.ID_TEXT)){
            eventID = intent.getStringExtra(Event.ID_TEXT);
            isFuture = intent.getBooleanExtra(Event.ISFUTURE_TEXT,false);
            Log.d("parse","eventID from list");
            Log.d("parse", String.valueOf(eventID));
            Log.d("parse event in future?", String.valueOf(isFuture));
        } else {
            eventID = TEST_EVENT_ID;
            Log.d("parse","didn't get event ID");
        }

    }

    @Override
    protected void onStart(){
        GridView gridView = (GridView) findViewById(R.id.gridView2);
        //put photosAdapter in a ListView or GridView
        photosAdapter = new PhotosAdapter(this, eventID);
        System.out.println(photosAdapter==null);
        System.out.println(gridView == null);
        gridView.setAdapter(photosAdapter);
        startMonitoringConnection();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        photosAdapter.loadObjects();
        Log.d("parse", "load objects");
    }

    private void loadPhotosFromParse(String eventID) {
        System.out.println("loading from parse");
        ParseQuery query = new ParseQuery("Photo");
        query.whereEqualTo(Photo.EVENT_ID_KEY, eventID);
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(List<Photo> photos, ParseException e) {
                if (e == null) {
                    Log.d("PaseEventDetailActivity","loadphotos");
                    ParseObject.pinAllInBackground((List<Photo>) photos,
                            new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("PaseEventDetailActivity","savedtolocal");
                                        if (!isFinishing()) {
                                            photosAdapter.loadObjects();
                                        }
                                    } else {
                                        Log.e("parse", "error pinning photos: " + e.getMessage());
                                    }
                                }
                            });
                } else {
                    Log.e("parse", "LoadFromParse: Error finding photos: " + e.getMessage());
                }
            }
        });
    }

    private void uploadPhotosToParse(String eventID){
        ParseQuery query = new ParseQuery("Photo");
        query.whereEqualTo(Photo.EVENT_ID_KEY,eventID);
        query.whereEqualTo(Photo.IS_SAVED_INCLOUD_KEY,false);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(List<Photo> photos, ParseException e) {
                for (final Photo photo : photos){
                    final String uriString = photo.getLocaURIString();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    final ParseFile photoFile = new ParseFile("IMG" + timeStamp + ".jpg", photo.getBytesData(getApplicationContext()));
                    photoFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                photo.clearLocalURI();
                                photo.setContent(photoFile);
                                photo.upLoadedToCloud();
                                photo.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null){
                                            Log.d("parse", "uploaded a photo");
                                        } else {
                                            Log.e("parse", "cannot upload the photo");
                                            photo.setLocalURI(uriString);
                                            photo.savedLocally();
                                        }
                                    }
                                });
                            } else {
                                Log.e("parse", "cannot save ParseFile");
                            }
                        }
                    });
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

    private class ConnectionMonitor extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
                return;
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            NetworkInfo aNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (!noConnectivity) {
                if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                        || (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                    loadPhotosFromParse(eventID);
                    stopMonitoringConnection();
                }
            } else {
                if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                        || (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                }
            }
        }
    }

    ConnectionMonitor mConnectionReceiver = new ConnectionMonitor();

    private synchronized void startMonitoringConnection() {
        IntentFilter aFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mConnectionReceiver, aFilter);
    }
    private synchronized void stopMonitoringConnection() {
        unregisterReceiver(mConnectionReceiver);
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
        if (id == R.id.action_upload){
            uploadPhotosToParse(eventID);
        }

        return super.onOptionsItemSelected(item);
    }

}
