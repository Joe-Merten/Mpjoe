#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       semaphore-build.sh
# \creation   2015-02-03, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Kompilieren aller Subprojekte via semaphoreapp.com
########################################################################################################################

# semaphoreapp am 03.02.2015:
# - Linux TODO
# - Java 1.7.0_55 (Oracle, 64 Bit)
# - Maven TODO
# - ANDROID_HOME = TODO

#source semaphore-env.sh

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

#./semaphore-setup-android.sh

cd ../Swing
mvn -q -DskipTests=true clean install
cd ../Ci

#cd ../Android
#mvn -q -DskipTests=true -Dandroid.sdk.path=$ANDROID_HOME clean install
## ls -l target
#cd ../Ci
