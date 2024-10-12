package pers.sweven.common.app;

import android.app.Application;

import pers.sweven.common.GlobalApp;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalApp.setInstance(this).registerActivity();
        GlobalApp.getInstance().initCacheManager();
    }
}