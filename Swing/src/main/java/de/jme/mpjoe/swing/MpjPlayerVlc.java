package de.jme.mpjoe.swing;

// TODO: Video angucken: https://www.youtube.com/watch?v=orMgNh0o38A
//                       https://www.youtube.com/watch?v=uRK6MHlBi-0
// vlcj Api: http://caprica.github.io/vlcj/javadoc/3.1.0

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.LibVlcFactory;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.AudioDevice;
import uk.co.caprica.vlcj.player.AudioOutput;
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
import de.jme.mpj.MpjPlayer.Delegate.MpjAnswer;
import de.jme.mpj.MpjPlayer.Delegate.MpjRunnable;
import de.jme.mpj.MpjPlaylistEntry;
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


public class MpjPlayerVlc implements MpjPlayer, AutoCloseable {

    static boolean initialized = false;

    Delegate            delegate;
    MyThread            thread;
    JPanel              panel;
    LibVlcFactory       libVlcFactory;
    LibVlc              libVlc;
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

        {
            System.out.println("Detected audio outputs:");
            final List<AudioOutput> audioOutputs = mediaPlayerFactory.getAudioOutputs();
            for (AudioOutput audioOutput : audioOutputs) {
                System.out.println("  name = \"" + audioOutput.getName() + "\", description = \"" + audioOutput.getDescription() + "\"");
                final List<AudioDevice> devices = audioOutput.getDevices();
                for (AudioDevice device : devices) {
                    System.out.println("    id = \"" + device.getDeviceId() + "\", longName = \"" + device.getLongName() + "\"");
                }
            }
        }


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

        /*{
            System.out.println("Available audio devices for this player:");
            final List<AudioDevice> devices = mediaPlayer.getAudioOutputDevices(); // gibt's wohl noch nicht in vlcj 3.1.0
            for (AudioDevice device : devices)
                System.out.println("    id = \"" + device.getDeviceId() + "\", longName = \"" + device.getLongName() + "\"");
        }*/


        // Notwendig zur Wiedergabe von Youtube Videos, siehe auch: http://stackoverflow.com/questions/15829583/playing-youtube-videos-with-vlcj-not-working-anymore
        mediaPlayer.setPlaySubItems(true);

        // final String[] standardMediaOptions = { "video-filter=logo", "Mpjoe-Icon-256.png", "logo-opacity=25" };
        // mediaPlayer.setStandardMediaOptions(standardMediaOptions);
        // mediaPlayer.setLogoFile("Mpjoe-Icon-256.png");
        // mediaPlayer.setLogoOpacity(25);
        // mediaPlayer.setLogoLocation(10, 10);
        // mediaPlayer.enableLogo(true);

        addVlcListeners();
        thread.start();
    }

    // TODO: log4j in betrieb nehmen!
    static int LOG_TRACE = 1;
    static int LOG_DEBUG = 2;
    static int LOG_INFO  = 3;
    static int LOG_WARN  = 4;
    static int LOG_ERROR = 5;
    static int LOG_FATAL = 6;
    private void impLog(int loglevel, String msg) {
        if (loglevel >= LOG_DEBUG)
            System.out.println(msg);
    }

    private void addVlcListeners() {
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventListener() {
            @Override public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
                impLog(LOG_DEBUG, "Vlc event: mediaChanged" + /*" media = " + media +*/ " mrl = \"" + mrl + "\"");
            }

            @Override public void opening(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: opening");
            }

            @Override public void buffering(MediaPlayer mediaPlayer, float newCache) {
                if (newCache <= 0.01 || newCache >= 99.99)
                    impLog(LOG_DEBUG, "Vlc event: buffering " + newCache);
                else
                    impLog(LOG_TRACE, "Vlc event: buffering " + newCache);
            }

            @Override public void playing(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: playing");
                // Der Event kommt beim initialen Start des Track sowie auch bei Resume nach Pause
                // TODO: bei den Callbacks Synchronize erforderlich?
                if (getPlayerState() == PlayerState.PAUSE)
                    MpjPlayerVlc.this.delegate.setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_RESUME);
                else
                    MpjPlayerVlc.this.delegate.setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_START);
            }

            @Override public void paused(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: paused");
                MpjPlayerVlc.this.delegate.setPlayerStateWithEvent(PlayerState.PAUSE, PlayerEvent.TRACK_PAUSE);
            }

            @Override public void stopped(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: stopped");
                MpjPlayerVlc.this.delegate.setPlayerStateWithEvent(PlayerState.STOP, PlayerEvent.TRACK_STOP);
            }

            @Override public void forward(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: forward");
            }

            @Override public void backward(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: backward");
            }

            @Override public void finished(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: finished");
                MpjPlayerVlc.this.delegate.setPlayerStateWithEvent(PlayerState.END, PlayerEvent.TRACK_END);
            }

            @Override public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                impLog(LOG_TRACE, "Vlc event: timeChanged " + newTime);
            }

            @Override public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                impLog(LOG_TRACE, "Vlc event: positionChanged " + newPosition);
            }

            @Override public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
                impLog(LOG_DEBUG, "Vlc event: seekableChanged " + newSeekable);
            }

            @Override public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
                impLog(LOG_DEBUG, "Vlc event: pausableChanged " + newPausable);
            }

            @Override public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
                impLog(LOG_DEBUG, "Vlc event: titleChanged " + newTitle);
            }

            @Override public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
                impLog(LOG_DEBUG, "Vlc event: snapshotTaken " + filename);
            }

            @Override public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
                // Hmm, ändert sich ständig, auch bei lokalen Files
                impLog(LOG_TRACE, "Vlc event: lengthChanged " + newLength);
            }

            @Override public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
                impLog(LOG_DEBUG, "Vlc event: videoOutput " + newCount);
            }

            @Override public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
                impLog(LOG_DEBUG, "Vlc event: scrambledChanged " + newScrambled);
            }

            @Override public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
                impLog(LOG_DEBUG, "Vlc event: elementaryStreamAdded " + type + " " + id);
            }

            @Override public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
                impLog(LOG_DEBUG, "Vlc event: elementaryStreamDeleted " + type + " " + id);
            }

            @Override public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
                impLog(LOG_DEBUG, "Vlc event: elementaryStreamSelected " + type + " " + id);
            }

            @Override public void error(MediaPlayer mediaPlayer) {
                // Hmm, bei »file not found« liefert libvlc_errmsg() null :(
                impLog(LOG_ERROR, "Vlc event: error, msg = " + libVlc.libvlc_errmsg());
            }

            @Override public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
                impLog(LOG_DEBUG, "Vlc event: mediaMetaChanged " + metaType);
            }

            @Override public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
                impLog(LOG_DEBUG, "Vlc event: mediaSubItemAdded");
            }

            @Override public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
                // Hmm, ändert sich ständig, auch bei lokalen Files
                impLog(LOG_TRACE, "Vlc event: mediaDurationChanged " + newDuration);
            }

            @Override public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
                impLog(LOG_DEBUG, "Vlc event: mediaParsedChanged " + newStatus);
            }

            @Override public void mediaFreed(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: mediaFreed");
            }

            @Override public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
                impLog(LOG_DEBUG, "Vlc event: mediaStateChanged " + newState);
            }

            @Override public void newMedia(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: newMedia");
                if (getTrack() != null) {
                    delegate.setPlayerState(PlayerState.STOP);
                } else {
                    delegate.setPlayerState(PlayerState.EJECT);
                }
            }

            @Override public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
                impLog(LOG_DEBUG, "Vlc event: subItemPlayed " + subItemIndex);
            }

            @Override public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
                impLog(LOG_DEBUG, "Vlc event: subItemFinished" + subItemIndex);
            }

            @Override public void endOfSubItems(MediaPlayer mediaPlayer) {
                impLog(LOG_DEBUG, "Vlc event: endOfSubItems");
            }
        });

    }

    private class MyThread extends Thread {
        public MyThread(String name) {
            super(name);
        }

        @Override public void run() {
            try {
                for (;;) {
                    MpjPlayerVlc.this.delegate.dispatchCommand(-1);
                }

                // TODO: hier auch auf die Events des Vlc Player reagieren
                /*for (;;) {
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
                }*/
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

        // Auf meinem Dell M4400 (Kubuntu 14.04) bekomme ich folgenden Fehler
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
        // Auf BBB (ebenfalls Kubuntu 14.04) trat das Problem nicht auf

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

        //libVlc = LibVlc.INSTANCE;
        libVlcFactory = LibVlcFactory.factory();
        libVlc = libVlcFactory.create();
        String version = libVlc.libvlc_get_version();
        System.out.println("VlcJ initialized, vlc version = " + version);
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
            // Wenn ich dem Vlc hier ein mediaPlayer.stop() schicke obwohl kein Track geladen ist, dann bekomme ich von dem ein »stopped« Event Callback.
            // Wäre eigentlich nicht weiter schlimm, nervt mich nur ein klein wenig - deshalb fange ich das hier ab.
            if (getTrack() != null) {
                if (getPlayerState() != PlayerState.STOP)
                    mediaPlayer.stop();
                // eine besseren Weg zum "loslassen" des Track habe ich bislang noch nicht gefunden
                // TODO: Mail an vlcj
                mediaPlayer.prepareMedia("/dev/null");
                // impSetTrack(), damit nicht stop() gerufen wird
                delegate.impSetTrack(null, null);
                // TODO: etwas doofe Grauzone(n) hier
                // - ich habe mich schon von dem Track verabschieded, aber der Event zu meinen Client(s) wird erst später über die vlcj Callbacks generiert
                // - gleiches habe ich ja auch bei allen anderen Aktionen (play, pause, ...)
                // Schön wäre wenn:
                // - der Aufrufer ejectTrack() aufruft und nach Rückkehr wirklich alles erledigt ist
                // - ich müsste also auf die vlcj Callbacks warten, vielleicht sowas wie
                //   delegate.waitForState() oder delegate.waitForEvent(), natürlich mit Timeout und so
                // - ob das aber auch für die UI Buttons sinnvoll ist, ist noch fraglich
                //   falls z.B. prepareMedia() mal deutlich länger dauert hängt dann das UI ...
                // - Aber: Der Client soll im Event Callback durchaus neue Aktionen an demselben Player ausführen können (setTrack & Play, ...), das wird ja letztlich für die Playlist benötigt
            }
        }});
    }

    @Override public void stopTrack() throws InterruptedException, MpjPlayerException {
        delegate.invokeCommand(new MpjRunnable() { @Override public void run(MpjAnswer answer) throws MpjPlayerException {
            if (getTrack() != null && getPlayerState() != PlayerState.STOP)
                mediaPlayer.stop();
        }});
    }

    @Override public void playTrack() throws InterruptedException, MpjPlayerException {
        delegate.invokeCommand(new MpjRunnable() { @Override public void run(MpjAnswer answer) throws MpjPlayerException {
            MpjTrack track = getTrack();
            if (track != null) {
                //System.out.println("Start Playback");
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

                //System.out.println("Preparing (" + nam + ")");
                if (!mediaPlayer.prepareMedia(nam)) {
                    String msg = "Failed to load " + track.getUri() + "\"";
                    delegate.setError(msg);
                    delegate.setPlayerStateWithEvent(PlayerState.ERROR, PlayerEvent.STATE_CHANGED);
                    throw new MpjPlayerException(msg);
                }
                //if (!mediaPlayer.isPlayable()) // Hmm, der liefert mir bei allem false?!
                //    throw new MpjPlayerException("Could not play this \"" + track.getUri() + "\"");
                //System.out.println("Starting (" + nam + ")");

                // Vlcj MediaPlayer: play() startet die Wiedergabe asynchron, start() hingegen blockiert bis wirklich gestartet wurde
                //mediaPlayer.play();
                if (!mediaPlayer.start()) {
                    String msg = "Failed to start " + track.getUri() + "\"";
                    delegate.setError(msg);
                    delegate.setPlayerStateWithEvent(PlayerState.ERROR, PlayerEvent.STATE_CHANGED);
                    throw new MpjPlayerException(msg);
                }
                //System.out.println("Playback started (" + nam + ", name = " + track.getName() + ")");
                //delegate.setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_START);
                //System.out.println("Playback started event sent");
            }
        }});
    }

    @Override public void pauseTrack() throws InterruptedException, MpjPlayerException {
        delegate.invokeCommand(new MpjRunnable() { @Override public void run(MpjAnswer answer) throws MpjPlayerException {
            if (getTrack() != null)
                // TODO: evtl. getPlayerState()? Fading etc. müsste ich hier auch abfangen
                mediaPlayer.pause();
        }});
    }

    @Override public void resumeTrack() throws InterruptedException, MpjPlayerException {
        delegate.invokeCommand(new MpjRunnable() { @Override public void run(MpjAnswer answer) throws MpjPlayerException {
            if (getTrack() != null)
                // TODO: evtl. getPlayerState()? Fading etc. müsste ich hier auch abfangen
                // Hier ist wohl egal
                mediaPlayer.pause();
                //mediaPlayer.start();
        }});
    }

}

/*
  Bzgl. Audio Outputs
  Dell M4400 Kubuntu 14.04:
    Systemsteuerung:
        "Internes Audio analog Stereo"
    Vlc:
        "Internes Audio analog Stereo"
    vlcj:
        name = "pulse", description = "Pulseaudio audio output"
        name = "adummy", description = "Dummy audio output"
        name = "alsa", description = "ALSA audio output"
          id = "default", longName = "Playback/recording through the PulseAudio sound server"
          id = "null", longName = "Discard all samples (playback) or generate zero samples (capture)"
          id = "pulse", longName = "PulseAudio Sound Server"
          id = "sysdefault:CARD=Intel", longName = "HDA Intel, 92HD71B7X Analog Default Audio Device"
          id = "front:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog Front speakers"
          id = "surround40:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog 4.0 Surround output to Front and Rear speakers"
          id = "surround41:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog 4.1 Surround output to Front, Rear and Subwoofer speakers"
          id = "surround50:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog 5.0 Surround output to Front, Center and Rear speakers"
          id = "surround51:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog 5.1 Surround output to Front, Center, Rear and Subwoofer speakers"
          id = "surround71:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog 7.1 Surround output to Front, Center, Side, Rear and Woofer speakers"
          id = "dmix:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog Direct sample mixing device"
          id = "dsnoop:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog Direct sample snooping device"
          id = "hw:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog Direct hardware device without any conversions"
          id = "plughw:CARD=Intel,DEV=0", longName = "HDA Intel, 92HD71B7X Analog Hardware device with all software conversions"
        name = "amem", description = "Audio memory output"
        name = "afile", description = "File audio output"

    Mit BM8810 Bluetooth Headphone, verbunden via Iogear Bluetooth 4.0 USB Stick als "Audio Sink"
    Systemsteuerung zusätzlich:
        "BM-8810"
    Vlc zusätzlich:
        "BM-8810"
    vlcj:
        hmm, nichts neues dabei?!
  Beim Macbook sehe ich weniger Einträge (adummy, afile, amem, auhal), aber mit "BM-8810" auch keine Veränderung
*/