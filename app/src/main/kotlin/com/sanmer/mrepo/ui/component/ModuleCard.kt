package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R

@Composable
fun ModuleCard(
    name: String,
    version: String,
    author: String,
    description: String,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    decoration: TextDecoration = TextDecoration.None,
    progress: Float = 0f,
    switch: @Composable (() -> Unit?)? = null,
    indicator: @Composable (() -> Unit?)? = null,
    message: @Composable (BoxScope.() -> Unit)? = null,
    buttons: @Composable RowScope.() -> Unit,
)  = Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    shape = RoundedCornerShape(20.dp)
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .alpha(alpha = alpha)
                        .weight(1f)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall
                            .copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        textDecoration = decoration,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(id = R.string.module_version_author,
                            version, author),
                        style = MaterialTheme.typography.bodySmall,
                        textDecoration = decoration
                    )
                }

                switch?.invoke()
            }

            Text(
                modifier = Modifier
                    .alpha(alpha = alpha)
                    .padding(horizontal = 16.dp),
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textDecoration = decoration
            )

            Box(
                modifier = Modifier.padding(top = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.background
                )
                if (progress != 0f) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .height(3.5.dp)
                            .fillMaxWidth(),
                        trackColor = MaterialTheme.colorScheme.background,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(start = 16.dp,end = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (message != null) {
                    ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                        Box(
                            modifier = Modifier.weight(1f),
                            content = message
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                buttons()
            }
        }

        indicator?.invoke()
    }
}

@Composable
fun stateIndicator(
    @DrawableRes id: Int,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) = @Composable {
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(id = id),
        contentDescription = null,
        alpha = 0.05f,
        colorFilter = ColorFilter.tint(color)
    )
}
