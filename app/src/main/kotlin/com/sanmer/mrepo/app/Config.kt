package com.sanmer.mrepo.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.sanmer.mrepo.ui.theme.Colors
import com.sanmer.mrepo.utils.preference.getValue
import com.sanmer.mrepo.utils.preference.setValue


object Config {
    // WORKING_MODE
    const val FIRST_SETUP = 0
    const val MODE_ROOT = 1
    const val MODE_NON_ROOT = 2
    var workingMode by mutableStateOf(FIRST_SETUP)

    val isSetup get() = workingMode == FIRST_SETUP
    val isRoot get() = workingMode == MODE_ROOT
    val isNonRoot get() = workingMode == MODE_NON_ROOT

    // THEME_COLOR
    var themeColor by mutableStateOf(
        if (Const.atLeastS) {
            Colors.Dynamic.id
        } else {
            Colors.Sakura.id
        }
    )

    // DARK_MODE
    const val FOLLOW_SYSTEM = 0
    const val ALWAYS_OFF = 1
    const val ALWAYS_ON = 2
    var darkMode by mutableStateOf(FOLLOW_SYSTEM)

    @Composable
    fun isDarkMode() = when (darkMode) {
        ALWAYS_ON -> true
        ALWAYS_OFF -> false
        else -> isSystemInDarkTheme()
    }

    // DOWNLOAD
    var downloadPath: String by mutableStateOf(Const.DIR_PUBLIC_DOWNLOADS.absolutePath)
}