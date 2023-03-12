package com.sanmer.mrepo.provider.spdx

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.json.License
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

interface SpdxService {
    @GET("{id}.json")
    fun getLicense(@Path("id") id: String): Call<License>

    companion object {
        fun create(): SpdxService {
            return Retrofit.Builder()
                .baseUrl(Const.SPDX_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create()
        }
    }
}