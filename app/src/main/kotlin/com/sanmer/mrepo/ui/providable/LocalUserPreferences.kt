package com.sanmer.mrepo.ui.providable

import androidx.compose.runtime.staticCompositionLocalOf
import com.sanmer.mrepo.datastore.UserPreferencesCompat

val LocalUserPreferences = staticCompositionLocalOf { UserPreferencesCompat.default() }
