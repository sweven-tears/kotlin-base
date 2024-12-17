package com.app.test.base

import android.content.DialogInterface
import android.content.Intent
import androidx.databinding.ViewDataBinding
import com.app.test.base.BaseViewModel
import pers.sweven.common.base.BaseActivity
import pers.sweven.common.base.BaseDialogFragment as Base

/**
 * Created by Sweven on 2023/11/7--15:35.
 * Email: sweventears@163.com
 */
abstract class BaseDialogFragment<T : ViewDataBinding, VM : BaseViewModel>(val layout: Int) :
    Base<T, VM>(layout) {

    override fun initObservable(model: VM) {
        model.showLoading.observe(this, { show: Boolean? ->
            if (show != null && show) {
                showLoading()
            } else {
                dismissLoading()
            }
        })
    }

    private var dismissListener: ((dialog: BaseDialogFragment<*, *>, result: Int, data: Intent?) -> Unit)? =
        null

    open fun setOnDismissListener(listener: ((dialog: BaseDialogFragment<*, *>, result: Int, data: Intent?) -> Unit)?): BaseDialogFragment<*, *> {
        dismissListener = listener
        return this
    }

    open fun setOnDismissListener(listener: ((dialog: BaseDialogFragment<*, *>, result: Int) -> Unit)?): BaseDialogFragment<*, *> {
        dismissListener = { dialog, result, data ->
            listener?.invoke(dialog, result)
        }
        return this
    }

    fun setResult(result: Int, data: Intent? = null) {
        dismissListener?.invoke(this, result, data)
    }

    fun dismiss(result: Int, data: Intent? = null) {
        setResult(result, data)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        setResult(0, null)
        super.onDismiss(dialog)
    }

    abstract override fun setLayoutStyle()

    abstract override fun initView()

    abstract override fun doBusiness()

    companion object {
        const val RESULT_OK = -1
        const val RESULT_ERROR = 0
        const val RESULT_FIRST_USER = 1
    }
}