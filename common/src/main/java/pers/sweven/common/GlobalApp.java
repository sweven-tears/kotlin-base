package pers.sweven.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import pers.sweven.common.app.AppManager;
import pers.sweven.common.utils.cache.CacheManager;

/**
 * 全局初始化
 * Created by Sweven on 2024/10/12--13:30.
 * Email: sweventears@163.com
 */
public class GlobalApp {
    private static GlobalApp instance;
    private final Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {

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
    private Application application;

    public static GlobalApp getInstance() {
        if (instance == null) {
            synchronized (GlobalApp.class) {
                if (instance == null) {
                    instance = new GlobalApp();
                }
            }
        }
        return instance;
    }

    public static GlobalApp setInstance(Application application) {
        if (instance == null) {
            synchronized (GlobalApp.class) {
                if (instance == null) {
                    instance = new GlobalApp();
                }
            }
        }
        instance.application = application;
        return instance;
    }

    public void registerActivity() {
        application.registerActivityLifecycleCallbacks(mCallbacks);
    }

    public void unRegisterActivity() {
        application.unregisterActivityLifecycleCallbacks(mCallbacks);
    }

    public void initCacheManager() {
        CacheManager.init(application);
    }

    public Application getApplication() {
        return application;
    }
}
