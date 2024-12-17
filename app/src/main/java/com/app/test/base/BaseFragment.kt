package com.app.test.base

import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.components.SimpleImmersionOwner
import com.gyf.immersionbar.components.SimpleImmersionProxy
import pers.sweven.common.utils.Utils
import pers.sweven.common.base.BaseFragment as Base

/**
 * Created by Sweven on 2023/7/25--11:07.
 * Email: sweventears@163.com
 */
abstract class BaseFragment<T : ViewDataBinding, VM : BaseViewModel>(
    layoutId: Int,
    merge: Boolean = false
) :
    Base<T, VM>(layoutId, merge), SimpleImmersionOwner {

    abstract override fun initView()

    public abstract override fun doBusiness()

    override fun initObservable(model: VM) {
        model.showLoading.observe(this, { show: Boolean? ->
            if (show != null && show) {
                showLoading()
            } else {
                dismissLoading()
            }
        })
    }

    @ColorInt
    fun color(@ColorRes color: Int): Int {
        return ContextCompat.getColor(hostActivity, color)
    }

    /**
     * 将代理类通用行为抽出来
     */
    private val mSimpleImmersionProxy = SimpleImmersionProxy(this)

    override fun initImmersionBar() {}

    override fun immersionBarEnabled(): Boolean {
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSimpleImmersionProxy.onActivityCreated(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mSimpleImmersionProxy.isUserVisibleHint = isVisibleToUser
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mSimpleImmersionProxy.onHiddenChanged(hidden)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mSimpleImmersionProxy.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSimpleImmersionProxy.onDestroy()
    }

    fun px(dp: Float): Int {
        return Utils.dip2px(hostActivity, dp)
    }

    companion object {
        const val RESULT_OK = -1
    }
}