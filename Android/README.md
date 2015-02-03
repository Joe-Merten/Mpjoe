Joe’s Media Player & DJ’ing App
===============================

Android application, very very early alpha version!


Build & run
===========

Prerequisites see [../README.md](../README.md), then:

    cd Mpjoe/Android
    mvn clean install android:deploy android:run


Status
======
* zunächst nur ein Projectsetup, basierend auf dem HelloFlashlight Beispielcode von [android-maven-plugin Repository](https://github.com/simpligility/android-maven-plugin)
* build & run von Kommandozeile geht:  `mvn clean install android:deploy android:run`
* Siehe auch:
  * Homepage http://www.simpligility.com
  * https://groups.google.com/forum/#!forum/maven-android-developers
* Eclipse Integration funktioniert aber noch nicht richtig
* komisch:
  * das android-maven-plugin gibt es ein mal von `com.jayway.maven.plugins.android.generation2` sowie auch von `com.simpligility.maven.plugins`
  * ich verwende vorerst das von `simpligility`, da ich von denen auch den HelloFlashlight Beispielcode habe
  * wenn man in Eclipse mit m2e-android ein neues Projekt anlegt, dann steht im `pom.xml` jedoch `jayway`
  * welche der beiden Versionen aktueller / stabiler / besser gepflegt ist ... ?
  * [simpligility](http://mvnrepository.com/artifact/com.jayway.maven.plugins.android.generation2/android-maven-plugin) war aktuell auf Version 4.1.0 (zum Zeitpunkt meiner Recherche)
  * [jayway](http://mvnrepository.com/artifact/com.simpligility.maven.plugins/android-maven-plugin) hingegen auf Version 4.0.0-rc2
  * simpligility scheint ein Fork von jayway zu sein?
* TODO: [das hier](http://books.sonatype.com/mvnref-book/reference/android-dev.html) lesen

Eclipse Integration
-------------------
* Maven Build mittels Run Config `Mpjoe-Android Maven.launch` funktioniert immerhin
  * ja, eine Run Config und keine External Tools Config (wie aktuell bei Mpjoe-Swing); weil das irgendwo so empfohlen wurde
* bzgl. der momentan angelegten `.project`, `.classpath` und der beiden Launch-Configs in `.settings` bin ich mir nicht sicher ob das so alles stimmt; z.T. habe ich das selbst gemacht, z.T. wurde es aber auch von diversen Plugins generiert → am besten noch mal alles neu machen
* Rechtsklick auf's Projekt → Maven → Update Maven Project (Alt+F5) bringt Fehlermeldungen:

      Errors occurred during the build.
      Errors running builder 'Android Package Builder' on project 'Mpjoe-Android'.
      Resource '/Mpjoe-Android/bin' does not exist.
      Resource '/Mpjoe-Android/bin' does not exist.

  * klar, der Output liegt ja auch nicht in `bin` sondern im Verzeichnis `target`

* Debug nach [dieser Anleitung](https://code.google.com/p/maven-android-plugin/wiki/Debug) (aus Oktober 2010) (habe ich nicht richtig hinbekommen)
  * maven Build & Updload via `mvn clean install android:deploy android:run` (bzw. mit meiner Launch Config `Mpjoe-Android maven.launch`)
  * Eclipse → Window → Open Perspective → DDMS
    * Anmerkung: Wenn ich DDMS von Kommandozeile starte `/opt/android/sdk/tools/ddms` dann heist es dort: "The standalone version of DDMS is deprecated. Please use Android Device Monitor (tools/monitor) instead."
    * Breakpoint setzen etc. hat funktioniert, allerdings hat der Debugger meine Sourcen nicht gefunden
  * siehe auch [hier](http://developer.android.com/tools/debugging/ddms.html)

* Hab' dann auch mal das Eclipse [m2e-android Plugin](http://rgladwell.github.io/m2e-android/) ausprobiert
  * via Marketplace kommt eine Version 1.2.1 die offenbar nicht mit Luna funktioniert (siehe [hier](https://github.com/rgladwell/m2e-android/issues/226)
  * installiert man das Plugin jedoch mittels `http://rgladwell.github.io/m2e-android/updates/master` (kein Browser Link, sondern für Eclipse Install new Software), dann kommt eine Version 1.2.2 die zumindest bei der Installation keine Fehlermeldungen bringt
  * Mit dem Plugin bekommt man u.a. eine Ergänzung zum m2e New Project Wizard, mit dem man neue Android Projekte anlegen und bestehende Projakte nach Maven konvertieren kann (allerdings basierend auf jayway und nicht simpligility)
  * allerdings liessen sich diese Projekte nicht auf Kommansozeile bauen (div. komische Fehlermeldungen) und Debug via Eclipse habe ich dann gar nicht mehr ausprobiert.

[Hier](http://www.tikalk.com/devops/android-eclipse-maven/) hat einer was bzgl. Android+Maven+Eclipse geschrieben - muss ich noch ausprobieren.


Hack für Debugging mit Eclipse
==============================

Statt (wie unten beschrieben) via Symlink habe ich nun entsprechende Eclipse Links angelegt. Damit kann man jetzt Debuggen ohne ständig Symlinks anlegen und löschen zu müssen.

Alter Stand
-----------

Mit folgendem Hack ist es mir letztlich doch gelungen, das Projekt via Eclipse zu debuggen:

* zunächst gaukeln wir dem Eclipse ein non-Maven Projekt vor:

        ln -s src/main/AndroidManifest.xml
        ln -s src/main/res
        ln -s r/de target/generated-sources/
        ln -sT target bin
        ln -sT target/generated-sources gen
        ln -sT Mpjoe-Android-0.0.1-SNAPSHOT.apk target/Mpjoe-Android.apk

* dann Rechtsklick auf das Projekt → Refresh
* ggf. noch die Meldungen der Problems View löschen (Manifest nicht gefunden ...)
* dann Rechtsklick auf das Projekt → Debug as → Android Application
* Soll das Projekt neu gebaut werden, müssen die Links erst mal wieder entfernt werden - Maven nörgelt sonst: »Found files or folders in non-standard locations in the project!«

        rm -f AndroidManifest.xml bin gen res


Weitere Todos
-------------
* Versionsinfo generieren (siehe Mpjoe-Swing)
* Log4j zufügen (siehe Mpjoe-Swing), aber mit Logcat Appender
* Junit & Hamcrest zufügen (siehe Mpjoe-Swing)
  * Achtung: Nicht alle Tests dürfen in `src/test` liegen, sondern nur die, die beim Build von Maven direkt auf dem Hostsystem ausgeführt werden können
  * Tests die auf dem Android Target ausgeführt werden sollen, muss man wohl nach src/main tun ...
