package com.sanmer.mrepo.ui.activity.main

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import com.sanmer.mrepo.ui.activity.setup.SetupActivity
import com.sanmer.mrepo.utils.NotificationUtils
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDataRepository.userData
                    .collect {
                        if (it.isSetup) SetupActivity.start(this@MainActivity)
                    }
            }
        }

        setActivityContent {
            if (!it.isSetup && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationUtils.PermissionState()
            }

            MainScreen()
        }
    }
}