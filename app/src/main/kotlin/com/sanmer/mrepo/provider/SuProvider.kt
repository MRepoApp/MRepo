package com.sanmer.mrepo.provider

import com.sanmer.mrepo.api.local.LocalApi
import com.sanmer.mrepo.app.Event
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.flow.StateFlow

interface SuProvider {
    val state: StateFlow<Event>
    val isInitialized: Boolean

    val fs: FileSystemManager
    val api: LocalApi
}