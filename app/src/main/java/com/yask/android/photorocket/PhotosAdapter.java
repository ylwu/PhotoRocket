package com.yask.android.photorocket;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by ylwu on 3/5/15.
 */
public class PhotosAdapter extends ParseQueryAdapter<Photo>{

    public PhotosAdapter(Context context, final String eventID) {
        super(context,new QueryFactory<Photo>(){
            public ParseQuery<Photo> create() {
                ParseQuery query = new ParseQuery("Photo");
                query.whereEqualTo(Photo.EVENT_ID_KEY,eventID);
                query.fromLocalDatastore();
                return query;
            }
        });

    }
    @Override
    public View getItemView(Photo photo, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.photo_in_grid, null);
        }
        int columns = 3;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int picSize = displaymetrics.widthPixels / columns;

        super.getItemView(photo, v, parent);

        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.icon);
        Log.d("parsePhotosApapter", "find a photo");
        if (photo.isSavedInCloud()){
            ParseFile imageFile = photo.getParseFile("content");
            Log.d("parsePhotosApapter", "find a saved photo");
            if (imageFile != null) {
                todoImage.setParseFile(imageFile);
                todoImage.loadInBackground();
            }
        } else {
            //Magic number 7 is the amount of characters that needs to be truncated from the beginning of the URI to the actrual useful URI.
            //removes the 'file://' at the beginning of string.
            File img = new File(photo.getLocaUIRString().substring(7));
            System.out.println(photo.getLocaUIRString().substring(7));
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("found photo not saved");
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeStream(fis,null,options);
            todoImage.setImageBitmap(bitmap);
            todoImage.loadInBackground();

        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(picSize,picSize);
        todoImage.setLayoutParams(layoutParams);
        return v;
    }



}
