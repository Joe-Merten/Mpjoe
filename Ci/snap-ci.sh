#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       snap-ci.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Wird von snap-ci.com aufgerufen
#
# Bei snap-ci gibt es zwar die Moglichkeit mehrere Stages zu konfigurieren (z.B. »Build« und »Test«), jedoch wird
# zwischen den Stages mein Buildoutput gelöscht und somit ist das für eine solche Aufteilung offenbar unbrauchbar.
# Deshalb fasse ich hier Build & Test zusammen.
########################################################################################################################

./snap-build.sh
./snap-test.sh
