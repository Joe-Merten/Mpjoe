Joe’s Media Player & DJ’ing App
===============================

Java Swing application, very early alpha version!


Prerequisites
=============

All platforms
-------------
- java jdk 1.7 (or later)
- vlc 2.1.5 (or later)

Linux
-----
- sudo apt-get install git openjdk-7-jdk maven vlc
- git clone https://github.com/Joe-Merten/Mpjoe.git
- vlc must be at least 2.1.5
  - it's because of GnuTls issues in vlc 2.1.4
  - I succeed by adding [this ppa](https://launchpad.net/~djcj/+archive/ubuntu/vlc-stable) which updates my vlc to 2.2.0-rc2
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
- download & install vlc from https://www.videolan.org
- download & install java from https://www.java.com (e.g. jdk 8 from [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
- tested using Yosemite 10.10 (64 Bit), Sun Jdk 1.8.0_31

Windows
-------
- TODO


Build & run
===========

    git clone https://github.com/Joe-Merten/Mpjoe.git
    cd Mpjoe/Swing
    ./Update-Symlinks.sh
    mvn clean install
    java -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar


Playback Libraries
==================

javax.sound
-----------
- Eher nur eine Spielerei, weil funktioniert nur mit Wav Files

javax.media
-----------
- (Java Media Framework)
- Unter Windows läuft das bei mir noch nicht mit MP3
- Video Playback habe ich noch nicht zum Laufen bekommen
- Ansonsten scheint mp3 und wav zu funktionieren (sowie etwas midi und so)
- Aber kein ogg und kein flac
- wird wohl seit einiger Zeit nicht mehr weiter entwickelt, deshalb investiere ich hier auch nicht weiter

javafx
------
- siehe z.B. hier:
  - https://docs.oracle.com/javafx/2/swing/media-player.htm
  - http://docs.oracle.com/javafx/2/media/overview.htm
- jede javafx Applikation muss offenbar von javafx.application.Application abgeleitet sein? -> das würde mir Unbehagen bereiten
- gucke ich mir vielleicht später noch mal etwas genauer an
- kann auch Streams abspielen

vlcj
----
- spielt diverse Formate (auch ogg, flac, div. Video)
- bei mehreren parallelen Playern "out of process" erforderlich
  - Macht auch Sinn, damit beim Vlc Crash nicht gleich mein ganzer Player wegstürzt
  - http://capricasoftware.co.uk/legacy/projects/vlcj/out-of-process.html

Todo
----
- fmj → http://fmj-sf.net/index.php
- gstreamer → https://code.google.com/p/gstreamer-java
  - gstreamer findet man in diversen Open Source Projekten, z.B. auch bei Guayadeque
- javazoom
  - http://www.javazoom.net/jlgui/api.html
  - http://www.onjava.com/pub/a/onjava/2004/08/11/javasound-mp3.html
  - http://stackoverflow.com/a/22305518/2880699
  - Kleines Beispiel mit Sourcen: http://introcs.cs.princeton.edu/java/faq/mp3/MP3.java.html
  - Hier noch Sourcen mit fade-in/out -> http://stackoverflow.com/questions/14959566/java-error-when-trying-to-use-mp3plugin-for-playing-an-mp3-file/14959818#14959818
  - und noch eins: http://www.javazoom.net/jlgui/addons/PlayIT.java
- Tritonus → http://www.tritonus.org
- Jaco Mp3 Player → http://jacomp3player.sourceforge.net/index.html
  - offenbar sehr leichtgewichtig, kann vermutlich nur mp3, hat wohl keine Lautstärkenregelung, ...
- SampleDsp → http://www.tagtraum.com/sampledsp.html
- Vlc auf Osx lauffähig bekommen
  - Hier hat das einer angeblich mit 4-8 simultanen Videoplayern am laufen: http://stackoverflow.com/questions/11078586/vlcj-creating-multiple-video-panels
- JOrbis
  - http://stackoverflow.com/questions/244375/playing-small-sounds-in-java-game
- Mplayer
- http://www.opencore.net
  - siehe auch http://stackoverflow.com/questions/4343872/are-there-some-open-source-media-players-on-android
  - http://www.netmite.com/android/mydroid/frameworks/base/media/java/android/media/MediaPlayer.java
- Open Source Projekte, die ich mir noch näher ansehen sollte
  - siehe auch: http://alternativeto.net/software/spotify/?license=opensource&platform=linux
  - http://alternativeto.net/software/tomahawk-player/
    - wie verwenden wohl auch vlc
  - DeaDBeeF
    - Linux, Mac, Android
    - deadbeef.sourceforge.net
    - https://github.com/Alexey-Yakovenko/deadbeef
    - https://play.google.com/store/apps/details?id=org.deadbeef.android&hl=de
    - Zeigt Tooltips, wenn ich auf meinem SM-P605 mit den Pen über einen Button schwebe
    - Source ist offenbar Plain C
    - kompiliert offenbar auf drone.io
      - https://drone.io/github.com/Alexey-Yakovenko/deadbeef/latest
  - Guayadeque
    - Linux
    - mp3, ogg, flac, wma, mp4, ...
    - C++ und Gstreamer media framework
    - guayadeque.org
    - http://sourceforge.net/p/guayadeque/code/HEAD/tree/Trunk
    - aktuell ist 0.3.7, via apt-get kam auf Kubuntu 14.04 die Version 0.3.5
  - Tomahawk
    - ist jedoch C++
    - läuft auf Linux, Mac, Android, Windows
    - spielt lokale Musik & Streams (auch Spotify und Google und ein paar freie Streamingdienste, z.B. Last.fm)
    - u.a. mp3, ogg, flac
    - www.tomahawk-player.org
    - github.com/tomahawk-player/tomahawk
    - de.wikipedia.org/wiki/Tomahawk_(Medienplayer)
  - Eina
    - eina.sourceforge.net
    - Plain C, Gtk, GStreamer
  - Atraci
    - Linux, Mac, Windows
    - Kann wohl auch Video?
    - Verwendet offenbar ffmpeg
    - github.com/Atraci/Atraci
  - Clementine
    - Linux, Mac, Windows
    - Lokale Musik und Streaming (z.B. Spotify)
    - C++ und Qt
    - per Android App fernsteuerbar
    - www.clementine-player.org/de
    - github.com/clementine-player/Clementine
  - Rhythmbox
    - Linux
    - C
  - Amarok
    - Linux, Mac, Windows
    - C++ und Qt
  - Banshee
    - Linux, Mac, Windows
  - Miro
    - Linux, Mac, Windows
  - C* Music Player
    - Linux
  - Mp3blaster
    - Linux
  - wohl eher nicht betrachtenswert
    - Listen
      - Python
      - Entwicklung wurde 2010 eingestellt


