package com.sanmer.mrepo.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.database.RepoDatabase
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Repository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: RepoDatabase
    private val repoDao get() = db.repoDao()

    val repo = mutableStateListOf<Repo>()
    val repoSize get() = repo.size
    val enabledRepoSize get() = repo.filter { it.enable }.size

    fun init(context: Context) {
        db = RepoDatabase.getDatabase(context)
        coroutineScope.launch(Dispatchers.IO) {
            getAll()
        }
    }

    fun getById(id: Long) = repo.find { it.id == id }

    suspend fun getAll() = withContext(Dispatchers.IO) {
        if (repo.isNotEmpty()) {
            repo.clear()
        }

        repoDao.getAll().apply {
            if (isNotEmpty()) {
                repo.addAll(this)
            } else {
                if (EnvProvider.isSetup) {
                    insert(Repo(url = Const.MY_REPO_URL))
                }
            }
        }
    }

    suspend fun insert(value: Repo) = withContext(Dispatchers.IO) {
        repo.add(value)
        repoDao.insert(value)
    }

    suspend fun update(value: Repo) = withContext(Dispatchers.IO) {
        repo.update(value)
        repoDao.update(value)
    }

    suspend fun delete(value: Repo) = withContext(Dispatchers.IO) {
        repo.remove(value)
        repoDao.delete(value)
    }
}