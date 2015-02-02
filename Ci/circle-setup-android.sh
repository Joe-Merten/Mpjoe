#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       circle-setup-android.sh
# \creation   2015-02-02, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Installation des Android Sdk
# Siehe auch:
# - https://circleci.com/docs/android
# - http://www.ericrgon.com/android-with-circle-ci
# - http://blog.circleci.com/announcing-ios-and-android-support
########################################################################################################################

source circle-env-sh

if [ "$ANDROID_HOME" == "" ]; then
    echo "ANDROID_HOME is not set!" 1>&2
    exit 1
fi

rm -rf "$ANDROID_HOME"
mkdir -p "$ANDROID_HOME"
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
