package com.sanmer.mrepo.utils

import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.Shell.Result

object ShellHelper {
    fun submit(
        onCallback: (String?) -> Unit = {},
        onSucceeded: (Result) -> Unit = {},
        onFailed: (Result) -> Unit = {},
        onFinished: (Result) -> Unit = {},
        command: String,
    ) {
        Shell.cmd(command)
            .to(object : CallbackList<String?>() {
                override fun onAddElement(str: String?) {
                    onCallback(str)
                }
            })
            .submit {
                if (it.isSuccess) {
                    onSucceeded(it)
                } else {
                    onFailed(it)
                }
                onFinished(it)
            }
    }
}