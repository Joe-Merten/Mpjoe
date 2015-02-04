Continuous Integration
======================

Um zu sehen was am besten funktioniert, lasse ich zunächst erst mal 4 unterschiedliche CI Dienste auf meinen Code los.

- [Snap-ci](https://snap-ci.com/Joe-Merten/Mpjoe)
- [Drone.io](https://drone.io/github.com/Joe-Merten/Mpjoe)
- [Travis-ci](https://travis-ci.org/Joe-Merten/Mpjoe)
- [Circleci](https://circleci.com/gh/Joe-Merten/Mpjoe)
- [Semaphoreapp](https://semaphoreapp.com/joe-merten/mpjoe)
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
  - sowas wie "Running" oder "Pending" zeigt offenbar keiner an (doch, semaphoreapp tut das)


Snap-ci
-------
- Status:
  - baut die Swing und auch die Android App
- Sonstige Anmerkungen:
  - baut mit Android Sdk Build Tools 20.0.0
  - kann auch Android im Emuator und somit Tests gegen die Android Builds [siehe hier](https://docs.snap-ci.com/the-ci-environment/languages/android/)
    (habe ich jedoch noch nicht getestet)
  - vorerst mein Favorit
- Notification
  - cc.xml funktioniert von allen getesteten Hostern hier am besten
- Sonstige Anmerkungen:
  - bei snap-ci kann man mehrere Stages konfigurieren, z.B. für »Build« und »Test« (dachte ich zumindest)
    - allerdings wird bei jeder neuen Stage neu ausgecheckt und damit ist mein Buildoutput weg, somit kann ich das nicht in dieser Art und Weise benutzen
    - fernen, wenn ich mehrere Stages konfiguriert habe (z.B. Build und Test) dann bekomme ich dafür via cc.xml separate Einträge
      - Vorteil: Der Buildstatus kann dadurch feingranunarer visualisiert werden
      - Nachteil: Osx CCMenu & Windows CCTray müssen bei Änderung der Stages umkonfiguriert werden (Linux BuildNotify erkennt das hingegen selbst)


Drone.io
--------
- Status:
  - baut die Swing Applikation, aber (noch) nicht das Android Projekt
- Probleme:
  - hat nur Maven 3.0.4, aber für das simpligility android-maven-plugin wird mind. Maven 3.0.5 benötigt
    - deshalb baue ich via drone.io vorerst nur die Java Swing Applikation
  - Status Badge
    - zeigte mir mitunter "failing" obwohl der letzte Build erfolgreich war
      - manuelles anzeigen von https://drone.io/github.com/Joe-Merten/Mpjoe/status.png im Browser zeigte hingegen "passing" -> Problem mit Bitmap caching?!
- Notification
  - kein cc.xml Interface (im Gegensatz zu Snap, Travis und Circle)
- Sonstige Anmerkungen:
  - Unterscheidet nicht zwischen Build und Test
  - zählt folglich nicht zu meinen Favoriten


Travis-ci
---------
- Status:
  - baut die Swing und auch die Android App
- Sonstige Anmerkungen:
  - benötigt zur Konfiguration ein .travis.yml im Root meines Repository
  - macht checkout mittels `git clone --depth=50`, somit ist im Kompilat der Versionsstring falsch (ähnlich wie bei circleci)
- Notification
  - hat auf der Website eine [schöne Übersicht](http://docs.travis-ci.com/user/apps) über diverse Notification Clients
  - beim Start eines neuen Build wird leider via cc.xml der letzte Buildstatus nicht mehr geliefert (und lastBuildLabel steht schon auf dem neuen Build)
    - `activity="Sleeping" lastBuildStatus="Unknown" lastBuildLabel="46" lastBuildTime=""`
    - etwas später schaltet er dann auf Building, aber immernoch mit `lastBuildStatus="Unknown"`
    - `activity="Building" lastBuildStatus="Unknown" lastBuildLabel="46" lastBuildTime=""`
    - snap-ci und circleci machen das besser
    - Windows cctray bringt dies etwas durcheinander (falsche "Build Failed" Meldungen, siehe unten)
    - Linux BuildNotify Osx Ccmenu färben ihre Icons für Travis in diesem Fall grau


Circleci
--------
- Status:
  - baut die Swing und auch die Android App
- Notification
  - liefert via cc.xml keine `lastBuildTime`
- Sonstige Anmerkungen:
  - kann optional auch via `circle.yml` konfiguriert werden, [siehe hier](https://circleci.com/docs/configuration)
  - Zeigt via `gitversion` den Commit Count nicht korrekt an, somit ist im Kompilat der Versionsstring falsch (ähnlich wie bei travis-ci)


Semaphoreapp
------------
- Status:
  - baut die Swing App aber noch nicht für Android
- Notification
  - liefert im Status Badge auch »Pending«


Notification Clients
====================

- Eine Übersicht über diverse Notification Clients gibt es z.B. [hier](http://docs.travis-ci.com/user/apps)


Cruise Control Api (cc.xml)
---------------------------
Der CI Server `Cruise Control` definierte einst ein simples Api zur Abfrage des Buildstatus. Hierbei wird vom Buildserver eine Xml Datei mit den nötigen Informationen gezogen und ausgewertet. Der Informationsgehalt ist zwar sehr dürftig, aber das Api hat sich als defacto Standard etabliert und wird mittlerweile von diversen CI Servern und Notification Clients unterstützt.


### cc.xml Url's für Mpjoe

| Server       | Url                                                                                                                                |
|--------------|------------------------------------------------------------------------------------------------------------------------------------|
| snap-ci      | https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/cctray.xml                                                                      |
| travis-ci    | https://api.travis-ci.org/repos/Joe-Merten/Mpjoe/cc.xml                                                                            |
| circleci     | https://circleci.com/gh/Joe-Merten/Mpjoe.cc.xml                                                                                    |
| semaphoreapp | https://semaphoreapp.com/api/v1/projects/ed34e48b-8b31-4d78-a3cd-0730d586feaa/cc.xml?auth_token=hrY18iHUrtHtSyXC5Z3K&ccmenu=cc.xml |


### Linux BuildNotify
- [Website](https://bitbucket.org/Anay/buildnotify/wiki/Home)
- [Sourcecode](https://bitbucket.org/Anay/buildnotify/src)
- [Tutorial auf travis-ci.com](http://docs.travis-ci.com/user/cc-menu)
- erster Test
  - getestet mit Kubuntu 14.04 32 Bit
  - ab Ubuntu 14.10 in den offiziellen Repositories enthalten
  - vor 14.10 wird via ppa installiert
  - Trayicon schaltet während des Build auf grau, somit ist der letzte Buildstatus nicht sichtbar
    - aber nur bei travis-ci, da dieser ein fehlerhaftes cc.xml liefert
  - kleinstes Polling Intervall ist 60s


### Osx CCMenu
- [Website](http://ccmenu.org)
- [Appstore](https://itunes.apple.com/us/app/ccmenu/id603117688?mt=12&ign-mpt=uo%3D4)
- [Tutorial auf travis-ci.com](http://docs.travis-ci.com/user/cc-menu)
- erster Test
  - endlich mal ein Tool, dass auch als Status »Build in progress« (in Kombination mit dem letzten Buildergebnis) anzeigt
  - zeigt auch eine geschätzte Restzeit an
Probleme:
- Trayicon bleibt grün, obgleich eines Failure (snap-ci Test failed)
  - zumindest solange, wie noch andere Buildprozesse laufen
  - erst beim Aufklappen sieht man, dass einer der Buildprozesse rot ist
  - wenn alle Buildprozesse auf »Sleeping« stehen, dann wird das Trayicon rot


### Windows CCTray
- [Website](http://www.cruisecontrolnet.org/projects/cctray)
- [Setup.exe (Version 1.8.5)](http://sourceforge.net/projects/ccnet/files/CruiseControl.NET%20Releases/CruiseControl.NET%201.8.5/)
- erster Test
  - getestet mit CCTray Version 1.8.5 auf Windows XP 32 Bit
  - 😎 viele Einstellungsmöglichkeiten
  - Erstkonfiguration hat einen kleinen Fallstrick:
    - nach Installation & Start erscheint im System Tray das Icon
    - dort Rechtsklick → »Settings…« → Tab »Build Projects« → »Add…«
    - »Add Server« → »Supply a curtom Http Url« → dann Url (siehe oben) eingegeben und »Ok«
    - jetzt im zweigeteilten Fenster links den neu hinzugefügten »Buildserver« anklicken und (Wichtig!) in der rechten Liste das Projekt (z.B. »Joe-Merten/Mpjoe«) anklicken, dann »Ok«
    - im Settings Dialog sollte nun das Projekt stehen → »Ok«
  - zeigt auch eine geschätzte Restzeit an
  - wg. des fehlerhaften cc.xml von travis-ci wird hier ständig ein false negative »Recent checkins have broken the build« angezeigt


### Android Cruise Control Mobile
- [Playstore](https://play.google.com/store/apps/details?id=com.artech.ccsd.ccmobile)
- erster Test → hat auf meinem Samsung SM-P605 schlichtweg garnicht funktioniert


### iOS Cruise Control Mobile
- [Appstore](https://itunes.apple.com/us/app/cruise-control-mobile/id528029176)
- erster Test → ähnlich wie die Android App, meine Buildserver lassen sich nicht in die App hineinkonfigurieren
- Vermutung: Möglicherweise läuft Cruise Control Mobile nur mit echten Cruise Control im Lan.


Spezielle Clients für travis-ci
-------------------------------

### Android Mantis CI
- [Website](http://floydpink.github.io/Mantis-CI)
- [Playstore](https://play.google.com/store/apps/details?id=com.floydpink.android.travisci)
- [Sourcecode](https://github.com/floydpink/Mantis-CI)
- erster Test
  - arbeitet offenbar nur mit travis-ci
  - zeigte mir eine Liste irgendwelcher Repositories / Builds an, aber meins konnte ich nicht finden


### Android Travis Jr.
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


### Android Comrade Travis
- [Playstore](https://play.google.com/store/apps/details?id=com.perone.comradetravis)
- erster Test
  - arbeitet offenbar nur mit travis-ci
  - bei `Search Repository` gibt man `Mpjoe` oder `Joe-Merten` ein
  - noch schlichter als Travis Jr.
  - View Build → Crash auf meinem Samsung SM-P605


Weitere Clients
---------------

### Android Siren of Shame
- [Website](http://sirenofshame.com)
- [Playstore](https://play.google.com/store/apps/details?id=com.automatedarchitecture.sirenofshame)
- erster Test
  - unterstützt 8 verschiedene Buildserver ([siehe hier](http://sirenofshame.com/BuildMonitor)]; Bamboo, Buildbot, CruiseControl, Jenkins, Hudson, TeamCity, Travis, MS irgendwas)
  - aber … vieles funktioniert offenbar nur, wenn man eine Desktop App installiert und damit ein `SoS` Account einrichtet
    - die Desktop App gibt's aber nur für Windoof (erfordert .net 4.5)
    - [Sourcecode der Desktop App](https://github.com/automatedarchitecture/sirenofshame)
    - 😎 immerhin kann man dort dann via Usb eine [Sirene](http://sirenofshame.com/Products) anschliessen
  - Account kann man auch ohne Installation des Windows App einrichten und zumindest ein travis-ci anbinden, dessen Status das in der Android App sichtbar ist
- [hier](http://sirenofshame.blogspot.de) hat jemand die Sirene an einen Raspberry Pi angesteckert
