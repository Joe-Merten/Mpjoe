Hello Flashlight Beispielprojekt
================================

Kopie aus dem [android-maven-plugin Repository](https://github.com/simpligility/android-maven-plugin)

Prerequisites
=============

- java jdk 1.7 (or later)
- Android Sdk
  - default installiert unter /opt/android/sdk
  - falls anderswo installiert, dann:
    - als Property mitgeben (z.B.: ` mvn -Dandroid.sdk.path=/home/user/android/sdk install`)
    - oder Symlink anlegen
    - evtl. geht's auch via `mavenrc`?


Build & run
===========

    git clone https://github.com/Joe-Merten/Mpjoe.git
    cd Mpjoe/HelloFlashlight
    mvn clean install android:deploy android:run


Original Readme
===============

HelloFlashlight is a simple sample application written by Manfred Moser <manfred@simpligility.com>

It was created to demonstrate the minimal pom.xml setup needed to use the android-maven-plugin
and is used as an example in the book Maven: The Complete Reference

See the book chapter for more details.
http://www.sonatype.com/books/mvnref-book/reference/public-book.html

The following steps were done for the project creation.

- setup of Java, Maven, Android SDK and Android artifacts
- created project with Android SDK using Eclipse tool (new android project...)
- some hacking to have a little flashlight application with buttons to change color
- added minimal pom.xml file
- built application with running emulator:

mvn clean install android:deploy

- if an avd with the name 16 with platform 1.6 or higher exists the emulator can be started with

mvn android:emulator-start

- test the application on emulator (or device) by starting it and pressing the buttons to change the flashlight color
