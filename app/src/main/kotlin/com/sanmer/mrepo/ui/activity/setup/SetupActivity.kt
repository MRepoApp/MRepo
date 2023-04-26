package com.sanmer.mrepo.ui.activity.setup

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import com.sanmer.mrepo.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDataRepository.userData
                    .collect {
                        if (!it.isSetup) finish()
                    }
            }
        }

        setActivityContent {
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
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SetupActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            context.startActivity(intent)
        }
    }
}