package com.sanmer.mrepo.app.runtime

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.app.Config

object Configure {
    private var THEME_COLOR by mutableStateOf(Config.THEME_COLOR)
    private var DARK_MODE by mutableStateOf(Config.DARK_MODE)

    private var DOWNLOAD_PATH by mutableStateOf(Config.DOWNLOAD_PATH)

    private var REPO_TAG by mutableStateOf(Config.REPO_TAG)
    private var REPO_GITHUB by mutableStateOf(Config.REPO_GITHUB)
    private var REPO_BRANCH by mutableStateOf(Config.REPO_BRANCH)
    private var REPO_URL by mutableStateOf(Config.REPO_URL)

    var themeColor: Int
        get() = THEME_COLOR
        set(value) {
            THEME_COLOR = value
            Config.THEME_COLOR = value
        }

    var darkTheme: Int
        get() = DARK_MODE
        set(value) {
            DARK_MODE = value
            Config.DARK_MODE = value
        }

    var downloadPath: String
        get() = DOWNLOAD_PATH
        set(value) {
            DOWNLOAD_PATH = value
            Config.DOWNLOAD_PATH = value
        }

    var repoTag: Int
        get() = REPO_TAG
        set(value) {
            REPO_TAG = value
            Config.REPO_TAG = value
        }

    var repoGithub: String
        get() = REPO_GITHUB
        set(value) {
            REPO_GITHUB = value
            Config.REPO_GITHUB = value
        }

    var repoBranch: String
        get() = REPO_BRANCH
        set(value) {
            REPO_BRANCH = value
            Config.REPO_BRANCH = value
        }

    var repoUrl: String
        get() = REPO_URL
        set(value) {
            REPO_URL = value
            Config.REPO_URL = value
        }

    @Composable
    fun isDarkTheme(): Boolean {
        return when (darkTheme) {
            Config.ALWAYS_ON -> true
            Config.ALWAYS_OFF -> false
            else -> isSystemInDarkTheme()
        }
    }
}
