#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       drone-io.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Dieses Buildskript wird via drone.io angeworfen
########################################################################################################################

# drone.io am 31.01.2015:
# - Ubuntu TODO
# - OpenJdk 1.7.0_25
# - Maven 3.0.4
echo "===== linux version ====="
uname -a
echo "===== java version ====="
java -version
echo "===== maven version ====="
mvn --version
echo "========================="


cd ../Swing
mvn install -q -DskipTests=true
mvn test

# Android bauen geht mit drone.io noch nicht, weil wg. dem simpligility android-maven-plugin mind. Maven 3.0.5 benötigt wird
# cd ../Android
# mvn install -q -DskipTests=true
# mvn test
# cd ..
