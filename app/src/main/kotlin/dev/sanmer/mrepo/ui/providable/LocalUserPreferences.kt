package dev.sanmer.mrepo.ui.providable

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sanmer.mrepo.datastore.UserPreferencesCompat

val LocalUserPreferences = staticCompositionLocalOf { UserPreferencesCompat.default() }
