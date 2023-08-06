package com.sanmer.mrepo.provider

import com.sanmer.mrepo.api.local.ModulesLocalApi
import com.sanmer.mrepo.app.event.Event
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.flow.StateFlow

interface SuProvider {
    val state: StateFlow<Event>

    val context: String

    fun getFileSystemManager(): FileSystemManager

    fun getModulesApi(): ModulesLocalApi
}