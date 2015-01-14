Joe’s Media Player & DJ’ing App
===============================

Swing application

Build & run
===========

    mvn -q clean install
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
- Läuft bei mir noch nicht auf Mac Osx
- spielt diverse Formate (auch ogg)
- flac auf Linux -> Crash
- unter Windows geht Video Output
- Video Output auf Linux noch nicht zum Laufen bekommen
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


