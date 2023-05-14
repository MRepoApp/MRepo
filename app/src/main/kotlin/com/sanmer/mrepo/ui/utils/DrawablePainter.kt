package com.sanmer.mrepo.ui.utils

import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun rememberDrawablePainter(
    @DrawableRes id: Int
): Painter {
    val drawable = AppCompatResources.getDrawable(LocalContext.current, id)
    return rememberDrawablePainter(drawable = drawable)
}