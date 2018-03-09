LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := DeviceTest

#LOCAL_JNI_SHARED_LIBRARIES := librkinfoDeviceTest
#LOCAL_JAVA_LIBRARIES := javax.obex

LOCAL_STATIC_JAVA_LIBRARIES += user_mode
LOCAL_STATIC_JAVA_LIBRARIES += ftp4j-1.7.2
LOCAL_STATIC_JAVA_LIBRARIES += jcifs-1.3.16
LOCAL_STATIC_JAVA_LIBRARIES += frizz
LOCAL_STATIC_JAVA_LIBRARIES += libandroidutils
LOCAL_REQUIRED_MODULES := librkinfoDeviceTest
#LOCAL_DEX_PREOPT := false
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_JAVA_LIBRARIES := telephony-common
include $(BUILD_PACKAGE)

include $(CLEAR_VARS) 

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := user_mode:/libs/user_mode.jar \
			ftp4j-1.7.2:/libs/ftp4j-1.7.2.jar \
			jcifs-1.3.16:/libs/jcifs-1.3.16.jar \
			frizz:libs/FrizzService.jar \
			libandroidutils:libs/androidutils.jar

include $(BUILD_MULTI_PREBUILT)
#include $(LOCAL_PATH)/libs/armeabi/Android.mk

include $(call all-makefiles-under,$(LOCAL_PATH))
