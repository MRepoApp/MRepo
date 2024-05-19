package dev.sanmer.mrepo.compat

import dev.sanmer.mrepo.compat.delegate.ContextDelegate

internal object Const {
    const val TIMEOUT_MILLIS = 15_000L

    val PACKAGE_NAME: String by lazy {
        val context = ContextDelegate.getContext()
        context.packageName
    }
}