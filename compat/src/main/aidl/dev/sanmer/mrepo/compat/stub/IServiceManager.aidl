package dev.sanmer.mrepo.compat.stub;

import dev.sanmer.mrepo.compat.stub.IFileManager;
import dev.sanmer.mrepo.compat.stub.IModuleManager;

interface IServiceManager {
    int getUid() = 0;
    int getPid() = 1;
    String getSELinuxContext() = 2;
    IModuleManager getModuleManager() = 3;
    IFileManager getFileManager() = 4;
    String currentPlatform() = 5;

    void destroy() = 16777114; // Only for Shizuku
}