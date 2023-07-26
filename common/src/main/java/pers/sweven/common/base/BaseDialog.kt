package pers.sweven.common.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Created by Sweven on 2021/7/28--21:16.
 * Email: sweventears@163.com
 */
abstract class BaseDialog<T : ViewDataBinding?> : Dialog {
    protected var binding: T? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        init()
    }

    private fun init() {
        binding = DataBindingUtil.inflate(layoutInflater, layout, null, false)
        setContentView(binding!!.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        doBusiness()
    }

    protected abstract fun doBusiness()
    protected abstract fun initView()
    protected abstract val layout: Int
}