package com.sanmer.mrepo.ui.activity.main

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import com.sanmer.mrepo.ui.screens.settings.workingmode.WorkingModeItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean = if (isReady) {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        )

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDataRepository.userData
                    .distinctUntilChanged()
                    .collect {
                        if (it.isSetup) {
                            setSetup()
                        } else {
                            setMain()
                        }
                        isReady = true
                    }
            }
        }
    }

    private fun setSetup() = setActivityContent {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationUtils.PermissionState()
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.setup_mode).toUpperCase(Locale.current),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(30.dp))
            WorkingModeItem(
                title = stringResource(id = R.string.setup_root_title),
                desc1 = stringResource(id = R.string.setup_root_desc1),
                desc2 = stringResource(id = R.string.setup_root_desc2),
                onClick = {
                    userDataRepository.setWorkingMode(WorkingMode.MODE_ROOT)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            WorkingModeItem(
                title = stringResource(id = R.string.setup_non_root_title),
                desc1 = stringResource(id = R.string.setup_non_root_desc1),
                desc2 = stringResource(id = R.string.setup_non_root_desc2),
                onClick = {
                    userDataRepository.setWorkingMode(WorkingMode.MODE_NON_ROOT)
                }
            )
        }
    }

    private fun setMain() = setActivityContent {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationUtils.PermissionState()
        }

        MainScreen(it)
    }
}