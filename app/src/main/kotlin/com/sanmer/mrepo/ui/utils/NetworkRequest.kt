package com.sanmer.mrepo.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.utils.HttpUtils
import timber.log.Timber

@Composable
fun <T> launchRequest(
    get: suspend () -> Result<T>,
    onFailure: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit
): Event {
    var event by remember { mutableStateOf(Event.LOADING) }

    LaunchedEffect(null) {
        get().onSuccess {
            event = Event.SUCCEEDED
            onSuccess(it)
        }.onFailure {
            event = Event.FAILED
            onFailure(it)

            Timber.e(it)
        }
    }

    return event
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