Joe’s Media Player & DJ’ing App
===============================

[![Build Status](https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/build_image)](https://snap-ci.com/Joe-Merten/Mpjoe)
[![Build Status](https://drone.io/github.com/Joe-Merten/Mpjoe/status.png)](https://drone.io/github.com/Joe-Merten/Mpjoe)
[![Build Status](https://travis-ci.org/Joe-Merten/Mpjoe.svg?branch=master)](https://travis-ci.org/Joe-Merten/Mpjoe)
[![Build Status](https://circleci.com/gh/Joe-Merten/Mpjoe.svg?style=shield)](https://circleci.com/gh/Joe-Merten/Mpjoe)


Allgemeines zur Entwicklung
===========================
- Zum bauen verwende ich Maven für alle Unterprojekte
- Bevorzugte IDE ist Eclipse
- Durch Verwendung von Maven können aber alle Projekte auch von Kommandozeile kompiliert werden
- Java 1.7
- Android Api Level 19 (wg. Java 1.7, also mind. Android 4.4 erforderlich)


Continuous Integration
======================

Um zu sehen was am besten funktioniert, lasse ich zunächst erst mal 4 unterschiedliche CI Dienste auf meinen Code los.

- [Snap-ci](https://snap-ci.com/Joe-Merten/Mpjoe)
- [Drone.io](https://drone.io/github.com/Joe-Merten/Mpjoe)
- [Travis.ci](https://travis-ci.org/Joe-Merten/Mpjoe)
- [Circleci](https://circleci.com/gh/Joe-Merten/Mpjoe)


Allgemeines
-----------
- Status Badge
  - Sowas wie "Running" oder "Pending" zeigt offenbar keiner an


Snap-ci
-------
- Status Badge
- Android
  - Baut mit Android Sdk Build Tools 20.0.0
  - Kann Android Tests mit (habe ich jedoch noch nicht getestet)


Drone.io
--------
- hat nur Maven 3.0.4, aber für das simpligility android-maven-plugin wird mind. Maven 3.0.5 benötigt
  - deshalb baue ich via drone.io vorerst nur die Java Swing Applikation


Travis-ci
---------
- benötigt zur Konfiguration ein .travis.yml im Root meines Repository
- TODO


Circleci
--------
- TODO
