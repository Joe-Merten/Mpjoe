#!/bin/bash -e
########################################################################################################################
# CI Server Buildskript
#-----------------------------------------------------------------------------------------------------------------------
# \project    Mpjoe
# \file       circle-test.sh
# \creation   2015-01-30, Joe Merten
#-----------------------------------------------------------------------------------------------------------------------
# Testdurchführung aller Subprojekte via circleci.com
########################################################################################################################

source circle-env.sh

cd ../Swing
mvn -q surefire:test
java -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar --version
java -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar --help
cd ..

# TODO: Android Tests
