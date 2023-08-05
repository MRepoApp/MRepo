package com.sanmer.mrepo.repository

import androidx.compose.runtime.toMutableStateList
import com.sanmer.mrepo.database.dao.LocalDao
import com.sanmer.mrepo.database.dao.OnlineDao
import com.sanmer.mrepo.database.dao.RepoDao
import com.sanmer.mrepo.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.toEntity
import com.sanmer.mrepo.database.entity.toModule
import com.sanmer.mrepo.di.ApplicationScope
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.utils.extensions.merge
import com.sanmer.mrepo.utils.extensions.update
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
    private val repoDao: RepoDao,
    private val onlineDao: OnlineDao,
    private val localDao: LocalDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    private var _online = listOf<OnlineModule>()
    private var _local = listOf<LocalModule>()
    val online get() = _online.toMutableStateList()
    val local get() = _local.toMutableStateList()

    init {
        getLocalAllAsFlow()
            .distinctUntilChanged()
            .onEach { list ->
                if (list.isEmpty()) return@onEach

                _local = list
                Timber.d("update local list")
            }.launchIn(applicationScope)

        getOnlineAllAsFlow()
            .distinctUntilChanged()
            .onEach { list ->
                if (list.isEmpty()) return@onEach

                _online = list
                Timber.d("update online list")

            }.launchIn(applicationScope)
    }

    private fun getLocalAllAsFlow() = localDao.getAllAsFlow().map { list ->
        list.map { it.toModule() }
    }

    suspend fun insertLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        localDao.insert(value.toEntity())
    }

    suspend fun insertLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        localDao.insert(list.map { it.toEntity() })
    }

    suspend fun deleteLocalAll() = withContext(Dispatchers.IO) {
        localDao.deleteAll()
    }

    fun getRepoAllAsFlow() = repoDao.getAllAsFlow()

    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        repoDao.getAll()
    }

    suspend fun getRepoByUrl(url: String) = withContext(Dispatchers.IO) {
        repoDao.getByUrl(url)
    }

    suspend fun insertRepo(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.insert(value)
    }

    suspend fun updateRepo(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.update(value)
    }

    suspend fun deleteRepo(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.delete(value)
    }

    private fun getOnlineAllAsFlow() = repoDao.getWithModuleAsFlow().map { list ->
        list.filter { it.repo.enable && it.repo.isCompatible() }
            .map { it.modules }
            .merge()
            .toModuleList()
    }

    suspend fun insertOnline(list: List<OnlineModuleEntity>) = withContext(Dispatchers.IO) {
        onlineDao.insert(list)
    }

    suspend fun deleteOnlineByUrl(repoUrl: String) = withContext(Dispatchers.IO) {
        onlineDao.deleteByUrl(repoUrl)
    }

    private suspend fun List<OnlineModuleEntity>.toModuleList() = withContext(Dispatchers.Default) {
        val values = map { it.toModule() }
        val selector: (String, String) -> OnlineModule = { i, r ->
            values.first { it.id == i && it.repoUrl == r }
        }

        return@withContext mutableListOf<OnlineModule>().apply {
            values.forEach { module ->
                if (contains(module)) {
                    val old = first { it == module }
                    old.repoUrls.update(module.repoUrl)
                    old.repoUrls.sortByDescending { repoUrl ->
                        selector(old.id, repoUrl).versionCode
                    }

                    val new = selector(old.id, old.repoUrl)
                    update(new.copy(repoUrls = old.repoUrls))
                } else {
                    add(module)
                }
            }
        }
    }
}