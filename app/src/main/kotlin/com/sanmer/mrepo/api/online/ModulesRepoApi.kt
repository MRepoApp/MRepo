package com.sanmer.mrepo.api.online

import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.model.json.ModuleUpdate
import com.sanmer.mrepo.model.json.Modules
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber

interface ModulesRepoApi {
    @GET("json/modules.json")
    fun getModules(): Call<Modules>

    @GET("modules/{id}/update.json")
    fun getUpdate(@Path("id") id: String): Call<ModuleUpdate>

    companion object {
        fun create(repoUrl: String): ModulesRepoApi {
            if (BuildConfig.DEBUG) Timber.d("RepoService.create: $repoUrl")

            return Retrofit.Builder()
                .baseUrl(repoUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create()
        }
    }
}