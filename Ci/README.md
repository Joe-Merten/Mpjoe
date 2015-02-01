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


Notification Clients
====================

- Eine Übersicht über diverse Notification Clients gibt es z.B. [hier](http://docs.travis-ci.com/user/apps)


Android Mantis CI
-----------------
- [Website](http://floydpink.github.io/Mantis-CI)
- [Playstore](https://play.google.com/store/apps/details?id=com.floydpink.android.travisci)
- [Sourcecode](https://github.com/floydpink/Mantis-CI)
- Erster Test
  - Arbeitet offenbar nur mit travis-ci
  - Zeigte mir eine Liste irgendwelcher Repositories / Builds an, aber meins konnte ich nicht finden


Android Travis Jr.
------------------
- [Website](http://sahan.me/Travis-Jr)
- [Playstore](https://play.google.com/store/apps/details?id=com.lonepulse.travisjr)
- [Sourcecode](https://github.com/sahan/Travis-Jr)
  - Interessant: Projektsetup ist ebenfalls Android + Maven + Eclipse
- Erster Test
  - Arbeitet offenbar nur mit travis-ci
  - Schlicht und einfach, tut aber halbwegs was es soll
  - Listet alle Travis Builds auf und zeigt mir auch die Logs
    - jedoch nicht mit dem Folding das der Travis Webclient bietet
    - Farben werden nicht dargestellt (statt dessen `[21;1m` et cetera)
  - kein Status Badge in der Display Headline


Android Comrade Travis
----------------------
- [Playstore](https://play.google.com/store/apps/details?id=com.perone.comradetravis)
- Erster Test
  - Arbeitet offenbar nur mit travis-ci
  - Bei `Search Repository` gibt man `Mpjoe' oder `Joe-Merten` ein
- Erster Test
  - Arbeitet offenbar nur mit travis-ci
  - Noch schlichter als Travis Jr.
  - View Build → Crash auf meinem Samsung SM-P605


Android Siren of Shame
----------------------
- [Website](http://sirenofshame.com)
- [Playstore](https://play.google.com/store/apps/details?id=com.automatedarchitecture.sirenofshame)
- Erster Test
  - Unterstützt 8 verschiedene Buildserver ([siehe hier](http://sirenofshame.com/BuildMonitor)])
  - Aber … funktioniert offenbar nur, wenn man eine Desktop App installiert und damit ein `SoS` Account einrichtet
    - die Desktop App gibt's aber nur für Windoof
    - [Sourcecode der Desktop App](https://github.com/automatedarchitecture/sirenofshame)
    - 😎 immerhin kann man dort dann via Usb eine [Sirene](http://sirenofshame.com/Products) anschliessen
- [hier](http://sirenofshame.blogspot.de) hat jemand die Sirene an einen Raspberry Pi angesteckert


Linux BuildNotify
-----------------
- [Website](https://bitbucket.org/Anay/buildnotify/wiki/Home)
- [Sourcecode](https://bitbucket.org/Anay/buildnotify/src)
- [Tutorial auf travis-ci.com](http://docs.travis-ci.com/user/cc-menu)
- Erster Test
  - Ab Ubuntu 14.10 in den offiziellen Repositories enthalten
  - Vor 14.10 wird via ppa installiert
  - Zur Anbindung an travis-ci wird als Server Url `https://api.travis-ci.org/repos/Joe-Merten/Mpjoe/cc.xml` angegeben


Osx CCMenu
----------
- [Website](http://ccmenu.org)
- [Appstore](https://itunes.apple.com/us/app/ccmenu/id603117688?mt=12&ign-mpt=uo%3D4)
- [Tutorial auf travis-ci.com](http://docs.travis-ci.com/user/cc-menu)
- Erster Test
  - Zur Anbindung an travis-ci wird als Server Url `https://api.travis-ci.org/repos/Joe-Merten/Mpjoe/cc.xml` angegeben


Windows CCTray
--------------
- TODO
