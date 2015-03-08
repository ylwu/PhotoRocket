package com.yask.android.photorocket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class EventDetailActivity extends ActionBarActivity {

    public static String TEST_EVENT_ID = "yorLShkZPR";

    private PhotosAdapter photosAdapter;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().add(R.id.container, (Fragment) new DetailFragment()).commit();
        }
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            eventID = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.d("parse","eventID from list");
            Log.d("parse", String.valueOf(eventID));
        } else {
            eventID = TEST_EVENT_ID;
            Log.d("parse","didn't get event ID");
        }

    }

    @Override
    protected void onStart(){
        GridView gridView = (GridView) findViewById(R.id.gridView2);
        //put photosAdapter in a ListView or GridView
        photosAdapter = new PhotosAdapter(this,TEST_EVENT_ID);
        System.out.println(photosAdapter==null);
        System.out.println(gridView == null);
        gridView.setAdapter(photosAdapter);
        super.onStart();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
