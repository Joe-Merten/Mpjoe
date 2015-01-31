Continuous Integration
======================

Um zu sehen was am besten funktioniert, lasse ich zunächst erst mal 4 unterschiedliche CI Dienste auf meinen Code los.

- [Snap-ci](https://snap-ci.com/Joe-Merten/Mpjoe)
- [Drone.io](https://drone.io/github.com/Joe-Merten/Mpjoe)
- [Travis-ci](https://travis-ci.org/Joe-Merten/Mpjoe)
- [Circleci](https://circleci.com/gh/Joe-Merten/Mpjoe)

Meine Vorstellungen, was CI tun sollte und welche Informationen möglichst schnell und kompakt zur Verfügung gestellt werden sollten.

Build:

- Kompilieren aller Projekte

Test:

- allgemeine Tests (evtl. auf dem Buildsystem)
- Tests auf dem Zielsystem
- Kommunikationstests zwischen den einzelnen Apps, mitunter auch zwischen mehreren Instanzen dieser

Notification:

- die Status Badges auf der README.md sind schon mal nett, ich hätte aber gerne eine noch Unterscheidungen zwischen
  - Last Build succeed / warnings / errors
  - Last Test succeed / warnings / errors
  - New Build / Test running
  - App für Desktop Rechner (Linux, Mac, Windows) mit kleinem farbigen Indikator im System Tray
  - App für Mobile Devices (Android, iOS) mit kleinem farbigen Indikator im der Display Headline
  - und das alles möglichst auch für jedes einzelne Subprojekt (mein kleiner Mcp Buildserver konnte all dies (außer Android & Apple Apps))


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
