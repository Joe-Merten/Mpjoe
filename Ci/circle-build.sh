#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       circle-build.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Dieses Buildskript wird via circleci.com angeworfen
########################################################################################################################

# circleci am 31.01.2015:
# - Linux box337 3.14.28-031428-generic #201501081937 SMP Thu Jan 8 19:39:13 UTC 2015 x86_64 x86_64 x86_64 GNU/Linux
# - Java 1.7.0_55 (Oracle, 64 Bit)
# - Maven 3.2.5
echo "===== linux version ====="
uname -a
echo "===== java version ====="
java -version
echo "===== maven version ====="
mvn --version
echo "========================="


cd ../Swing
mvn install -q -DskipTests=true
#mvn test

cd ../Android
mvn install -q -DskipTests=true
#mvn test
cd ..
