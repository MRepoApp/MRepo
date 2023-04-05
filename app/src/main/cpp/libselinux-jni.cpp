#include <selinux/selinux.h>
#include <jni.h>
#include <cstring>
#include "logging.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sanmer_mrepo_provider_SELinux_getContext(JNIEnv *env, jobject thiz) {
    char *context = nullptr;
    int ok = getcon(&context) == 0;
    context = ok ? context : strdup("unknown");
    freecon(context);
    return env->NewStringUTF(context);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sanmer_mrepo_provider_SELinux_getEnforce(JNIEnv *env, jobject thiz) {
    int enforce = security_getenforce();
    return enforce;
}