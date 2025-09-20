package pers.sweven.common.app

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import java.util.*

class PageInit private constructor() : ActivityLifecycleCallbacks {
    companion object {
        private val _instance by lazy { PageInit() }

        @JvmStatic
        fun getInstance(): PageInit = _instance
    }

    private val activities: MutableList<Activity> = ArrayList()
    private var saveCount = 0
    val backToFront: Boolean get() = saveCount == 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        saveCount++
    }

    override fun onActivityStopped(activity: Activity) {
        saveCount--
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    fun <T> finishOtherActivity(clazz: Class<T>) {
        val removes: MutableSet<Activity> = HashSet()
        for (activity in activities) {
            if (activity.javaClass != clazz) {
                if (!activity.isDestroyed) {
                    activity.finish()
                    removes.add(activity)
                }
            }
        }
        activities.removeAll(removes)
    }

    fun <T> finishSameActivity(tClass: Class<T>) {
        var first = true
        for (i in activities.indices.reversed()) {
            val activity = activities[i]
            if (activity.javaClass == tClass) {
                if (activity.isDestroyed) {
                    activities.remove(activity)
                } else {
                    if (!first) {
                        activity.finish()
                    }
                    first = false
                }
            }
        }
    }

    fun <T> isCurrentActivity(tClass: Class<T>): Boolean {
        return activities.lastOrNull()?.javaClass == tClass
    }

    fun <T> isHas(tClass: Class<T>): Boolean {
        return activities.find { it.javaClass == tClass } != null
    }

    fun getActivity(tClass: Class<*>): Activity? {
        return activities.find { it.javaClass == tClass }
    }

    fun clearAll() {
        for (activity in activities) {
            if (!activity.isDestroyed) {
                activity.finish()
            }
        }
        activities.clear()
        saveCount = 0
    }

    val context: Context? get() = activities.lastOrNull()
}