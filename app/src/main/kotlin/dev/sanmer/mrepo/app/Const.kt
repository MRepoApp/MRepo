package dev.sanmer.mrepo.app

import android.os.Environment
import java.io.File

object Const {
    val PUBLIC_DOWNLOADS: File = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS
    )

    const val MY_GITHUB_URL = "https://github.com/SanmerDev"
    const val TRANSLATE_URL = "https://weblate.sanmer.app/engage/mrepo"
    const val GITHUB_URL = "https://github.com/MRepoApp/MRepo"
    const val TELEGRAM_URL = "https://t.me/mrepo_news"
    const val DEMO_REPO_URL = "https://demo-repo.sanmer.app/"
    const val SPDX_URL = "https://spdx.org/licenses/%s.json"
}