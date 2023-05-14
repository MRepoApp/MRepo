package com.sanmer.mrepo.repository

import androidx.compose.runtime.toMutableStateList
import com.sanmer.mrepo.database.dao.ModuleDao
import com.sanmer.mrepo.database.dao.RepoDao
import com.sanmer.mrepo.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.toEntity
import com.sanmer.mrepo.database.entity.toModule
import com.sanmer.mrepo.di.ApplicationScope
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.utils.expansion.merge
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val moduleDao: ModuleDao,
    private val repoDao: RepoDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    private val _online = mutableListOf<OnlineModule>()
    private val _local = mutableListOf<LocalModule>()
    val online get() = _online.toMutableStateList()
    val local get() = _local.toMutableStateList()

    init {
        getLocalAllAsFlow()
            .distinctUntilChanged()
            .onEach { list ->
                if (list.isEmpty()) return@onEach

                _local.clear()
                _local.addAll(list)
                Timber.d("update local list")
            }.launchIn(applicationScope)

        getOnlineAllAsFlow()
            .distinctUntilChanged()
            .onEach { list ->
                if (list.isEmpty()) return@onEach

                _online.clear()
                _online.addAll(list)
                Timber.d("update online list")

            }.launchIn(applicationScope)
    }

    fun getLocalAllAsFlow() = moduleDao.getLocalAllAsFlow().map { list ->
        list.map { it.toModule() }
    }

    suspend fun insertLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        moduleDao.insertLocal(value.toEntity())
    }

    suspend fun insertLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        moduleDao.deleteLocalAll()
        moduleDao.insertLocal(list.map { it.toEntity() })
    }

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

    fun getOnlineAllAsFlow() = repoDao.getRepoWithModuleAsFlow().map { list ->
        list.filter { it.repo.enable }
            .map { it.modules }
            .merge()
            .toModuleList()
    }

    suspend fun insertOnline(list: List<OnlineModuleEntity>) = withContext(Dispatchers.IO) {
        repoDao.insertModule(list)
    }

    suspend fun deleteOnlineByUrl(repoUrl: String) = withContext(Dispatchers.IO) {
        repoDao.deleteModuleByUrl(repoUrl)
    }

    private suspend fun List<OnlineModuleEntity>.toModuleList() = withContext(Dispatchers.Default) {
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