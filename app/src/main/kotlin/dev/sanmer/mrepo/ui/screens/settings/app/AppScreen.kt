package dev.sanmer.mrepo.ui.screens.settings.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.app.Const
import dev.sanmer.mrepo.compat.BuildCompat
import dev.sanmer.mrepo.datastore.model.WorkingMode.Companion.isRoot
import dev.sanmer.mrepo.ui.component.NavigateUpTopBar
import dev.sanmer.mrepo.ui.component.SettingNormalItem
import dev.sanmer.mrepo.ui.component.SettingSwitchItem
import dev.sanmer.mrepo.ui.component.TextFieldDialog
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.ui.screens.settings.app.items.AppThemeItem
import dev.sanmer.mrepo.utils.extensions.applicationLocale
import dev.sanmer.mrepo.viewmodel.SettingsViewModel
import java.io.File
import java.util.Locale

@Composable
fun AppScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userPreferences = LocalUserPreferences.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            LanguageItem(
                context = context
            )

            AppThemeItem(
                themeColor = userPreferences.themeColor,
                darkMode = userPreferences.darkMode,
                isDarkMode = userPreferences.isDarkMode(),
                onThemeColorChange = viewModel::setThemeColor,
                onDarkModeChange = viewModel::setDarkTheme
            )

            DownloadPathItem(
                downloadPath = userPreferences.downloadPath,
                onChange = viewModel::setDownloadPath
            )

            SettingSwitchItem(
                icon = R.drawable.file_type_zip,
                title = stringResource(id = R.string.settings_delete_zip),
                desc = stringResource(id = R.string.settings_delete_zip_desc),
                checked = userPreferences.deleteZipFile,
                onChange = viewModel::setDeleteZipFile,
                enabled = userPreferences.workingMode.isRoot
            )
        }
    }
}

@SuppressLint("InlinedApi")
@Composable
private fun LanguageItem(
    context: Context
) = SettingNormalItem(
    icon = R.drawable.world,
    title = stringResource(id = R.string.settings_language),
    desc = context.applicationLocale?.localizedDisplayName ?: stringResource(id = R.string.settings_language_system),
    onClick = {
        context.startActivity(
            Intent(
                Settings.ACTION_APP_LOCALE_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
        )
    },
    enabled = BuildCompat.atLeastT
)

@Composable
private fun DownloadPathItem(
    downloadPath: String,
    onChange: (String) -> Unit
) {
    var edit by remember { mutableStateOf(false) }
    if (edit) EditDialog(
        path = downloadPath,
        onClose = { edit = false },
        onConfirm = onChange
    )

    SettingNormalItem(
        icon = R.drawable.files,
        title = stringResource(id = R.string.settings_download_path),
        desc = downloadPath,
        onClick = { edit = true }
    )
}

@Composable
private fun EditDialog(
    path: String,
    onClose: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember {
        mutableStateOf(
            File(path).toRelativeString(Const.PUBLIC_DOWNLOADS)
        )
    }

    TextFieldDialog(
        shape = RoundedCornerShape(20.dp),
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.settings_download_path)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        File(Const.PUBLIC_DOWNLOADS, name.trim()).path
                    )
                    onClose()
                },
            ) {
                Text(text = stringResource(id = R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onClose
            ) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        },
        launchKeyboard = false
    ) {
        OutlinedTextField(
            textStyle = MaterialTheme.typography.bodyLarge,
            value = name,
            onValueChange = { name = it },
            shape = RoundedCornerShape(15.dp),
            label = { Text(text = Const.PUBLIC_DOWNLOADS.absolutePath) },
            singleLine = true
        )
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) = NavigateUpTopBar(
    title = stringResource(id = R.string.settings_app),
    scrollBehavior = scrollBehavior,
    navController = navController
)

private val Locale.localizedDisplayName: String
    get() = getDisplayName(this)
        .replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(this)
            } else {
                it.toString()
            }
        }