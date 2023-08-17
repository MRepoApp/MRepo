package com.sanmer.mrepo.utils.extensions

import com.topjohnwu.superuser.Shell.Result

val Result.output get() = out.joinToString().trim()