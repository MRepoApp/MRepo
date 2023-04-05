package com.sanmer.mrepo.provider

object SELinux {
    init {
        System.loadLibrary("selinux-jni")
    }

    object Root {
        val context: String get() = try {
            SuProvider.Root.context
        } catch (e: Exception) {
            SELinux.context
        }

        val enforce: Int get() = try {
            SuProvider.Root.enforce
        } catch (e: Exception) {
            SELinux.enforce
        }
    }

    val context: String
        external get

    val enforce: Int
        external get
}