package com.sanmer.mrepo.datastore

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.ui.theme.Colors
import com.sanmer.mrepo.utils.expansion.toFile
import java.io.File

data class UserData(
    val workingMode: WorkingMode,
    val isRoot: Boolean = workingMode == WorkingMode.MODE_ROOT,
    val isNonRoot: Boolean = workingMode == WorkingMode.MODE_NON_ROOT,
    val isSetup: Boolean = workingMode == WorkingMode.FIRST_SETUP,
    val darkMode: DarkMode,
    val themeColor: Int,
    val downloadPath: File,
    val deleteZipFile: Boolean,
    val enableNavigationAnimation: Boolean
) {
    companion object {
        fun default() = UserData(
            workingMode = WorkingMode.FIRST_SETUP,
            darkMode = DarkMode.FOLLOW_SYSTEM,
            themeColor = if (Const.atLeastS) Colors.Dynamic.id else Colors.Sakura.id,
            downloadPath = Const.DIR_PUBLIC_DOWNLOADS.resolve("MRepo"),
            deleteZipFile = true,
            enableNavigationAnimation = false
        )
    }
}

@Composable
fun UserData.isDarkMode() = when (darkMode) {
    DarkMode.ALWAYS_OFF -> false
    DarkMode.ALWAYS_ON -> true
    else -> isSystemInDarkTheme()
}

fun UserData.toPreferences(): UserPreferences = UserPreferences.newBuilder()
    .setWorkingMode(workingMode)
    .setDarkMode(darkMode)
    .setThemeColor(themeColor)
    .setDownloadPath(downloadPath.absolutePath)
    .setDeleteZipFile(deleteZipFile)
    .setEnableNavigationAnimation(enableNavigationAnimation)
    .build()

fun UserPreferences.toUserData() = UserData(
    workingMode = workingMode,
    darkMode = darkMode,
    themeColor = themeColor,
    downloadPath = downloadPath.toFile(),
    deleteZipFile = deleteZipFile,
    enableNavigationAnimation = enableNavigationAnimation
)