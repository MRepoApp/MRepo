package com.sanmer.mrepo.utils.preference

import android.content.Context
import com.sanmer.mrepo.App
import com.sanmer.mrepo.BuildConfig

object SPUtils {
    private val context by lazy { App.context }
    private const val SP_NAME = "${BuildConfig.APPLICATION_ID}_preferences"
    private val sp by lazy { context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE) }

    fun <T> getValue(name: String, default: T): T = with(sp) {
        val value: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default).orEmpty()
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        return@with value as T
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

