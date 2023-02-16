package com.sanmer.mrepo.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.app.status.Event
import timber.log.Timber

object Status {
    open class State(
        initialState: Event = Event.NON
    ) {
        var event by mutableStateOf(initialState)
            private set

        val isLoading get() = event == Event.LOADING
        val isSucceeded get() = event == Event.SUCCEEDED
        val isFailed get() = event == Event.FAILED
        val isFinished get() = isSucceeded or isFailed

        open fun setSucceeded(value: Any? = null) {
            event = Event.SUCCEEDED
        }

        open fun setFailed(value: Any? = null) {
            event = Event.FAILED
        }

        open fun setLoading(value: Any? = null) {
            event = Event.LOADING
        }

        open fun setNon(value: Any? = null) {
            event = Event.NON
        }
    }

    open class Events(
        initialState: Event = Event.NON
    ) {
        var event = initialState
            private set

        val isLoading get() = event == Event.LOADING
        val isSucceeded get() = event == Event.SUCCEEDED
        val isFailed get() = event == Event.FAILED
        val isFinished get() = isSucceeded or isFailed

        open fun setSucceeded(value: Any? = null) {
            event = Event.SUCCEEDED
        }

        open fun setFailed(value: Any? = null) {
            event = Event.FAILED
        }

        open fun setLoading(value: Any? = null) {
            event = Event.LOADING
        }

        open fun setNon(value: Any? = null) {
            event = Event.NON
        }
    }

    object Env : State(initialState = Event.LOADING)

    object Provider : State()

    object Local : State() {
        override fun setFailed(value: Any?) {
            Timber.d("setFailed")
            super.setFailed(value)
        }

        override fun setLoading(value: Any?) {
            Timber.d("setLoading")
            super.setLoading(value)
        }

        override fun setSucceeded(value: Any?) {
            Timber.d("setSucceeded")
            super.setSucceeded(value)
        }
    }

    object Cloud : State()
}