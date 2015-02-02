#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       travis-setup-android.sh
# \creation   2015-02-01, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Installation des Android Sdk
########################################################################################################################


# - Auf meinem Kubuntu 14.04 habe ich ende Januar das Android Sdk Version 24.0.2 installiert
# - bei travis ist noch kein ANDROID_HOME gesetzt und offenbar ist es üblich (siehe dreamDroid Projekt), das Android Sdk immer wieder neu im Projekt Root zu installieren
# - ich installiere es in Mpjoe/tmp (da hab' ich auch ein .gitignore drauf)
# - das ANDROID_HOME setze ich im .travis.yml, dadurch ist es in allen Skripten sichtbar

declare ANDROID_SDK_VERSION="24.0.2"
declare ANDROID_BUILDTOOLS_VERSION="21.1.2"
declare ANDROID_API_LEVEL="19"

echo "===================================================================================================="
echo "Android Sdk Setup:"
echo "    ANDROID_SDK  = $ANDROID_SDK_VERSION"
echo "    BUILDTOOLS   = $ANDROID_BUILDTOOLS_VERSION"
echo "    API_LEVEL    = $ANDROID_API_LEVEL"
echo "    ANDROID_HOME = $ANDROID_HOME"

rm -rf ../tmp
mkdir -p ../tmp
cd ../tmp

sudo apt-get update -qq
if [ "$(uname -m)" == "x86_64" ]; then sudo apt-get install -qq --force-yes libgd2-xpm ia32-libs ia32-libs-multiarch >/dev/null; fi
wget http://dl.google.com/android/android-sdk_r$ANDROID_SDK_VERSION-linux.tgz
tar xzf android-sdk_r$ANDROID_SDK_VERSION-linux.tgz

# Install required components.
# For a full list, run `android list sdk -a --extended`
echo yes | android update sdk --no-ui --filter platform-tools                                       --force  >/dev/null
#echo yes | android update sdk --no-ui --filter tools                                                --force  >/dev/null
echo yes | android update sdk --no-ui --filter build-tools-$ANDROID_BUILDTOOLS_VERSION        --all --force  >/dev/null
echo yes | android update sdk --no-ui --filter android-$ANDROID_API_LEVEL                           --force  >/dev/null

#echo yes | android update sdk --no-ui --filter extra-android-support                          --all --force  >/dev/null
#echo yes | android update sdk --no-ui --filter extra-android-m2repository                     --all --force  >/dev/null

echo yes | android update sdk --no-ui --filter sys-img-armeabi-v7a-android-$ANDROID_API_LEVEL --all --force  >/dev/null

echo "===================================================================================================="
echo "--- ANDROID_HOME -----------------------------------------------------------------------------------"
ls -l $ANDROID_HOME
echo "===================================================================================================="

cd ../Ci
