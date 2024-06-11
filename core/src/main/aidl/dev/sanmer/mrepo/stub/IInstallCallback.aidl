package dev.sanmer.mrepo.stub;

import dev.sanmer.mrepo.content.Module;

oneway interface IInstallCallback {
    void onStdout(String msg);
    void onStderr(String msg);
    void onSuccess(in Module module);
    void onFailure();
}