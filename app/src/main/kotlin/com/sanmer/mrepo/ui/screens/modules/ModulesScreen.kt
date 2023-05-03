package com.sanmer.mrepo.ui.screens.modules

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.activity.install.InstallActivity
import com.sanmer.mrepo.ui.animate.slideInBottomToTop
import com.sanmer.mrepo.ui.animate.slideOutTopToBottom
import com.sanmer.mrepo.ui.navigation.navigateToHome
import com.sanmer.mrepo.ui.screens.modules.pages.CloudPage
import com.sanmer.mrepo.ui.screens.modules.pages.InstalledPage
import com.sanmer.mrepo.ui.screens.modules.pages.UpdatablePage
import com.sanmer.mrepo.ui.utils.isScrollingUp
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun ModulesScreen(
    navController: NavController,
    viewModel: ModulesViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pagerState = rememberPagerState(initialPage = Pages.Cloud.id)

    val isScrollingUp by viewModel.getListSate(pagerState.currentPage).isScrollingUp()
    val showFab by remember(isScrollingUp) {
        derivedStateOf {
            isScrollingUp && userData.isRoot && !viewModel.isSearch
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.progress,
        onRefresh = {
            when (pagerState.currentPage) {
                Pages.Cloud.id -> viewModel.getOnlineAll()
                Pages.Installed.id -> viewModel.getLocalAll()
            }
        }
    )

    BackHandler {
        if (viewModel.isSearch) {
            viewModel.closeSearch()
        } else {
            navController.navigateToHome()
        }
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.closeSearch() }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ModulesTopBar(
                pagerState = pagerState,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = fadeIn() + slideInBottomToTop(),
                exit = fadeOut() + slideOutTopToBottom()
            ) {
                InstallFloatingButton()
            }
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            SegmentedButtonsItem(
                state = pagerState,
                userData = userData,
                scrollBehavior = scrollBehavior
            )

            Box(modifier = Modifier
                .pullRefresh(
                    state = pullRefreshState,
                    enabled = !viewModel.isSearch &&
                            pagerState.currentPage != Pages.Updatable.id
                )
            ) {
                ModulesPager(
                    state = pagerState,
                    userData = userData,
                    navController = navController
                )

                PullRefreshIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    refreshing = viewModel.progress,
                    state = pullRefreshState,
                    backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    contentColor = MaterialTheme.colorScheme.primary,
                    scale = true
                )
            }
        }
    }
}

@Composable
private fun ModulesPager(
    state: PagerState,
    userData: UserData,
    navController: NavController,
    viewModel: ModulesViewModel = hiltViewModel()
) = HorizontalPager(
    pageCount = pages.size,
    state = state,
    flingBehavior = PagerDefaults.flingBehavior(
        state = state,
        pagerSnapDistance = PagerSnapDistance.atMost(0)
    ),
    userScrollEnabled = if (viewModel.isSearch) false else userData.isRoot
) {
    when (it) {
        Pages.Cloud.id -> CloudPage(navController)

        Pages.Installed.id -> InstalledPage()

        Pages.Updatable.id -> UpdatablePage(navController)
    }
}

@Composable
private fun ModulesTopBar(
    pagerState: PagerState,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ModulesViewModel = hiltViewModel()
) = if (viewModel.isSearch) {
    ModulesSearchTopBar(
        scrollBehavior = scrollBehavior
    )
} else {
    ModulesNormalTopBar(
        pagerState = pagerState,
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ModulesNormalTopBar(
    pagerState: PagerState,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ModulesViewModel = hiltViewModel()
) = TopAppBar(
    title = {
        Text(text = stringResource(id = R.string.page_modules))
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

        val context = LocalContext.current
        IconButton(
            onClick = {
                Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sort_outline),
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
                pagerState = pagerState,
                onClose = { expanded = false }
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun ModulesSearchTopBar(
    viewModel: ModulesViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    navigationIcon = {
        IconButton(
            onClick = {
                viewModel.closeSearch()
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
            colors = OutlinedTextFieldDefaults.colors(
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
                Text(text = stringResource(id = R.string.modules_page_search_placeholder))
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun InstallFloatingButton() {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        InstallActivity.start(context = context, uri = uri)
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                launcher.launch("application/zip")
            }
        }
    }

    ExtendedFloatingActionButton(
        interactionSource = interactionSource,
        onClick = {},
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary,
        icon = {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = R.drawable.add_outline),
                contentDescription = null
            )
        },
        text = {
            Text(text = stringResource(id = R.string.module_install))
        }
    )
}