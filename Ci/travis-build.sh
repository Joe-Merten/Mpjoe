#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       travis-build.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Kompilieren aller Subprojekte via travis-ci.org
########################################################################################################################

# travis-ci am 31.01.2015:
# - Linux testing-worker-linux-ac3bc85a-1-13783-linux-7-49015226 2.6.32-042stab090.5 #1 SMP Sat Jun 21 00:15:09 MSK 2014 x86_64 x86_64 x86_64 GNU/Linux
# - Java 1.7.0_72 (Oracle, 64 Bit)
# - Maven 3.2.3
# - ANDROID_HOME = /home/travis/build/Joe-Merten/Mpjoe/tmp/android-sdk-linux

echo "===== git rev ====="
echo "git rev = $(../Common/make/gitversion)"
git status
echo "===== linux version ====="
uname -a
echo "===== java version ====="
java -version
echo "===== maven version ====="
mvn --version
echo "===== android sdk ====="
echo "ANDROID_HOME = $ANDROID_HOME"
echo "========================="

# Installation des Android Sdk wird via travis.yml separat angeworfen, sollte hier also bereits erledigt sein

cd ../Swing
mvn -q -DskipTests=true clean install
cd ../Ci
