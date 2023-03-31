package com.sanmer.mrepo.data

import android.content.Context
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.database.RepoDatabase
import com.sanmer.mrepo.data.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.database.entity.toModule
import com.sanmer.mrepo.data.module.OnlineModule
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.utils.expansion.merge
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

object RepoManger {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: RepoDatabase
    private val repoDao get() = db.repoDao()

    val all get() = repoDao.getRepoCount()
    val enable get() = repoDao.getEnableCount()

    fun init(context: Context) {
        db = RepoDatabase.getDatabase(context)
        coroutineScope.launch {
            val list = getRepoAll()
            if (list.isEmpty() && EnvProvider.isSetup) {
                Timber.d("add default repository")
                insertRepo(Repo(url = Const.MY_REPO_URL))
            }
        }
    }

    fun getRepoWithModuleAsFlow() = repoDao.getRepoWithModuleAsFlow()

    fun getRepoAllAsFlow() = repoDao.getRepoAllAsFlow()

    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        repoDao.getRepoAll()
    }

    suspend fun getRepoByUrl(url: String) = withContext(Dispatchers.IO) {
        repoDao.getRepoByUrl(url)
    }

    suspend fun insertRepo(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.insertRepo(value)
    }

    suspend fun updateRepo(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.updateRepo(value)
    }

    suspend fun deleteRepo(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.deleteRepo(value)
    }

    suspend fun getModuleAll() = withContext(Dispatchers.IO) {
        val list = repoDao.getRepoWithModule()
            .filter { it.repo.enable }
            .map { it.modules }
            .merge()

        return@withContext list.toModuleList()
    }

    suspend fun insertModule(list: List<OnlineModuleEntity>) = withContext(Dispatchers.IO) {
        repoDao.insertModule(list)
    }

    suspend fun deleteModules(repoUrl: String) = withContext(Dispatchers.IO) {
        repoDao.deleteModule(repoUrl)
    }

    suspend fun List<OnlineModuleEntity>.toModuleList() = withContext(Dispatchers.Default) {
        val list = mutableListOf<OnlineModule>()

        forEach { item ->
            val module = item.toModule()
            if (module !in list) {
                list.add(module)
            } else {
                val old = list.first { it == module }

                old.repoUrls.update(item.repoUrl)
                old.repoUrls.sortByDescending {
                    first {
                        it.id == item.id && it.repoUrl == item.repoUrl
                    }.versionCode
                }

                val new = first {
                    it.id == item.id && it.repoUrl == old.repoUrl
                }.toModule()

                list.update(new.copy(repoUrls = old.repoUrls))
            }
        }

        return@withContext list.toList()
    }
}