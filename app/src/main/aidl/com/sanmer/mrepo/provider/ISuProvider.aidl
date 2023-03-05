package com.sanmer.mrepo.provider;

interface ISuProvider {
    int getPid();
    boolean isSelinuxEnabled();
    boolean getEnforce();
    String getContext();
    String getContextByPid(int pid);
    IBinder getFileSystemService();
    int getKsuVersionCode();
}