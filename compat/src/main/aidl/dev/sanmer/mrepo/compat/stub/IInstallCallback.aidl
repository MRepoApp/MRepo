package dev.sanmer.mrepo.compat.stub;

interface IInstallCallback {
    void onStdout(String msg);
    void onStderr(String msg);
    void onSuccess(String id);
    void onFailure();
}