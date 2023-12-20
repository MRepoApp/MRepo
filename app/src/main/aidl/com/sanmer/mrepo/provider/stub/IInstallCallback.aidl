package com.sanmer.mrepo.provider.stub;

import com.sanmer.mrepo.model.local.LocalModule;

interface IInstallCallback {
    void onStdout(String msg);
    void onStderr(String msg);
    void onSuccess(String id);
    void onFailure();
}