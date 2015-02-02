#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript inlcude
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       circle-env.sh
# \creation   2015-02-02, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Environment setzen als Bash Include für die Buildskripte
########################################################################################################################

# Wir gehen davon aus, dass wir uns im Verzeichnis Ci befinden
# Bei circleci befindet sich bereits
# Das Android Sdk wird später in ../tmp installiert

echo "    ANDROID_HOME vorher = $ANDROID_HOME"

export ANDROID_SDK_VERSION="24.0.2"
export ANDROID_BUILDTOOLS_VERSION="21.1.2"
export ANDROID_API_LEVEL="19"
export ANDROID_HOME=$(cd ..; pwd)/tmp/android-sdk-linux
export PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

echo "===================================================================================================="
echo "Envirnoment:"
echo "    ANDROID_SDK  = $ANDROID_SDK_VERSION"
echo "    BUILDTOOLS   = $ANDROID_BUILDTOOLS_VERSION"
echo "    API_LEVEL    = $ANDROID_API_LEVEL"
echo "    ANDROID_HOME = $ANDROID_HOME"
echo "    PATH         = $PATH"
echo "===================================================================================================="

echo "===================================================================================================="
echo "whoami = $(whoami)"
echo "--- android-sdk-linux ------------------------------------------------------------------------------"
ls -l /usr/local/android-sdk-linux
echo "--- tools ------------------------------------------------------------------------------------------"
ls -l /usr/local/android-sdk-linux/tools
echo "--- platform-tools ---------------------------------------------------------------------------------"
ls -l /usr/local/android-sdk-linux/platform-tools
echo "===================================================================================================="
