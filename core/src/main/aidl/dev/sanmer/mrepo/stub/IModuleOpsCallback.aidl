package dev.sanmer.mrepo.stub;

import dev.sanmer.mrepo.content.ThrowableWrapper;

oneway interface IModuleOpsCallback {
    void onSuccess(String id);
    void onFailure(String id, in ThrowableWrapper error);
}