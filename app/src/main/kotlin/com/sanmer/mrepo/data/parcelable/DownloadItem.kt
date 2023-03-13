package com.sanmer.mrepo.data.parcelable

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadItem(
    val id: Int = System.currentTimeMillis().toInt(),
    val name: String,
    val path: String,
    val url: String,
    val install: Boolean
) : Parcelable