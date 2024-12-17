package com.app.test.base

import android.view.View
import androidx.databinding.ViewDataBinding
import pers.sweven.common.app.PageInit
import pers.sweven.common.base.BaseActivity as Base

/**
 * Created by Sweven on 2024/11/7--11:44.
 * Email: sweventears@163.com
 */
abstract class RxRootActivity<T : ViewDataBinding, VM : BaseViewModel>(
    layout: Int,
    private val onlyOne: Boolean = false,
) : Base<T, VM>(layout) {

    open fun onBack(view: View?) {
        finish()
    }

    override fun initObservable(model: VM) {
        model.showLoading.observe(this, { show: Boolean? ->
            if (show != null && show) {
                showLoading()
            } else {
                dismissLoading()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (onlyOne) {
            PageInit.getInstance().finishSameActivity(this::class.java)
        }
    }

    abstract override fun initView()

    abstract override fun doBusiness()
}