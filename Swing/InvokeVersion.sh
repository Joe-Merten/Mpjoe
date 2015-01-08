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
# Der generierte Versionsstring hat dann die Form: "Mpjoe Java Client V1 2013-08-12 16:33:37 svn-r201M"
#-----------------------------------------------------------------------------------------------------------------------
# Siehe auch: http://stackoverflow.com/questions/690419/build-and-version-numbering-for-java-projects-ant-cvs-hudson
########################################################################################################################

declare SVN_REV=""
declare SVN_MOD=""
declare SVN_INFO=""

declare VER_TITLE="Mpjoe Java Client"
declare VER_NUMBER="1"
declare VER_DATETIME=""
declare VER_SVN=""
declare VER_STRING=""
declare VER_DEVELOPER=""

# Ermilltung der Versionsinformationen zur Kompilierzeit (packen des jar Archivs)
if [ "$1" == "--make-version-string" ]; then
    # Svn Informationen einholen. Bei Fehler (z.B. kein Svn), dann Leerstring
    #VER_SVN="$(svnversion -n)"
    #[ "$VER_SVN" != "" ] && VER_SVN="svn-r$VER_SVN"
    VER_SVN="TODO"

    # Build Datum "YYYY-MM-DD" und Uhrzeit "HH:MM:SS" (UTC)
    VER_DATETIME="`date -u +"%Y-%m-%d %H:%M:%S"`"

    # Sowas wie VER_DEVELOPER ist (noch) nicht implementiert

    # SoftwareVersionString zusammensetzen
    VER_STRING="$VER_TITLE V$VER_NUMBER $VER_DATETIME $VER_SVN$VER_DEVELOPER"
fi

if [ "$VER_STRING" != "" ]; then
    echo "$VER_STRING"
else
	echo "$0: Unknown or missing option" 2>&1
	exit 1
fi
