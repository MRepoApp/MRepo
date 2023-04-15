package com.sanmer.mrepo.provider

object SELinux {
    init {
        System.loadLibrary("selinux-jni")
    }

    val context: String
        external get

    val enforce: Int
        external get
}