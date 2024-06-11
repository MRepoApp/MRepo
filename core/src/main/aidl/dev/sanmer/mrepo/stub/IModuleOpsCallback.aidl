package dev.sanmer.mrepo.stub;

oneway interface IModuleOpsCallback {
    void onSuccess(String id);
    void onFailure(String id, String msg);
}