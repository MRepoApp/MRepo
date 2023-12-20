package com.sanmer.mrepo.provider.stub;

interface IModuleOpsCallback {
    void onSuccess(String id);
    void onFailure(String id, String msg);
}