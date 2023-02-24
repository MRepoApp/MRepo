package com.sanmer.mrepo.ui.screens.modules

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.ui.animate.SlideIn
import com.sanmer.mrepo.ui.animate.SlideOut
import com.sanmer.mrepo.ui.component.LinearProgressIndicator
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.navigation.navigateToHome
import com.sanmer.mrepo.ui.screens.modules.pages.CloudPage
import com.sanmer.mrepo.ui.screens.modules.pages.InstalledPage
import com.sanmer.mrepo.ui.screens.modules.pages.UpdatesPage
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun ModulesScreen(
    viewModel: ModulesViewModel = viewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val state = rememberPagerState(initialPage = Pages.Installed.id)

    BackHandler {
        if (viewModel.isSearch) {
            viewModel.close()
        } else {
            navController.navigateToHome()
        }
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.close() }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ModulesTopBar(
                scrollBehavior = scrollBehavior,
                state = state
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = Constant.isReady,
                enter = fadeIn(animationSpec = tween(400)),
                exit = fadeOut(animationSpec = tween(400))
            ) {
                HorizontalPager(
                    pageCount = pages.size,
                    state = state,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = state,
                        pagerSnapDistance = PagerSnapDistance.atMost(0)
                    ),
                    userScrollEnabled = !viewModel.isSearch
                ) {
                    when (it) {
                        Pages.Cloud.id -> CloudPage(navController = navController)
                        Pages.Installed.id -> InstalledPage()
                        Pages.Updates.id -> UpdatesPage(navController = navController)
                    }
                }
            }

            if (Status.Cloud.isLoading || Status.Local.isLoading) {
                AnimatedVisibility(
                    visible = Constant.isReady,
                    enter = SlideIn.topToBottom,
                    exit = SlideOut.bottomToTop
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                AnimatedVisibility(
                    visible = !Constant.isReady,
                    enter = fadeIn(animationSpec = tween(400)),
                    exit = fadeOut(animationSpec = tween(400))
                ) {
                    Loading()
                }
            }
        }
    }
}

@Composable
private fun ModulesTopBar(
    viewModel: ModulesViewModel = viewModel(),
    scrollBehavior: TopAppBarScrollBehavior,
    state: PagerState
) = if (viewModel.isSearch) {
    ModulesSearchTopBar(
        scrollBehavior = scrollBehavior,
    )
} else {
    ModulesNormalTopBar(
        scrollBehavior = scrollBehavior,
        state = state
    )
}

@Composable
private fun ModulesNormalTopBar(
    viewModel: ModulesViewModel = viewModel(),
    scrollBehavior: TopAppBarScrollBehavior,
    state: PagerState
) = TopAppBar(
    title = {
        TabsItem(state = state)
    },
    actions = {
        IconButton(
            onClick = { viewModel.isSearch = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_normal_outline),
                contentDescription = null
            )
        }

        var expanded by remember { mutableStateOf(false) }
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )

            MenuItem(
                expanded = expanded,
                onClose = { expanded = false }
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun ModulesSearchTopBar(
    viewModel: ModulesViewModel = viewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    navigationIcon = {
        IconButton(
            onClick = {
                viewModel.close()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close_square_outline),
                contentDescription = null
            )
        }
    },
    title = {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequester),
            value = viewModel.key,
            onValueChange = { viewModel.key = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions {
                defaultKeyboardAction(ImeAction.Search)
            },
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search_normal_outline),
                    contentDescription = null
                )
            },
            placeholder = {
                Text(text = stringResource(id = R.string.modules_page_search_placeholder),)
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    },
    actions = {
        var expanded by remember { mutableStateOf(false) }
        IconButton(
            onClick = {
                expanded = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }

        MenuItem(
            expanded = expanded,
            onClose = { expanded = false }
        )
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun Loading() {
    val transition = rememberInfiniteTransition()
    val animateZ by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            RepeatMode.Reverse
        )
    )

    PageIndicator(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.box_outline),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        rotationZ = animateZ
                    }
            )
        },
        text = {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(140.dp)
                    .height(5.dp)
            )
        }
    )
}