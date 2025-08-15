package pers.sweven.common.base

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.components.support.RxDialogFragment
import pers.sweven.common.R
import pers.sweven.common.utils.ToastUtils
import java.lang.reflect.ParameterizedType

/**
 * Created by Sweven on 2021/10/26.
 * Email:sweventears@Foxmail.com
 */
abstract class BaseDialogFragment<T : ViewDataBinding, VM : BaseViewModel>(
    val layoutId: Int,
    private val viewModelClass: Class<VM>? = null
) : RxDialogFragment() {
    lateinit var binding: T
    val model: VM by lazy {
        initViewModel()
    }
    protected val hostActivity: RxAppCompatActivity
        get() = activity as RxAppCompatActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, null, false)
        onCreatedSelf(savedInstanceState)
        return binding.root
    }

    open fun onCreatedSelf(savedInstanceState: Bundle?) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    open fun initObservable() {
        model.showLoading.observe(this, { show: Boolean? ->
            if (show != null && show) {
                showLoading()
            } else {
                dismissLoading()
            }
        })
        model.throwable.observe(this, { throwable: Throwable? ->
            showError(throwable?.message)
        })
        initObservable(model)
    }

    /**
     * use it not yet
     * @param [model] 型
     */
    @Deprecated("please use initObservable()", replaceWith = ReplaceWith("initObservable()"))
    open fun initObservable(model: VM) {
    }

    open fun showError(message: String?) {
        Toast.makeText(hostActivity, message, Toast.LENGTH_SHORT).show()
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

    open fun getBundle(bundle: Bundle) {

    }

    override fun onResume() {
        setLayoutStyle()
        super.onResume()
    }

    /**
     * 设置布局为由下到上
     */
    protected fun setBottomLayout() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setWindowAnimations(R.style.BottomAnimation)
        val attributes = dialog?.window?.attributes as WindowManager.LayoutParams
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        attributes.width = dm.widthPixels
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = attributes
    }

    /**
     * 设置布局为由下到上
     */
    protected fun setBottomLayout(heightScale: Float) {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setWindowAnimations(R.style.BottomAnimation)
        val attributes = dialog?.window?.attributes as WindowManager.LayoutParams
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        attributes.width = dm.widthPixels
        attributes.height = (dm.heightPixels * heightScale).toInt()
        dialog?.window?.attributes = attributes
    }

    /**
     * 设置布局为由下到上
     */
    protected fun setBottomLayout(height: Int) {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setWindowAnimations(R.style.BottomAnimation)
        val attributes = dialog?.window?.attributes as WindowManager.LayoutParams
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        attributes.width = dm.widthPixels
        attributes.height = height
        dialog?.window?.attributes = attributes
    }

    /**
     * 设置布局居中
     */
    protected fun setCenterLayout() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.setGravity(Gravity.CENTER)
        val attributes = dialog?.window?.attributes as WindowManager.LayoutParams
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        attributes.width = (dm.widthPixels * 0.8).toInt()
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = attributes
    }

    protected fun setFullScreen() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.setGravity(Gravity.CENTER)
        val attributes = dialog?.window?.attributes as WindowManager.LayoutParams
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = attributes
    }

    abstract fun initView()

    abstract fun setLayoutStyle()

    abstract fun doBusiness()

    open fun show(supportFragmentManager: FragmentManager?) {
        if (supportFragmentManager != null) {
            if (!isAdded) {
                show(supportFragmentManager, this::class.java.simpleName)
            }
        } else {
            ToastUtils.showShort("发生异常，请稍后重试！")
        }
    }
}