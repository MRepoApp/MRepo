package com.sanmer.mrepo.provider.app

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.json.AppUpdate
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET

interface AppService {
    @GET("stable.json")
    fun getStable(): Call<AppUpdate>

    companion object {
        fun create(): AppService {
            return Retrofit.Builder()
                .baseUrl(Const.UPDATE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create()
        }
    }
}