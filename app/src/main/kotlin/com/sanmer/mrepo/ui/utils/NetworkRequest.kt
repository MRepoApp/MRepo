package com.sanmer.mrepo.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.State
import com.sanmer.mrepo.utils.HttpUtils
import timber.log.Timber

@Composable
fun <T> launchRequest(
    get: suspend () -> Result<T>,
    onFailure: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit
): Event {
    val state = remember {
        object : State(initial = Event.LOADING) {
            override fun setSucceeded(value: Any?) {
                @Suppress("UNCHECKED_CAST")
                onSuccess(value as T)
                super.setSucceeded(value)
            }

            override fun setFailed(value: Any?) {
                onFailure(value as Throwable)
                super.setFailed(value)
            }
        }
    }

    LaunchedEffect(null) {
        get().onSuccess {
            state.setSucceeded(it)
        }.onFailure {
            state.setFailed(it)
            Timber.e(it)
        }
    }

    return state.event
}

@Composable
fun stringRequest(
    url: String,
    onFailure: (Throwable) -> Unit = {},
    onSuccess: (String) -> Unit
) = launchRequest(
    get = { HttpUtils.requestString(url) },
    onSuccess = onSuccess,
    onFailure = onFailure
)

@Composable
inline fun <reified T> jsonRequest(
    url: String,
    noinline onFailure: (Throwable) -> Unit = {},
    noinline onSuccess: (T) -> Unit
) = launchRequest(
    get = { HttpUtils.requestJson(url) },
    onSuccess = onSuccess,
    onFailure = onFailure
)