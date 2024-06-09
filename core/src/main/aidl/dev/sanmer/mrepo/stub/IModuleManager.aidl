package dev.sanmer.mrepo.stub;

import dev.sanmer.mrepo.content.LocalModule;
import dev.sanmer.mrepo.stub.IInstallCallback;
import dev.sanmer.mrepo.stub.IModuleOpsCallback;

interface IModuleManager {
    String getVersion();
    int getVersionCode();
    String getPlatform();
    List<LocalModule> getModules();
    LocalModule getModuleById(String id);
    LocalModule getModuleInfo(String zipPath);
    oneway void enable(String id, IModuleOpsCallback callback);
    oneway void disable(String id, IModuleOpsCallback callback);
    oneway void remove(String id, IModuleOpsCallback callback);
    oneway void install(String path, IInstallCallback callback);
}