package com.yask.android.photorocket;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ActiveEventActivity extends Activity {

    private static final String APP_NAME = "PhotoRocket";
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 1993;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_event);

        ImageButton camera_button = (ImageButton) findViewById(R.id.camera_button);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_active_event, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                // Image captured and saved
                Toast.makeText(this, "Image saved!", Toast.LENGTH_LONG).show();

                // Retrieve image uri
                Uri current_image_uri = imageUri;
                getContentResolver().notifyChange(current_image_uri, null);

                //Put photo in image view
                ImageView imageView = (ImageView) findViewById(R.id.photo_view);

                ContentResolver cr = getContentResolver();
                Bitmap imageBitmap;

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(cr, current_image_uri);
                    imageView.setImageBitmap(imageBitmap);
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
