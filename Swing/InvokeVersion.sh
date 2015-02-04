#!/bin/bash

########################################################################################################################
# Generierung des Software Versionsstrings für den Mpjoe Java Client
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       InvokeVersion.sh
# \creation   2014-01-08, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Dieses Skript ist in das Maven-Skript "pom.xml" eingebunden und versorgt dieses bei jedem Build mit dem aktuellen
# Versionsstring.
# Der generierte Versionsstring hat dann die Form: "Mpjoe Java Client V1 2013-08-12 16:33:37, git rev 21:808a..."
#-----------------------------------------------------------------------------------------------------------------------
# Siehe auch: http://stackoverflow.com/questions/690419/build-and-version-numbering-for-java-projects-ant-cvs-hudson
########################################################################################################################

declare VER_TITLE="Mpjoe Java Client"
declare VER_NUMBER="1"
declare VER_DATETIME=""
declare VER_SOURCE=""
declare VER_STRING=""
declare VER_DEVELOPER=""

# Ermilltung der Versionsinformationen zur Kompilierzeit (packen des jar Archivs)
if [ "$1" == "--make-version-string" ]; then
    # Git Informationen einholen.
    VER_SOURCE=", git rev $(../Tools/gitversion)"

    # Build Datum "YYYY-MM-DD" und Uhrzeit "HH:MM:SS" (UTC)
    VER_DATETIME="$(date -u +"%Y-%m-%d %H:%M:%S")"

    # Sowas wie VER_DEVELOPER ist (noch) nicht implementiert

    # SoftwareVersionString zusammensetzen
    VER_STRING="$VER_TITLE V$VER_NUMBER $VER_DATETIME$VER_SOURCE$VER_DEVELOPER"
fi

if [ "$VER_STRING" != "" ]; then
    echo "$VER_STRING"
else
    echo "$0: Unknown or missing option" 2>&1
    exit 1
fi
