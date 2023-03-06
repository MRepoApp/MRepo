package com.sanmer.mrepo.data

import com.sanmer.mrepo.App
import com.sanmer.mrepo.data.json.Modules
import com.sanmer.mrepo.utils.MediaStoreUtils.toFileDir
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CloudManager {
    private val context by lazy { App.context }
    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter<Modules>()

    private const val REPO_PATH = "repositories"
    private fun Long.toDir() = "${REPO_PATH}/${this}.json"

    fun getById(id: Long): Modules? {
        val json = context.filesDir.resolve(id.toDir())

        return if (json.exists()) {
            adapter.fromJson(json.readText())
        } else {
            null
        }
    }

    fun updateById(
        id: Long,
        value: Modules
    ) = context.toFileDir(adapter.toJson(value), id.toDir())

    fun deleteById(id: Long) {
        val json = context.filesDir.resolve(id.toDir())
        json.delete()
    }

    suspend fun getAll() = withContext(Dispatchers.IO) {
        clear()

        mutableListOf<Modules>().apply {
            RepoManger.getAll().forEach { repo ->
                if (repo.enable) {
                    getById(repo.id)?.let {
                        add(it.copy(repoId = repo.id))
                    }
                }
            }
        }
    }

    private suspend fun clear() {
        val list = RepoManger.getAll().map { it.id.toString() }
        context.filesDir.resolve(REPO_PATH).listFiles { _, name ->
            name.replace(".json", "") !in list
        }?.forEach {
            it.delete()
        }
    }
}