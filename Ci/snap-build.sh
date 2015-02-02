#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       snap-build.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Kompilieren aller Subprojekte via snap-ci.com
########################################################################################################################

# snap-ci am 31.01.2015:
# - Linux ct-10-0-130-14 2.6.32-042stab102.9 #1 SMP Fri Dec 19 20:34:40 MSK 2014 x86_64 x86_64 x86_64 GNU/Linux
# - Java 1.7.0_75 (Oracle, 64 Bit)
# - Maven 3.2.3
# - ANDROID_HOME = /var/go/android

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

# Android Sdk wird installiert nach $ANDROID_HOME="/var/go/android"
./snap-setup-android.sh

echo "=== android sdk ========="
ls -l $ANDROID_HOME
echo "=== android sdk build-tools ========="
ls -l $ANDROID_HOME/build-tools
echo "=== android sdk platforms ========="
ls -l $ANDROID_HOME/platforms
echo "=== android platform-tools ========"
ls -l $ANDROID_HOME/platform-tools
echo "========================="


cd ../Swing
mvn -q -DskipTests=true clean install
cd ../Ci

cd ../Android
mvn -q -DskipTests=true -Dandroid.sdk.path=$ANDROID_HOME clean install
cd ../Ci
