package com.sanmer.mrepo.model.local

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.utils.ModuleUtils.getVersionDisplay

data class LocalModule(
    var id: String = "unknown",
    var name: String = id,
    var version: String = id,
    var versionCode: Int = -1,
    var author: String = id,
    var description: String = id
) {
    var state by mutableStateOf(State.DISABLE)

    val versionDisplay get() = getVersionDisplay(version, versionCode)

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is LocalModule -> id == other.id
            else -> false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun example() = LocalModule(
            id = "example",
            name = "Example",
            version = "2022.08.16",
            versionCode = 1703,
            author = "Sanmer",
            description = "This is an example!"
        )
    }
}