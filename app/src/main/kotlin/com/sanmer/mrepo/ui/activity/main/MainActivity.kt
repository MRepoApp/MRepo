package com.sanmer.mrepo.ui.activity.main

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import com.sanmer.mrepo.app.utils.NotificationUtils
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

        SetupScreen(
            onRoot = {
                userDataRepository.setWorkingMode(WorkingMode.MODE_ROOT)
            },
            onNonRoot = {
                userDataRepository.setWorkingMode(WorkingMode.MODE_NON_ROOT)
            }
        )
    }

    private fun setMain() = setActivityContent {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationUtils.PermissionState()
        }

        MainScreen()
    }
}