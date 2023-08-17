package com.sanmer.mrepo.ui.activity.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanmer.mrepo.datastore.UserPreferencesExt
import com.sanmer.mrepo.datastore.isDarkMode
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.providable.LocalSuState
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var suRepository: SuRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    fun setActivityContent(
        content: @Composable () -> Unit
    ) = setContent {
        val userPreferences by userPreferencesRepository.flow
            .collectAsStateWithLifecycle(UserPreferencesExt.default())

        val suState by suRepository.state
            .collectAsStateWithLifecycle()

        CompositionLocalProvider(
            LocalUserPreferences provides userPreferences,
            LocalSuState provides suState
        ) {
            AppTheme(
                darkMode = userPreferences.isDarkMode(),
                themeColor = userPreferences.themeColor,
                content = content
            )
        }
    }
}