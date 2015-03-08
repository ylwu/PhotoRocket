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
            getSupportFragmentManager().beginTransaction().add(R.id.container, (Fragment) new DetailFragment()).commit();
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

        //put photosAdapter in a ListView or GridView
        photosAdapter = new PhotosAdapter(this,eventID);

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

            return rootView;
        }

        @Override
        public void onStart() {
            File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
            int imageCount = 0;
            int columnCount = 0;
            int columns = 3;
            ImageView[] prevRow = new ImageView[columns];
            ImageView[] currRow = new ImageView[columns];

            Display display = getWindowManager().getDefaultDisplay();
            int width = 300;
            int height =300;

            System.out.println("----------------------------------------------");

            List<File> imageList = fileList(imageDir);
            System.out.println("----------------------------------------------");

            for (File image : imageList) {
                System.out.println(image.toString());
                if (image.isDirectory()) {
                    continue;
                }
                if (imageCount >= 10){
                    break;
                }
                imageCount += 1;
                if (columnCount == 0) {
                    currRow[columnCount] = createImageView(image, (RelativeLayout) findViewById(R.id.eventDetail), prevRow[columnCount], null, width, height);
                    columnCount++;
                } else {
                    currRow[columnCount] = createImageView(image, (RelativeLayout) findViewById(R.id.eventDetail), prevRow[columnCount], currRow[columnCount - 1], width, height);
                    columnCount++;
                    if (columnCount == columns) {
                        columnCount = 0;
                        prevRow = currRow;
                    }
                }
            }
            super.onStart();
        }

        private int imageId = 100;


        /*
            returns all files at root directory recursively.
         */
        private List<File> fileList(File root){
            List<File> fileList = new ArrayList<>();
            fileListHelper(root, fileList);
            return fileList;
        }

        private void fileListHelper(File root, List<File> fileList){
            if (root.isDirectory()){
                for(File file: root.listFiles()){
                    if (file.isDirectory()){
                        fileListHelper(file, fileList);
                    } else {
                        fileList.add(file);
                    }
                }
            } else {
                fileList.add(root);
            }

        }

        private ImageView createImageView(File imageFile, RelativeLayout parentLayout, ImageView aboveView, ImageView leftView, int width, int height){
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setId(imageId);
            imageId ++;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true);
            imageView.setImageBitmap(bitmap);
            //bitMap.recycle();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            if (aboveView != null) {
                System.out.println("this happened");
                System.out.println(aboveView.getId());
                layoutParams.addRule(RelativeLayout.BELOW, aboveView.getId());
            } else {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            if (leftView != null) {
                layoutParams.addRule(RelativeLayout.RIGHT_OF, leftView.getId());
            } else {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            }
            parentLayout.addView(imageView,layoutParams);
            return imageView;
        }

    }
}
