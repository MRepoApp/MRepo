package com.sanmer.mrepo.stub

import com.sanmer.mrepo.model.online.ModulesJson
import com.sanmer.mrepo.network.NetworkUtils
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET

interface IRepoManager {

    @get:GET("json/modules.json")
    val modules: Call<ModulesJson>

    companion object {
        fun build(repoUrl: String): IRepoManager {
            return NetworkUtils.createRetrofit()
                .baseUrl(repoUrl)
                .build()
                .create()
        }
    }
}