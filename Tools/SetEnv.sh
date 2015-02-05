########################################################################################################################
# Setzen diverser Environmentvariablen
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       SetEnv.sh
# \creation   2014-02-05, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Bash include, also Verwendung:
#     source Tools/SetEnv.sh
# Durch vorheriges setzen von MPJOE_SETENV_NO_AUTOMATIC="true" werden nur die Funktionen bereit gestellt, aber nicht
# automatisch aufgerufen - wird z.B. verwendet von InstallAndroidSdk.sh
########################################################################################################################


function setEnvAndroidSdkVersion() {
    export ANDROID_SDK_VERSION="24.0.2"
    export ANDROID_BUILDTOOLS_VERSION="21.1.2"
    export ANDROID_API_LEVEL="19"
    export ANDROID_HOME_DEFAULT="/opt/android/sdk"
    export ANDROID_HOME_BREW="/usr/local/Cellar/android-sdk"
}

function setEnvOS() {
    export OS=""
    if [ "$(uname)" == "Darwin" ]; then
        export OS="Osx"
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        export OS="Linux"
    else
        echo "Error: Unknown OS \"$(uname)\"" >&2
        return 1
    fi
}

function setEnv() {
    setEnvAndroidSdkVersion
    setEnvOS || return 1

    # ggf. ANDROID_HOME suchen & setzen
    [ "$ANDROID_HOME" == "" ] && [ -d "$ANDROID_HOME_DEFAULT" ] && export ANDROID_HOME="$ANDROID_HOME_DEFAULT"
    [ "$ANDROID_HOME" == "" ] && [ -d "$ANDROID_HOME_BREW"    ] && export ANDROID_HOME="$ANDROID_HOME_BREW"
    if [ "$ANDROID_HOME" == "" ]; then
        echo "Error: No android sdk found" >&2
        return 1
    fi

    # Android tools und platform-tools im PATH aufnehmen, damit z.B. adb und emulator direkt aufrufbar sind
    local ANDROID_TOOLS="$ANDROID_HOME/tools"
    local ANDROID_PLATFORMTOOLS="$ANDROID_HOME/platform-tools"
    # Ich könnte hier entweder echecken, ob die Verzeichnisse in PATH enthalten sind, z.B. ala: `if [[ ":$PATH:" == *":$ANDROID_TOOLS:"* ]]; then`
    # oder einfach $(which adb); bei der which Variante würde ich aber auch z.B. eine ggf. andere vorhandene Android Sdk Installation erwischen ... hmm
    [[ ":$PATH:" == *":$ANDROID_PLATFORMTOOLS:"* ]] || export PATH=$ANDROID_PLATFORMTOOLS:$PATH
    [[ ":$PATH:" == *":$ANDROID_TOOLS:"*         ]] || export PATH=$ANDROID_TOOLS:$PATH

    # Für Maven unter OSX ggf. JAVA_HOME setzen, siehe http://blog.tompawlak.org/maven-default-java-version-mac-osx oder http://www.jayway.com/2013/03/08/configuring-maven-to-use-java-7-on-mac-os-x
    [ "$OS" == "Osx" ] && [ "$JAVA_HOME" == "" ] && [ -x "/usr/libexec/javahome" ] && export JAVA_HOME="$("/usr/libexec/javahome")"

    return 0
}


if [ "$MPJOE_ENVIRONMENT_SET" == "" ] && [ "$MPJOE_SETENV_NO_AUTOMATIC" != "true" ] ; then
    if ! setEnv; then
        # Exit mit Fehler, sofern wir nicht direkt von Kommandozeile aufgerufen wurden (weil das würde sonst die Konsole schliessen)
        [[ "$-" =~ "i" ]] || exit 1
    else
        export MPJOE_ENVIRONMENT_SET="true"
    fi
fi
