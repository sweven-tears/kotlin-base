package pers.sweven.common.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import pers.sweven.common.utils.cache.CacheManager;

public class BaseApplication extends Application {
    private RefWatcher mRefWatcher;
    private static Context mContext;
    public static boolean DEBUG;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        CacheManager.init(this);
        if (DEBUG) {
            initLeakCanary();
        }else {
            UMConfigure.init(this, "5f9936e245b2b751a91e41e4", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        }

        //注册监听每个activity的生命周期,便于堆栈式管理
        registerActivityLifecycleCallbacks(mCallbacks);
    }

    public void setDEBUG(boolean debug) {
        DEBUG = debug;
    }

    public static Context getContext() {
        return mContext;
    }


    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        mRefWatcher = LeakCanary.install(this);
    }


    public static RefWatcher getRefWatcher(Context context) {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        return application.mRefWatcher;
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