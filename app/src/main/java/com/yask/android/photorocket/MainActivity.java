package com.yask.android.photorocket;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


//        Uncomment this to add a new event
//        Calendar c = Calendar.getInstance();
//        Date now = c.getTime();
//        c.roll(Calendar.HOUR,2);
//        Date twoHoursLater = c.getTime();
//        Event event = new Event("Celtics Game",now,twoHoursLater);
//        event.addParticipant(ParseUser.getCurrentUser());
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
        if (id == R.id.action_upload_photo) {
            new FetchAndUploadPhotoTask().execute();
        }

        return super.onOptionsItemSelected(item);
    }



    public void openCamera(){

    }

    //Helper function to save photo
    protected void savePhoto(final String eventID, byte[] imageData) {
        final ParseFile photoFile = new ParseFile("photo_0", imageData);
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Photo photo = new Photo(eventID,photoFile);
                photo.saveInBackground();
            }
        });
    }

    //This is just for testing purpose, don't touch it
    class FetchAndUploadPhotoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                uploadphoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /*
        Testing function to fill database, testing purpose as well
     */
    public void uploadphoto() throws IOException {

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://labs.yahoo.com/_c/uploads/fbentley/avatar/frankmot.jpg");
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
        final ParseFile photoFile = new ParseFile("Frank.jpg", imageBlob);
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Photo photo = new Photo(EventDetailActivity.TEST_EVENT_ID,photoFile);
                photo.saveInBackground();
            }
        });

    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private EventListAdapter eventListAdapter;

        private static final String APP_NAME = "PhotoRocket";
        private static final int MEDIA_TYPE_IMAGE = 1;
        private static final int MEDIA_TYPE_VIDEO = 2;
        private static final int CAPTURE_IMAGE_REQUEST_CODE = 1993;
        private static final String EVENT_ID = "yorLShkZPR";
        private Uri imageUri;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            eventListAdapter = new EventListAdapter(this.getActivity(),ParseUser.getCurrentUser());
            eventListAdapter.setTextKey(Event.NAME_KEY);
            ListView eventListView = (ListView) rootView.findViewById(R.id.listview_main);
            if (eventListView == null){
                Log.d("parse", "listView null");
            }
            eventListView.setAdapter(eventListAdapter);

            eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event event = eventListAdapter.getItem(position);
                    Intent intent = new Intent(view.getContext(),EventDetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT,event.getObjectId());
                    startActivity(intent);
                }
            });
            // Camera stuff
            ImageButton camera_button = (ImageButton) rootView.findViewById(R.id.camera_button);
            camera_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePhoto(v);
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

                    // Retrieve image uri
                    Uri current_image_uri = imageUri;
                    this.getActivity().getContentResolver().notifyChange(current_image_uri, null);

                    //Put photo in image view
                    ImageView imageView = (ImageView) getView().findViewById(R.id.photo_view);

                    ContentResolver cr = this.getActivity().getContentResolver();
                    Bitmap imageBitmap;

                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(cr, current_image_uri);
                        imageView.setImageBitmap(imageBitmap);

                        //Convert bitmap to byte array
                        int bytes = imageBitmap.getByteCount();
                        ByteBuffer buffer = ByteBuffer.allocate(bytes);
                        imageBitmap.copyPixelsToBuffer(buffer);
                        byte[] imageArray = buffer.array();

                        ((MainActivity) getActivity()).savePhoto(EVENT_ID, imageArray);

                    } catch (Exception e){
                        Log.e("IMAGE_CAPTURE", e.toString());
                    }

                }
            }
        }

        private void takePhoto(View v){
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
}
