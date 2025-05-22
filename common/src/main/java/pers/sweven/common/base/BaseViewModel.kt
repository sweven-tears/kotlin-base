package pers.sweven.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import pers.sweven.common.repository.exception.ApiException
import pers.sweven.common.rx.RxUtil

/**
 * Created by Sweven on 2021/7/26--21:48.
 * Email: sweventears@163.com
 */
open class BaseViewModel : ViewModel() {
    var provider: LifecycleProvider<*>? = null
        private set
    val showLoading: MutableLiveData<Boolean> = MutableLiveData()
    val throwable: MutableLiveData<Throwable> = MutableLiveData()
    var params = HashMap<String, Any>()
    fun attachLifecycle(mProvider: LifecycleProvider<*>?) {
        provider = mProvider
    }

    fun detach() {
        provider = null
    }

    open fun postThrowable(throws: Throwable) {
        val exception = ApiException.handleException(throws)
        if (exception.tag != null) {
            postThrowable(exception, exception.tag)
        } else {
            throwable.postValue(exception)
        }
    }

    open fun postThrowable(throws: Throwable, tag: String) {
        val exception = ApiException.handleException(throws, tag)
        throwable.postValue(exception)
    }

    fun <T> liveData(): MutableLiveData<T> = MutableLiveData<T>()

    //

    protected val compositeDisposable = CompositeDisposable()

    protected inline fun <T> Observable<T>.requestFlow(
        crossinline onNext: (T) -> Unit,
        crossinline onError: (Throwable) -> Unit = { postThrowable(it) },
        showLoading: Boolean = false,
        interceptor: () -> Throwable? = { null }
    ) {
        interceptor().let {
            if (it != null) {
                onError(it)
                return
            }
        }

        compositeDisposable.add(
            this.compose(RxUtil.applySchedulers(this@BaseViewModel, showLoading))
                .subscribe({
                    runCatching { onNext(it) }.onFailure { onError(it) }
                }, { onError(it) })
        )
    }


    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}