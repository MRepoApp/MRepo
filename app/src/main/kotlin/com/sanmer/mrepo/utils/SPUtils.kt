package com.sanmer.mrepo.utils

import android.content.Context
import com.sanmer.mrepo.App
import com.sanmer.mrepo.BuildConfig
import kotlin.reflect.KProperty

object SPUtils {
    private val context by lazy { App.context }
    private const val SP_NAME = "${BuildConfig.APPLICATION_ID}_preferences"
    private val sp by lazy { context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE) }

    fun <T> getValue(name: String, default: T): T = with(sp) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default).orEmpty()
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        res as T
    }

    fun <T> putValue(name: String, value: T) = with(sp.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences!")
        }.apply()
    }
}

data class Preference<T>(
    var value: T
) {
    operator fun getValue(
        thisObj: Any?, property:
        KProperty<*>
    ): T {
        return SPUtils.getValue(property.name, value)
    }

    operator fun setValue(
        thisObj: Any?,
        property: KProperty<*>,
        value: T
    ) {
        SPUtils.putValue(property.name, value)
        this.value = value
    }
}

fun <T>mutablePreferenceOf(value: T) = Preference(value)