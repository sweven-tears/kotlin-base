package pers.sweven.common.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import pers.sweven.common.utils.cache.CacheManager;

public class BaseApplication extends Application {
    private static Context mContext;
    public static boolean DEBUG;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        CacheManager.init(this);

        //注册监听每个activity的生命周期,便于堆栈式管理
        registerActivityLifecycleCallbacks(mCallbacks);
    }

    public void setDEBUG(boolean debug) {
        DEBUG = debug;
    }

    public static Context getContext() {
        return mContext;
    }


    private final ActivityLifecycleCallbacks mCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            AppManager.getInstance().addActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            AppManager.getInstance().removeActivity(activity);
        }
    };


}