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


# Auf meinem Kubuntu 14.04 habe ich ende Januar das Android Sdk Version 24.0.2 installiert
# Bei snap-ci sehe ich bei der Sdk Installation: "Downloading android sdk from http://dl.google.com/android/android-sdk_r23.0.2-linux.tgz"
# Im snap-ci Beispielskript war 20.0.0 voreingestellt.
# Folgende Ergebnisse mit snap-ci:
# - 20.0.0 -> Baut durch
# - 23.0.2 -> "Error: Ignoring unknown package filter 'build-tools-24.0.2'" -> build-tools werden nicht installiert, obwohl "Downloading android sdk from http://dl.google.com/android/android-sdk_r23.0.2-linux.tgz"
# - 24.0.2 -> "Error: Ignoring unknown package filter 'build-tools-24.0.2'" -> build-tools werden nicht installiert, obwohl "Android SDK Tools, revision 24.0.2" und "Downloading Android SDK Tools, revision 24.0.2" und "Installed Android SDK Tools, revision 24.0.2"

#    declare ANDROID_BUILDTOOLS_VERSION="20.0.0"
#    declare ANDROID_API_LEVEL="19"
#
#    # existance of this file indicates that all dependencies were previously installed, and any changes to this file will use a different filename.
#    INITIALIZATION_FILE="$ANDROID_HOME/.initialized-dependencies-$(git log -n 1 --format=%h -- $0)"
#
#    echo "Android Sdk Setup:"
#    echo "    ANDROID_HOME = $ANDROID_HOME"
#    echo "    BUILDTOOLS   = $ANDROID_BUILDTOOLS_VERSION"
#    echo "    API_LEVEL    = $ANDROID_API_LEVEL"
#    echo "    INIT_FILE    = $INITIALIZATION_FILE"
#
#    # Neuinstallation des Android Sdk forcieren
#    echo "Removing $ANDROID_HOME to force reinstallation of Android Sdk"
#    rm -rf "$ANDROID_HOME"
#
#    if [ ! -e ${INITIALIZATION_FILE} ]; then
#        # fetch and initialize $ANDROID_HOME
#        download-android
#        # Use the latest android sdk tools
#        echo y | android update sdk --no-ui --filter platform-tools                                       >/dev/null
#        echo y | android update sdk --no-ui --filter tools                                                >/dev/null
#
#        # The BuildTools version used by your project
#        echo y | android update sdk --no-ui --filter build-tools-$ANDROID_BUILDTOOLS_VERSION --all        >/dev/null
#
#        # The SDK version used to compile your project
#        echo y | android update sdk --no-ui --filter android-$ANDROID_API_LEVEL                           >/dev/null
#
#        # uncomment to install the Extra/Android Support Library
#        # echo y | android update sdk --no-ui --filter extra-android-support --all                        >/dev/null
#
#        # uncomment these if you are using maven/gradle to build your android project
#        # echo y | android update sdk --no-ui --filter extra-google-m2repository --all                    >/dev/null
#        # echo y | android update sdk --no-ui --filter extra-android-m2repository --all                   >/dev/null
#
#        # Specify at least one system image if you want to run emulator tests
#        echo y | android update sdk --no-ui --filter sys-img-armeabi-v7a-android-$ANDROID_API_LEVEL --all >/dev/null
#
#        touch ${INITIALIZATION_FILE}
#    fi

# Install base Android SDK
# - bei travis ist noch kein ANDROID_HOME gesetzt und offenbar ist es üblich (siehe dreamDroid Projekt), das Android Sdk immer wieder neu im Projekt Root zu installieren
# - ich installiere es in Mpjoe/tmp (da hab' ich auch ein .gitignore drauf)

#declare ANDROID_SDK_VERSION="23.0.2"
declare ANDROID_SDK_VERSION="24.0.2"
declare ANDROID_BUILDTOOLS_VERSION="21.1.2"
declare ANDROID_API_LEVEL="19"

echo "===================================================================================================="
echo "Android Sdk Setup:"
echo "    ANDROID_SDK  = $ANDROID_SDK_VERSION"
echo "    BUILDTOOLS   = $ANDROID_BUILDTOOLS_VERSION"
echo "    API_LEVEL    = $ANDROID_API_LEVEL"

rm -rf ../tmp
mkdir -p ../tmp
cd ../tmp

sudo apt-get update -qq
if [ "$(uname -m)" == "x86_64" ]; then sudo apt-get install -qq --force-yes libgd2-xpm ia32-libs ia32-libs-multiarch >/dev/null; fi
wget http://dl.google.com/android/android-sdk_r$ANDROID_SDK_VERSION-linux.tgz
tar xzf android-sdk_r$ANDROID_SDK_VERSION-linux.tgz
export ANDROID_HOME=$PWD/android-sdk-linux
export PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools
echo "    ANDROID_HOME = $ANDROID_HOME"

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
