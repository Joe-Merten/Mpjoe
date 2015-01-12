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
- gstreamer → https://code.google.com/p/gstreamer-java/
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