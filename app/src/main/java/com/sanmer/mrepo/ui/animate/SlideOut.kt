package com.sanmer.mrepo.ui.animate

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset

object SlideOut {
    val topToBottom = slideOut(
        targetOffset = { IntOffset(0,  it.height / 2) },
        animationSpec = tween(200)
    )

    val bottomToTop = slideOut(
        targetOffset = { IntOffset(0,  - it.height / 2) },
        animationSpec = tween(200)
    )

    val rightToLeft = slideOut(
        targetOffset = { IntOffset(- it.width / 2,  0) },
        animationSpec = tween(200)
    )

    val leftToRight = slideOut(
        targetOffset = { IntOffset(it.width / 2,  0) },
        animationSpec = tween(200)
    )
}