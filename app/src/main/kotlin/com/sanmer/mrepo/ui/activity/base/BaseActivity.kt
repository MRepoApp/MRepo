package com.sanmer.mrepo.ui.activity.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.datastore.isDarkMode
import com.sanmer.mrepo.repository.UserDataRepository
import com.sanmer.mrepo.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : ComponentActivity() {
    @Inject
    lateinit var userDataRepository: UserDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    fun setActivityContent(
        content: @Composable (UserData) -> Unit
    ) = setContent {
        val userData by userDataRepository.userData.collectAsStateWithLifecycle(UserData.default())

        AppTheme(
            darkMode = userData.isDarkMode(),
            themeColor = userData.themeColor
        ) {
            content(userData)
        }
    }
}