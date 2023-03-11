package com.sanmer.mrepo.app

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import kotlin.reflect.KProperty

object Status {
    open class State(
        initialState: Event = Event.NON
    ) {
        val state = MutableStateFlow(initialState)
        var event by mutableStateOf(initialState)
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
            runCatching {
                this.value = value
            }.onFailure {
                Timber.w(it.message)
            }
            state.value = value
        }
    }

    object Env : State()

    object Provider : State()

    object Local : State()

    object Cloud : State()
}