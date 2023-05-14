package com.sanmer.mrepo.ui.activity.license

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.State
import com.sanmer.mrepo.model.json.License
import com.sanmer.mrepo.ui.component.MarkdownText
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.component.NormalChip
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.utils.HttpUtils
import timber.log.Timber

@Composable
fun LicenseScreen(
    licenseId: String
) {
    var license: License? by remember { mutableStateOf(null) }
    var message: String? by remember { mutableStateOf(null) }
    val state = object : State(initial = Event.LOADING) {
        override fun setSucceeded(value: Any?) {
            license = value as License
            super.setSucceeded(value)
        }

        override fun setFailed(value: Any?) {
            message = value.toString()
            super.setFailed(value)
        }
    }

    LaunchedEffect(licenseId) {
        HttpUtils.requestJson<License>(Const.SPDX_URL.format(licenseId)).onSuccess {
            state.setSucceeded(it)
        }.onFailure {
            state.setFailed(it)
            Timber.e(it, "getLicense: $licenseId")
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                Event.NON -> {}
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
) = Column(
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
                LabelsItem(license = license)
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

@Composable
private fun LabelsItem(
    license: License
) = Row(
    modifier = Modifier.fillMaxWidth(),
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
            painter = painterResource(id = R.drawable.ic_osi),
            text = stringResource(id = R.string.license_osi_approved)
        )
    }
}

@Composable
private fun Loading() = PageIndicator(
    icon = {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Round
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