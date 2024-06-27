package dev.sanmer.mrepo.datastore.model

enum class WorkingMode {
    Setup,
    None,
    Superuser,
    Shizuku;

    companion object {
        val WorkingMode.isRoot get() = this == Superuser || this == Shizuku
        val WorkingMode.isNonRoot get() = this == None
        val WorkingMode.isSetup get() = this == Setup
    }
}
