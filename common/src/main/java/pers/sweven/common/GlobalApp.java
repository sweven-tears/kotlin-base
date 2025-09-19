package pers.sweven.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import pers.sweven.common.app.AppManager;
import pers.sweven.common.app.PageInit;
import pers.sweven.common.utils.cache.CacheManager;

/**
 * 全局初始化
 * Created by Sweven on 2024/10/12--13:30.
 * Email: sweventears@163.com
 */
public class GlobalApp {
    private static volatile GlobalApp instance;
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
        application.registerActivityLifecycleCallbacks(PageInit.getInstance());
    }

    public void unRegisterActivity() {
        application.unregisterActivityLifecycleCallbacks(PageInit.getInstance());
    }

    public void initCacheManager() {
        CacheManager.init(application);
    }

    public Application getApplication() {
        return application;
    }
}
