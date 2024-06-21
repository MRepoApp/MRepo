package dev.sanmer.mrepo.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Module(
    val id: String,
    val name: String,
    val version: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val updateJson: String,
    val state: State,
    val lastUpdated: Long
) : Parcelable