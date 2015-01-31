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
# - Linux drone-dce685b379 3.2.0-23-virtual #36-Ubuntu SMP Tue Apr 10 22:29:03 UTC 2012 x86_64 x86_64 x86_64 GNU/Linux
# - Java 1.7.0_25 (OpenJdk 64 Bit)
# - Maven 3.0.4
echo "===== git rev ====="
echo "git rev = $(../Common/make/gitversion)"
echo "===== linux version ====="
uname -a
echo "===== java version ====="
java -version
echo "===== maven version ====="
mvn --version
echo "===== android sdk ====="
echo "ANDROID_HOME = $ANDROID_HOME"
echo "========================="


cd ../Swing
mvn install -q -DskipTests=true
mvn test

# Android bauen geht mit drone.io noch nicht, weil wg. dem simpligility android-maven-plugin mind. Maven 3.0.5 benötigt wird
# cd ../Android
# mvn install -q -DskipTests=true
# mvn test
# cd ..
