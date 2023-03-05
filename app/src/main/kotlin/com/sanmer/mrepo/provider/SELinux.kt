package com.sanmer.mrepo.provider

import java.io.IOException

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

        val isSelinuxEnabled: Boolean get() = try {
            SuProvider.Root.isSelinuxEnabled
        } catch (e: Exception) {
            isSelinuxEnabled()
        }

        val enforce: Boolean get() = try {
            SuProvider.Root.enforce
        } catch (e: Exception) {
            getEnforce()
        }

        fun getContextByPid(pid: Int): String = try {
            SuProvider.Root.getContextByPid(pid)
        } catch (e: Exception) {
            SELinux.getContextByPid(pid)
        }
    }

    val context: String
        external get

    external fun isSelinuxEnabled(): Boolean

    @Throws(IOException::class)
    external fun getEnforce(): Boolean

    external fun getContextByPid(pid: Int): String
}