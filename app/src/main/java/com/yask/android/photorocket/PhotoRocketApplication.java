package com.yask.android.photorocket;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crittercism.app.Crittercism;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ylwu on 3/4/15.
 */
public class PhotoRocketApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ParseCrashReporting.enable(this);
        Crittercism.initialize(getApplicationContext(), "553c471f7365f84f7d3d6fa8");
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(EventToUpload.class);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "oAfNdFvHufxJJbgX1577YXmFT531UZxq54UjSJrD", "e53mYyUhmRBEZQi9hJumUK90TTxQJK20sDV5XCiV");

        if (ParseUser.getCurrentUser() == null){
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                }
            });
        }
    }
}
