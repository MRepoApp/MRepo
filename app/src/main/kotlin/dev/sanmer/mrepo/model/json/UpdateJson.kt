package dev.sanmer.mrepo.model.json

import dev.sanmer.mrepo.compat.NetworkCompat
import dev.sanmer.mrepo.model.online.VersionItem
import dev.sanmer.mrepo.utils.StrUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream

@Serializable
data class UpdateJson(
    val version: String,
    val versionCode: Int,
    val zipUrl: String,
    val changelog: String
) {
    fun toItemOrNull(timestamp: Long): VersionItem? {
        if (!NetworkCompat.isUrl(zipUrl)) return null

        val changelog = when {
            !NetworkCompat.isUrl(changelog) -> ""
            NetworkCompat.isBlobUrl(changelog) -> ""
            else -> changelog
        }

        return VersionItem(
            timestamp = timestamp,
            version = StrUtil.getVersionDisplay(version, versionCode),
            versionCode = versionCode,
            zipUrl = zipUrl,
            changelog = changelog
        )
    }

    companion object {
        suspend fun load(url: String): VersionItem? {
            if (!NetworkCompat.isUrl(url)) return null

            val result = NetworkCompat.request(url) { body, headers ->
                val json = NetworkCompat.defaultJson.decodeFromStream<UpdateJson>(body.byteStream())
                val lastModified = headers.getInstant("Last-Modified")?.toEpochMilli()

                json.toItemOrNull(lastModified ?: System.currentTimeMillis())
            }

            return result.getOrNull()
        }
    }
}