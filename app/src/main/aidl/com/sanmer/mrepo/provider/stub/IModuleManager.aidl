package com.sanmer.mrepo.provider.stub;

import com.sanmer.mrepo.model.local.LocalModule;
import com.sanmer.mrepo.provider.stub.IInstallCallback;
import com.sanmer.mrepo.provider.stub.IModuleOpsCallback;

interface IModuleManager {
    String getVersion();
    int getVersionCode();
    List<LocalModule> getModules();
    LocalModule getModuleById(String id);
    LocalModule getModuleInfo(String zipPath);
    oneway void enable(String id, IModuleOpsCallback callback);
    oneway void disable(String id, IModuleOpsCallback callback);
    oneway void remove(String id, IModuleOpsCallback callback);
    oneway void install(String path, IInstallCallback callback);
}