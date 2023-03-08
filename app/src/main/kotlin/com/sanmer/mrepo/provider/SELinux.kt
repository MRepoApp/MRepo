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

        val isSelinuxEnabled: Boolean get() = try {
            SuProvider.Root.isSelinuxEnabled
        } catch (e: Exception) {
            isSelinuxEnabled()
        }

        fun getContextByPid(pid: Int): String = try {
            SuProvider.Root.getContextByPid(pid)
        } catch (e: Exception) {
            SELinux.getContextByPid(pid)
        }
    }

    val context: String
        external get

    val enforce: Int
        external get

    external fun isSelinuxEnabled(): Boolean

    external fun getContextByPid(pid: Int): String
}