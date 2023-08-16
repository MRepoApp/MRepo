package com.sanmer.mrepo.model.state

import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule

data class OnlineState(
    val installed: Boolean,
    val updatable: Boolean,
    val hasLicense: Boolean
) {
    val hasLabel get() = installed or hasLicense

    companion object {
        fun OnlineModule.createState(
            local: LocalModule?
        ): OnlineState {
            val installed = local != null
            val updatable = if (installed) {
                local!!.versionCode < versionCode
            } else {
                false
            }

            return OnlineState(
                installed = installed,
                updatable = updatable,
                hasLicense = track.license.isNotBlank()
            )
        }
    }
}
