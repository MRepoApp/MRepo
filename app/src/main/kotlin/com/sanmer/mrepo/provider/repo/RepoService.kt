package com.sanmer.mrepo.provider.repo

import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.data.json.Modules
import com.sanmer.mrepo.data.json.Update
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber

interface RepoService {
    @GET("json/modules.json")
    fun getModules(): Call<Modules>

    @GET("modules/{id}/update.json")
    fun getUpdate(@Path("id") id: String): Call<Update>

    companion object {
        fun create(repoUrl: String): RepoService {
            if (BuildConfig.DEBUG) Timber.d("RepoService.create: $repoUrl")

            return Retrofit.Builder()
                .baseUrl(repoUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(RepoService::class.java)
        }
    }
}