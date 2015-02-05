#!/bin/bash -e

########################################################################################################################
# Installation des Android Sdk
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       InstallAndroidSdk.sh
# \creation   2014-02-03, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# I choose `/opt/android/sdk` as default installation location like also mentioned at http://askubuntu.com/questions/308422/android-sdk-and-path
# TODO: For Osx, there is a recommendation for using homebrew: "brew install android-sdk"; see also http://stackoverflow.com/a/7697173/2880699
########################################################################################################################


# Mein eigenes Skriptverzeichnis ermitteln, damit ich SetEnv.sh finde
SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

MPJOE_SETENV_NO_AUTOMATIC="true"
source $SCRIPT_DIR/SetEnv.sh
MPJOE_SETENV_NO_AUTOMATIC=""

setEnvAndroidSdkVersion
setEnvOS

[ "$ANDROID_HOME" == "" ] && ANDROID_HOME="$ANDROID_HOME_DEFAULT"
ANDROID_DIR="$(dirname "$ANDROID_HOME")"

# Die Downloadlinks des reinen Sdk sehen in etwa so aus:
#    siehe hier: https://developer.android.com/sdk/index.html#Other
#    http://dl.google.com/android/android-sdk_r24.0.2-linux.tgz
#    http://dl.google.com/android/android-sdk_r24.0.2-macosx.zip
#    http://dl.google.com/android/android-sdk_r24.0.2-windows.zip
#    http://dl.google.com/android/installer_r24.0.2-windows.exe

# Und das Bundle mit Eclipse:
#    siehe hier: http://stackoverflow.com/a/27418547/2880699
#    aber auch hier: https://docs.google.com/presentation/d/1JOICG5Ow6QgMBrUKChenvSSxjylCpeQKvfvpPbJMx3M/edit#slide=id.p
#    https://dl.google.com/android/adt/adt-bundle-linux-x86-20140702.zip
#    https://dl.google.com/android/adt/adt-bundle-linux-x86_64-20140702.zip
#    https://dl.google.com/android/adt/adt-bundle-mac-x86_64-20140702.zip
#    https://dl.google.com/android/adt/adt-bundle-windows-x86-20140702.zip
#    https://dl.google.com/android/adt/adt-bundle-windows-x86_64-20140702.zip
# leider beinhalten die o.g. Links ein recht altes Eclipse (4.2 Juno); aktuellere Links habe ich nicht gefunden
# Um Bundle mit Android Studio kümmere ich mich vorerst nicht.

declare ARCHIVENAME=""
declare ARCHIVEDIR=""
[ "$OS" == "Linux" ] && ARCHIVENAME="android-sdk_r$ANDROID_SDK_VERSION-linux.tgz"
[ "$OS" == "Linux" ] && ARCHIVEDIR="android-sdk-linux"
[ "$OS" == "Osx"   ] && ARCHIVENAME="android-sdk_r$ANDROID_SDK_VERSION-macosx.zip"
[ "$OS" == "Osx"   ] && ARCHIVEDIR="android-sdk-macosx"


echo "Android Sdk Setup:"
echo "    ANDROID_SDK  = $ANDROID_SDK_VERSION"
echo "    BUILDTOOLS   = $ANDROID_BUILDTOOLS_VERSION"
echo "    API_LEVEL    = $ANDROID_API_LEVEL"
echo "    ANDROID_DIR  = $ANDROID_DIR"
echo "    ANDROID_HOME = $ANDROID_HOME"
echo "    ARCHIVENAME  = $ARCHIVENAME"


# Update / Nachinstallation ist hier noch nicht berücksichtigt
# Deshalb exit mit Fehlermeldung, falls $ANDROID_HOME schon existiert
if [ -d "$ANDROID_HOME" ]; then
    echo "Error: $ANDROID_HOME already exists!" >&2
    exit 1
fi

# Basisverzeichnis anlegen, falls noch nicht existent (also z.B. /opt/android)
if ! mkdir -p "$ANDROID_DIR" 2>/dev/null; then
    # Ok, we might to need sudo access
    echo "Need root permission to create directory $ANDROID_DIR"
    sudo mkdir -p "$ANDROID_DIR"
    sudo chown $USER:$(id -gn) "$ANDROID_DIR"
fi

# Schreibberechtigung im Basisverzeichnis prüfen
if ! touch "$ANDROID_DIR/try.touch"; then
    echo "Error: Failed to write into $ANDROID_DIR" >&2
    exit 1
fi
rm "$ANDROID_DIR/try.touch"

# aktuelles Sdk ziehen
declare TMP_DIR="$(mktemp -d -t InstallAndroidSdk-XXXX)"
echo "Downloading http://dl.google.com/android/$ARCHIVENAME to $TMP_DIR ..."
wget "http://dl.google.com/android/$ARCHIVENAME" -P "$TMP_DIR"
[ "$OS" == "Linux" ] && tar   -C"$ANDROID_DIR" -xzf "$TMP_DIR/$ARCHIVENAME"
[ "$OS" == "Osx"   ] && unzip -d"$ANDROID_DIR" -q   "$TMP_DIR/$ARCHIVENAME"
rm "$TMP_DIR/$ARCHIVENAME"
rmdir "$TMP_DIR"
# Rename Directory "android-sdk-linux" into something like "sdk"
mv "$ANDROID_DIR/$ARCHIVEDIR" "$ANDROID_HOME"

# Benötigte Komponenten nachinstallieren
echo "Installing additional Sdk components, please stand by ..."
echo yes | $ANDROID_HOME/tools/android update sdk --no-ui --force --filter platform-tools                                         >/dev/null
echo yes | $ANDROID_HOME/tools/android update sdk --no-ui --force --filter build-tools-$ANDROID_BUILDTOOLS_VERSION --all          >/dev/null
echo yes | $ANDROID_HOME/tools/android update sdk --no-ui --force --filter android-$ANDROID_API_LEVEL                             >/dev/null
echo yes | $ANDROID_HOME/tools/android update sdk --no-ui --force --filter sys-img-armeabi-v7a-android-$ANDROID_API_LEVEL --all   >/dev/null


# Hier nur noch etwas Komfort Output für den Aufrufer
SETENV_CALL="$SCRIPT_DIR/SetEnv.sh"
SETENV_CALL=$(python -c "import os.path; print os.path.relpath('$SETENV_CALL', '$(pwd)')")
SETENV_CALL="source $SETENV_CALL"

echo "Android Sdk installation complete."
echo ""
echo "You might also want to set ANDROID_HOME and PATH entvironment variables for your convinience, like:"
echo "    export ANDROID_HOME=\"$ANDROID_HOME\""
echo "    export PATH=\"\$PATH:\${ANDROID_HOME}/tools:\${ANDROID_HOME}/platform-tools\""
echo "You could archive that for your current terminal session by calling the SetEnv.sh bash script:"
echo "    $SETENV_CALL"
