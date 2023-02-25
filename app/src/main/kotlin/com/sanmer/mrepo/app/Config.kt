package com.sanmer.mrepo.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.ui.theme.Colors
import com.sanmer.mrepo.utils.SPUtils
import java.util.concurrent.TimeUnit

object Config {
    private val sp = SPUtils

    // WORKING_MODE
    const val FIRST_SETUP = 0
    const val MODE_ROOT = 1
    const val MODE_NON_ROOT = 2
    private const val WORKING_MODE_KEY = "WORKING_MODE"
    var workingMode: Int
        get() = sp.getValue(WORKING_MODE_KEY, FIRST_SETUP)
        set(value) { sp.putValue(WORKING_MODE_KEY, value) }

    // THEME_COLOR
    private const val THEME_COLOR_KEY = "THEME_COLOR"
    var themeColor: Int
        get() = sp.getValue(THEME_COLOR_KEY,
            if (Const.atLeastS) Colors.Dynamic.id else Colors.Sakura.id
        )
        set(value) { sp.putValue(THEME_COLOR_KEY, value) }

    // DARK_MODE
    private const val FOLLOW_SYSTEM = 0
    const val ALWAYS_OFF = 1
    const val ALWAYS_ON = 2
    private const val DARK_MODE_KEY = "DARK_MODE"
    var darkMode: Int
        get() = sp.getValue(DARK_MODE_KEY, FOLLOW_SYSTEM)
        set(value) { sp.putValue(DARK_MODE_KEY, value) }

    // DOWNLOAD
    private const val DOWNLOAD_PATH_KEY = "DOWNLOAD_PATH"
    var downloadPath: String
        get() = sp.getValue(DOWNLOAD_PATH_KEY, Const.DIR_PUBLIC_DOWNLOADS.absolutePath)
        set(value) { sp.putValue(DOWNLOAD_PATH_KEY, value) }

    // CHECK_MODULES_UPDATE
    private const val CHECK_MODULES_UPDATE_KEY = "CHECK_MODULES_UPDATE"
    var checkModulesUpdate: Boolean
        get() = sp.getValue(CHECK_MODULES_UPDATE_KEY, true)
        set(value) { sp.putValue(CHECK_MODULES_UPDATE_KEY, value) }

    // TASKS_PERIOD
    private const val TASKS_PERIOD_UNIT_KEY = "TASKS_PERIOD_UNIT"
    var tasksPeriodUnit: TimeUnit
        get() = TimeUnit.valueOf(sp.getValue(TASKS_PERIOD_UNIT_KEY, TimeUnit.HOURS.toString()))
        set(value) { sp.putValue(TASKS_PERIOD_UNIT_KEY, value.toString()) }

    private const val TASKS_PERIOD_COUNT_KEY = "TASKS_PERIOD_COUNT"
    var tasksPeriodCount: Long
        get() = sp.getValue(TASKS_PERIOD_COUNT_KEY, 12)
        set(value) { sp.putValue(TASKS_PERIOD_COUNT_KEY, value) }

    object State {
        private var themeColorSate by mutableStateOf(Config.themeColor)
        private var darkModeSate by mutableStateOf(Config.darkMode)

        private var downloadPathSate by mutableStateOf(Config.downloadPath)
        private var checkModulesUpdateSate by mutableStateOf(checkModulesUpdate)

        var themeColor: Int
            get() = themeColorSate
            set(value) {
                themeColorSate = value
                Config.themeColor = value
            }

        var darkMode: Int
            get() = darkModeSate
            set(value) {
                darkModeSate = value
                Config.darkMode = value
            }

        var downloadPath: String
            get() = downloadPathSate
            set(value) {
                downloadPathSate = value
                Config.downloadPath = value
            }

        var isChackModulesUpdate: Boolean
            get() = checkModulesUpdateSate
            set(value) {
                checkModulesUpdateSate = value
                checkModulesUpdate = value
            }

        @Composable
        fun isDarkTheme() = when (darkMode) {
            ALWAYS_ON -> true
            ALWAYS_OFF -> false
            else -> isSystemInDarkTheme()
        }
    }
}