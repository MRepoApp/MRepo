package dev.sanmer.mrepo.stub

import dev.sanmer.mrepo.model.online.ModulesJson
import dev.sanmer.mrepo.network.NetworkUtils
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