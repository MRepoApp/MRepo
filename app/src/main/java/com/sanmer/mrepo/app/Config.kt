package com.sanmer.mrepo.app

import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.ui.theme.Colors
import com.tencent.mmkv.MMKV

object Config {
    private var kv = MMKV.mmkvWithID("config")

    // WORKING_MODE
    const val FIRST_SETUP = 0
    const val MODE_ROOT = 1
    private const val WORKING_MODE_KEY = "WORKING_MODE"

    var WORKING_MODE: Int
        get() = kv.decodeInt(WORKING_MODE_KEY, FIRST_SETUP)
        set(value) { kv.encode(WORKING_MODE_KEY, value) }

    // PREFERENCE
    private const val FOLLOW_SYSTEM = 0
    const val ALWAYS_OFF = 1
    const val ALWAYS_ON = 2

    // THEME_COLOR
    private const val THEME_COLOR_KEY = "THEME_COLOR"
    var THEME_COLOR: Int
        get() = kv.decodeInt(
            THEME_COLOR_KEY,
            if (Const.atLeastS) Colors.Dynamic.id else Colors.Sakura.id
        )
        set(value) { kv.encode(THEME_COLOR_KEY, value) }

    // DARK_MODE
    private const val DARK_MODE_KEY = "DARK_MODE"
    var DARK_MODE: Int
        get() = kv.decodeInt(DARK_MODE_KEY, FOLLOW_SYSTEM)
        set(value) { kv.encode(DARK_MODE_KEY, value) }

    // DOWNLOAD
    private const val DOWNLOAD_PATH_KEY = "DOWNLOAD_PATH"
    var DOWNLOAD_PATH: String
        get() = kv.decodeString(DOWNLOAD_PATH_KEY, Const.DIR_PUBLIC_DOWNLOADS.absolutePath)!!
        set(value) { kv.encode(DOWNLOAD_PATH_KEY, value) }

    // REPO
    val REPO_LIST = listOf("Github", "URL")
    const val REPO_GITHUB_TAG = 0
    const val REPO_URL_TAG = 1
    val displayRepoName get() = REPO_LIST[REPO_TAG]

    // REPO_TAG
    private const val REPO_TAG_KEY = "REPO_TAG"
    var REPO_TAG: Int
        get() = kv.decodeInt(REPO_TAG_KEY, REPO_GITHUB_TAG)
        set(value) { kv.encode(REPO_TAG_KEY, value) }

    // REPO_GITHUB
    private const val REPO_GITHUB_KEY = "REPO_GITHUB"
    var REPO_GITHUB: String
        get() = kv.decodeString(REPO_GITHUB_KEY, Const.REPO_GITHUB)!!
        set(value) { kv.encode(REPO_GITHUB_KEY, value) }

    private const val REPO_BRANCH_KEY = "REPO_BRANCH"
    var REPO_BRANCH: String
        get() = kv.decodeString(REPO_BRANCH_KEY, if (BuildConfig.DEBUG) "dev" else Const.REPO_BRANCH )!!
        set(value) { kv.encode(REPO_BRANCH_KEY, value) }

    // REPO_URL
    private const val REPO_URL_KEY = "REPO_URL"
    var REPO_URL: String
        get() = kv.decodeString(REPO_URL_KEY, "${Const.REPO_URL}json/modules.json")!!
        set(value) { kv.encode(REPO_URL_KEY, value) }
}