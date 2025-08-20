package com.app.test.feature.main

import androidx.databinding.ViewDataBinding
import com.app.test.R
import com.app.test.base.BaseActivity
import com.app.test.base.BaseAdapter
import com.app.test.base.BaseViewModel
import com.app.test.databinding.ActivityMainBinding
import com.app.test.utils.RefreshHelper

/**
 * Created by Sweven on 2025/8/15--16:39.
 * Email: sweventears@163.com
 */
class MainActivity : BaseActivity<ActivityMainBinding, BaseViewModel>(R.layout.activity_main) {
    private val adapter by lazy { Adapter() }

    override fun initView() {
    }

    override fun doBusiness() {
    }

    private inner class Adapter : BaseAdapter<String, ViewDataBinding>(0)
}