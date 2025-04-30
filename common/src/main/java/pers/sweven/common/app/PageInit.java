package pers.sweven.common.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pers.sweven.common.GlobalApp;

public class PageInit implements Application.ActivityLifecycleCallbacks {
    private static PageInit instance;
    public boolean backToFront = false;
    public List<Activity> activities = new ArrayList<>();
    private WeakReference<Context> context;
    private Application application;
    private int saveCount = 0;

    private PageInit(Application application) {
        if (application != null) {
            this.application = application;
            application.registerActivityLifecycleCallbacks(this);
        }
    }

    public static void init(Application application) {
        if (instance == null) {
            instance = new PageInit(application);
        }
    }

    public static PageInit getInstance() {
        if (instance == null) {
            synchronized (PageInit.class) {
                if (instance == null) {
                    init(GlobalApp.getInstance().getApplication());
                }
            }
        }
        return instance;
    }

    @Override
    public void onActivityCreated(@NonNull @NotNull Activity activity, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        context = new WeakReference<>(activity);
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(@NonNull @NotNull Activity activity) {
        backToFront = saveCount == 0;
        saveCount++;
    }

    @Override
    public void onActivityResumed(@NonNull @NotNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull @NotNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull @NotNull Activity activity) {
        saveCount--;
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull @NotNull Activity activity, @NonNull @NotNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull @NotNull Activity activity) {
        context = null;
        activities.remove(activity);
    }

    public <T> void finishOtherActivity(Class<T> clazz) {
        if (activities == null) {
            return;
        }

        Set<Activity> removes = new HashSet<>();
        for (Activity activity : activities) {
            if (activity.getClass() != clazz) {
                if (!activity.isDestroyed()) {
                    activity.finish();
                    removes.add(activity);
                }
            }
        }
        activities.removeAll(removes);
    }

    public <T> void finishSameActivity(Class<T> tClass) {
        if (activities == null) {
            return;
        }
        boolean first = true;
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (activity.getClass() == tClass) {
                if (activity.isDestroyed()) {
                    activities.remove(activity);
                } else {
                    if (!first) {
                        activity.finish();
                    }
                    first = false;
                }
            }
        }
    }

    public <T> boolean isCurrentActivity(Class<T> tClass) {
        if (activities == null) {
            return false;
        }
        if (activities.size() > 0) {
            int last = activities.size() - 1;
            return activities.get(last).getClass() == tClass;
        }
        return false;
    }

    public <T> boolean isHas(Class<T> tClass) {
        if (activities == null) {
            return false;
        }
        for (int i = activities.size() - 1; i >= 0; i--) {
            if (activities.get(i).getClass() == tClass) {
                return true;
            }
        }
        return false;
    }

    public Activity getActivity(Class<?> tClass) {
        if (activities == null) {
            return null;
        }
        for (Activity activity : activities) {
            if (activity.getClass() == tClass) {
                return activity;
            }
        }
        return null;
    }

    public void clearAll() {
        if (activities != null) {
            for (Activity activity : activities) {
                if (!activity.isDestroyed()) {
                    activity.finish();
                }
            }
            activities.clear();
        }
        saveCount = 0;
        backToFront = false;
        context = null;
    }

    public Context getContext() {
        if (context == null || context.get()==null) {
            return application;
        }else {
            return context.get();
        }
    }
}