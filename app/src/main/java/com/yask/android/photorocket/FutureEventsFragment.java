package com.yask.android.photorocket;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FutureEventsFragment extends android.support.v4.app.Fragment {

    public EventListAdapter eventListAdapter;
    public ListView eventListView;

    private static final String APP_NAME = "PhotoRocket";
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 1993;
    public static String EVENT_ID;
    private Uri imageUri;
    private boolean isPast;

    public FutureEventsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("parse future events", "create future view");

        // Make user go back and forth between main and past doesn't do weird stuff
//            getActivity().getSupportFragmentManager().popBackStack("main", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        eventListAdapter = new EventListAdapter(this.getActivity(), FutureEventsFragment.this, false);
        eventListAdapter.setTextKey(Event.NAME_KEY);
        eventListView = (ListView) rootView.findViewById(R.id.listview_main);
        if (eventListView == null){
            Log.d("parse", "listView null");
        }
        eventListView.setAdapter(eventListAdapter);

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = eventListAdapter.getItem(position);
                EVENT_ID = event.getObjectId();
                    /*+*/

                Intent intent;

                if (! event.isFuture()) {

                    intent = new Intent(view.getContext(), EventDetailActivity.class)
                            .putExtra(Event.ID_TEXT, event.getObjectId())
                            .putExtra(Event.ISFUTURE_TEXT, event.isFuture())
                            .putExtra(Event.ISPAST_TEXT, event.isPast())
                            .putExtra(Event.NAME_KEY, event.getEventName());
                } else {

                    Date ssstart = event.getStartTime();
                    Calendar scal = Calendar.getInstance();
                    scal.setTime(ssstart);
                    int ssyear = scal.get(Calendar.YEAR);
                    int ssmonth = scal.get(Calendar.MONTH);
                    int ssday = scal.get(Calendar.DAY_OF_MONTH);
                    int sshr = scal.get(Calendar.HOUR_OF_DAY);
                    int ssmin = scal.get(Calendar.MINUTE);

                    String ssstrhr;
                    String ssstrmin;
                    String ssstrmn;
                    String ssstrdt;
                    if (ssday < 10){
                        ssstrdt = "0" + ssday;
                    } else {
                        ssstrdt = "" + ssday;
                    }
                    if (ssmonth < 9){
                        ssstrmn = "0" + (ssmonth + 1);
                    } else {
                        ssstrmn = "" + (ssmonth + 1);
                    }
                    if (sshr < 10){
                        ssstrhr = "0" + sshr;
                    } else {
                        ssstrhr = "" + sshr;
                    }
                    if (ssmin < 10){
                        ssstrmin = "0" + ssmin;
                    } else {
                        ssstrmin = "" + ssmin;
                    }

                    Date eeend = event.getEndTime();
                    Calendar ecal = Calendar.getInstance();
                    ecal.setTime(eeend);
                    int eeyear = ecal.get(Calendar.YEAR);
                    int eemonth = ecal.get(Calendar.MONTH);
                    int eeday = ecal.get(Calendar.DAY_OF_MONTH);
                    int eehr = ecal.get(Calendar.HOUR_OF_DAY);
                    int eemin = ecal.get(Calendar.MINUTE);

                    Log.d("aaa", scal.toString());
                    Log.d("bbb", ecal.toString());

                    String eestrhr;
                    String eestrmin;
                    String eestrmn;
                    String eestrdt;
                    if (eeday < 10){
                        eestrdt = "0" + eeday;
                    } else {
                        eestrdt = "" + eeday;
                    }
                    if (eemonth < 9){
                        eestrmn = "0" + (eemonth + 1);
                    } else {
                        eestrmn = "" + (eemonth + 1);
                    }
                    if (eehr < 10){
                        eestrhr = "0" + eehr;
                    } else {
                        eestrhr = "" + eehr;
                    }
                    if (eemin < 10){
                        eestrmin = "0" + eemin;
                    } else {
                        eestrmin = "" + eemin;
                    }

                    intent = new Intent(view.getContext(), NewEventActivity.class)
                            .putExtra(Event.NAME_KEY, event.getEventName())
                            .putExtra(Event.STARTTIME_KEY, ssyear + "-" + ssstrmn + "-" + ssstrdt + " " + ssstrhr + ":" + ssstrmin)
                            .putExtra(Event.ENDTIME_KEY, eeyear + "-" + eestrmn + "-" + eestrdt + " " + eestrhr + ":" + eestrmin)
                            .putExtra(Event.ID_TEXT, event.getObjectId())
                            .putExtra(Event.ISOCCURING_TEXT, event.isOccuring())
                            .putExtra(Event.ISPAST_TEXT, event.isPast())
                            .putExtra(Event.ISFUTURE_TEXT, event.isFuture());
                    Log.d("aaa", ssyear + "-" + ssstrmn + "-" + ssstrdt + " " + ssstrhr + ":" + ssstrmin);
                    Log.d("bbb", eeyear + "-" + eestrmn + "-" + eestrdt + " " + eestrhr + ":" + eestrmin);
                }
                startActivity(intent);
            }
        });

        ImageButton add_new_event_button = (ImageButton) rootView.findViewById(R.id.add_new_event_button);

        add_new_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /*+*/
                Intent i = new Intent(v.getContext(), NewEventActivity.class);
                startActivity(i);
//                    startActivity(new Intent(v.getContext(), NewEventActivity.class));
            }
        });

        ImageButton past_event_button = (ImageButton) rootView.findViewById(R.id.history_button);

        past_event_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Create new fragment and transaction
                android.support.v4.app.Fragment newFragment = new PastEventsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack("main");

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
            if (resultCode == Activity.RESULT_OK){
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
