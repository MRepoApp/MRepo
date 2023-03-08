package com.sanmer.mrepo.ui.activity.install

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.app.isLoading
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.viewmodel.InstallViewModel
import timber.log.Timber
import java.io.File

class InstallActivity : ComponentActivity() {
    private val viewModel by viewModels<InstallViewModel>()

    init {
        Timber.d("InstallActivity init")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        /**
         * Use livedata instead of sate(crashed by "readError") to
         * safety started by other apps.
         * */
        Status.Provider.value.observe(this) {
            if (it.isLoading) {
                viewModel.send("SuProvider init")
                SuProvider.init(this)
            }
            if (it.isSucceeded && Status.Env.isLoading) {
                viewModel.send("EnvProvider init")
                EnvProvider.init()
            }
        }

        Status.Env.value.observe(this) {
            if (it.isSucceeded) {
                val uri = intent.data
                if (uri != null) {
                    viewModel.install(this, uri)
                } else {
                    viewModel.state.setFailed("The uri is null!")
                }
            }
        }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalViewModelStoreOwner provides this
                    ) {
                        InstallScreen()
                    }
                }
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