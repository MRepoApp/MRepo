package com.sanmer.mrepo.api.online

import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.model.json.ModulesJson
import com.sanmer.mrepo.model.json.UpdateJson
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber

interface ModulesRepoApi {
    @GET("json/modules.json")
    fun getModules(): Call<ModulesJson>

    @GET("modules/{id}/update.json")
    fun getUpdate(@Path("id") id: String): Call<UpdateJson>

    companion object {
        fun build(repoUrl: String): ModulesRepoApi {
            if (BuildConfig.DEBUG) Timber.d("repoUrl: $repoUrl")

            return Retrofit.Builder()
                .baseUrl(repoUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create()
        }
    }
}