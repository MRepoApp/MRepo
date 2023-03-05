package com.sanmer.mrepo.utils.expansion

import com.topjohnwu.superuser.Shell.Result

val Result.output get() = out.joinToString().trim()