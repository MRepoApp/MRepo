package dev.sanmer.mrepo.compat

import com.sanmer.mrepo.BuildConfig

internal object BuildConfig {
    val DEBUG get() = BuildConfig.DEBUG
    val APPLICATION_ID: String get() = BuildConfig.APPLICATION_ID
    val BUILD_TYPE: String get() = BuildConfig.BUILD_TYPE
    val VERSION_CODE get() = BuildConfig.VERSION_CODE
    val VERSION_NAME: String get() = BuildConfig.VERSION_NAME

}