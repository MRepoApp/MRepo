#include <selinux/selinux.h>
#include <jni.h>
#include <cstring>
#include <unistd.h>
#include <cerrno>
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
JNIEXPORT jboolean JNICALL
Java_com_sanmer_mrepo_provider_SELinux_isSelinuxEnabled(JNIEnv *env, jobject thiz) {
    int enabled = is_selinux_enabled();
    auto javaEnabled = (jboolean) (enabled ? JNI_TRUE : JNI_FALSE);
    return javaEnabled;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sanmer_mrepo_provider_SELinux_getEnforce(JNIEnv *env, jobject thiz) {
    int enforce = TEMP_FAILURE_RETRY(security_getenforce());
    if (enforce == -1 && !errno) {
        errno = EIO;
    }
    if (errno) {
        jclass e_cls = env->FindClass("java/io/IOException");
        env->ThrowNew(e_cls, "Permission denied");
    }
    LOGD("enforce=%i", enforce);
    auto javaEnforce = (jboolean) (enforce ? JNI_TRUE : JNI_FALSE);
    return javaEnforce;
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