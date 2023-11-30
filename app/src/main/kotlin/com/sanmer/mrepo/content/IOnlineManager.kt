package com.sanmer.mrepo.content

import com.sanmer.mrepo.model.online.ModulesJson
import com.sanmer.mrepo.network.NetworkUtils
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET

interface IOnlineManager {
    @GET("json/modules.json")
    fun getModules(): Call<ModulesJson>

    companion object {
        fun build(repoUrl: String): IOnlineManager {
            return NetworkUtils.createRetrofit()
                .baseUrl(repoUrl)
                .build()
                .create()
        }
    }
}