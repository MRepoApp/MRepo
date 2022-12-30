package com.sanmer.mrepo.ui.animate

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.ui.unit.IntOffset

object SlideIn {
    val bottomToTop = slideIn(
        initialOffset = { IntOffset(0,  it.height / 2) },
        animationSpec = tween(600)
    )

    val topToBottom = slideIn(
        initialOffset = { IntOffset(0,  - it.height / 2) },
        animationSpec = tween(600)
    )

    val leftToRight = slideIn(
        initialOffset = { IntOffset(- it.width / 2, 0) },
        animationSpec = tween(600)
    )

    val rightToLeft = slideIn(
        initialOffset = { IntOffset(it.width / 2, 0) },
        animationSpec = tween(600)
    )
}