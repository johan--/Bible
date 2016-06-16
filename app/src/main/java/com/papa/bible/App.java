package com.papa.bible;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Papa on 2016/4/20.
 */
public class App extends Application {

    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Stetho.initializeWithDefaults(this);
    }
}
