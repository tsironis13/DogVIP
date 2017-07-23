package com.tsiro.dogvip.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tsiro.dogvip.utilities.MyLifecycleHandler;

/**
 * Created by giannis on 23/7/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
