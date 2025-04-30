package pers.sweven.common.base

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.components.support.RxFragment
import pers.sweven.common.utils.ToastUtils
import pers.sweven.common.utils.Utils
import java.lang.reflect.ParameterizedType

/**
 * Created by Sweven on 2021/7/26--21:48.
 * Email: sweventears@163.com
 */
abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel>(private val layoutId: Int) :
    RxAppCompatActivity() {

    @JvmField
    var loadingDialog: Dialog? = null
    lateinit var binding: V
    var model: VM? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateSelf(savedInstanceState)
        loadingDialog = initLoadingDialog()
        activity = this
        var extras = intent.extras
        if (extras == null) {
            extras = Bundle()
        }
        getBundle(extras)
        registerLayout()
        model = initViewModel()
        model?.attachLifecycle(this)
        initView()
        model?.let { initObservable(it) }
        doBusiness()
    }

    open fun onCreateSelf(savedInstanceState: Bundle?) {
    }

    open fun registerLayout() {
        binding = DataBindingUtil.setContentView(this, layoutId)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        var extras = intent?.extras
        if (extras == null) {
            extras = Bundle()
        }
        getBundle(extras)
    }

    open fun initViewModel(): VM? {
        var vm: VM? = null
        try {
            val p = javaClass.genericSuperclass as ParameterizedType?
            val type = p!!.actualTypeArguments[1] as Class<*>
            vm = newViewModel(type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vm
    }

    open fun newViewModel(clazz: Class<*>): VM {
        return clazz.newInstance() as VM
    }

    lateinit var activity: BaseActivity<*, *>

    open fun initObservable(model: VM) {
        model.showLoading.observe(this, { show: Boolean? ->
            if (show != null && show) {
                showLoading()
            } else {
                dismissLoading()
            }
        })
        model.throwable.observe(this, { `val`: Throwable? ->
            if (`val` == null) {
                return@observe
            }
            `val`.printStackTrace()
            ToastUtils.showShort(`val`.message)
        })

    }

    fun color(colorInt: Int): Int {
        return ContextCompat.getColor(this, colorInt)
    }

    /**
     * 自定义加载Loading
     */
    open fun initLoadingDialog(): Dialog? {
        return null
    }

    fun dismissLoading() {
        loadingDialog?.dismiss()
    }

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = ProgressDialog(activity)
        }
        loadingDialog?.show()
    }

    fun replaceFragment(containerId: Int, fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, fragment)
            .commitAllowingStateLoss()
    }

    fun removeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .remove(fragment)
    }

    fun replaceFragmentWithStack(containerId: Int, fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(fragment.javaClass.canonicalName)
            .commitAllowingStateLoss()
    }

    var currentFragment: Fragment? = null

    fun switchFragment(containerId: Int, tagFragment: RxFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        if (currentFragment == tagFragment) {
            return
        }
        if (!tagFragment.isAdded) {
            if (currentFragment != null) {
                transaction.hide(currentFragment!!)
            }
            transaction
                .add(containerId, tagFragment)
                .commitAllowingStateLoss()
        } else {
            if (currentFragment != null) {
                transaction.hide(currentFragment!!)
            }
            transaction
                .show(tagFragment)
                .commitAllowingStateLoss()
        }
        currentFragment = tagFragment
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (Utils.isShouldHideKeyboard(view, ev) && mustClose(view)) {
                Utils.hiddenKeyboard(this, view)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    var mustCloseKeyboard = true

    open fun mustClose(view: View?):Boolean{
        return mustCloseKeyboard && !notMustCloseKeyboardViews.contains(view)
    }

    var notMustCloseKeyboardViews = arrayListOf<View>()

    override fun onConfigurationChanged(newConfig: Configuration) { //字体大小处理
        if (newConfig.fontScale != 1f) //非默认值
            resources
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources? { //还原字体大小
        val res = super.getResources()
        val configuration = res.configuration
        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f
            res.updateConfiguration(configuration, res.displayMetrics)
        }
        return res
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
            loadingDialog = null
        }
        if (model != null) {
            model!!.detach()
            model = null
        }
    }

    open fun getBundle(bundle: Bundle) {}

    protected abstract fun initView()
    protected abstract fun doBusiness()
}