package com.sanmer.mrepo.ui.activity.install

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import com.sanmer.mrepo.viewmodel.InstallViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.File

class InstallActivity : BaseActivity() {
    private val viewModel: InstallViewModel by viewModels()

    init {
        Timber.d("InstallActivity init")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cacheDir.resolve("log")
            .walkBottomUp()
            .forEach {
                if (it.name.startsWith("module")) {
                    it.delete()
                }
            }

        viewModel.suState.onEach {
            if (it.isSucceeded) {
                val uri = intent.data
                if (uri != null) {
                    viewModel.install(this, uri)
                } else {
                    viewModel.state.setFailed("The uri is null!")
                }
            }
        }.launchIn(lifecycleScope)

        setActivityContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides this
            ) {
                InstallScreen()
            }
        }
    }

    companion object {
        fun start(context: Context, uri: Uri) {
            val intent = Intent(context, InstallActivity::class.java).apply {
                flags =  Intent.FLAG_ACTIVITY_NEW_TASK
                data = uri
            }
            context.startActivity(intent)
        }

        fun start(context: Context, path: File) = start(context, path.toUri())
    }
}