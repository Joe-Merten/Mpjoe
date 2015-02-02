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
# Bei circleci befindet sich bereits ein vorinstalliertes Android Sdk im Verzeichnis /usr/local/android-sdk-linux, allerdings ist ANDROID_HOME nicht passend gesetzt
# Im Path sind ${ANDROID_HOME}/tools und ${ANDROID_HOME}/platform-tools aber schon enthalten
# Auf das Verzeichnis ANDROID_HOME habe ich auch schreibrechte, sodass ich ggf. mein Zeug nachinstallieren kann

export ANDROID_SDK_VERSION="24.0.2"
export ANDROID_BUILDTOOLS_VERSION="21.1.2"
export ANDROID_API_LEVEL="19"
export ANDROID_HOME=/usr/local/android-sdk-linux

echo "===================================================================================================="
echo "Envirnoment:"
echo "    ANDROID_SDK  = $ANDROID_SDK_VERSION"
echo "    BUILDTOOLS   = $ANDROID_BUILDTOOLS_VERSION"
echo "    API_LEVEL    = $ANDROID_API_LEVEL"
echo "    ANDROID_HOME = $ANDROID_HOME"
echo "    PATH         = $PATH"
echo "===================================================================================================="

# echo "===================================================================================================="
# echo "whoami = $(whoami)"
# echo "--- android-sdk-linux ------------------------------------------------------------------------------"
# ls -l /usr/local/android-sdk-linux
# echo "--- tools ------------------------------------------------------------------------------------------"
# ls -l /usr/local/android-sdk-linux/tools
# echo "--- platform-tools ---------------------------------------------------------------------------------"
# ls -l /usr/local/android-sdk-linux/platform-tools
# echo "===================================================================================================="
