package com.sanmer.mrepo.provider.spdx

import com.sanmer.mrepo.utils.expansion.runRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LicenseProvider {
    private val api by lazy { SpdxService.create() }

    suspend fun getLicense(id: String) = withContext(Dispatchers.IO) {
        runRequest {
            api.getLicense(id).execute()
        }.onFailure {
            return@withContext Result.failure(RuntimeException("The specified key does not exist!"))
        }
    }
}