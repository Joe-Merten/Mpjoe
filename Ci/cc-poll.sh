#!/bin/bash -e
########################################################################################################################
# CI Notification Skript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       cc-poll.sh
# \creation   2015-02-02, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Hier pollen wir die Status der Buildserver, so wie es auch die Notification Tools tun (CCMenu & Co).
########################################################################################################################


########################################################################################################################
# Ansi Terminalcodes
########################################################################################################################
declare ESC=$'\e'
declare RESET_TERMINAL="${ESC}c"
declare CLEAR_SCREEN="${ESC}[2J"
declare CLEAR_CURRENT_LINE="${ESC}[2K"
declare CLEAR_CURRENT_LINE_LEFT="${ESC}[1K"
declare CLEAR_CURRENT_LINE_RIGHT="${ESC}[K"
declare CURSOR_XY_HOME="${ESC}[H"
declare CURSOR_ON="${ESC}[?25h"
declare CURSOR_OFF="${ESC}[?25l"


########################################################################################################################
# Abfrage eines Buildservers
#-----------------------------------------------------------------------------------------------------------------------
# \in  url
########################################################################################################################
function pollServer() {
    local url="$1"
    local status="$(mktemp -t --suffix .txt  Mpjoe-ccpoll-XXXX)"
    local output="$(mktemp -t --suffix .xml  Mpjoe-ccpoll-XXXX)"
    wget -o "$status" -O "$output" "$url"
    echo "========== $url ==========$CLEAR_CURRENT_LINE_RIGHT"

    # Datei zeilenweise ausgeben, damit ich jeweils am Zeilenende ggf. Reste alter Ausgaben löschen kann
    cat "$output" | xmllint --format - | while IFS= read -r line; do
        echo "$line$CLEAR_CURRENT_LINE_RIGHT"
    done

    echo "==========================$CLEAR_CURRENT_LINE_RIGHT"
    echo "$CLEAR_CURRENT_LINE_RIGHT"

    rm "$status" "$output"
}

function pollServers() {
    #printf "$CURSOR_XY_HOME"
    echo "================================================================================"
    echo "================================================================================"
    echo "================================================================================"
    echo ""
    pollServer "https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/cctray.xml"
    pollServer "https://api.travis-ci.org/repos/Joe-Merten/Mpjoe/cc.xml"
    pollServer "https://circleci.com/gh/Joe-Merten/Mpjoe.cc.xml"
}


printf "$RESET_TERMINAL"
while true; do
    pollServers
    sleep 1
done
