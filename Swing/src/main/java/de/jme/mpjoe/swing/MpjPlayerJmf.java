package de.jme.mpjoe.swing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;

import de.jme.mpj.MpjPlayer;
import de.jme.mpj.MpjTrack;

/**
 * Klasse zur Wiedergabe von Audiodateien im Mpjoe Java Swing Client unter Verwendung des Java Media Framework
 *
 * @author Joe Merten
                                                                   Linux  Video
 * "Testfiles/100_1404 - Limbo.MOV"                                 ja    nein?
 * "Testfiles/16 - Eric Clapton - Tears in Heaven.wma"             nein
 * "Testfiles/3ddemo.mod"                                          nein
 * "Testfiles/afile-547676518.flv"                                 nein
 * "Testfiles/American Football - Voll Verarscht.wmv"              nein
 * "Testfiles/America - The Last Unicorn.mp3"                       ja
 * "Testfiles/audio.rm"                                            nein
 * "Testfiles/bee gees - words video.mpeg"                          ja    nein?
 * "Testfiles/Beethovens Für Elise.rmi"                            nein
 * "Testfiles/Bill Clinton - Computergame - Wifehunter.mpe"         ja    nein?
 * "Testfiles/Brandenburg_Concerto_no._1_in_F_major_64.rm"         nein
 * "Testfiles/Brandenburg_Concerto_no._1_in_F_major_WMA_48.wma"    nein
 * "Testfiles/canyon.mid"                                          nein
 * "Testfiles/canyon.midi"                                         nein
 * "Testfiles/car.fsb"
 * "Testfiles/examples.fev"                                        nein
 * "Testfiles/flashvideo.flv"                                      nein
 * "Testfiles/Hello2.swf"                                          nein
 * "Testfiles/invtro94.s3m"                                        nein
 * "Testfiles/Karate Ulk - Cow Matrix.asf"                         nein
 * "Testfiles/Laura Merten - 1998-11-12 Hallo 2 Jahre alt.Wav"      ja
 * "Testfiles/Livin'_On_Borrowed_Time_zero.ogg"                    nein
 * "Testfiles/Marc-Julian Zech - Strafraum.FLV"                    nein
 * "Testfiles/Marc_Nelson_02_zero.ogg"
 * "Testfiles/MOTOR.WAV"                                            ja
 * "Testfiles/movie.swf"
 * "Testfiles/nibbles.swf"
 * "Testfiles/other.fsb"
 * "Testfiles/Safri Dou - Played-A-Live.avi"                        ja    nein?
 * "Testfiles/sample.asf"
 * "Testfiles/seal.ogg"
 * "Testfiles/shakira.mov"                                         nein
 * "Testfiles/Tricks Tutorials - Aerial Tutorial (short).mpg"
 * "Testfiles/Tricks Tutorials - Backflip Tutorial (short).mpg"
 * "Testfiles/Tricks Tutorials - Doubleleg Tutorial (early).mpg"
 * "Testfiles/Tricks Tutorials - Doubleleg Tutorial (short).mpg"
 * "Testfiles/Tricks Tutorials - Finally Backwards.mpg"             ja    nein?
 * "Testfiles/videotest.rm"
 * "Testfiles/wi64.mid"
 * "Testfiles/zel3.xm"
 */



public class MpjPlayerJmf extends MpjPlayer implements AutoCloseable {

    static boolean mp3PluginInitialized = false;

    public MpjPlayerJmf(String name) {
        super(name);

        synchronized (MpjPlayerJmf.class) {
            if (!mp3PluginInitialized) {
                initMp3Plugin();
                mp3PluginInitialized = true;
            }
        }

        start();
    }

    @Override public void run() {
        try {
            while (true) {
                MpjTrack track = getTrack();
                if (track != null) {
                    Player mediaPlayer = null;
                    try {
                        mediaPlayer = Manager.createRealizedPlayer(track.getUri().toURL());
                        mediaPlayer.start();
                        setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_START);

                        long t0 = System.nanoTime();
                        int i = 0;
                        while (true) { // Warten bis der Clip zu Ende ist
                            //int trackPos = mediaPlayer.get
                            long t1 = System.nanoTime();
                            if (t1 - t0 >= 1000 * 1000000) { // Jede Sekunde ein Progress-Event
                                t0 = t1;
                                setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_PROGRESS);
                                i++;
                                if (i >= 10) { // Hack erst mal
                                    mediaPlayer.stop();
                                    break;
                                }
                            }
                            Thread.sleep(20);
                        }
                        // close() rufen wir später, damit uns das hier keine Latenz zwischen den Tracks bringt.
                        setPlayerStateWithEvent(PlayerState.IDLE, PlayerEvent.TRACK_END);
                    } catch (NoPlayerException | CannotRealizeException | IOException e) {
                        setError(e);
                    }
                } else {
                    //currentTrack = null;
                    //checkCleanup();
                    setPlayerState(PlayerState.IDLE);
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            // Das ist eine normale Thread-Beendigung
        }
    }

    @Override public void close() throws IOException {
        // TODO ...
    }

    private void initMp3Plugin() {
        {
            // http://stackoverflow.com/a/9460999/2880699
            System.out.println("Trying mp3plugin.jar from getResrouce");
            System.out.println("Working Directory = " + System.getProperty("user.dir"));


            // Siehe auch http://www.oracle.com/technetwork/java/javase/download-137625.html
            // Funktioniert bei mir unter Linux, aber leider nicht unter Windows.
            // Geht unter Windows auch nicht, wenn ich die mp3plugin.jar ins Programma/Java/libs/ext kopiere

            // mp3plugin.jar liegt in meinem distributierten jar
            try {
                URL url = getClass().getResource("/de/jme/mpjoe/swing/mp3plugin.jar");

                { // Direkt aus dem jar heraus geht offenbar nicht, also kopieren wir das Teil in eine temporäre Datei
                    File tempFile = File.createTempFile("mp3plugin", ".jar");
                    tempFile.deleteOnExit();
                    //System.out.println("TMP=" + tempFile.toString());
                    InputStream is = getClass().getResourceAsStream("/de/jme/mpjoe/swing/mp3plugin.jar");
                    Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    url = tempFile.toURI().toURL();
                    System.out.println("Tmp Url = " + url.toString());
                }

                java.lang.reflect.Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
                method.setAccessible(true);
                method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{url});
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
                e.printStackTrace();
            }/**/

            /*
            // Hier die alten Variante, wenn das mp3plugin.jar auf den Zielsystem als Datei vorliegt (im Working Directory)
            try {
                File file = new File("mp3plugin.jar");
                //File file = new File("src/main/java/de/jme/mpjoe/swing/mp3plugin.jar");
                java.lang.reflect.Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
                method.setAccessible(true);
                method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | MalformedURLException e) {
                e.printStackTrace();
            }/**/

        }

        // http://stackoverflow.com/questions/9890922/how-to-play-mp3-files-in-java
        // http://www.morgenstille.at/blog/how-to-play-a-mp3-file-in-java-simple-and-beautiful/
        {
            javax.media.Format input1 = new javax.media.format.AudioFormat(javax.media.format.AudioFormat.MPEGLAYER3);
            javax.media.Format input2 = new javax.media.format.AudioFormat(javax.media.format.AudioFormat.MPEG);
            javax.media.Format output = new javax.media.format.AudioFormat(javax.media.format.AudioFormat.LINEAR);
            javax.media.PlugInManager.addPlugIn(
                "com.sun.media.codec.audio.mp3.JavaDecoder",
                new javax.media.Format[]{input1, input2},
                new javax.media.Format[]{output},
                javax.media.PlugInManager.CODEC
            );
        }
    }

}
