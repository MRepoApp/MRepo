package com.sanmer.mrepo.api

import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.json.Modules
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber

interface CustomService {

    @GET("{json}")
    fun getModules(@Path("json") json: String): Call<Modules>

    companion object {
        private var baseUrl: String = "${Const.REPO_URL}json/"
        var json: String = "modules.json"
            private set

        fun create(): CustomService {
            Config.REPO_URL.split("/").toMutableList().apply {
                json = last()
                remove(json)
                baseUrl = joinToString("/", postfix = "/")
            }

            Timber.d("getRepo: $baseUrl")


            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CustomService::class.java)
        }
    }
}