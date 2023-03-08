package com.sanmer.mrepo.provider;

interface ISuProvider {
    int getPid();
    String getContext();
    int getEnforce();
    boolean isSelinuxEnabled();
    String getContextByPid(int pid);
    IBinder getFileSystemService();
    int getKsuVersionCode();
}