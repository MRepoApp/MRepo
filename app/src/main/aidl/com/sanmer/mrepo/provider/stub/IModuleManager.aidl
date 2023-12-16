package com.sanmer.mrepo.provider.stub;

import com.sanmer.mrepo.model.local.LocalModule;

interface IModuleManager {
    String getVersion();
    int getVersionCode();
    boolean enable(String id);
    boolean disable(String id);
    boolean remove(String id);
    List<LocalModule> getModules();
    LocalModule getModuleById(String id);
    LocalModule install(String path, out List<String> msg);
}