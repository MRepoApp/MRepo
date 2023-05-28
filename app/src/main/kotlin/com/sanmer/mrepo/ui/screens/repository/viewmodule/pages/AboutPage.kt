package com.sanmer.mrepo.ui.screens.repository.viewmodule.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.component.PageIndicator

@Composable
fun AboutPage() = PageIndicator(
    icon = R.drawable.box_outline,
    text = stringResource(id = R.string.search_empty)
)