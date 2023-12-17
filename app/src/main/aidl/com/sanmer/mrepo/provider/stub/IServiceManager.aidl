package com.sanmer.mrepo.provider.stub;

import com.sanmer.mrepo.provider.stub.IFileManager;
import com.sanmer.mrepo.provider.stub.IModuleManager;

interface IServiceManager {
    int getUid();
    int getPid();
    String getSELinuxContext();
    IModuleManager getModuleManager();
    IFileManager getFileManager();
}