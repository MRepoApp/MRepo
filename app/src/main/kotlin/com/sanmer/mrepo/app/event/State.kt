package com.sanmer.mrepo.app.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow

open class State(
    initial: Event = Event.NON
) {
    val state = MutableStateFlow(initial)
    var event by mutableStateOf(initial)
        private set

    val isLoading get() = event.isLoading
    val isSucceeded get() = event.isSucceeded
    val isFailed get() = event.isFailed
    val isFinished get() = event.isFinished
    val isNotReady get() = event.isNotReady

    open fun setSucceeded(value: Any? = null) {
        event = Event.SUCCEEDED
        state.value = Event.SUCCEEDED
    }

    open fun setFailed(value: Any? = null) {
        event = Event.FAILED
        state.value = Event.FAILED
    }

    open fun setLoading(value: Any? = null) {
        event = Event.LOADING
        state.value = Event.LOADING
    }
}