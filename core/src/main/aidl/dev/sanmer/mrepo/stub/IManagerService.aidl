package dev.sanmer.mrepo.stub;

import dev.sanmer.mrepo.stub.IFileManager;
import dev.sanmer.mrepo.stub.IModuleManager;
import dev.sanmer.mrepo.stub.IPowerManager;

interface IManagerService {
    IModuleManager getModuleManager();
    IFileManager getFileManager();
    IPowerManager getPowerManager();
}