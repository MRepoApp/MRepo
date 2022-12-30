package com.sanmer.mrepo.api

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.json.Modules
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import timber.log.Timber

interface GithubService {

    @GET("json/modules.json")
    fun getModules(): Call<Modules>

    companion object {
        fun create(): GithubService {
            Timber.d("getRepo: ${Const.REPO_URL}")

            return Retrofit.Builder()
                .baseUrl(Const.REPO_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubService::class.java)
        }
    }
}