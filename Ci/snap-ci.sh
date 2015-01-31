#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       snap-ci.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Dieses Buildskript wird via snap-ci.com angeworfen
########################################################################################################################

# snap-ci am 31.01.2015:
# - Linux ct-10-0-130-14 2.6.32-042stab102.9 #1 SMP Fri Dec 19 20:34:40 MSK 2014 x86_64 x86_64 x86_64 GNU/Linux
# - Java 1.7.0_75 (Oracle, 64 Bit)
# - Maven 3.2.3
echo "===== linux version ====="
uname -a
echo "===== java version ====="
java -version
echo "===== maven version ====="
mvn --version
echo "========================="

# Android Sdk wird installiert nach $ANDROID_HOME="/var/go/android"
./snap-setup-android.sh

echo "=== android sdk ========="
ls -l /var/go/android
echo "=== android sdk build-tools ========="
ls -l /var/go/android/build-tools
echo "=== android sdk platforms ========="
ls -l /var/go/android/platforms
echo "=== android platform-tools ========"
ls -l /var/go/android/platform-tools
echo "========================="

./snap-build.sh
./snap-test.sh
