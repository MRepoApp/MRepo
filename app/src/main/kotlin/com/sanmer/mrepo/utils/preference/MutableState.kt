package com.sanmer.mrepo.utils.preference

import androidx.compose.runtime.MutableState
import kotlin.reflect.KProperty

operator fun <T> MutableState<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    value: T
) {
    update(value) { this.value = it }
    SPUtils.putValue(property.name, value)
}

operator fun <T> MutableState<T>.getValue(
    thisObj: Any?, property:
    KProperty<*>
): T {
    return SPUtils.getValue(property.name, value)
}

private fun <T> update(value: T, update: (T) -> Unit) {
    val v = when (value) {
        is Long -> Long.MAX_VALUE
        is String -> ""
        is Int -> Int.MAX_VALUE
        is Boolean -> !value
        is Float -> Float.MAX_VALUE
        else -> throw IllegalArgumentException()
    }

    @Suppress("UNCHECKED_CAST")
    update(v as T)
    update(value)
}