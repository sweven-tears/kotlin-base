package com.app.test.base

import androidx.databinding.ViewDataBinding
import pers.sweven.common.base.BaseActivity

/**
 * Created by Sweven on 2025/8/15--16:39.
 * Email: sweventears@163.com
 */
abstract class BaseActivity<T : ViewDataBinding, VM : BaseViewModel>(layoutId: Int) :
    BaseActivity<T, VM>(layoutId) {
}