package com.sanmer.mrepo.model.json

import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.ModuleUtils.getVersionDisplay
import com.squareup.moshi.JsonClass
import kotlinx.datetime.Clock

@JsonClass(generateAdapter = true)
data class MagiskUpdateJson(
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

    val versionDisplay get() = getVersionDisplay(version, versionCode)

    fun toItemOrNull(): VersionItem? {
        if (!HttpUtils.isUrl(zipUrl)) return null

        val changelog = when {
            !HttpUtils.isUrl(changelog) -> ""
            HttpUtils.isBlobUrl(changelog) -> ""
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
}