package com.app.test.manager

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import pers.sweven.common.app.PageInit
import pers.sweven.common.app.PageManager as PManager

/**
 * 页面跳转管理
 * <p>仿ARouter写法，便于后期更新换代
 *
 *
 * Created by Sweven on 2024/3/28--17:40.
 * Email: sweventears@163.com
 */
class PageManager private constructor() : PManager() {

    /**
     * @param page [Router]
     */
    fun build(@Router("") page: String?): Navigation {
        this.page = page
        clazz = map[page]
        return Navigation()
    }

    fun go(context: Context, @Router("") page: String?) {
        build(page).navigation(context)
    }

    fun clearAll(){
        PageInit.getInstance().clearAll()
    }

    companion object {
        private val map = hashMapOf<String, Class<*>>()

        @JvmStatic
        fun initActivities(context: Context): List<Class<*>> {
            val packageManager: PackageManager = context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_ACTIVITIES
            )


            val list = arrayListOf<Class<*>>()
            for (activity in packageInfo.activities) {
                val aClass = Class.forName(activity.name)
                aClass.getAnnotation(Router::class.java)?.apply {
                    map[value] = aClass
                }
                list.add(aClass)
            }
            return list
        }

        @JvmStatic
        fun getInstance(): PageManager {
            return PageManager()
        }
    }
}