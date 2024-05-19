package com.sanmer.mrepo.datastore

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.compat.BuildCompat
import com.sanmer.mrepo.datastore.modules.ModulesMenuCompat
import com.sanmer.mrepo.datastore.repository.RepositoryMenuCompat
import com.sanmer.mrepo.ui.theme.Colors
import java.io.File

data class UserPreferencesCompat(
    val workingMode: WorkingMode,
    val isRoot: Boolean = workingMode != WorkingMode.MODE_NON_ROOT,
    val isNonRoot: Boolean = workingMode == WorkingMode.MODE_NON_ROOT,
    val isSetup: Boolean = workingMode == WorkingMode.FIRST_SETUP,
    val darkMode: DarkMode,
    val themeColor: Int,
    val deleteZipFile: Boolean,
    val useDoh: Boolean,
    val downloadPath: File,
    val repositoryMenu: RepositoryMenuCompat,
    val modulesMenu: ModulesMenuCompat
) {
    constructor(original: UserPreferences) : this(
        workingMode = original.workingMode,
        darkMode = original.darkMode,
        themeColor = original.themeColor,
        deleteZipFile = original.deleteZipFile,
        useDoh = original.useDoh,
        downloadPath = original.downloadPath.ifEmpty{ Const.PUBLIC_DOWNLOADS.absolutePath }.let(::File),
        repositoryMenu = original.repositoryMenuOrNull?.let(::RepositoryMenuCompat)
            ?: RepositoryMenuCompat.default(),
        modulesMenu = original.modulesMenuOrNull?.let(::ModulesMenuCompat)
            ?: ModulesMenuCompat.default()
    )

    @Composable
    fun isDarkMode() = when (darkMode) {
        DarkMode.ALWAYS_OFF -> false
        DarkMode.ALWAYS_ON -> true
        else -> isSystemInDarkTheme()
    }

    fun toProto(): UserPreferences = UserPreferences.newBuilder()
        .setWorkingMode(workingMode)
        .setDarkMode(darkMode)
        .setThemeColor(themeColor)
        .setDeleteZipFile(deleteZipFile)
        .setUseDoh(useDoh)
        .setDownloadPath(downloadPath.absolutePath)
        .setRepositoryMenu(repositoryMenu.toProto())
        .setModulesMenu(modulesMenu.toProto())
        .build()

    companion object {
        fun default() = UserPreferencesCompat(
            workingMode = WorkingMode.FIRST_SETUP,
            darkMode = DarkMode.FOLLOW_SYSTEM,
            themeColor = if (BuildCompat.atLeastS) Colors.Dynamic.id else Colors.Pourville.id,
            deleteZipFile = false,
            useDoh = false,
            downloadPath = Const.PUBLIC_DOWNLOADS,
            repositoryMenu = RepositoryMenuCompat.default(),
            modulesMenu = ModulesMenuCompat.default()
        )

        fun UserPreferences.new(
            block: UserPreferencesKt.Dsl.() -> Unit
        ) = UserPreferencesCompat(this)
            .toProto()
            .copy(block)
    }
}