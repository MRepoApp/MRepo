package com.sanmer.mrepo.model.state

import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule

data class OnlineState(
    val installed: Boolean,
    val updatable: Boolean,
    val hasLicense: Boolean,
    val lastUpdated: Float
) {
    @Suppress("FloatingPointLiteralPrecision")
    companion object {
        fun OnlineModule.createState(
            local: LocalModule?,
            hasUpdatableTag: Boolean,
        ): OnlineState {
            val installed = local != null && local.id == id
            val updatable = if (installed && hasUpdatableTag) {
                local!!.versionCode < versionCode
            } else {
                false
            }

            return OnlineState(
                installed = installed,
                updatable = updatable,
                hasLicense = track.license.isNotBlank(),
                lastUpdated = versions.firstOrNull()?.timestamp ?: 1473339588.0f
            )
        }

        fun example() = OnlineState(
            installed = true,
            updatable = false,
            hasLicense = true,
            lastUpdated = 1660640580.0f
        )
    }
}
