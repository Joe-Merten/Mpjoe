package de.jme.mpjoe.swing;

// TODO: Video angucken: https://www.youtube.com/watch?v=orMgNh0o38A
//                       https://www.youtube.com/watch?v=uRK6MHlBi-0

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import de.jme.mpj.MpjPlayer;
import de.jme.mpj.MpjPlayer.Delegate.MpjRunnable;
import de.jme.mpj.MpjPlaylistEntry;
import de.jme.mpj.MpjTrack;
import de.jme.mpj.MpjPlayer.Delegate.MpjAnswer;
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


public class MpjPlayerVlc implements MpjPlayer, AutoCloseable {

    static boolean initialized = false;

    Delegate            delegate;
    MyThread            thread;
    JPanel              panel;
    MediaPlayerFactory  mediaPlayerFactory;
    MediaPlayer         mediaPlayer;
    boolean             directPlayer = true;

    // für EmbeddedMediaPlayer
    Canvas              canvas;
    EmbeddedMediaPlayer embeddedMediaPlayer;
    CanvasVideoSurface  videoSurface;

    // für DirectMediaPlayer
    private final int   maxWidth  = 720;
    private final int   maxHeight = 480;
    DirectMediaPlayer   directMediaPlayer;
    BufferedImage       image;
    ImagePane           imagePane;


    public MpjPlayerVlc(String name, boolean directPlayer) throws IOException {
        this.directPlayer = directPlayer;
        delegate = new MpjPlayer.Delegate(this, name);
        thread = new MyThread(name);

        synchronized (MpjPlayerVlc.class) {
            if (!initialized) {
                init();
                initialized = true;
            }
        }

        if (directPlayer) {
            // Vlcj funktioniert auf Mac nicht ohne weiteres, da wird DirectMediaPlayer benötigt; siehe auch:
            // - http://stackoverflow.com/a/25651219/2880699
            // - https://github.com/caprica/vlcj/blob/vlcj-3.0.1/src/test/java/uk/co/caprica/vlcj/test/direct/DirectTestPlayer.java
            image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(maxWidth, maxHeight);
            image.setAccelerationPriority(1.0f); // 1.0 = Top Priority
        }

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setVisible(true);
        delegate.setGuiComponent(panel);

        mediaPlayerFactory = new MediaPlayerFactory();

        if (directPlayer) {
            imagePane = new ImagePane(image);
            imagePane.setSize(maxWidth, maxHeight);
            //imagePane.setMinimumSize(new Dimension(width, height));
            //imagePane.setPreferredSize(new Dimension(width, height));
            panel.add(imagePane, BorderLayout.CENTER);

            directMediaPlayer = mediaPlayerFactory.newDirectMediaPlayer(new DirectBufferFormatCallback(), new DirectRenderCallback());
            mediaPlayer = directMediaPlayer;
        } else {
            canvas = new Canvas();
            canvas.setVisible(true);
            panel.add(canvas, BorderLayout.CENTER);

            embeddedMediaPlayer  = mediaPlayerFactory.newEmbeddedMediaPlayer();
            mediaPlayer = embeddedMediaPlayer;
            videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
            embeddedMediaPlayer.setVideoSurface(videoSurface);
        }

        // Notwendig zur Wiedergabe von Youtube Videos, siehe auch: http://stackoverflow.com/questions/15829583/playing-youtube-videos-with-vlcj-not-working-anymore
        mediaPlayer.setPlaySubItems(true);

        addVlcListeners();
        thread.start();
    }

    private void addVlcListeners() {
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventListener() {
            @Override public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
                System.out.println("Vlc event: mediaChanged media = " + media + " mrl = " + mrl);
            }

            @Override public void opening(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: opening");
            }

            @Override public void buffering(MediaPlayer mediaPlayer, float newCache) {
                System.out.println("Vlc event: buffering " + newCache);
            }

            @Override public void playing(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: playing");
            }

            @Override public void paused(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: paused");
            }

            @Override public void stopped(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: stopped");
            }

            @Override public void forward(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: forward");
            }

            @Override public void backward(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: backward");
            }

            @Override public void finished(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: finished");
            }

            @Override public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                System.out.println("Vlc event: timeChanged " + newTime);
            }

            @Override public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                System.out.println("Vlc event: positionChanged " + newPosition);
            }

            @Override public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
                System.out.println("Vlc event: seekableChanged " + newSeekable);
            }

            @Override public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
                System.out.println("Vlc event: pausableChanged " + newPausable);
            }

            @Override public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
                System.out.println("Vlc event: titleChanged " + newTitle);
            }

            @Override public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
                System.out.println("Vlc event: snapshotTaken " + filename);
            }

            @Override public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
                System.out.println("Vlc event: lengthChanged " + newLength);
            }

            @Override public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
                System.out.println("Vlc event: videoOutput " + newCount);
            }

            @Override public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
                System.out.println("Vlc event: scrambledChanged " + newScrambled);
            }

            @Override public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
                System.out.println("Vlc event: elementaryStreamAdded " + type + " " + id);
            }

            @Override public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
                System.out.println("Vlc event: elementaryStreamDeleted " + type + " " + id);
            }

            @Override public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
                System.out.println("Vlc event: elementaryStreamSelected " + type + " " + id);
            }

            @Override public void error(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: error!!!");
            }

            @Override public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
                System.out.println("Vlc event: mediaMetaChanged " + metaType);
            }

            @Override public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
                System.out.println("Vlc event: mediaSubItemAdded");
            }

            @Override public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
                System.out.println("Vlc event: mediaDurationChanged " + newDuration);
            }

            @Override public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
                System.out.println("Vlc event: mediaParsedChanged " + newStatus);
            }

            @Override public void mediaFreed(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: mediaFreed");
            }

            @Override public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
                System.out.println("Vlc event: mediaStateChanged " + newState);
            }

            @Override public void newMedia(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: newMedia");
            }

            @Override public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
                System.out.println("Vlc event: subItemPlayed " + subItemIndex);
            }

            @Override public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
                System.out.println("Vlc event: subItemFinished" + subItemIndex);
            }

            @Override public void endOfSubItems(MediaPlayer mediaPlayer) {
                System.out.println("Vlc event: endOfSubItems");
            }
        });

    }

    private class MyThread extends Thread {
        public MyThread(String name) {
            super(name);
        }

        @Override public void run() {
            try {
                // TODO: hier auch auf die Events des Vlc Player reagieren
                while (true) {
                    // TODO: Das ist so wohl noch nicht ok!
                    if (mediaPlayer.isPlaying()) {
                        MpjPlayerVlc.this.delegate.dispatchCommand(1000);
                        if (mediaPlayer.isPlaying()) {
                            MpjPlayerVlc.this.delegate.setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_PROGRESS);
                        } else {
                            MpjPlayerVlc.this.delegate.setPlayerStateWithEvent(PlayerState.IDLE, PlayerEvent.TRACK_END);
                        }
                    } else {
                        MpjPlayerVlc.this.delegate.setPlayerState(PlayerState.IDLE);
                        MpjPlayerVlc.this.delegate.dispatchCommand(-1);
                    }
                }
            } catch (InterruptedException e) {
                // Das ist eine normale Thread-Beendigung
            }
        }
    }

    @Override public void close() throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            embeddedMediaPlayer = null;
            directMediaPlayer = null;
        }
        if (mediaPlayerFactory != null) {
            mediaPlayerFactory.release();
            mediaPlayerFactory = null;
        }
    }

    // Hier 3 Klassen für den DirectMediaPlayer
    @SuppressWarnings("serial")
    private final class ImagePane extends JPanel {
        private final BufferedImage image;
        //private final Font font = new Font("Sansserif", Font.BOLD, 36);

        public ImagePane(BufferedImage image) {
            this.image = image;
        }

        @Override public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(image, null, 0, 0);
            // You could draw on top of the image here...
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            //g2.setColor(Color.red);
            //g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
            //g2.fillRoundRect(100, 100, 100, 80, 32, 32);
            //g2.setComposite(AlphaComposite.SrcOver);
            //g2.setColor(Color.white);
            //g2.setFont(font);
            //g2.drawString("vlcj direct media player", 130, 150);
        }
    }

    private final class DirectRenderCallback extends RenderCallbackAdapter {
        public DirectRenderCallback() {
            super(((DataBufferInt) image.getRaster().getDataBuffer()).getData());
        }

        @Override public void onDisplay(DirectMediaPlayer mediaPlayer, int[] data) {
            // The image data could be manipulated here...
            /* RGB to GRAYScale conversion example */
            // for(int i=0; i < data.length; i++){
            // int argb = data[i];
            // int b = (argb & 0xFF);
            // int g = ((argb >> 8 ) & 0xFF);
            // int r = ((argb >> 16 ) & 0xFF);
            // int grey = (r + g + b + g) >> 2 ; //performance optimized - not real grey!
            // data[i] = (grey << 16) + (grey << 8) + grey;
            // }
            imagePane.repaint();
        }
    }

    // Offenbar 1 Callback je Videofile
    private final class DirectBufferFormatCallback implements BufferFormatCallback {
        @Override public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            //return new RV32BufferFormat(sourceWidth, sourceHeight);
            System.out.println("sourceWidth=" + sourceWidth + ", sourceHeight=" + sourceHeight);
            return new RV32BufferFormat(maxWidth, maxHeight);
        }
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

    @Override public void addListener(EventListner listener) {
        delegate.addListener(listener);
    }

    @Override public void removeListener(EventListner listener) {
        delegate.removeListener(listener);
    }


    @Override public String getErrorMessage() {
        return delegate.getErrorMessage();
    }

    @Override public PlayerState getPlayerState() {
        return delegate.getPlayerState();
    }

    @Override public String getPlayerStateString() {
        return delegate.getPlayerStateString();
    }

    @Override public String getName() {
        return delegate.getName();
    }

    @Override public Object getGuiComponent() {
        return delegate.getGuiComponent();
    }


    @Override public void setTrack(MpjTrack newTrack, MpjPlaylistEntry newPle) throws InterruptedException, MpjPlayerException {
        delegate.setTrack(newTrack, newPle);
    }


    @Override public void setTrack(MpjTrack newTrack) throws InterruptedException, MpjPlayerException {
        delegate.setTrack(newTrack);
    }

    @Override public void setTrack(MpjPlaylistEntry ple) throws InterruptedException, MpjPlayerException {
        delegate.setTrack(ple);
    }

    @Override public MpjTrack getTrack() {
        return delegate.getTrack();
    }

    @Override public MpjPlaylistEntry getPlaylistEntry() {
        return delegate.getPlaylistEntry();
    }


    @Override public void ejectTrack() throws InterruptedException, MpjPlayerException {
        delegate.invokeCommand(new MpjRunnable() { @Override public void run(MpjAnswer answer) throws MpjPlayerException {
            mediaPlayer.stop();
            delegate.setPlayerStateWithEvent(PlayerState.IDLE, PlayerEvent.TRACK_STOP);
        }});
    }

    @Override public void stopTrack() throws InterruptedException, MpjPlayerException {
        delegate.invokeCommand(new MpjRunnable() { @Override public void run(MpjAnswer answer) throws MpjPlayerException {
            mediaPlayer.stop();
            delegate.setPlayerStateWithEvent(PlayerState.IDLE, PlayerEvent.TRACK_STOP);
        }});
    }

    @Override public void playTrack() throws InterruptedException, MpjPlayerException {
        delegate.invokeCommand(new MpjRunnable() { @Override public void run(MpjAnswer answer) throws MpjPlayerException {
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
                //if (!mediaPlayer.isPlayable()) // Hmm, der liefert mir bei allem false?!
                //    throw new MpjPlayerException("Could not play this \"" + track.getUri() + "\"");
                System.out.println("Starting (" + nam + ")");

                // Vlcj MediaPlayer: play() startet die Wiedergabe asynchron, start() hingegen blockiert bis wirklich gestartet wurde
                //mediaPlayer.play();
                if (!mediaPlayer.start()) {
                    String msg = "Failed to start " + track.getUri() + "\"";
                    delegate.setError(msg);
                    delegate.setPlayerStateWithEvent(PlayerState.ERROR, PlayerEvent.STATE_CHANGED);
                    throw new MpjPlayerException(msg);
                }
                System.out.println("Playback started (" + nam + ", name = " + track.getName() + ")");
                delegate.setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_START);
                //System.out.println("Playback started event sent");
            }
        }});
    }

    @Override public void pauseTrack() throws InterruptedException, MpjPlayerException {
        // TODO
    }

    @Override public void resumeTrack() throws InterruptedException, MpjPlayerException {
        // TODO
    }

}
