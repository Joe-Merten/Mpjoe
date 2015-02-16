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

# Display Modes:
#   1 = Vewendung von CURSOR_XY_HOME und Trennzeilen etc
#   2 = Platzsparende Ausgabe und ohne CURSOR_XY_HOME
declare DISPLAY_MODE="2"

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
    local timestamp=""
    [ "$DISPLAY_MODE" == "2" ] && timestamp="$(date +"%T")  "
    [ "$DISPLAY_MODE" == "1" ] && echo "========== $url ==========$CLEAR_CURRENT_LINE_RIGHT"

    # Datei zeilenweise ausgeben, damit ich jeweils am Zeilenende ggf. Reste alter Ausgaben löschen kann
    cat "$output" | xmllint --c14n --format - | while IFS= read -r line || [[ -n "$line" ]]; do
        echo "$timestamp$line$CLEAR_CURRENT_LINE_RIGHT"
    done

    [ "$DISPLAY_MODE" == "1" ] && echo "==========================$CLEAR_CURRENT_LINE_RIGHT"
    [ "$DISPLAY_MODE" == "1" ] && echo "$CLEAR_CURRENT_LINE_RIGHT"

    rm "$status" "$output"
}

function pollServers() {
    [ "$DISPLAY_MODE" == "1" ] && printf "$CURSOR_XY_HOME"
    [ "$DISPLAY_MODE" == "1" ] && echo "================================================================================$CLEAR_CURRENT_LINE_RIGHT"
    [ "$DISPLAY_MODE" == "1" ] && echo "================================================================================$CLEAR_CURRENT_LINE_RIGHT"
    [ "$DISPLAY_MODE" == "1" ] && echo "================================================================================$CLEAR_CURRENT_LINE_RIGHT"
    echo "$CLEAR_CURRENT_LINE_RIGHT"
    pollServer "https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/cctray.xml"
    pollServer "https://api.travis-ci.org/repos/Joe-Merten/Mpjoe/cc.xml"
    pollServer "https://circleci.com/gh/Joe-Merten/Mpjoe.cc.xml"
    pollServer "https://semaphoreapp.com/api/v1/projects/ed34e48b-8b31-4d78-a3cd-0730d586feaa/cc.xml?auth_token=hrY18iHUrtHtSyXC5Z3K&ccmenu=cc.xml"
}


if [ "$#" == "1" ]; then
    [ "$1" == "1" ] && DISPLAY_MODE="$1"
    [ "$1" == "2" ] && DISPLAY_MODE="$1"
fi

[ "$DISPLAY_MODE" == "1" ] && printf "$RESET_TERMINAL"
while true; do
    pollServers
    sleep 1
done
