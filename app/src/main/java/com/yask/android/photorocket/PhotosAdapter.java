package com.yask.android.photorocket;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.parse.ParseFile;
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

    public static Bitmap decodeByte(byte[] bytes, int WIDTH, int HEIGHT){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, o);
            final int REQUIRED_WIDTH=WIDTH;
            final int REQUIRED_HIGHT=HEIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, o2);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeStream(FileInputStream f,int WIDTH,int HIGHT){
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(f,null,o);

        //The new size we want to scale to
        final int REQUIRED_WIDTH=WIDTH;
        final int REQUIRED_HIGHT=HIGHT;
        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        return BitmapFactory.decodeStream(f, null, o2);
    }

    @Override
    public View getItemView(Photo photo, View v, ViewGroup parent) {
        Log.d("parse", "get photo view");
        if (v == null) {
            v = View.inflate(getContext(), R.layout.photo_in_grid, null);
            int columns = 3;
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int picSize = displaymetrics.widthPixels / columns;

            super.getItemView(photo, v, parent);

            // Add and download the image
            ImageView todoImage = (ImageView) v.findViewById(R.id.icon);
            Log.d("parsePhotosApapter", "find a photo");
            if (photo.isSavedInCloud()) {
                ParseFile imageFile = photo.getParseFile("content");
                Log.d("parsePhotosApapter", "find a saved photo");
                if (imageFile != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    try {

                        //imageFile.getData();

                    Bitmap bitmap = decodeByte(imageFile.getData(), picSize, picSize);
                    todoImage.setImageBitmap(bitmap);
                    if (bitmap != null) {
                        //bitmap.recycle();
                    }
                    //bitmap = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //Magic number 7 is the amount of characters that needs to be truncated from the beginning of the URI to the actrual useful URI.
                //removes the 'file://' at the beginning of string.
                File img = new File(photo.getLocaURIString().substring(7));
                System.out.println(photo.getLocaURIString().substring(7));
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(img);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("found photo not saved");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
                todoImage.setImageBitmap(bitmap);
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(picSize, picSize);
            todoImage.setLayoutParams(layoutParams);
            return v;
        }
        else {
            return v;
        }
    }
}
