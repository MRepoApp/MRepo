#include <selinux/selinux.h>
#include <jni.h>
#include <cstring>
#include <sys/prctl.h>
#include <linux/seccomp.h>
#include "logging.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sanmer_mrepo_provider_SELinux_getContext(JNIEnv *env, jobject thiz) {
    char *context = nullptr;
    int ok = getcon(&context) == 0;
    context = ok ? context : strdup("unknown");
    LOGD("context=%s, ok=%i ", context, ok);
    freecon(context);
    return env->NewStringUTF(context);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sanmer_mrepo_provider_SELinux_getEnforce(JNIEnv *env, jobject thiz) {
    int enforce = security_getenforce();
    return enforce;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sanmer_mrepo_provider_SELinux_isSelinuxEnabled(JNIEnv *env, jobject thiz) {
    int enabled = is_selinux_enabled();
    auto javaEnabled = (jboolean) (enabled ? JNI_TRUE : JNI_FALSE);
    return javaEnabled;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sanmer_mrepo_provider_SELinux_getContextByPid(JNIEnv *env, jobject thiz, jint pid) {
    char *context = nullptr;
    int ok = getpidcon(pid, &context) == 0;
    context = ok ? context : strdup("unknown");
    LOGD("context=%s, pid=%i, ok=%i", context, pid, ok);
    freecon(context);
    return env->NewStringUTF(context);
}