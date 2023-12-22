package dev.sanmer.mrepo.compat.stub;

interface IModuleOpsCallback {
    void onSuccess(String id);
    void onFailure(String id, String msg);
}