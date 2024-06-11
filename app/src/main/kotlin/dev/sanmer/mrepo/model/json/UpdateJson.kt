package dev.sanmer.mrepo.model.json

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dev.sanmer.mrepo.compat.NetworkCompat
import dev.sanmer.mrepo.model.online.VersionItem

@JsonClass(generateAdapter = true)
data class UpdateJson(
    val version: String,
    val versionCode: Int,
    val zipUrl: String,
    val changelog: String
) {
    fun toItemOrNull(timestamp: Float): VersionItem? {
        if (!NetworkCompat.isUrl(zipUrl)) return null

        val changelog = when {
            !NetworkCompat.isUrl(changelog) -> ""
            NetworkCompat.isBlobUrl(changelog) -> ""
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
        suspend fun load(url: String): VersionItem? {
            if (!NetworkCompat.isUrl(url)) return null

            val result = NetworkCompat.request(url) { body, headers ->
                val adapter = Moshi.Builder()
                    .build()
                    .adapter<UpdateJson>()

                adapter.fromJson(body.string()) to headers
            }

            return when {
                result.isSuccess -> {
                    val (json, headers) = result.getOrThrow()
                    json?.let {
                        val lastModified = headers.getInstant("Last-Modified")?.toEpochMilli()
                        val timestamp = (lastModified ?: System.currentTimeMillis()) / 1000f
                        json.toItemOrNull(timestamp)
                    }
                }
                else -> null
            }
        }
    }
}