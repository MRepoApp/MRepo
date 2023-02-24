package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PageIndicator(
    icon: @Composable ColumnScope.() -> Unit,
    text: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(20.dp))
        text()
    }
}

@Composable
fun PageIndicator(
    @DrawableRes icon: Int,
    text: String,
    modifier: Modifier = Modifier,
) = PageIndicator(
    modifier = modifier,
    icon = {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline.copy(0.2f),
            modifier = Modifier
                .size(80.dp)
        )
    },
    text = {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.outline.copy(0.5f),
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp),
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
        )
    }
)

@Composable
fun PageIndicator(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    modifier: Modifier = Modifier
) = PageIndicator(
    modifier = modifier,
    icon = icon,
    text = stringResource(id = text)
)