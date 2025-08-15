package pers.sweven.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.components.support.RxFragment
import pers.sweven.common.utils.ToastUtils
import java.lang.reflect.ParameterizedType

/**
 * Created by Sweven on 2021/7/27--21:40.
 * Email: sweventears@163.com
 */
abstract class BaseFragment<T : ViewDataBinding, VM : BaseViewModel>(
    val layout: Int,
    var merge: Boolean = false,
    val viewModelClass: Class<VM>? = null
) : RxFragment() {

    lateinit var binding: T
        private set
    val model: VM by lazy {
        initViewModel()
    }
    protected lateinit var hostActivity: RxAppCompatActivity

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
        return binding.root
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
        model.attachLifecycle(this)
        initView()
        initObservable()
        doBusiness()
    }

    open fun onSecondCreate() {
    }

    open fun initObservable(){
        initObservable(model)
    }

    @Deprecated("use new func", replaceWith = ReplaceWith("initObservable()"))
    open fun initObservable(model: VM) {
        model.showLoading.observe(viewLifecycleOwner, { show: Boolean? ->
            if (show != null && show) {
                showLoading()
            } else {
                dismissLoading()
            }
        })
        model.throwable.observe(viewLifecycleOwner, { throwable: Throwable? ->
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

    open fun initViewModel(): VM {
        return if (viewModelClass != null) {
            getViewModelSafe(viewModelClass)
        } else {
            val p = javaClass.genericSuperclass as ParameterizedType?
            val arguments = p!!.actualTypeArguments
            val type = if (arguments.size > 1) {
                arguments[1] as Class<*>
            } else {
                throw RuntimeException("未定义第二个泛型类")
            }
            if (BaseViewModel::class.java.isAssignableFrom(type)) {
                getViewModelSafe(type as Class<VM>)
            } else {
                throw RuntimeException("第二个泛型类非BaseViewModel")
            }
        }
    }

    protected fun <T : VM> getViewModelSafe(clazz: Class<T>): T {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return newViewModel(modelClass) as T
            }
        })[clazz]
    }

    open fun newViewModel(clazz: Class<*>): VM {
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
        model.detach()
    }

    protected abstract fun initView()
    protected abstract fun doBusiness()
}
