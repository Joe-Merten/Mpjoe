#!/bin/bash -e

########################################################################################################################
# Android virtual Device starten
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       WaitForAvd.sh
# \creation   2014-02-03, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Insperied from android-wait-for-emulator by Ralf Kistner <ralf@embarkmobile.com>
# https://github.com/embarkmobile/android-maven-example/blob/master/wait_for_emulator
########################################################################################################################

# echo no | android create avd --force -n test -t android-19 --abi armeabi-v7a
# emulator -avd test -no-skin -no-audio -no-window &

declare ANDROID_HOME_DEFAULT="/opt/android/sdk"
if [ "$ANDROID_HOME" == "" ]; then
    ANDROID_HOME="$ANDROID_HOME_DEFAULT"
fi

declare ADB=$ANDROID_HOME/platform-tools/adb

# Nach dem Starten des Emulators liefert `adb -e shell getprop init.svc.bootanim`
# - "error: device not found"  ca.  5s
# - "error: device offline"    ca. 20s
# - "running"                  ca. 30s
# - "stopped"                  sobald das Device fertig gebootet ist und auf dem Sperrscreen steht (heisst vermutlich soviel wie "Cpu Sleep Mode")

declare FAIL_COUNTER=0
while true; do
    declare RESPONSE=$($ADB -e shell getprop init.svc.bootanim 2>&1)
    #echo "$RESPONSE"
    [[ "$RESPONSE" =~ "stopped" ]] && break
    if [[ "$RESPONSE" =~ "not found" ]]; then
        let "FAIL_COUNTER += 1"
        if [[ $FAIL_COUNTER -gt 15 ]]; then
            echo "Failed to start emulator" >&2
            exit 1
        fi
    fi
    sleep 1
done

# Keyevent zum Entsperren des Device senden
$ADB shell input keyevent 82
