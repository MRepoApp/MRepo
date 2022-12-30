package com.sanmer.mrepo.api

import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.data.json.Modules
import retrofit2.Call

object RepoApi {
    fun getModules(): Call<Modules> {
        return when(Config.REPO_TAG) {
            Config.REPO_GITHUB_TAG -> {
                GithubService.create().getModules()
            }
            Config.REPO_URL_TAG -> {
                CustomService.let {
                    it.create().getModules(it.json)
                }
            }
            else -> {
                GithubService.create().getModules()
            }
        }
    }
}