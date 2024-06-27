package dev.sanmer.mrepo.ui.providable

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sanmer.mrepo.datastore.model.UserPreferences

val LocalUserPreferences = staticCompositionLocalOf { UserPreferences() }
