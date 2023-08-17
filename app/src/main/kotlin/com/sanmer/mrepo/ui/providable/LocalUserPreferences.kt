package com.sanmer.mrepo.ui.providable

import androidx.compose.runtime.compositionLocalOf
import com.sanmer.mrepo.datastore.UserPreferencesExt

val LocalUserPreferences = compositionLocalOf { UserPreferencesExt.default() }
