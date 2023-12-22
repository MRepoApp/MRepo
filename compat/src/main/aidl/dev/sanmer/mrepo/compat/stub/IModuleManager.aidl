package dev.sanmer.mrepo.compat.stub;

import dev.sanmer.mrepo.compat.content.LocalModule;
import dev.sanmer.mrepo.compat.stub.IInstallCallback;
import dev.sanmer.mrepo.compat.stub.IModuleOpsCallback;

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