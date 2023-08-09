package com.sanmer.mrepo.ui.screens.repository.viewmodule.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.utils.LicenseContent
import com.sanmer.mrepo.ui.utils.expandedShape

@Composable
fun LicenseItem(
    licenseId: String
) = Box {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    LabelItem(
        icon = R.drawable.receipt_search_outline,
        onClick = { openBottomSheet = true }
    )

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            shape = BottomSheetDefaults.expandedShape(15.dp),
            windowInsets = WindowInsets.navigationBars
        ) {
            Text(
                text = stringResource(id = R.string.license_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            LicenseContent(licenseId = licenseId)
        }
    }
}