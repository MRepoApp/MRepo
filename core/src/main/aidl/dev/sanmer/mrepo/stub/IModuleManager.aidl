package dev.sanmer.mrepo.stub;

import dev.sanmer.mrepo.content.Module;
import dev.sanmer.mrepo.stub.IInstallCallback;
import dev.sanmer.mrepo.stub.IModuleOpsCallback;

interface IModuleManager {
    String getVersion();
    int getVersionCode();
    String getPlatform();

    List<Module> getModules();
    Module getModuleById(String id);
    Module getModuleInfo(String path);

    oneway void enable(String id, IModuleOpsCallback callback);
    oneway void disable(String id, IModuleOpsCallback callback);
    oneway void remove(String id, IModuleOpsCallback callback);
    oneway void install(String path, IInstallCallback callback);

    boolean deleteOnExit(String path);
    oneway void reboot();
}