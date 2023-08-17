package com.sanmer.mrepo.datastore

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.utils.OsUtils
import com.sanmer.mrepo.datastore.repository.RepositoryMenuExt
import com.sanmer.mrepo.datastore.repository.toExt
import com.sanmer.mrepo.datastore.repository.toProto
import com.sanmer.mrepo.ui.theme.Colors
import com.sanmer.mrepo.utils.extensions.toFile
import java.io.File

data class UserPreferencesExt(
    val workingMode: WorkingMode,
    val isRoot: Boolean = workingMode == WorkingMode.MODE_ROOT,
    val isNonRoot: Boolean = workingMode == WorkingMode.MODE_NON_ROOT,
    val isSetup: Boolean = workingMode == WorkingMode.FIRST_SETUP,
    val darkMode: DarkMode,
    val themeColor: Int,
    val downloadPath: File,
    val deleteZipFile: Boolean,
    val repositoryMenu: RepositoryMenuExt
) {
    companion object {
        fun default() = UserPreferencesExt(
            workingMode = WorkingMode.FIRST_SETUP,
            darkMode = DarkMode.FOLLOW_SYSTEM,
            themeColor = if (OsUtils.atLeastS) Colors.Dynamic.id else Colors.Sakura.id,
            downloadPath = Const.DIR_PUBLIC_DOWNLOADS.resolve("MRepo"),
            deleteZipFile = true,
            repositoryMenu = RepositoryMenuExt.default()
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
    .setDownloadPath(downloadPath.absolutePath)
    .setDeleteZipFile(deleteZipFile)
    .setRepositoryMenu(repositoryMenu.toProto())
    .build()

fun UserPreferences.toExt() = UserPreferencesExt(
    workingMode = workingMode,
    darkMode = darkMode,
    themeColor = themeColor,
    downloadPath = downloadPath.toFile(),
    deleteZipFile = deleteZipFile,
    repositoryMenu = repositoryMenuOrNull?.toExt() ?: RepositoryMenuExt.default()
)

fun UserPreferences.new(
    block: UserPreferencesKt.Dsl.() -> Unit
) = toExt()
    .toProto()
    .copy(block)