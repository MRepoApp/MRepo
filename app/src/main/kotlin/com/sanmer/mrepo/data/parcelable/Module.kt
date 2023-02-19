package com.sanmer.mrepo.data.parcelable

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Module(
    val name: String,
    val path: String,
    val url: String,
) : Parcelable