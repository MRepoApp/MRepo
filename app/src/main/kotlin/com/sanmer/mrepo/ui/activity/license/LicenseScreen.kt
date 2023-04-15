package com.sanmer.mrepo.ui.activity.license

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.app.State
import com.sanmer.mrepo.model.json.License
import com.sanmer.mrepo.ui.component.CircularProgressIndicator
import com.sanmer.mrepo.ui.component.NormalChip
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.utils.MarkdownText
import com.sanmer.mrepo.ui.utils.NavigateUpTopBar
import com.sanmer.mrepo.utils.HttpUtils
import timber.log.Timber

@Composable
fun LicenseScreen(licenseId: String) {
    var license: License? by remember { mutableStateOf(null) }
    var message: String? by remember { mutableStateOf(null) }
    val state = object : State(initial = Event.LOADING) {
        override fun setSucceeded(value: Any?) {
            super.setSucceeded(value)
            license = value as License
        }

        override fun setFailed(value: Any?) {
            super.setFailed(value)
            val error = value as Throwable
            message = error.message
            Timber.e(error.message)
        }
    }

    LaunchedEffect(licenseId) {
        HttpUtils.requestJson<License>(Const.SPDX_URL + "${licenseId}.json").onSuccess {
            state.setSucceeded(it)
        }.onFailure {
            state.setFailed(it)
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LicenseTopBar(scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (state.event) {
                Event.LOADING -> {
                    Loading()
                }
                Event.SUCCEEDED -> {
                    ViewLicense(license = license!!)
                }
                Event.FAILED -> {
                    Failed(message = message)
                }
                Event.NON -> {
                    Failed(message = message)
                }
            }
        }
    }
}

@Composable
private fun LicenseTopBar(
    scrollBehavior: TopAppBarScrollBehavior
) = NavigateUpTopBar(
    title = R.string.license_title,
    scrollBehavior = scrollBehavior
)

@Composable
private fun ViewLicense(
    license: License
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(all = 20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = license.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                if (license.seeAlso.isNotEmpty()) {
                    val text = license.seeAlso
                        .joinToString(separator = "\n") {
                        " - [${it}](${it})"
                    }

                    MarkdownText(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (license.hasLabel()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        if (license.isFsfLibre) {
                            NormalChip(
                                painter = painterResource(id = R.drawable.people_bold),
                                text = stringResource(id = R.string.license_fsf_libre)
                            )
                        }

                        if (license.isOsiApproved) {
                            NormalChip(
                                painter = painterResource(id = R.drawable.osi),
                                text = stringResource(id = R.string.license_osi_approved)
                            )
                        }
                    }
                }
            }
        }

        Text(
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 5.dp)
                .fillMaxWidth(),
            text = license.licenseText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun Loading() = PageIndicator(
    icon = {
        CircularProgressIndicator(
            modifier = Modifier
                .size(50.dp),
            strokeWidth = 5.dp
        )
    },
    text = {
        Text(
            text = stringResource(id = R.string.message_loading),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
)

@Composable
private fun Failed(
    message: String?
) = PageIndicator(
    icon = R.drawable.danger_outline,
    text = message ?: stringResource(id = R.string.unknown_error)
)