#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       semaphore-test.sh
# \creation   2015-02-03, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Testdurchführung aller Subprojekte via semaphoreapp.com
########################################################################################################################

#source semaphore-env.sh

cd ../Swing
mvn -q surefire:test
java -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar --version
java -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar --help
cd ..

# TODO: Android Tests
