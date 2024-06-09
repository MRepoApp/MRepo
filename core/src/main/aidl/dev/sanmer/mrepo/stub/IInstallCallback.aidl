package dev.sanmer.mrepo.stub;

import dev.sanmer.mrepo.content.LocalModule;

interface IInstallCallback {
    void onStdout(String msg);
    void onStderr(String msg);
    void onSuccess(in LocalModule module);
    void onFailure();
}