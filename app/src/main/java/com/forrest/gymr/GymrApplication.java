package com.forrest.gymr;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by Forrest on 5/11/15.
 * Application Layer that when any Activity is run, initializes the Parse Local Datastore and Facebook Utils required for user interaction
 */
public class GymrApplication extends Application {

    public static final String TAG = "GymrApplication";
    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, "qhnKfVz7X9X6OEBVibgscZwDxMdNSQRMK8tP9Lmw", "PZ1lPqxn3qF3M3T6yNxvCiszcmasjhX5l4ypTkkJ");


        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        ParseFacebookUtils.initialize(getApplicationContext());
    }

}