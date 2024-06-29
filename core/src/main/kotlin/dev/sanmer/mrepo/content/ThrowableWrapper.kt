package dev.sanmer.mrepo.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThrowableWrapper(
    val original: Throwable
) : Parcelable {
    companion object {
        fun Throwable.wrap() = ThrowableWrapper(this)
    }
}
