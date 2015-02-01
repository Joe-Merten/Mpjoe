Continuous Integration
======================

Um zu sehen was am besten funktioniert, lasse ich zunächst erst mal 4 unterschiedliche CI Dienste auf meinen Code los.

- [Snap-ci](https://snap-ci.com/Joe-Merten/Mpjoe)
- [Drone.io](https://drone.io/github.com/Joe-Merten/Mpjoe)
- [Travis-ci](https://travis-ci.org/Joe-Merten/Mpjoe)
- [Circleci](https://circleci.com/gh/Joe-Merten/Mpjoe)
- [hier](en.wikipedia.org/wiki/Comparison_of_continuous_integration_software) gibt's noch reichlich mehr

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
- erster Test
  - arbeitet offenbar nur mit travis-ci
  - zeigte mir eine Liste irgendwelcher Repositories / Builds an, aber meins konnte ich nicht finden


Android Travis Jr.
------------------
- [Website](http://sahan.me/Travis-Jr)
- [Playstore](https://play.google.com/store/apps/details?id=com.lonepulse.travisjr)
- [Sourcecode](https://github.com/sahan/Travis-Jr)
  - Interessant: Projektsetup ist ebenfalls Android + Maven + Eclipse
- erster Test
  - arbeitet offenbar nur mit travis-ci
  - schlicht und einfach, tut aber halbwegs was es soll
  - listet alle Travis Builds auf und zeigt mir auch die Logs
    - jedoch nicht mit dem Folding das der Travis Webclient bietet
    - Farben werden nicht dargestellt (statt dessen `[21;1m` et cetera)
  - kein Status Badge in der Display Headline


Android Comrade Travis
----------------------
- [Playstore](https://play.google.com/store/apps/details?id=com.perone.comradetravis)
- erster Test
  - arbeitet offenbar nur mit travis-ci
  - bei `Search Repository` gibt man `Mpjoe` oder `Joe-Merten` ein
  - noch schlichter als Travis Jr.
  - View Build → Crash auf meinem Samsung SM-P605


Android Siren of Shame
----------------------
- [Website](http://sirenofshame.com)
- [Playstore](https://play.google.com/store/apps/details?id=com.automatedarchitecture.sirenofshame)
- erster Test
  - unterstützt 8 verschiedene Buildserver ([siehe hier](http://sirenofshame.com/BuildMonitor)])
  - aber … funktioniert offenbar nur, wenn man eine Desktop App installiert und damit ein `SoS` Account einrichtet
    - die Desktop App gibt's aber nur für Windoof
    - [Sourcecode der Desktop App](https://github.com/automatedarchitecture/sirenofshame)
    - 😎 immerhin kann man dort dann via Usb eine [Sirene](http://sirenofshame.com/Products) anschliessen
- [hier](http://sirenofshame.blogspot.de) hat jemand die Sirene an einen Raspberry Pi angesteckert


Android Cruise Control Mobile
-----------------------------
- [Playstore](https://play.google.com/store/apps/details?id=com.artech.ccsd.ccmobile)
- erster Test → hat auf meinem Samsung SM-P605 schlichtweg garnicht funktioniert


iOS Cruise Control Mobile
-------------------------
- [Appstore](https://itunes.apple.com/us/app/cruise-control-mobile/id528029176)
- erster Test → TODO


Linux BuildNotify
-----------------
- [Website](https://bitbucket.org/Anay/buildnotify/wiki/Home)
- [Sourcecode](https://bitbucket.org/Anay/buildnotify/src)
- [Tutorial auf travis-ci.com](http://docs.travis-ci.com/user/cc-menu)
- erster Test
  - getestet mit Kubuntu 14.04 32 Bit
  - ab Ubuntu 14.10 in den offiziellen Repositories enthalten
  - vor 14.10 wird via ppa installiert
  - zur Anbindung an snap-ci wird als Server Url `https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/cctray.xml` angegeben
  - zur Anbindung an travis-ci wird als Server Url `https://api.travis-ci.org/repos/Joe-Merten/Mpjoe/cc.xml` angegeben
  - zur Anbindung an circleci wird als Server Url `https://circleci.com/gh/Joe-Merten/Mpjoe.cc.xml` angegeben
  - Trayicon schaltet während des Build auf grau, somit ist der letzte Buildstatus nicht sichtbar
  - kleinstes Polling Intervall ist 60s


Osx CCMenu
----------
- [Website](http://ccmenu.org)
- [Appstore](https://itunes.apple.com/us/app/ccmenu/id603117688?mt=12&ign-mpt=uo%3D4)
- [Tutorial auf travis-ci.com](http://docs.travis-ci.com/user/cc-menu)
- erster Test
  - zur Anbindung an snap-ci wird als Server Url `https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/cctray.xml` angegeben
  - zur Anbindung an travis-ci wird als Server Url `https://api.travis-ci.org/repos/Joe-Merten/Mpjoe/cc.xml` angegeben
  - zur Anbindung an circleci wird als Server Url `https://circleci.com/gh/Joe-Merten/Mpjoe.cc.xml` angegeben
  - endlich mal ein Tool, dass auch als Status »Build in progress« (in Kombination mit dem letzten Buildergebnis) anzeigt
  - zeigt auch eine geschätzte Restzeit an


Windows CCTray
--------------
- [Website](http://www.cruisecontrolnet.org/projects/cctray)
- [Setup.exe (Version 1.8.5)](http://sourceforge.net/projects/ccnet/files/CruiseControl.NET%20Releases/CruiseControl.NET%201.8.5/)
- erster Test
  - getestet mit CCTray Version 1.8.5 auf Windows XP 32 Bit
  - 😎 viele Einstellungsmöglichkeiten
  - Erstkonfiguration hat einen kleinen Fallstrick:
    - nach Installation & Start erscheint im System Tray das Icon
    - dort Rechtsklick → »Settings…« → Tab »Build Projects« → »Add…«
    - »Add Server« → »Supply a curtom Http Url« → dann Serveradresse (siehe unten) eingegeben und »Ok«
      - zur Anbindung an snap-ci wird als Server Url `https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/cctray.xml` angegeben
      - Zur Anbindung an travis-ci wird als Server Url `https://api.travis-ci.org/repos/Joe-Merten/Mpjoe/cc.xml` angegeben
      - Zur Anbindung an circleci wird als Server Url `https://circleci.com/gh/Joe-Merten/Mpjoe.cc.xml` angegeben
    - jetzt im zweigeteilten Fenster links den neu hinzugefügten »Buildserver« anklicken und (Wichtig!) in der rechten Liste »Joe-Merten/Mpjoe« anklicken, dann »Ok«
    - im Settings Dialog sollte nun das Projekt stehen → »Ok«
  - zeigt auch eine geschätzte Restzeit an
  - zeigt mir aber auch ständig »Recent checkins have broken the build«, obwohl ich nur in einer Readme geändert habe (wenn der Build dann fertig ist, zeigt er »Recent checkins have fixed the build«)
