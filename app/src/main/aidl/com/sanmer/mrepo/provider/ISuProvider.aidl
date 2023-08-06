package com.sanmer.mrepo.provider;

interface ISuProvider {
    String getContext();
    IBinder getFileSystemService();
}