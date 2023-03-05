cmake_minimum_required(VERSION 3.22.1)

project(MRepo)

set(CMAKE_C_FLAGS_RELEASE "-O3 -Wall -Werror -Wunused-but-set-variable")
set(CMAKE_CXX_FLAGS_RELEASE ${CMAKE_C_FLAGS_RELEASE})
set(CMAKE_C_FLAGS_RELWITHDEBINFO ${CMAKE_C_FLAGS_RELEASE})
set(CMAKE_CXX_FLAGS_RELWITHDEBINFO ${CMAKE_C_FLAGS_RELWITHDEBINFO})

add_library(pcre2 STATIC
        external/pcre/src/pcre2_auto_possess.c
        external/pcre/src/pcre2_compile.c
        external/pcre/src/pcre2_config.c
        external/pcre/src/pcre2_context.c
        external/pcre/src/pcre2_convert.c
        external/pcre/src/pcre2_dfa_match.c
        external/pcre/src/pcre2_error.c
        external/pcre/src/pcre2_extuni.c
        external/pcre/src/pcre2_find_bracket.c
        external/pcre/src/pcre2_maketables.c
        external/pcre/src/pcre2_match.c
        external/pcre/src/pcre2_match_data.c
        external/pcre/src/pcre2_jit_compile.c
        external/pcre/src/pcre2_newline.c
        external/pcre/src/pcre2_ord2utf.c
        external/pcre/src/pcre2_pattern_info.c
        external/pcre/src/pcre2_script_run.c
        external/pcre/src/pcre2_serialize.c
        external/pcre/src/pcre2_string_utils.c
        external/pcre/src/pcre2_study.c
        external/pcre/src/pcre2_substitute.c
        external/pcre/src/pcre2_substring.c
        external/pcre/src/pcre2_tables.c
        external/pcre/src/pcre2_ucd.c
        external/pcre/src/pcre2_valid_utf.c
        external/pcre/src/pcre2_xclass.c
        external/pcre/src/pcre2_chartables.c)
target_compile_options(pcre2
        PRIVATE
        -DHAVE_CONFIG_H
        -Wall
        -Werror
        -DPCRE2_CODE_UNIT_WIDTH=8)
target_include_directories(pcre2
        PUBLIC
        external/pcre/include
        PRIVATE
        external/pcre/src)

add_library(selinux STATIC
        external/selinux/libselinux/src/avc.c
        external/selinux/libselinux/src/avc_internal.c
        external/selinux/libselinux/src/avc_sidtab.c
        external/selinux/libselinux/src/booleans.c
        external/selinux/libselinux/src/callbacks.c
        external/selinux/libselinux/src/canonicalize_context.c
        external/selinux/libselinux/src/checkAccess.c
        external/selinux/libselinux/src/check_context.c
        external/selinux/libselinux/src/compute_av.c
        external/selinux/libselinux/src/compute_create.c
        external/selinux/libselinux/src/compute_member.c
        external/selinux/libselinux/src/context.c
        external/selinux/libselinux/src/deny_unknown.c
        external/selinux/libselinux/src/disable.c
        external/selinux/libselinux/src/enabled.c
        external/selinux/libselinux/src/fgetfilecon.c
        external/selinux/libselinux/src/freecon.c
        external/selinux/libselinux/src/fsetfilecon.c
        external/selinux/libselinux/src/get_initial_context.c
        external/selinux/libselinux/src/getenforce.c
        external/selinux/libselinux/src/getfilecon.c
        external/selinux/libselinux/src/getpeercon.c
        external/selinux/libselinux/src/init.c
        external/selinux/libselinux/src/label.c
        external/selinux/libselinux/src/label_backends_android.c
        external/selinux/libselinux/src/label_file.c
        external/selinux/libselinux/src/label_support.c
        external/selinux/libselinux/src/lgetfilecon.c
        external/selinux/libselinux/src/load_policy.c
        external/selinux/libselinux/src/lsetfilecon.c
        external/selinux/libselinux/src/mapping.c
        external/selinux/libselinux/src/matchpathcon.c
        external/selinux/libselinux/src/policyvers.c
        external/selinux/libselinux/src/procattr.c
        external/selinux/libselinux/src/regex.c
        external/selinux/libselinux/src/reject_unknown.c
        external/selinux/libselinux/src/selinux_internal.c
        external/selinux/libselinux/src/sestatus.c
        external/selinux/libselinux/src/setenforce.c
        external/selinux/libselinux/src/setfilecon.c
        external/selinux/libselinux/src/setrans_client.c
        external/selinux/libselinux/src/sha1.c
        external/selinux/libselinux/src/stringrep.c)
target_compile_options(selinux
        PRIVATE
        -DNO_PERSISTENTLY_STORED_PATTERNS
        -DDISABLE_SETRANS
        -DDISABLE_BOOL
        -D_GNU_SOURCE
        -DNO_MEDIA_BACKEND
        -DNO_X_BACKEND
        -DNO_DB_BACKEND
        -Wall
        -Werror
        -Wno-error=missing-noreturn
        -Wno-error=unused-function
        -Wno-error=unused-variable
        -DUSE_PCRE2
        -DAUDITD_LOG_TAG=1003)
target_include_directories(selinux
        PUBLIC
        external/selinux/libselinux/include
        PRIVATE
        external/selinux/libselinux/src)
target_link_libraries(selinux
        PRIVATE
        pcre2)

find_library(log-lib log)

add_library(selinux-jni SHARED libselinux-jni.cpp logging.h)
target_link_libraries(selinux-jni selinux ${log-lib})

add_library(ksu SHARED libksu.cpp logging.h)
target_link_libraries(ksu ${log-lib})