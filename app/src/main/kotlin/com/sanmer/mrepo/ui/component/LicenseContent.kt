package com.sanmer.mrepo.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Event.Companion.isFailed
import com.sanmer.mrepo.app.Event.Companion.isLoading
import com.sanmer.mrepo.app.Event.Companion.isSucceeded
import com.sanmer.mrepo.model.json.License
import com.sanmer.mrepo.network.compose.requestJson
import timber.log.Timber

@Composable
fun LicenseContent(
    licenseId: String
) {
    var license: License? by remember { mutableStateOf(null) }
    var message: String? by remember { mutableStateOf(null) }
    val event = requestJson<License>(
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
        .padding(horizontal = 18.dp)
        .padding(top = 18.dp)
        .fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(18.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Surface(
        shape = RoundedCornerShape(15.dp),
        tonalElevation = 6.dp,
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline)
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
                MarkdownText(
                    text = license.seeAlso.joinToString(separator = "\n") {
                        " - [${it}](${it})"
                    },
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
            .verticalScroll(rememberScrollState())
            .padding(bottom = 18.dp)
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
        LabelItem(
            painter = painterResource(id = R.drawable.users),
            text = stringResource(id = R.string.license_fsf_libre)
        )
    }

    if (license.isOsiApproved) {
        LabelItem(
            painter = painterResource(id = R.drawable.brand_open_source),
            text = stringResource(id = R.string.license_osi_approved)
        )
    }
}

@Composable
private fun LabelItem(
    painter: Painter,
    text: String,
    containerColor: Color = Color.Transparent,
    shape: Shape = RoundedCornerShape(10.dp)
) = Surface(
    shape = shape,
    color = containerColor,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
) {
    Row(
        modifier = Modifier.padding(all = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium
        )
    }
}