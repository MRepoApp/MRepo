package com.sanmer.mrepo.ui.page.home

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.component.EventCard
import com.sanmer.mrepo.ui.component.EventCardItem

@Composable
fun InfoItem() {
    EventCard(
        stringRes = R.string.device_info
    ) {
        EventCardItem(
            key = stringResource(id = R.string.device_name),
            value = getDevice()
        )

        EventCardItem(
            key = stringResource(id = R.string.device_system_version),
            value = if (Build.VERSION.PREVIEW_SDK_INT != 0) {
                "${Build.VERSION.CODENAME} Preview (API ${Build.VERSION.SDK_INT})"
            } else {
                "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
            }
        )

        EventCardItem(
            key = stringResource(id = R.string.device_system_abi),
            value = Build.SUPPORTED_ABIS[0]
        )
    }
}

private fun getDevice(): String {
    var manufacturer =
        Build.MANUFACTURER[0].uppercaseChar().toString() + Build.MANUFACTURER.substring(1)
    if (Build.BRAND != Build.MANUFACTURER) {
        manufacturer += " " + Build.BRAND[0].uppercaseChar() + Build.BRAND.substring(1)
    }
    manufacturer += " " + Build.MODEL + " "
    return manufacturer
}