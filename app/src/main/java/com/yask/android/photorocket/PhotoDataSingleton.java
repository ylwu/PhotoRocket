package com.yask.android.photorocket;

import android.graphics.Bitmap;

/**
 * Created by Tony on 2015/4/12.
 * Stores the data of one single photo using the singleton pattern.
 */
public class PhotoDataSingleton {
    private Bitmap data;
    public Bitmap getData() {return data;}
    public void setData(Bitmap data) {this.data = data;}

    private static final PhotoDataSingleton holder = new PhotoDataSingleton();
    public static PhotoDataSingleton getInstance() {return holder;}
}

