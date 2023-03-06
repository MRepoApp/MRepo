package com.sanmer.mrepo.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.sanmer.mrepo.ui.theme.Colors
import com.sanmer.mrepo.utils.SPUtils
import kotlin.reflect.KProperty

object Config {
    private val sp = SPUtils

    // WORKING_MODE
    const val FIRST_SETUP = 0
    const val MODE_ROOT = 1
    const val MODE_NON_ROOT = 2
    private const val WORKING_MODE_KEY = "WORKING_MODE"
    var WORKING_MODE: Int
        get() = sp.getValue(WORKING_MODE_KEY, FIRST_SETUP)
        set(value) { sp.putValue(WORKING_MODE_KEY, value) }

    // THEME_COLOR
    var THEME_COLOR by mutableStateOf(
        if (Const.atLeastS) {
            Colors.Dynamic.id
        } else {
            Colors.Sakura.id
        }
    )

    // DARK_MODE
    const val FOLLOW_SYSTEM = 0
    const val ALWAYS_OFF = 1
    const val ALWAYS_ON = 2
    var DARK_MODE by mutableStateOf(FOLLOW_SYSTEM)

    // DOWNLOAD
    var DOWNLOAD_PATH: String by mutableStateOf(Const.DIR_PUBLIC_DOWNLOADS.absolutePath)

    operator fun <T>MutableState<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
        sp.putValue(property.name, value)
        this.value = update(value)
        this.value = value
    }

    operator fun <T>MutableState<T>.getValue(thisObj: Any?, property: KProperty<*>): T {
        return sp.getValue(property.name, value)
    }

    private fun <T> update(value: T): T {
        val res: Any = when (value) {
            is Long -> Long.MAX_VALUE
            is String -> ""
            is Int -> Int.MAX_VALUE
            is Boolean -> true
            is Float -> Float.MAX_VALUE
            else -> throw java.lang.IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        return res as T
    }

    @Composable
    fun isDarkTheme() = when (DARK_MODE) {
        ALWAYS_ON -> true
        ALWAYS_OFF -> false
        else -> isSystemInDarkTheme()
    }
}