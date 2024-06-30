package dev.sanmer.mrepo.stub;

import dev.sanmer.su.wrap.ThrowableWrapper;

oneway interface IModuleOpsCallback {
    void onSuccess(String id);
    void onFailure(String id, in ThrowableWrapper error);
}