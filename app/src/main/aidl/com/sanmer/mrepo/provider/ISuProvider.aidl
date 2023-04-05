package com.sanmer.mrepo.provider;

interface ISuProvider {
    int getPid();
    String getContext();
    int getEnforce();
    IBinder getFileSystemService();
}