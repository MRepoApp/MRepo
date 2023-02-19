package com.sanmer.mrepo.utils.expansion

import androidx.compose.runtime.snapshots.SnapshotStateList

inline fun <reified T> MutableList<T>.update(value: T) {
    val index = indexOfFirst { it == value }
    if (index == -1) {
        add(value)
    } else {
        set(index, value)
    }
}

inline fun <reified T> SnapshotStateList<T>.update(value: T) {
    val index = indexOfFirst { it == value }
    if (index == -1) {
        add(value)
    } else {
        removeAt(index)
        add(index, value)
    }
}