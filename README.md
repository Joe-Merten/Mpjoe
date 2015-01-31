Joe’s Media Player & DJ’ing App
===============================

[![Build Status](https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/build_image)](https://snap-ci.com/Joe-Merten/Mpjoe)
[![Build Status](https://drone.io/github.com/Joe-Merten/Mpjoe/status.png)](https://drone.io/github.com/Joe-Merten/Mpjoe)
[![Build Status](https://travis-ci.org/Joe-Merten/Mpjoe.svg?branch=master)](https://travis-ci.org/Joe-Merten/Mpjoe)
[![Build Status](https://circleci.com/gh/Joe-Merten/Mpjoe.svg?style=shield)](https://circleci.com/gh/Joe-Merten/Mpjoe)


Allgemeines zur Entwicklung
===========================
- zum bauen verwende ich Maven (für alle Unterprojekte)
- bevorzugte IDE ist Eclipse
- durch Verwendung von Maven können aber alle Projekte auch von Kommandozeile kompiliert werden
- Java 1.7
- Android Api Level 19 (wg. Java 1.7, also mind. Android 4.4 erforderlich)


Continuous Integration
======================

Um zu sehen was am besten funktioniert, lasse ich zunächst erst mal 4 unterschiedliche CI Dienste auf meinen Code los.

- [Snap-ci](https://snap-ci.com/Joe-Merten/Mpjoe)
- [Drone.io](https://drone.io/github.com/Joe-Merten/Mpjoe)
- [Travis-ci](https://travis-ci.org/Joe-Merten/Mpjoe)
- [Circleci](https://circleci.com/gh/Joe-Merten/Mpjoe)


Allgemeines
-----------
- Status Badge
  - Sowas wie "Running" oder "Pending" zeigt offenbar keiner an


Snap-ci
-------
- Status:
  - Baut die Swing und auch die Android App
- Sonstige Anmerkungen:
  - Baut mit Android Sdk Build Tools 20.0.0
  - Kann auch Android im Emuator und somit Tests gegen die Android Builds [siehe hier](https://docs.snap-ci.com/the-ci-environment/languages/android/)
    (habe ich jedoch noch nicht getestet)


Drone.io
--------
- Status:
  - Baut die Swing Applikation, aber (noch) nicht das Android Projekt
- Probleme:
  - hat nur Maven 3.0.4, aber für das simpligility android-maven-plugin wird mind. Maven 3.0.5 benötigt
    - deshalb baue ich via drone.io vorerst nur die Java Swing Applikation
  - Status Badge
    - zeigte mir mitunter "failing" obwohl der letzte Build erfolgreich war
      - manuelles anzeigen von https://drone.io/github.com/Joe-Merten/Mpjoe/status.png im Browser zeigte hingegen "passing" -> Problem mit Bitmap caching?!


Travis-ci
---------
- Status:
  - TODO
- Sonstige Anmerkungen:
  - benötigt zur Konfiguration ein .travis.yml im Root meines Repository


Circleci
--------
- Status:
  - Baut die Swing Applikation, aber noch nicht das Android Projekt
  - TODO: Gucken ob / wie man mit circleci für Android bauen kann, siehe Ci/circle-build.sh
- Sonstige Anmerkungen:
  - Zeigt via `gitversion` den Commit Count nicht korrekt an, somit ist im Kompilat der Versionsstring falsch
