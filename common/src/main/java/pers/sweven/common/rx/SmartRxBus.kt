package pers.sweven.common.rx

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

object SmartRxBus {
    /**
     * 听
     * @param [scope] 范围 lifecycleOwner(生命周期)、class(自定义)、null(全局)
     * @param [onNext] 在下一个
     */
    inline fun <reified T : Any> listen(
        scope: Any? = null,
        policy: LifecyclePolicy = LifecyclePolicy.DEFAULT,
        crossinline onNext: (T) -> Unit,
    ) = listen(scope, policy, onNext) { Log.e("RxBus", "Error: ${it.message}", it) }

    /**
     * 听
     * @param [scope] 范围 lifecycleOwner(生命周期)、class(自定义)、null(全局)
     * @param [onNext] 在下一个
     * @param [onError] 出错时
     */
    inline fun <reified T : Any> listen(
        scope: Any? = null,
        policy: LifecyclePolicy = LifecyclePolicy.DEFAULT,
        crossinline onNext: (T) -> Unit,
        crossinline onError: (Throwable) -> Unit = { e ->
            Log.e("RxBus", "Error: ${e.message}", e)
        }
    ) = RxBus.getDefault().toObservable(T::class.java)
        .subscribe({ event ->
            when {
                scope.isValidScope(policy) -> onNext(event)
                else -> Log.d("RxBus", "事件被策略拒绝 [state: ${(scope as? LifecycleOwner)?.lifecycle?.currentState}]")
            }
        }, { error ->
            if (scope.isValidScope(policy)) onError(error)
            else Log.d("RxBus", "Error ignored in invalid scope: ${error.message}")
        }).let { bind(it, scope = scope) }

    // 公开的扩展函数（必须用public）
    fun Any?.isValidScope(policy: LifecyclePolicy = LifecyclePolicy.DEFAULT): Boolean {
        return when (this) {
            is LifecycleOwner -> policy.isActive(lifecycle.currentState)
            is CustomLifecycle -> isValid()
            null -> true
            else -> false
        }
    }

    fun post(event: Any){
        RxBus.getDefault().post(event)
    }

    fun postSticky(event: Any){
        RxBus.getDefault().postSticky(event)
    }

    // 全局容器（适合长期订阅）
    private val globalDisposables = CompositeDisposable()

    // 按类名分组的容器（CustomLifecycle）
    private val scopedDisposables = WeakHashMap<Any, CompositeDisposable>()

    fun bind(vararg disposables: Disposable, scope: Any? = null) {
        when {
            scope == null -> disposables.forEach { globalDisposables.add(it) }
            scope is LifecycleOwner -> bindToLifecycle(disposables, scope)
            scope is CustomLifecycle -> bindToCustomLifecycle(disposables, scope)
            else -> Log.w("RxLifecycle", "Unsupported scope type: ${scope.javaClass}")
        }
    }

    private fun bindToCustomLifecycle(disposables: Array<out Disposable>, scope: CustomLifecycle) {
        val composite = scopedDisposables.getOrPut(scope) { CompositeDisposable() }
        composite.addAll(*disposables)

        // 注册销毁回调
        scope.addOnDestroyListener {
            composite.clear()
            scopedDisposables.remove(scope)
        }
    }

    // 绑定生命周期（自动清理）
    private fun bindToLifecycle(disposables: Array<out Disposable>, owner: LifecycleOwner) {
        val runnable = Runnable {
            val observer = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        disposables.forEach { it.dispose() }
                        source.lifecycle.removeObserver(this)
                    }
                }
            }
            owner.lifecycle.addObserver(observer)
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            Handler(Looper.getMainLooper()).post(runnable)
        }
    }

    // 全局清理
    fun clearGlobal() = globalDisposables.clear()

    // 按scope清理
    fun clear(scope: Any) {
        scopedDisposables[scope::class.java.name]?.clear()
    }

    interface CustomLifecycle {
        fun isValid(): Boolean
        fun addOnDestroyListener(listener: () -> Unit)
        fun removeOnDestroyListener(listener: () -> Unit)
    }

    // 生命周期策略配置接口
    interface LifecyclePolicy {
        fun isActive(state: Lifecycle.State): Boolean

        companion object {
            // 默认策略：非DESTROYED状态均可接收事件
            val DEFAULT = object : LifecyclePolicy {
                override fun isActive(state: Lifecycle.State) =
                    state != Lifecycle.State.DESTROYED
            }

            // 仅在可见状态（STARTED以上）
            val VISIBLE = object : LifecyclePolicy {
                override fun isActive(state: Lifecycle.State) =
                    state.isAtLeast(Lifecycle.State.STARTED)
            }

            // 完全自定义策略
            fun custom(block: (Lifecycle.State) -> Boolean) = object : LifecyclePolicy {
                override fun isActive(state: Lifecycle.State) = block(state)
            }
        }
    }
}
