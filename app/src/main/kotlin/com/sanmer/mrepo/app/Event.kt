package com.sanmer.mrepo.app

enum class Event {
    NON,
    LOADING,
    SUCCEEDED,
    FAILED
}

val Event.isNon get() = this == Event.NON
val Event.isLoading get() = this == Event.LOADING
val Event.isSucceeded get() = this == Event.SUCCEEDED
val Event.isFailed get() = this == Event.FAILED
val Event.isFinished get() = isSucceeded || isFailed
val Event.isNotReady get() = isNon || isFailed