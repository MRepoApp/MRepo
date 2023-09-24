package com.sanmer.mrepo.ui.providable

import androidx.compose.runtime.staticCompositionLocalOf
import com.sanmer.mrepo.datastore.UserPreferencesExt

val LocalUserPreferences = staticCompositionLocalOf { UserPreferencesExt.default() }
