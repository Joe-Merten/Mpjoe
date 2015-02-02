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

# Env nicht noch mal setzen, wird ja schon in circle-build.sh erledigt
# source circle-env.sh

if [ "$ANDROID_HOME" == "" ]; then
    echo "ANDROID_HOME is not set!" 1>&2
    exit 1
fi

# TODO: Hier nur Nachinstallieren bei Erfordernis.
# Hmm, lt. https://circleci.com/docs/android soll man das Zeug aus /usr/local/android-sdk-linux nach /home/ubuntu/android kopieren und dort dann nachinstallieren
# Aber warum setzen die dann den PATH auf /usr/local/android-sdk-linux ?
# Ich installiere jetzt erst man frech direkt in /usr/local/android-sdk-linux hinein

# Install required components. For a full list, run `android list sdk -a --extended`
echo yes | android update sdk --no-ui --filter platform-tools                                       --force  >/dev/null
#echo yes | android update sdk --no-ui --filter tools                                                --force  >/dev/null
echo yes | android update sdk --no-ui --filter build-tools-$ANDROID_BUILDTOOLS_VERSION        --all --force  >/dev/null
echo yes | android update sdk --no-ui --filter android-$ANDROID_API_LEVEL                           --force  >/dev/null

#echo yes | android update sdk --no-ui --filter extra-android-support                          --all --force  >/dev/null
#echo yes | android update sdk --no-ui --filter extra-android-m2repository                     --all --force  >/dev/null

echo yes | android update sdk --no-ui --filter sys-img-armeabi-v7a-android-$ANDROID_API_LEVEL --all --force  >/dev/null
