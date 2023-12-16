package com.sanmer.mrepo.model.json

import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.network.NetworkUtils
import com.squareup.moshi.JsonClass
import kotlinx.datetime.Clock
import timber.log.Timber

@JsonClass(generateAdapter = true)
data class UpdateJson(
    val version: String,
    val versionCode: Int,
    val zipUrl: String,
    val changelog: String
) {
    constructor(item: VersionItem) : this(
        version = item.version,
        versionCode = item.versionCode,
        zipUrl = item.zipUrl,
        changelog = item.changelog
    )

    fun toItemOrNull(): VersionItem? {
        if (!NetworkUtils.isUrl(zipUrl)) return null

        val changelog = when {
            !NetworkUtils.isUrl(changelog) -> ""
            NetworkUtils.isBlobUrl(changelog) -> ""
            else -> changelog
        }

        val timestamp = Clock.System.now().toEpochMilliseconds().toFloat() / 1000

        return VersionItem(
            timestamp = timestamp,
            version = version,
            versionCode = versionCode,
            zipUrl = zipUrl,
            changelog = changelog
        )
    }

    companion object {
        suspend fun load(url: String): UpdateJson? {
            if (!NetworkUtils.isUrl(url)) return null

            return NetworkUtils.requestJson<UpdateJson>(url)
                .let {
                    if (it.isSuccess) {
                        it.getOrThrow()
                    } else {
                        Timber.e(it.exceptionOrNull(), "updateJson = $url")
                        null
                    }
                }
        }
    }
}