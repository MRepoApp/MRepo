package dev.sanmer.mrepo.compat.impl

enum class Platform(
    val context: String,
    val manager: String
) {
    KERNELSU(
        context = "u:r:su:s0",
        manager = "/data/adb/ksud"
    ),
    MAGISK(
        context = "u:r:magisk:s0",
        manager = "magisk"
    )
}