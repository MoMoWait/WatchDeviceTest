LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := librkinfoDeviceTest
LOCAL_SRC_FILES := getinfo.cpp
LOCAL_SHARED_LIBRARIES := liblog libcutils libhardware libipcs libdl

#This is used for android log.
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)
