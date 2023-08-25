package com.sanmer.mrepo.ui.activity.install

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.sanmer.mrepo.app.event.isSucceeded
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import com.sanmer.mrepo.ui.providable.LocalSuState
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.utils.extensions.deleteLog
import com.sanmer.mrepo.viewmodel.InstallViewModel
import java.io.File

class InstallActivity : BaseActivity() {
    private val viewModel: InstallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deleteLog("module")

        setActivityContent { isReady ->
            val userPreferences = LocalUserPreferences.current
            val suState = LocalSuState.current

            LaunchedEffect(key1=suState, key2=isReady) {
                if (suState.isSucceeded && isReady) {
                    val uri = intent.data
                    if (uri != null) {
                        viewModel.install(
                            context = this@InstallActivity,
                            path = uri,
                            deleteZipFile = userPreferences.deleteZipFile
                        )
                    } else {
                        viewModel.state.setFailed("Uri is null")
                    }
                }
            }

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