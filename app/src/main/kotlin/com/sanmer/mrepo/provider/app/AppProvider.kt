package com.sanmer.mrepo.provider.app

import com.sanmer.mrepo.utils.expansion.runRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppProvider {
    private val api by lazy { AppService.create() }

    suspend fun getStable() = withContext(Dispatchers.IO) {
        return@withContext runRequest {
            api.getStable().execute()
        }
    }
}