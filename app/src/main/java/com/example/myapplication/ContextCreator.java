package com.example.myapplication;

import android.app.Application;
import android.content.Context;

public class ContextCreator extends Application {
    //this class is used for creating Context instances

    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}