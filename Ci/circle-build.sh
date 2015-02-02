#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       circle-build.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Kompilieren aller Subprojekte via circleci.com
########################################################################################################################

# circleci am 31.01.2015:
# - Linux box337 3.14.28-031428-generic #201501081937 SMP Thu Jan 8 19:39:13 UTC 2015 x86_64 x86_64 x86_64 GNU/Linux
# - Java 1.7.0_55 (Oracle, 64 Bit)
# - Maven 3.2.5
# - ANDROID_HOME = nicht gesetzt

source circle-env.sh

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

./circle-setup-android.sh

cd ../Swing
mvn -q -DskipTests=true clean install
cd ..

cd ../Android
mvn -q -DskipTests=true -Dandroid.sdk.path=$ANDROID_HOME clean install
cd ..
