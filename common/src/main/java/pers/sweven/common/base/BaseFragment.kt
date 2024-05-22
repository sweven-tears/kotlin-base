package pers.sweven.common.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.components.support.RxFragment
import pers.sweven.common.app.BaseApplication
import pers.sweven.common.utils.ToastUtils
import java.lang.reflect.ParameterizedType

/**
 * Created by Sweven on 2021/7/27--21:40.
 * Email: sweventears@163.com
 */
abstract class BaseFragment<T : ViewDataBinding, VM : BaseViewModel>
    (val layout: Int) : RxFragment() {

    var merge = false
    var binding: T? = null
        private set
    var model: VM? = null
        private set
    protected lateinit var hostActivity: RxAppCompatActivity

    constructor(layout: Int, merge: Boolean) : this(layout) {
        this.merge = merge
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = if (merge) {
            DataBindingUtil.inflate(inflater, layout, container, true)
        } else {
            DataBindingUtil.inflate(inflater, layout, container, false)
        }
        onCreateSelf(savedInstanceState)
        return binding?.root
    }

    protected open fun onCreateSelf(savedInstanceState: Bundle?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostActivity = activity as RxAppCompatActivity
        var bundle = arguments
        if (bundle == null) {
            bundle = Bundle()
        }
        getBundle(bundle)
        model = initViewModel()
        initView()
        initObservable(model!!)
        Thread(this::doBusiness).start()
    }

    open fun onSecondCreate() {
    }

    open fun initObservable(model: VM) {
        model.showLoading.observe(this, { show: Boolean? ->
            if (show != null && show) {
                showLoading()
            } else {
                dismissLoading()
            }
        })
        model.throwable.observe(this, { throwable: Throwable? ->
            if (throwable == null) {
                return@observe
            }
            throwable.printStackTrace()
            ToastUtils.showShort(throwable.message)
        })
    }

    open fun dismissLoading() {
        if (hostActivity is BaseActivity<*, *>) {
            (hostActivity as BaseActivity<*, *>).dismissLoading()
        }
    }

    open fun showLoading() {
        if (hostActivity is BaseActivity<*, *>) {
            (hostActivity as BaseActivity<*, *>).showLoading()
        }
    }

    private fun initViewModel(): VM? {
        var vm: VM? = null
        try {
            val superclass = javaClass.genericSuperclass as ParameterizedType?
            val clazz = superclass!!.actualTypeArguments[1] as Class<*>
            vm = newViewModel(clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        vm?.attachLifecycle(this)
        return vm
    }

    open fun newViewModel(clazz:Class<*>):VM{
        return clazz.newInstance() as VM
    }

    open fun getBundle(bundle: Bundle) {

    }

    fun replaceFragment(containerId: Int, fragment: Fragment) {
        hostActivity
            .supportFragmentManager
            .beginTransaction()
            .replace(containerId, fragment)
            .commitAllowingStateLoss()
    }

    fun removeFragment(fragment: Fragment) {
        hostActivity
            .supportFragmentManager
            .beginTransaction()
            .remove(fragment)

    }

    fun replaceFragmentWithStack(containerId: Int, fragment: Fragment) {
        hostActivity
            .supportFragmentManager
            .beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(fragment.javaClass.canonicalName)
            .commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (model != null) {
            model!!.detach()
            model = null
        }
    }

    protected abstract fun initView()
    protected abstract fun doBusiness()
}
