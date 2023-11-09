package pers.sweven.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trello.rxlifecycle2.LifecycleProvider
import pers.sweven.common.repository.exception.ApiException

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
}