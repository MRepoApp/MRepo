package com.sanmer.mrepo.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.event.isFailed
import com.sanmer.mrepo.app.event.isLoading
import com.sanmer.mrepo.app.event.isSucceeded
import com.sanmer.mrepo.model.json.License
import com.sanmer.mrepo.ui.component.Failed
import com.sanmer.mrepo.ui.component.Loading
import com.sanmer.mrepo.ui.component.MarkdownText
import com.sanmer.mrepo.ui.component.NormalChip
import timber.log.Timber

@Composable
fun LicenseContent(
    licenseId: String
) {
    var license: License? by remember { mutableStateOf(null) }
    var message: String? by remember { mutableStateOf(null) }
    val event = rememberJsonDataRequest<License>(
        url = Const.SPDX_URL.format(licenseId),
        onSuccess = { license = it },
        onFailure = {
            message = it.message
            Timber.e(it, "getLicense: $licenseId")
        }
    )

    Box(
        modifier = Modifier
            .animateContentSize(spring(stiffness = Spring.StiffnessLow))
    ) {
        AnimatedVisibility(
            visible = event.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Loading(minHeight = 200.dp)
        }

        AnimatedVisibility(
            visible = event.isSucceeded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ViewLicense(license = license!!)
        }

        AnimatedVisibility(
            visible = event.isFailed,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Failed(message = message, minHeight = 200.dp)
        }
    }
}

@Composable
private fun ViewLicense(
    license: License
) = Column(
    modifier = Modifier
        .padding(top = 18.dp, start = 18.dp, end = 18.dp)
        .fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(18.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    OutlinedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            .padding(bottom = 18.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
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