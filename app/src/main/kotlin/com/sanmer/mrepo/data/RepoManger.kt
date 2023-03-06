package com.sanmer.mrepo.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.database.RepoDatabase
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.provider.EnvProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object RepoManger {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: RepoDatabase
    private val repoDao get() = db.repoDao()

    var all by mutableStateOf(0)
        private set

    var enabled by mutableStateOf(0)
        private set

    fun init(context: Context) {
        db = RepoDatabase.getDatabase(context)
        coroutineScope.launch {
            getAll()
        }
    }

    suspend fun getAll() = withContext(Dispatchers.IO) {
        repoDao.getAll().apply {
            if (isEmpty() && EnvProvider.isSetup) {
                insert(Repo(url = Const.MY_REPO_URL))
            }

            all = size
            enabled = filter { it.enable }.size
        }
    }

    suspend fun getById(id: Long) = withContext(Dispatchers.IO) {
        repoDao.getById(id)
    }

    suspend fun insert(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.insert(value)
    }

    suspend fun update(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.update(value)
    }

    suspend fun delete(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.delete(value)
    }
}