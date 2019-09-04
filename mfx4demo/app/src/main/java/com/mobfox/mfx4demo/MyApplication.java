package com.mobfox.mfx4demo;

import android.app.Application;

import com.mobfox.android.MobfoxSDK;

public class MyApplication extends Application {
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MobfoxSDK.init(this);
    }
}
