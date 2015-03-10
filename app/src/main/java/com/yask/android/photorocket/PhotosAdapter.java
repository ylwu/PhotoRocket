package com.yask.android.photorocket;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

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
        if (photo.isSavedInCloud()){
            ParseFile imageFile = photo.getParseFile("content");
            if (imageFile != null) {
                todoImage.setParseFile(imageFile);
                todoImage.loadInBackground();
            }
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(picSize,picSize);
        todoImage.setLayoutParams(layoutParams);
        return v;
    }



}
