package com.yask.android.photorocket;

import android.app.Application;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by ylwu on 3/4/15.
 */
public class PhotoRocketApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("parse", "start application");

        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(EventToUpload.class);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "oAfNdFvHufxJJbgX1577YXmFT531UZxq54UjSJrD", "e53mYyUhmRBEZQi9hJumUK90TTxQJK20sDV5XCiV");

        if (ParseUser.getCurrentUser() == null){
            Log.d("parse user", "creating user for the first time");
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        Log.d("MyApp", "Anonymous login failed.");
                    } else {
                        Log.d("MyApp", "Anonymous user logged in.");
                    }
                }
            });
        } else {
            Log.d("parse user", "getting anoynymous user from app");
        }
    }
}
