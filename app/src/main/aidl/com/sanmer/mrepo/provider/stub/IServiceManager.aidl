package com.sanmer.mrepo.provider.stub;

import com.sanmer.mrepo.provider.stub.IFileManager;
import com.sanmer.mrepo.provider.stub.IModuleManager;

interface IServiceManager {
    int getUid() = 0;
    int getPid() = 1;
    String getSELinuxContext() = 2;
    IModuleManager getModuleManager() = 3;
    IFileManager getFileManager() = 4;
    boolean isKsu() = 5;

    void destroy() = 16777114; // Only for Shizuku
}