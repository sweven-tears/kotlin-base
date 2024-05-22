package pers.sweven.common.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PageInit implements Application.ActivityLifecycleCallbacks {
    private static PageInit instance;
    public boolean backToFront = false;
    public List<Activity> activities = new ArrayList<>();
    private Context context;
    private int saveCount = 0;

    private PageInit(Application application) {
        if (application != null) {
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
                    init(BaseApplication.application);
                }
            }
        }
        return instance;
    }

    @Override
    public void onActivityCreated(@NonNull @NotNull Activity activity, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        context = activity;
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
        for (Activity activity : activities) {
            if (activity.getClass() != clazz) {
                if (activity.isFinishing()) {
                    activities.remove(activity);
                } else {
                    activity.finish();
                }
            }
        }
    }

    public <T> void finishSameActivity(Class<T> tClass) {
        if (activities == null) {
            return;
        }
        boolean first = true;
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (activity.getClass() == tClass) {
                if (activity.isFinishing()) {
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

    public Context getContext() {
        return context;
    }
}