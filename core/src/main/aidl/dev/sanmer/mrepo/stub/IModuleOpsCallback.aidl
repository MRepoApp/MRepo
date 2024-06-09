package dev.sanmer.mrepo.stub;

interface IModuleOpsCallback {
    void onSuccess(String id);
    void onFailure(String id, String msg);
}