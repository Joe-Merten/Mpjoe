Joe’s Media Player & DJ’ing App
===============================

| CI Server    | Status
|--------------|--------
| snap-ci      | [![Build Status](https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/build_image)](https://snap-ci.com/Joe-Merten/Mpjoe)
| drone.io     | [![Build Status](https://drone.io/github.com/Joe-Merten/Mpjoe/status.png)](https://drone.io/github.com/Joe-Merten/Mpjoe)
| travis-ci    | [![Build Status](https://travis-ci.org/Joe-Merten/Mpjoe.svg?branch=master)](https://travis-ci.org/Joe-Merten/Mpjoe)
| circleci     | [![Build Status](https://circleci.com/gh/Joe-Merten/Mpjoe.svg)](https://circleci.com/gh/Joe-Merten/Mpjoe)
| circleci     | [![Build Status](https://circleci.com/gh/Joe-Merten/Mpjoe.svg?style=shield)](https://circleci.com/gh/Joe-Merten/Mpjoe)
| semaphoreapp | [![Build Status](https://semaphoreapp.com/api/v1/projects/ed34e48b-8b31-4d78-a3cd-0730d586feaa/341075/badge.png)](https://semaphoreapp.com/joe-merten/mpjoe)
| semaphoreapp | [![Build Status](https://semaphoreapp.com/api/v1/projects/ed34e48b-8b31-4d78-a3cd-0730d586feaa/341075/shields_badge.svg)](https://semaphoreapp.com/joe-merten/mpjoe)


Allgemeines zur Entwicklung
===========================
- zum bauen verwende ich Maven (für alle Unterprojekte)
- bevorzugte IDE ist Eclipse, es kann aber auch jeder andere Editor (vim et cetera) verwendet werden
- durch Verwendung von Maven können alle Projekte auch von Kommandozeile kompiliert werden
- Java 1.7
- Android Api Level 19 (wg. Java 1.7, also mind. Android 4.4 erforderlich)
- CI Anbindung siehe [Ci/README.md](Ci/README.md)


Prerequisites
=============

TODO: Eclipse Setup auch beschreiben


All platforms
-------------
- java jdk 1.7 (or later)
- maven 3.0.5 (or later)
- vlc 2.1.5 (or later)
- android sdk 24.0.2 with build-tools 21.1.2 and api level 19


Linux
-----

        sudo apt-get install git openjdk-7-jdk maven vlc
        [ "$(uname -m)" == "x86_64" ] && sudo apt-get install lib32stdc++6 lib32z1
        git clone https://github.com/Joe-Merten/Mpjoe.git
        cd Mpjoe
        ./InstallAndroidSdk.sh

- vlc must be at least 2.1.5
  - it's because of GnuTls issues in vlc 2.1.4
  - I succeed by adding [this ppa](https://launchpad.net/~djcj/+archive/ubuntu/vlc-stable) which updates my vlc to 2.2.0-rc2
- the 32 bit libs were needed for using the android sdk on 64 bit hosts
- there might be some different ways how and where to install android sdk; I'd described one in the script mentioned above
- tested using Kubuntu 14.04 (32 Bit and 64 Bit), OpenJdk 1.7.0_65


OS X
----
- install [Homebrew](http://brew.sh)
- brew update
- brew install maven
- sudo echo "JAVA_HOME=$(/usr/libexec/java_home)" >>/etc/mavenrc
  - because of an maven issue, [see here](http://blog.tompawlak.org/maven-default-java-version-mac-osx) or [here](http://www.jayway.com/2013/03/08/configuring-maven-to-use-java-7-on-mac-os-x/)
  - of course, there are some different approched to fix this issue, e.g.
    - export JAVA_HOME=$(/usr/libexec/java_home) within your .bash_profile et cetera
- install android sdk, e.g.:
  - git clone https://github.com/Joe-Merten/Mpjoe.git
  - cd Mpjoe
  - ./InstallAndroidSdk.sh
- download & install vlc from https://www.videolan.org
- download & install java from https://www.java.com (e.g. jdk 8 from [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
- tested using Yosemite 10.10 (64 Bit), Sun Jdk 1.8.0_31


Windows
-------
- TODO


Build & run
===========

Swing app:

    cd Mpjoe/Swing
    mvn clean install
    java -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Android app:

    cd Mpjoe/Android
    mvn clean install android:deploy android:run
