package dev.sanmer.mrepo.compat.stub;

import dev.sanmer.mrepo.compat.content.LocalModule;

interface IInstallCallback {
    void onStdout(String msg);
    void onStderr(String msg);
    void onSuccess(in LocalModule module);
    void onFailure();
}