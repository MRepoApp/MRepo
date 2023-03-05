#include <sys/prctl.h>
#include <jni.h>
#include "logging.h"

#define KERNEL_SU_OPTION 0xDEADBEEF
#define CMD_GET_VERSION 2

static bool ksuctl(int cmd, void* arg1, void* arg2) {
    int32_t result = 0;
    prctl(KERNEL_SU_OPTION, cmd, arg1, arg2, &result);
    return result == KERNEL_SU_OPTION;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sanmer_mrepo_provider_api_Ksu_getVersionCode(JNIEnv *env, jobject thiz) {
    int32_t version = -1;
    if (ksuctl(CMD_GET_VERSION, &version, nullptr)) {
        return version;
    }
    return version;
}