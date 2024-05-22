package dev.sanmer.mrepo.compat.impl

import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import dev.sanmer.mrepo.compat.content.LocalModule
import dev.sanmer.mrepo.compat.content.State
import dev.sanmer.mrepo.compat.stub.IModuleManager
import java.io.File
import java.util.zip.ZipFile

internal abstract class BaseModuleManagerImpl(
    private val shell: Shell
) : IModuleManager.Stub() {
    internal val modulesDir = File(MODULES_PATH)

    private val mVersion by lazy {
        runCatching {
            "su -v".exec()
        }.getOrDefault("unknown")
    }

    private val mVersionCode by lazy {
        runCatching {
            "su -V".exec().toInt()
        }.getOrDefault(-1)
    }

    override fun getVersion(): String {
        return mVersion
    }

    override fun getVersionCode(): Int {
        return mVersionCode
    }

    override fun getModules() = modulesDir.listFiles().orEmpty()
        .mapNotNull { moduleDir ->
            runCatching {
                readProps(moduleDir)
                    ?.toModule(
                        state = readState(moduleDir),
                        lastUpdated = readLastUpdated(moduleDir)
                    )
            }.getOrNull()
        }

    override fun getModuleById(id: String) = runCatching {
        val moduleDir = modulesDir.resolve(id)
        readProps(moduleDir)
            ?.toModule(
                state = readState(moduleDir),
                lastUpdated = readLastUpdated(moduleDir)
            )
    }.getOrNull()

    override fun getModuleInfo(zipPath: String) = runCatching {
        val zipFile = ZipFile(zipPath)
        val entry = zipFile.getEntry(PROP_FILE) ?: return@runCatching null

        zipFile.getInputStream(entry).use {
            it.bufferedReader()
                .readText()
                .let(::readProps)

        }.toModule()

    }.getOrNull()

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
        state: State = State.ENABLE,
        lastUpdated: Long = 0L
    ) = LocalModule(
        id = getOrDefault("id", "unknown"),
        name = getOrDefault("name", "unknown"),
        version = getOrDefault("version", ""),
        versionCode = getOrDefault("versionCode", "-1").toInt(),
        author = getOrDefault("author", ""),
        description = getOrDefault("description", ""),
        updateJson = getOrDefault("updateJson", ""),
        state = state,
        lastUpdated = lastUpdated
    )

    private fun String.exec() = ShellUtils.fastCmd(shell, this)

    internal fun String.submit(cb: Shell.ResultCallback) = shell
        .newJob().add(this).to(ArrayList(), null)
        .submit(cb)

    companion object {
        const val PROP_FILE = "module.prop"
        const val MODULES_PATH = "/data/adb/modules"

        val MODULE_FILES = listOf(
            "post-fs-data.sh", "service.sh", "uninstall.sh",
            "system", "system.prop", "module.prop"
        )
    }
}