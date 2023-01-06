package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.modify.LinearProgressIndicator

@Composable
fun ModuleCard(
    name: String,
    version: String,
    author: String,
    description: String,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    alpha: Float = 1f,
    decoration: TextDecoration = TextDecoration.None,
    switch: @Composable (() -> Unit?)? = null,
    cover: @Composable () -> Unit = {},
    buttons: @Composable RowScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .alpha(alpha = alpha)
                            .fillMaxWidth(if (switch == null) 1f else 0.82f)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            textDecoration = decoration
                        )
                        Text(
                            text = stringResource(
                                id = R.string.module_version_author, version, author
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            textDecoration = decoration
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {
                        switch?.let { it() }
                    }

                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    modifier = Modifier
                        .alpha(alpha = alpha)
                        .padding(horizontal = 16.dp),
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = decoration
                )

                Box(
                    modifier = Modifier
                        .padding(top = 10.dp),
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
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    content = buttons
                )

            }

            cover()
        }
    }
}

@Composable
fun StateIndicator(
    @DrawableRes id: Int,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Image(
        modifier = Modifier
            .fillMaxSize(),
        painter = painterResource(id = id),
        contentDescription = null,
        alpha = 0.05f,
        colorFilter = ColorFilter.tint(color)
    )
}
