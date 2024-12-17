package com.app.test.base

import android.content.Context
import android.view.View
import android.widget.PopupWindow
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * Created by Sweven on 2024/11/7--11:49.
 * Email: sweventears@163.com
 */
abstract class RxRootPopupWindow<T:ViewDataBinding>(
    private val context: Context,
    private val attachView: View? = null
) : PopupWindow(context), LifecycleOwner {
    private val registry = LifecycleRegistry(this)

}