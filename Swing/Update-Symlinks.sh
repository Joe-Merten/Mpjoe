#!/bin/bash -e
########################################################################################################################
# Anlegen der Symlinks
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       Update-Symlinks.sh
# \creation   2015-01-12, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Einige der hier verwendeten Sourcen stammen aus ../Common
# Im .gitignore sind passende Ausnahmen gesetzt, damit die Symlinks nicht (versehentlich) eingecheckt werden.
########################################################################################################################

declare MPJOE_COMMON_DIR="../Common"
declare FILES=()

FILES+=("src/main/java/de/jme/toolbox")
FILES+=("src/test/java/de/jme/toolbox")
FILES+=("src/main/java/de/jme/jsi")
FILES+=("src/main/java/de/jme/thrift")
FILES+=("src/main/java/de/jme/mpj")
FILES+=("src/main/resources/de/jme/mpj")


########################################################################################################################
# Fehlerbehandlung Hook
#-----------------------------------------------------------------------------------------------------------------------
# Da wir das Skript mit "bash -e" ausführen, führt jeder Befehls- oder Funktionsaufruf, der mit !=0 returniert zu einem
# Skriptabbruch, sofern der entsprechende Exitcode nicht Skriptseitig ausgewertet wird.
# Siehe auch http://wiki.bash-hackers.org/commands/builtin/set -e
# Mit dem OnError() stellen wir hier noch mal einen Fuss in die Tür um genau diesen Umstand (unerwartete Skriptbeendigung)
# sichtbar zu machen.
########################################################################################################################
function OnError() {
    local ESC=$'\e'
    local RED="${ESC}[0m${ESC}[31m${ESC}[1m"
    local NORMAL="${ESC}[0m"
    echo "${RED}Script error exception in line $1, exit code $2${NORMAL}" >&2
    trap SIGINT
    kill -INT 0
    exit 2
}
trap 'OnError $LINENO $?' ERR
# siehe auch http://stackoverflow.com/questions/64786/error-handling-in-bash
# Ohne "errtrace" wird mein OnError() nicht immer gerufen...
set -o pipefail  # trace ERR through pipes
set -o errtrace  # trace ERR through 'time command' and other functions
set -o nounset   ## set -u : exit the script if you try to use an uninitialised variable
set -o errexit   ## set -e : exit the script if any statement returns a non-true return value


########################################################################################################################
# Anlegen eines Symlink, sofern dieser noch nicht besteht
#-----------------------------------------------------------------------------------------------------------------------
# \in  $1  Dateiname, ggf. mit relativem Verzeichnis
#          Der relative Path muss gleichermassen für Stm Common und auch für Nrf Common passen
########################################################################################################################
function UpdateLink() {
    local file="$1"
    if ! test -L "${file}"; then
        # Verzeichnis für den Link ggf. anlegen
        mkdir -pv "$(dirname ${file})"
        # Das Linkziel muss relativ zu unserem Linknamen angegeben werden, dazu bedienen wir uns eines kleinen Pyton Skripts
        # siehe auch: http://stackoverflow.com/a/7305217/2880699
        local dots=$(python -c "import os.path; print os.path.relpath('.', '$(dirname ${file})')")
        ln -sv "${dots}/${MPJOE_COMMON_DIR}/${file}" "${file}" >&2
    fi
}

########################################################################################################################
# Aktualisierung
########################################################################################################################
function UpdateAllLinks() {
    local file
    for file in "${FILES[@]}"; do
        UpdateLink "$file"
    done
}

########################################################################################################################
# Entfernen aller Symlinks
########################################################################################################################
function RemoveAllLinks() {
    local file
    for file in "${FILES[@]}"; do
        if test -L "${file}"; then
            rm -v "${file}"
        fi
    done
}

########################################################################################################################
# Anzeigen aller derzeit existierenden Symlinks
########################################################################################################################
function ListAllExistingLinks() {
    ls -lR --color=always . | grep ^l --color=never
}

########################################################################################################################
# Help
########################################################################################################################
function ShowHelp() {
    echo "Usage: Update-Symlinks.sh [action]"
    scho "were action is one of:"
    echo "  help   - show this text"
    echo "  list   - list existing symlinks"
    echo "  clean  - remove symlinks"
    echo "  update - create / update symlinks (default action)"
}

########################################################################################################################
# Main
########################################################################################################################

declare ACTION="update"
[ "$#" != "0" ] && ACTION="$1"

case "$ACTION" in
    "-h"|"--help"|"help") ShowHelp;;
    "list"  ) ListAllExistingLinks;;
    "clean" ) RemoveAllLinks;;
    "update") UpdateAllLinks;;
    *) echo "Invalid action, try --help" >&2; exit 1;;
esac
