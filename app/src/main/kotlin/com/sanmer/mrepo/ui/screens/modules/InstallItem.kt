package com.sanmer.mrepo.ui.screens.modules

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.provider.local.InstallUtils
import com.sanmer.mrepo.ui.activity.install.InstallActivity

@Composable
fun InstallItem() {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val intent = Intent(context, InstallActivity::class.java)
        InstallUtils.install(context, uri)
        context.startActivity(intent)
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                launcher.launch("application/zip")
            }
        }
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                enabled = Status.Provider.isSucceeded,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary
                ),
                onClick = {}
            ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cube_scan_outline),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = stringResource(id = R.string.install_from_storage),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}