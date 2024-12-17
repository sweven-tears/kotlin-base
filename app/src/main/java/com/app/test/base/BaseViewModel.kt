package com.app.test.base

import android.app.Activity
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.MutableLiveData
import com.app.test.data.network.ApiException
import com.app.test.manager.PageManager
import pers.sweven.common.utils.ToastUtils
import pers.sweven.common.utils.Utils
import pers.sweven.common.utils.ifElse
import pers.sweven.common.base.BaseViewModel as Base

/**
 * Created by Sweven on 2023/7/25--11:10.
 * Email: sweventears@163.com
 */
open class BaseViewModel : Base() {
    companion object {
        @JvmStatic
        val PLACEHOLDER = arrayListOf(
            0 to "暂无数据",
        )
    }

    val loadingTextData = liveData<Pair<Boolean, CharSequence>>()

    fun showToast(message: String) {
        ToastUtils.showShort(message)
    }

    fun openPage(view: View, page: String) {
        openPage(view, page, "")
    }

    fun openPage(view: View, page: String, url: String) {
        val sharedName: String? = view.transitionName
        val options = Utils.isEmpty(sharedName).ifElse(null, initOptions(view, sharedName ?: ""))

        PageManager.getInstance()
            .build(page)
            .withUrl(url)
            .setOptions(options)
            .navigation(view.context)
    }

    fun openPage(view: View, page: String, url: String, jump: Boolean) {
        if (!jump) {
            return
        }
        val sharedName: String? = view.transitionName
        val options = Utils.isEmpty(sharedName).ifElse(null, initOptions(view, sharedName ?: ""))

        PageManager.getInstance()
            .build(page)
            .withUrl(url)
            .setOptions(options)
            .navigation(view.context)
    }

    fun openPage(
        view: View,
        page: String,
        url: String = "",
        options: ActivityOptionsCompat? = null
    ) {
        PageManager.getInstance()
            .build(page)
            .withUrl(url)
            .setOptions(options)
            .navigation(view.context)
    }

    fun initOptions(view: View, sharedName: String): ActivityOptionsCompat {
        return ActivityOptionsCompat.makeSceneTransitionAnimation(
            view.context as Activity,
            androidx.core.util.Pair(view, sharedName),
        )
    }


    override fun postThrowable(throws: Throwable) {
        val api = ApiException.handleException(throws)
        throwable.postValue(api)
    }

    fun <T> liveData(t: T? = null): MutableLiveData<T> = MutableLiveData<T>(t)
}