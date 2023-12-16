package com.sanmer.mrepo.provider.stub;

import com.sanmer.mrepo.model.local.LocalModule;
import com.sanmer.mrepo.provider.stub.IInstallCallback;

interface IModuleManager {
    String getVersion();
    int getVersionCode();
    boolean enable(String id);
    boolean disable(String id);
    boolean remove(String id);
    List<LocalModule> getModules();
    LocalModule getModuleById(String id);
    void install(String path, IInstallCallback callback);
}