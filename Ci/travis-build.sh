#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       travis-build.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
#
########################################################################################################################

# travis-ci am 31.01.2015:
# - Linux testing-worker-linux-ac3bc85a-1-13783-linux-7-49015226 2.6.32-042stab090.5 #1 SMP Sat Jun 21 00:15:09 MSK 2014 x86_64 x86_64 x86_64 GNU/Linux
# - Java 1.7.0_72 (Oracle, 64 Bit)
# - Maven 3.2.3
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


cd ../Swing
mvn install -q -DskipTests=true
#mvn test
