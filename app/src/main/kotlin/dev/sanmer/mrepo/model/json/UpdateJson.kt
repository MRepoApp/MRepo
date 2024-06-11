package dev.sanmer.mrepo.model.json

import dev.sanmer.mrepo.model.online.VersionItem
import dev.sanmer.mrepo.network.NetworkUtils
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter

@JsonClass(generateAdapter = true)
data class UpdateJson(
    val version: String,
    val versionCode: Int,
    val zipUrl: String,
    val changelog: String
) {
    fun toItemOrNull(timestamp: Float): VersionItem? {
        if (!NetworkUtils.isUrl(zipUrl)) return null

        val changelog = when {
            !NetworkUtils.isUrl(changelog) -> ""
            NetworkUtils.isBlobUrl(changelog) -> ""
            else -> changelog
        }

        return VersionItem(
            timestamp = timestamp,
            version = version,
            versionCode = versionCode,
            zipUrl = zipUrl,
            changelog = changelog
        )
    }

    companion object {
        suspend fun loadToVersionItem(url: String): VersionItem? {
            if (!NetworkUtils.isUrl(url)) return null

            val result = NetworkUtils.request(url) { body, headers ->
                val adapter = Moshi.Builder()
                    .build()
                    .adapter<UpdateJson>()

                adapter.fromJson(body.string()) to headers
            }

            if (result.isSuccess) {
                val (json, headers) = result.getOrThrow()
                if (json != null) {
                    val t = headers.getInstant("Last-Modified")?.toEpochMilli()
                    val timestamp = (t ?: System.currentTimeMillis()) / 1000f

                    return json.toItemOrNull(timestamp)
                }
            }

            return null
        }
    }
}