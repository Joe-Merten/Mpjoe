package de.jme.mpjoe.swing;

// TODO: Video angucken: https://www.youtube.com/watch?v=orMgNh0o38A
//                       https://www.youtube.com/watch?v=uRK6MHlBi-0

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.io.IOException;

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



public class MpjPlayerVlc extends MpjPlayer implements AutoCloseable {

    static boolean initialized = false;

    public MpjPlayerVlc(String name) throws IOException {
        super(name);

        synchronized (MpjPlayerVlc.class) {
            if (!initialized) {
                init();
                initialized = true;
            }
        }

        start();
    }

    @Override public void run() {
        try {
            while (true) {
                MpjTrack track = getTrack();
                if (track != null) {
                    try {
                        JPanel parentPanel = (JPanel)getGuiParent();
                        Canvas canvas = new Canvas();
                        canvas.setBackground(Color.BLUE);
                        canvas.setBounds(0, 0, 200, 200);
                        canvas.setVisible(true);
                        parentPanel.setLayout(new BorderLayout());
                        parentPanel.add(canvas, BorderLayout.CENTER);
                        parentPanel.setVisible(true);

                        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
                        EmbeddedMediaPlayer mediaPlayer  = mediaPlayerFactory.newEmbeddedMediaPlayer();
                        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
                        mediaPlayer.setVideoSurface(videoSurface);

                        // Notwendig zur Wiedergabe von Youtube Videos, siehe auch: http://stackoverflow.com/questions/15829583/playing-youtube-videos-with-vlcj-not-working-anymore
                        mediaPlayer.setPlaySubItems(true);

                        //frame.setLocation(100, 100);
                        //frame.setSize(1050, 600);
                        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        //frame.setVisible(true);

                        mediaPlayer.setMarqueeText("Tralala");
                        System.out.println("Start Playback");
                        String nam = track.getUri().toString();
                        if (nam.startsWith("file:")) {
                            // vlcj verträgt offenbar kein "file:/Blubber%20Bla.mp3"
                            nam = track.getUri().getPath();
                            if (SystemInfo.getMachineType() == MachineType.PcWindows) {
                                // Unter Windows liefert mir getPath() zuweilen sowas wie "/D:/Dir/File.ext", also ein störendes "/" am Stringanfang!?!
                                if (nam.length() >= 3 && nam.charAt(0) == '/' && nam.charAt(2) == ':')
                                    nam = nam.substring(1);
                                // Desweiteren verweigert vlcj unter Windows die Wiedergabe, wenn als Pfadtrenner "/" verwendet wird.
                                nam = nam.replace('/', '\\');
                            }
                        }

                        //mediaPlayer.prepareMedia(nam);
                        mediaPlayer.startMedia(nam);
                        System.out.println("Playback started (" + nam + ", name = " + track.getName() + ")");
                        setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_START);
                        System.out.println("Playback started event sent");

                        //mediaPlayer.start();

                        long t0 = System.nanoTime();
                        int i = 0;
                        while (true) { // Warten bis der Clip zu Ende ist
                            //int trackPos = mediaPlayer.get
                            long t1 = System.nanoTime();
                            if (t1 - t0 >= 1000 * 1000000) { // Jede Sekunde ein Progress-Event
                                t0 = t1;
                                setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_PROGRESS);
                                i++;
                                if (i >= 10) { // TODO: Hack erst mal
                                    ejectTrack();
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                    break;
                                }
                            }
                            Thread.sleep(100);
                        }
                        // close() rufen wir später, damit uns das hier keine Latenz zwischen den Tracks bringt.
                        setPlayerStateWithEvent(PlayerState.IDLE, PlayerEvent.TRACK_END);
                    } catch (Throwable e) {
                        ejectTrack(); // TODO: Hack erst mal
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

    private void init() throws IOException {
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
