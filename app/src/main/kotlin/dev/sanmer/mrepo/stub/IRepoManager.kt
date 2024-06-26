package dev.sanmer.mrepo.stub

import dev.sanmer.mrepo.compat.NetworkCompat
import dev.sanmer.mrepo.model.online.ModulesJson
import retrofit2.Call
import retrofit2.http.GET

interface IRepoManager {
    @get:GET("json/modules.json")
    val modules: Call<ModulesJson>

    companion object {
        fun create(repoUrl: String) = NetworkCompat.createApi<IRepoManager>(repoUrl)
    }
}