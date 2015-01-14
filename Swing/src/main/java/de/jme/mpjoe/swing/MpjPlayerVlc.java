package de.jme.mpjoe.swing;

// TODO: Video angucken: https://www.youtube.com/watch?v=orMgNh0o38A
//                       https://www.youtube.com/watch?v=uRK6MHlBi-0

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import de.jme.mpj.MpjPlayer;
import de.jme.mpj.MpjTrack;
import de.jme.toolbox.SystemInfo;
import de.jme.toolbox.SystemInfo.MachineType;

/**
 * Klasse zur Wiedergabe von Audiodateien im Mpjoe Java Swing Client unter Verwendung von Vlc
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


// TODO: Evtl. besser von JPanel ableiten?
public final class MpjPlayerVlc extends MpjPlayer implements AutoCloseable {

    static boolean initialized = false;

    //private EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    private BlockingQueue<Runnable> commandQueue = new ArrayBlockingQueue<Runnable>(100);

    Canvas              canvas;
    MediaPlayerFactory  mediaPlayerFactory;
    EmbeddedMediaPlayer mediaPlayer;
    CanvasVideoSurface  videoSurface;

    public MpjPlayerVlc(String name, JPanel parentPanel) throws IOException {
        super(name);
        setGuiParent(parentPanel);

        synchronized (MpjPlayerVlc.class) {
            if (!initialized) {
                init();
                initialized = true;
            }
        }

        canvas = new Canvas();
        //canvas.setBackground(Color.BLUE);
        canvas.setVisible(true);
        parentPanel.setLayout(new BorderLayout());
        parentPanel.add(canvas, BorderLayout.CENTER);
        parentPanel.setVisible(true);

        mediaPlayerFactory = new MediaPlayerFactory();
        mediaPlayer  = mediaPlayerFactory.newEmbeddedMediaPlayer();
        videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        mediaPlayer.setVideoSurface(videoSurface);

        // Notwendig zur Wiedergabe von Youtube Videos, siehe auch: http://stackoverflow.com/questions/15829583/playing-youtube-videos-with-vlcj-not-working-anymore
        mediaPlayer.setPlaySubItems(true);


        start();
    }

    public void start_Track() throws InterruptedException {
        invokeCommand(new Runnable() { @Override public void run() {
            MpjTrack track = getTrack();
            if (track != null) {
                System.out.println("Start Playback");
                String nam = track.getUri().toString();
                if (nam.startsWith("file:")) {
                    // vlcj verträgt offenbar kein "file:/Blubber%20Bla.mp3"
                    nam = track.getUri().getPath();
                    if (SystemInfo.isWindows()) {
                        // Unter Windows liefert mir getPath() zuweilen sowas wie "/D:/Dir/File.ext", also ein störendes "/" am Stringanfang!?!
                        if (nam.length() >= 3 && nam.charAt(0) == '/' && nam.charAt(2) == ':')
                            nam = nam.substring(1);
                        // Desweiteren verweigert vlcj unter Windows die Wiedergabe, wenn als Pfadtrenner "/" verwendet wird.
                        nam = nam.replace('/', '\\');
                    }
                }

                System.out.println("Preparing (" + nam + ")");
                mediaPlayer.prepareMedia(nam);
                //if (!mediaPlayer.isPlayable()) {
                //    throw new Exception("Could not play this");
                //}
                System.out.println("Starting (" + nam + ")");
                mediaPlayer.play();
                System.out.println("Playback started (" + nam + ", name = " + track.getName() + ")");
                setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_START);
                System.out.println("Playback started event sent");
            }
        }});
    }

    public void stop_Track() throws InterruptedException {
        invokeCommand(new Runnable() { @Override public void run() {
            mediaPlayer.stop();
        }});
    }

    private void invokeCommand(final Runnable func) throws InterruptedException {
        synchronized (this) {
            commandQueue.put(func);
        }
    }

    private void dispatchCommand(int timeout) throws InterruptedException {
        Runnable func = null;
        if (timeout < 0)
            func = commandQueue.take();
        else
            func = commandQueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (func != null)
            func.run();
    }

    @Override public void run() {
        try {
            // TODO: hier auch auf die Events des Vlc Player reagieren
            while (true) {
                if (mediaPlayer.isPlaying()) {
                    dispatchCommand(1000);
                    if (mediaPlayer.isPlaying()) {
                        setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_PROGRESS);
                    } else {
                        setPlayerStateWithEvent(PlayerState.IDLE, PlayerEvent.TRACK_END);
                    }
                } else {
                    setPlayerState(PlayerState.IDLE);
                    dispatchCommand(-1);
                }
            }
        } catch (InterruptedException e) {
            // Das ist eine normale Thread-Beendigung
        }
    }

    @Override public void close() throws IOException {
        // TODO ...
    }

    private void init() {
        // Siehe auch http://www.capricasoftware.co.uk/legacy/projects/vlcj/tutorial1.html

        // Unter Linux bekomme ich folgenden Fehler
        //   Exception in thread "AWT-EventQueue-0" java.lang.Error:
        //   There is an incompatible JNA native library installed on this system.
        //   To resolve this issue you may do one of the following:
        //    - remove or uninstall the offending library
        //    - set the system property jna.nosys=true
        //    - set jna.boot.library.path to include the path to the version of the
        //      jnidispatch library included with the JNA jar file you are using
        // Abhilfe:
        //   java -Djna.nosys=true -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar
        // Nicht funktioniert hat hingegen:
        //   java -Djna.boot.library.path=/usr/lib/jni -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar

        // Auf Osx läuft Vlcj bei mir noch nicht. Vielleicht mal versuchen:
        //     final String jnaLibraryPath = System.getProperty("jna.library.path");
        //     final StringBuilder newJnaLibraryPath = new StringBuilder(jnaLibraryPath != null ? (jnaLibraryPath + ":") : "");
        //     newJnaLibraryPath.append("/Applications/VLC.app/Contents/MacOS/lib");
        //     System.setProperty("jna.library.path", newJnaLibraryPath.toString());
        // Siehe hier: http://stackoverflow.com/questions/11078586/vlcj-creating-multiple-video-panels


        System.out.println("Initializing VlcJ");
        MachineType machineType = SystemInfo.getMachineType();

        switch (machineType) {
            case PcLinux: {
                //com.sun.jna.NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "/usr/lib");
                break;
            }
            case PcWindows: {
                String progFolder = System.getenv("ProgramFiles");
                //System.out.println("progFolder = \"" + progFolder + "\"");
                com.sun.jna.NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), progFolder + "/VideoLAN/VLC");
            }
            case PcOsx: {
                // Auf meinem Macbook landete der Vlc (also libvlc.dylib) an 2 Stellen:
                //   /Volumes/vlc-2.1.5/VLC.app/Contents/MacOS/include/vlc
                //   /Applications/VLC.app/Contents/MacOS/lib
                com.sun.jna.NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "/Applications/VLC.app/Contents/MacOS/lib");
                break;
            }
            case Android: {
                break;
            }
        }

        com.sun.jna.Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        System.out.println("VlcJ initialized");
    }

}
