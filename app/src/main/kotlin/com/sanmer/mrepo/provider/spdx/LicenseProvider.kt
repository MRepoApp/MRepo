package com.sanmer.mrepo.provider.spdx

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LicenseProvider {
    private val api by lazy { SpdxService.create() }

    suspend fun getLicense(id: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.getLicense(id).execute()

            if (response.isSuccessful) {
                val data = response.body()
                return@withContext if (data != null) {
                    Result.success(data)
                }else {
                    Result.failure(NullPointerException("The data is null!"))
                }
            } else {
                return@withContext Result.failure(RuntimeException("The specified key does not exist!"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}