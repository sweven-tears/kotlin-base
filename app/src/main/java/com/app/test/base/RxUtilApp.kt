package com.app.test.base

import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.components.support.RxDialogFragment
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Sweven on 2021/7/28--22:59.
 * Email: sweventears@163.com
 */
object RxUtilApp {
    /**
     * RxJava 绑定生命周期
     *
     * @param <T>
     * @return
    </T> */
    fun <T> bindToLifecycle(provider: LifecycleProvider<*>): LifecycleTransformer<T> {
        return (provider as? RxAppCompatActivity)?.bindUntilEvent(ActivityEvent.DESTROY)
            ?: ((provider as? RxFragment)?.bindUntilEvent(FragmentEvent.DESTROY)
                ?: ((provider as? RxDialogFragment)?.bindUntilEvent(FragmentEvent.DESTROY)
                    ?: throw IllegalArgumentException("lifecycle bind error")))
    }

    fun <T> applySchedulers(
        viewModel: BaseViewModel,
        showLoading: Pair<Boolean, CharSequence>
    ): ObservableTransformer<T, T> {
        return ObservableTransformer { observable: Observable<T> ->
            observable.subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    if (showLoading.first) {
                        viewModel.loadingTextData.value = true to showLoading.second
                    }
                }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    if (showLoading.first) {
                        viewModel.loadingTextData.value = false to "loading..."
                    }
                }.compose(bindToLifecycle(viewModel.provider!!))
        }
    }
}