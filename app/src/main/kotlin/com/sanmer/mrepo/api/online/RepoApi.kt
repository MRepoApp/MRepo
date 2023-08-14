package com.sanmer.mrepo.api.online

import com.sanmer.mrepo.model.online.ModulesJson
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET

interface RepoApi {
    @GET("json/modules.json")
    fun getModules(): Call<ModulesJson>

    companion object {
        fun build(repoUrl: String): RepoApi {
            return Retrofit.Builder()
                .baseUrl(repoUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create()
        }
    }
}