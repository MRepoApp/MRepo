package com.sanmer.mrepo.app.event

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KProperty

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
    }

    open fun setFailed(value: Any? = null) {
        event = Event.FAILED
    }

    open fun setLoading(value: Any? = null) {
        event = Event.LOADING
    }

    private operator fun MutableState<Event>.setValue(
        thisObj: Any?,
        property: KProperty<*>,
        value: Event
    ) {
        this.value = value
        state.value = value
    }
}