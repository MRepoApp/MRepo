package com.sanmer.mrepo.provider

import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.content.ILocalManager
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.flow.StateFlow

interface SuProvider {
    val state: StateFlow<Event>
    val isInitialized: Boolean

    val fs: FileSystemManager
    val lm: ILocalManager
}