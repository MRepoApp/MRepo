package com.sanmer.mrepo.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.sanmer.mrepo.app.utils.MediaStoreUtils
import com.sanmer.mrepo.datastore.isDarkMode
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.provider.ProviderCompat
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.utils.extensions.tmpDir
import com.sanmer.mrepo.viewmodel.InstallViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class InstallActivity : ComponentActivity() {
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository
    private val viewModule: InstallViewModel by viewModels()

    private var module: LocalModule? by mutableStateOf(null)
    private var zipPath: String? =null
    private var isReal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("InstallActivity onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (intent.data == null) {
            finish()
        }

        setContent {
            val userPreferences by userPreferencesRepository.data
                .collectAsStateWithLifecycle(initialValue = null)

            if (userPreferences == null) {
                return@setContent
            }

            LaunchedEffect(userPreferences) {
                ProviderCompat.init(userPreferences!!.workingMode)
            }

            LaunchedEffect(ProviderCompat.isAlive) {
                if (ProviderCompat.isAlive) {
                    initModule(intent)
                }
            }

            LaunchedEffect(module) {
                if (module != null) {
                    viewModule.install(
                        zipPath = zipPath!!,
                        isReal = isReal
                    )
                }
            }

            CompositionLocalProvider(
                LocalUserPreferences provides userPreferences!!,
                LocalViewModelStoreOwner provides this
            ) {
                AppTheme(
                    darkMode = userPreferences!!.isDarkMode(),
                    themeColor = userPreferences!!.themeColor
                ) {
                    InstallScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        Timber.d("InstallActivity onDestroy")
        tmpDir.deleteRecursively()
        super.onDestroy()
    }

    private fun initModule(intent: Intent) = lifecycleScope.launch {
        val zipUri = checkNotNull(intent.data)

        withContext(Dispatchers.IO) {
            zipPath = runCatching {
                MediaStoreUtils.getAbsolutePathForUri(
                    context = this@InstallActivity,
                    uri = zipUri
                )
            }.getOrNull()

            module = ProviderCompat.moduleManager
                .getModuleInfo(zipPath)

            Timber.d("module = $module")

            if (module != null) {
                isReal = true
            } else {
                val tmpFile = tmpDir.resolve("tmp.zip")
                contentResolver.openInputStream(zipUri)?.use { input ->
                    tmpFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                zipPath = tmpFile.path
                isReal = false

                module = ProviderCompat.moduleManager
                    .getModuleInfo(zipPath)

                Timber.d("module = $module")

                if (module == null) {
                    finish()
                }
            }
        }
    }

    companion object {
        fun start(context: Context, uri: Uri) {
            val intent = Intent(context, InstallActivity::class.java)
                .apply {
                    data = uri
                }

            context.startActivity(intent)
        }
    }
}