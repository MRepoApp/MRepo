package com.sanmer.mrepo.datastore

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.compat.BuildCompat
import com.sanmer.mrepo.datastore.modules.ModulesMenuExt
import com.sanmer.mrepo.datastore.modules.toExt
import com.sanmer.mrepo.datastore.modules.toProto
import com.sanmer.mrepo.datastore.repository.RepositoryMenuExt
import com.sanmer.mrepo.datastore.repository.toExt
import com.sanmer.mrepo.datastore.repository.toProto
import com.sanmer.mrepo.ui.theme.Colors
import java.io.File

data class UserPreferencesExt(
    val workingMode: WorkingMode,
    val isRoot: Boolean = workingMode != WorkingMode.MODE_NON_ROOT,
    val isNonRoot: Boolean = workingMode == WorkingMode.MODE_NON_ROOT,
    val isSetup: Boolean = workingMode == WorkingMode.FIRST_SETUP,
    val darkMode: DarkMode,
    val themeColor: Int,
    val deleteZipFile: Boolean,
    val useDoh: Boolean,
    val downloadPath: File,
    val repositoryMenu: RepositoryMenuExt,
    val modulesMenu: ModulesMenuExt
) {
    companion object {
        fun default() = UserPreferencesExt(
            workingMode = WorkingMode.FIRST_SETUP,
            darkMode = DarkMode.FOLLOW_SYSTEM,
            themeColor = if (BuildCompat.atLeastS) Colors.Dynamic.id else Colors.Pourville.id,
            deleteZipFile = false,
            useDoh = false,
            downloadPath = Const.PUBLIC_DOWNLOADS,
            repositoryMenu = RepositoryMenuExt.default(),
            modulesMenu = ModulesMenuExt.default()
        )
    }
}

@Composable
fun UserPreferencesExt.isDarkMode() = when (darkMode) {
    DarkMode.ALWAYS_OFF -> false
    DarkMode.ALWAYS_ON -> true
    else -> isSystemInDarkTheme()
}

fun UserPreferencesExt.toProto(): UserPreferences = UserPreferences.newBuilder()
    .setWorkingMode(workingMode)
    .setDarkMode(darkMode)
    .setThemeColor(themeColor)
    .setDeleteZipFile(deleteZipFile)
    .setUseDoh(useDoh)
    .setDownloadPath(downloadPath.absolutePath)
    .setRepositoryMenu(repositoryMenu.toProto())
    .setModulesMenu(modulesMenu.toProto())
    .build()

fun UserPreferences.toExt() = UserPreferencesExt(
    workingMode = workingMode,
    darkMode = darkMode,
    themeColor = themeColor,
    deleteZipFile = deleteZipFile,
    useDoh = useDoh,
    downloadPath = downloadPath.ifEmpty{ Const.PUBLIC_DOWNLOADS.absolutePath }.let(::File),
    repositoryMenu = repositoryMenuOrNull?.toExt() ?: RepositoryMenuExt.default(),
    modulesMenu = modulesMenuOrNull?.toExt() ?: ModulesMenuExt.default()
)

fun UserPreferences.new(
    block: UserPreferencesKt.Dsl.() -> Unit
) = toExt()
    .toProto()
    .copy(block)