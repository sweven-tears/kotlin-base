package com.app.test.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Created by Sweven on 2025/9/11--23:08.
 * Email: sweventears@163.com
 */
interface ScopeAttach {
    fun <T> Channel<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) {
        owner.lifecycleScope.launch {
            receiveAsFlow().collect(observer)
        }
    }
}

interface ScopeViewModelAttach : ScopeAttach {
    val viewModel: ViewModel

    fun <T> Channel<T>.postValue(t: T) {
        viewModel.viewModelScope.launch {
            send(t)
        }
    }

    fun <T> channel(capacity: Int = Channel.UNLIMITED): Channel<T> {
        return Channel(capacity)
    }
}