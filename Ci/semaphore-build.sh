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
# - Linux aaf617fb-1e28-4e9c-8f31-0623bf3c7d34 3.13.0-29-generic #53-Ubuntu SMP Wed Jun 4 21:00:20 UTC 2014 x86_64 x86_64 x86_64 GNU/Linux
# - Java 1.7.0_65 (OpenJdk, 64 Bit)
# - Maven 3.0.5
# - ANDROID_HOME = nicht gesetzt

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

echo "========== Build Mpjoe Swing =========="
cd ../Swing
mvn -q -DskipTests=true clean install
cd ../Ci
echo "========== Build Mpjoe Swing finished =========="

#echo "========== Build Mpjoe Android =========="
#cd ../Android
#mvn -q -DskipTests=true -Dandroid.sdk.path=$ANDROID_HOME clean install
## ls -l target
#cd ../Ci
#echo "========== Build Mpjoe Android finished =========="
