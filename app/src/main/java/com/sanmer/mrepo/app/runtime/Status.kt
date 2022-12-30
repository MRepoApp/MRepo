package com.sanmer.mrepo.app.runtime

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.app.status.Event

object Status {
    object Env {
        var event by mutableStateOf(Event.LOADING)
        val isSucceeded get() = event == Event.SUCCEEDED
        val isFailed get() = event == Event.FAILED
    }

    object FileSystem {
        var event by mutableStateOf(Event.LOADING)
        val isSucceeded get() = event == Event.SUCCEEDED
        val isFailed get() = event == Event.FAILED
    }

    object Online {
        var timestamp by mutableStateOf("")
        var event by mutableStateOf(Event.LOADING)
        val isSucceeded get() = event == Event.SUCCEEDED
        val isFailed get() = event == Event.FAILED
        val isFinished get() = isSucceeded or isFailed
    }

    object Local {
        var event by mutableStateOf(Event.LOADING)
        val isSucceeded get() = event == Event.SUCCEEDED
        val isFailed get() = event == Event.FAILED
        val isFinished get() = isSucceeded or isFailed
    }
}