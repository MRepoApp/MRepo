package dev.sanmer.mrepo.impl

import dev.sanmer.mrepo.content.LocalModule
import dev.sanmer.mrepo.content.State
import dev.sanmer.mrepo.impl.Shell.exec
import dev.sanmer.mrepo.impl.Shell.submit
import dev.sanmer.mrepo.stub.IInstallCallback
import dev.sanmer.mrepo.stub.IModuleManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.zip.ZipFile

internal abstract class BaseModuleManagerImpl(
    private val managerScope: CoroutineScope
) : IModuleManager.Stub() {
    internal val modulesDir = File(MODULES_PATH)

    private val mVersion by lazy {
        "su -v".exec().getOrDefault("unknown")
    }

    private val mVersionCode by lazy {
        "su -V".exec().getOrDefault("-1")
            .toIntOr(-1)
    }

    override fun getVersion(): String {
        return mVersion
    }

    override fun getVersionCode(): Int {
        return mVersionCode
    }

    override fun getModules() = modulesDir.listFiles()
        .orEmpty()
        .mapNotNull { dir ->
            readProps(dir)?.toModule(dir)
        }

    override fun getModuleById(id: String): LocalModule? {
        val dir = modulesDir.resolve(id)
        return readProps(dir)?.toModule(dir)
    }

    override fun getModuleInfo(zipPath: String): LocalModule? {
        val zipFile = ZipFile(zipPath)
        val entry = zipFile.getEntry(PROP_FILE) ?: return null

        return zipFile.getInputStream(entry).use {
            it.bufferedReader()
                .readText()
                .let(::readProps)
                .toModule()
        }
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
        moduleDir.resolve(PROP_FILE).let {
            when {
                it.exists() -> readProps(it.readText())
                else -> null
            }
        }

    private fun readState(moduleDir: File): State {
        moduleDir.resolve("remove").apply {
            if (exists()) return State.REMOVE
        }

        moduleDir.resolve("disable").apply {
            if (exists()) return State.DISABLE
        }

        moduleDir.resolve("update").apply {
            if (exists()) return State.UPDATE
        }

        return State.ENABLE
    }

    private fun readLastUpdated(moduleDir: File): Long {
        MODULE_FILES.forEach { filename ->
            val file = moduleDir.resolve(filename)
            if (file.exists()) {
                return file.lastModified()
            }
        }

        return 0L
    }

    private fun Map<String, String>.toModule(
        dir: File
    ) = toModule(
        path = dir.name,
        state = readState(dir),
        lastUpdated = readLastUpdated(dir)
    )

    private fun Map<String, String>.toModule(
        path: String = "unknown",
        state: State = State.ENABLE,
        lastUpdated: Long = 0L
    ) = LocalModule(
        id = getOrDefault("id", path),
        name = getOrDefault("name", path),
        version = getOrDefault("version", ""),
        versionCode = getOrDefault("versionCode", "-1").toIntOr(-1),
        author = getOrDefault("author", ""),
        description = getOrDefault("description", ""),
        updateJson = getOrDefault("updateJson", ""),
        state = state,
        lastUpdated = lastUpdated
    )

    private fun String.toIntOr(defaultValue: Int) =
        runCatching {
            toInt()
        }.getOrDefault(defaultValue)

    internal fun install(cmd: String, path: String, callback: IInstallCallback) {
        managerScope.launch {
            val result = cmd.submit(
                stdout = callback::onStdout,
                stderr = callback::onStderr
            )

            when {
                result.isSuccess -> {
                    val module = getModuleInfo(path)
                    callback.onSuccess(module)
                }
                else -> {
                    callback.onFailure()
                }
            }
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