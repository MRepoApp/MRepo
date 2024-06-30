package dev.sanmer.mrepo.impl

import dev.sanmer.mrepo.content.Module
import dev.sanmer.mrepo.content.State
import dev.sanmer.mrepo.impl.Shell.exec
import dev.sanmer.mrepo.stub.IInstallCallback
import dev.sanmer.mrepo.stub.IModuleManager
import dev.sanmer.su.wrap.ThrowableWrapper.Companion.wrap
import java.io.File
import java.util.zip.ZipFile

internal abstract class BaseModuleManagerImpl : IModuleManager.Stub() {
    internal val modulesDir = File(MODULES_PATH)

    private val mVersion by lazy {
        "su -v".exec().getOrDefault("unknown")
    }

    private val mVersionCode by lazy {
        "su -V".exec().getOrDefault("").toIntOr(-1)
    }

    override fun getVersion(): String {
        return mVersion
    }

    override fun getVersionCode(): Int {
        return mVersionCode
    }

    override fun getModules() = modulesDir.listFiles()
        .orEmpty()
        .mapNotNull { moduleDir ->
            readProps(moduleDir)?.toModule(moduleDir)
        }

    override fun getModuleById(id: String): Module? {
        val moduleDir = File(modulesDir, id)
        return readProps(moduleDir)?.toModule(moduleDir)
    }

    override fun getModuleInfo(path: String): Module? {
        val zipFile = ZipFile(path)
        val entry = zipFile.getEntry(PROP_FILE) ?: return null

        return zipFile.getInputStream(entry).use {
            it.bufferedReader()
                .readText()
                .let(::readProps)
                .toModule()
        }
    }

    override fun deleteOnExit(path: String) = with(File(path)) {
        when {
            isFile -> delete()
            isDirectory -> deleteRecursively()
            else -> false
        }
    }

    override fun reboot() {
        "svc power reboot || reboot".exec()
    }

    private fun readProps(props: String) = props.lines()
        .associate { line ->
            val items = line.split("=", limit = 2).map { it.trim() }
            if (items.size != 2) {
                "" to ""
            } else {
                items[0] to items[1]
            }
        }

    private fun readProps(moduleDir: File) =
        File(moduleDir, PROP_FILE).let {
            when {
                it.exists() -> readProps(it.readText())
                else -> null
            }
        }

    private fun readState(moduleDir: File): State {
        File(moduleDir, "remove").apply {
            if (exists()) return State.Remove
        }

        File(moduleDir, "disable").apply {
            if (exists()) return State.Disable
        }

        File(moduleDir, "update").apply {
            if (exists()) return State.Update
        }

        return State.Enable
    }

    private fun readLastUpdated(moduleDir: File): Long {
        MODULE_FILES.forEach { filename ->
            val file = File(moduleDir, filename)
            if (file.exists()) {
                return file.lastModified()
            }
        }

        return 0L
    }

    private fun Map<String, String>.toModule(
        moduleDir: File
    ) = toModule(
        path = moduleDir.name,
        state = readState(moduleDir),
        lastUpdated = readLastUpdated(moduleDir)
    )

    private fun Map<String, String>.toModule(
        path: String = "unknown",
        state: State = State.Enable,
        lastUpdated: Long = 0L
    ) = Module(
        id = getOrDefault("id", path),
        name = getOrDefault("name", path),
        version = getOrDefault("version", ""),
        versionCode = getOrDefault("versionCode", "").toIntOr(-1),
        author = getOrDefault("author", ""),
        description = getOrDefault("description", ""),
        updateJson = getOrDefault("updateJson", ""),
        state = state,
        lastUpdated = lastUpdated
    )

    private fun String.toIntOr(defaultValue: Int) =
        runCatching { toInt() }.getOrDefault(defaultValue)

    internal fun install(cmd: String, path: String, callback: IInstallCallback?) {
        if (callback == null) {
            cmd.exec()
            return
        }

        cmd.exec(
            stdout = callback::onStdout,
            stderr = callback::onStderr
        ).onSuccess {
            val module = getModuleInfo(path)
            callback.onSuccess(module)
        }.onFailure {
            callback.onFailure(it.wrap())
        }
    }

    companion object {
        const val PROP_FILE = "module.prop"
        const val MODULES_PATH = "/data/adb/modules"

        val MODULE_FILES = listOf(
            "post-fs-data.sh", "service.sh", "uninstall.sh",
            "system", "system.prop", "module.prop"
        )
    }
}