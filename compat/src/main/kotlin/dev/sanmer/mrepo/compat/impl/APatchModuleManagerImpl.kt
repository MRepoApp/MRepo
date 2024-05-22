package dev.sanmer.mrepo.compat.impl

import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import dev.sanmer.mrepo.compat.stub.IInstallCallback
import dev.sanmer.mrepo.compat.stub.IModuleOpsCallback

internal class APatchModuleManagerImpl(
    private val shell: Shell,
) : BaseModuleManagerImpl(shell) {
    override fun enable(id: String, callback: IModuleOpsCallback) {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) callback.onFailure(id, null)

        "apd module enable $id".submit {
            if (it.isSuccess) {
                callback.onSuccess(id)
            } else {
                callback.onFailure(id, it.out.joinToString())
            }
        }
    }

    override fun disable(id: String, callback: IModuleOpsCallback) {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) return callback.onFailure(id, null)

        "apd module disable $id".submit {
            if (it.isSuccess) {
                callback.onSuccess(id)
            } else {
                callback.onFailure(id, it.out.joinToString())
            }
        }
    }

    override fun remove(id: String, callback: IModuleOpsCallback) {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) return callback.onFailure(id, null)

        "apd module uninstall $id".submit {
            if (it.isSuccess) {
                callback.onSuccess(id)
            } else {
                callback.onFailure(id, it.out.joinToString())
            }
        }
    }

    override fun install(path: String, callback: IInstallCallback) {
        val cmd = "apd module install '${path}'"

        val stdout = object : CallbackList<String?>() {
            override fun onAddElement(msg: String?) {
                msg?.let(callback::onStdout)
            }
        }

        val stderr = object : CallbackList<String?>() {
            override fun onAddElement(msg: String?) {
                msg?.let(callback::onStderr)
            }
        }

        val result = shell.newJob().add(cmd).to(stdout, stderr).exec()
        if (result.isSuccess) {
            val module = getModuleInfo(path)
            callback.onSuccess(module)
        } else {
            callback.onFailure()
        }
    }
}