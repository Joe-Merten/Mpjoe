package de.jme.mpjoe.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import de.jme.jsi.Jsi;
import de.jme.jsi.MoniStd;
import de.jme.jsi.Monitor;
import de.jme.mpj.MpjPlayer;
import de.jme.mpj.MpjPlayer.MpjPlayerException;
import de.jme.mpj.MpjPlayer.PlayerEvent;
import de.jme.mpj.MpjPlayer.PlayerState;
import de.jme.mpj.MpjTrack;
import de.jme.mpjoe.swing.fsysview.FilesystemPanel;
import de.jme.mpjoe.swing.help.Help;
import de.jme.mpjoe.swing.playlist.PlaylistPanel;
import de.jme.mpjoe.swing.ui.CwdSaver;
import de.jme.mpjoe.swing.ui.DauWarning;
import de.jme.mpjoe.swing.ui.FileChooser;
import de.jme.mpjoe.swing.ui.Statusbar;
import de.jme.mpjoe.swing.ui.Toolbar;
import de.jme.toolbox.SystemInfo;
import de.jme.toolbox.VersionInfo;

/**
 * Applikationsfenster des Mpjoe Java Swing Client
 * @author Joe Merten
 */
public class MainWin {

    public enum PlayerType {
        AUTO,
        SOUND,
        JMF,
        VLC,
        VLC_EMBEDDED,
        VLC_DIRECT;
        @Override public String toString() {
            switch(this) {
                case AUTO        : return "auto";
                case SOUND       : return "java.sound";
                case JMF         : return "jmf";
                case VLC         : return "vlcj";
                case VLC_EMBEDDED: return "vlcj.embedded";
                case VLC_DIRECT  : return "vlcj.direct";
                default: throw new IllegalArgumentException();
            }
        }
    }

    private PlayerType playerType = PlayerType.AUTO;

    private JFrame      frame;
    private Statusbar   statusbar;
    JSplitPane          splitPaneMain;
    JSplitPane          splitPaneRight;
    private JPanel      leftPanel;
    private JPanel      middlePanel;
    private JPanel      rightPanel;
    private MpjPlayer   mpjPlayer;
    PlaylistPanel       playlistPanel;
    FilesystemPanel     filesystemPanel;

    private static CwdSaver cwd = new CwdSaver(new String[]{"/bbb-d/MP3/OGG-WMA-RM-Test/Testfiles/", "/D/MP3/", "D:/MP3/OGG-WMA-RM-Test/Testfiles/", "D:/MP3/"});  // TODO: Sinnvolle Defaultverzeichnisse

    private class QuitAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public QuitAction() {
            super("Quit");
            setIconFromResource("/de/jme/mpj/General/Quit1-16.png");
            setShortDescription("Quit Mpjoe");
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        }
        public void actionPerformed(ActionEvent ae) {
            //System.out.println("QuitAction: " + ae.toString());
            // TODO: Quit besser gestalten, auch sowas wie "Vorher Speichert?" etc. berücksichtigen
            // siehe auch http://stackoverflow.com/questions/258099/how-to-close-a-java-swing-application-from-the-code
            frame.dispose();
            System.exit(0);
        }
    }
    private QuitAction quitAction;

    private class ChooseFileAndPlayAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public ChooseFileAndPlayAction() {
            super("Play...");
            setIconFromResource("/de/jme/mpj/Player/Play-16.png");
            setShortDescription("Choose file and play");
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                chooseFileAndPlay();
            } catch (InterruptedException | MpjPlayerException e) {
                e.printStackTrace();
            }
        }
    }
    private ChooseFileAndPlayAction chooseFileAndPlayAction;

    private class ToggleDarkThemeAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public ToggleDarkThemeAction() {
            super("Dark");
            setShortDescription("Toggle dark color theme");
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
            //setMnemonic(KeyEvent.VK_D);
        }
        public void actionPerformed(ActionEvent ae) {
            toggleDarkTheme();
        }
    }
    private ToggleDarkThemeAction toggleDarkThemeAction;

    private class ToggleLookAndFeelAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public ToggleLookAndFeelAction() {
            super("Look & Feel");
            setShortDescription("Toggle look & feel");
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
        }
        public void actionPerformed(ActionEvent ae) {
            toggleLookAndFeel();
        }
    }
    private ToggleLookAndFeelAction toggleLookAndFeelAction;


    private static MainWin mainWindow;
    /**
     * Start der Applikation
     */
    public static void main(final String[] args) {
        boolean justPrintVersion = false;
        boolean justPrintHelp = false;
        String uriString = null;
        PlayerType playerType = PlayerType.AUTO;
        int index = 0;
        while (index < args.length) {
            String arg = args[index];
            if      (arg.equals("--version")) justPrintVersion = true;
            else if (arg.equals("--help" )) justPrintHelp = true;
            else if (arg.equals("--sound")) playerType = PlayerType.SOUND;
            else if (arg.equals("--jfm"  )) playerType = PlayerType.JMF;
            else if (arg.equals("--vlc"  )) playerType = PlayerType.VLC;
            else if (arg.equals("--vld"  )) playerType = PlayerType.VLC_DIRECT;
            else if (arg.equals("--vle"  )) playerType = PlayerType.VLC_EMBEDDED;
            else if (arg.equals("--dark")) MpjLookAndFeel.setDarkTheme(true);
            else if (arg.equals("--yellow")) {
                index++;
                if (index >= args.length) {
                    System.err.println("Missing argument");
                    System.exit(1);
                }
                arg = args[index];
                String[] yellow = arg.split(",");
                MpjLookAndFeel.setYellow(yellow);
            }
            else if (uriString == null) uriString = arg;
            else {
                System.err.println("Unreconized parameter: \"" + arg + "\"");
                System.exit(1);
            }
            index++;
        }
        String version = VersionInfo.getVersionInfo();
        if (justPrintVersion || justPrintHelp) {
            // Ausgabe ohne Logger, damit z.B. vom Shellskript vernünftig auswertbar
            if (justPrintHelp) {
                System.out.println(version);
                System.out.println("Usage: Mpjoe [filespec] [uri] [options]");
                System.out.println("  filespec  =  filename or directory");
                System.out.println("  uri       =  something like \"http://youtu.be/0w1mP3oFXRU\" or \"file:/MP3/Great%20Track.mp3\"");
                System.out.println("  options:");
                System.out.println("  --help     =  show this help");
                System.out.println("  --version  =  show version info");
                System.out.println("  --sound    =  media output using javax.sound");
                System.out.println("  --jmf      =  media output using javax.media (java media framework)");
                System.out.println("  --vlc      =  media output using vlc (auto selection), needs to have vlc installed");
                System.out.println("  --vld      =  media output using vlc direct media player");
                System.out.println("  --vle      =  media output using vlc embedded media player");
            } else if (justPrintVersion)
                System.out.println(version);
            System.exit(0);
        }

        final PlayerType playerTypeFinal = playerType;
        final String uriStringFinal = uriString;
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                if (DauWarning.isWarningNeeded()) {
                    DauWarning warn = new DauWarning();
                    warn.setVisible(true);
                }
                try {
                    mainWindow = new MainWin(playerTypeFinal, uriStringFinal);
                    mainWindow.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                if (mainWindow != null && mainWindow.playlistPanel != null) {
                    mainWindow.playlistPanel.requestFocus();
                }
            }
        });
    }

    /**
     * Konstruktur
     * @throws IOException
     */
    public MainWin(PlayerType playerType, String uriString) throws IOException {
        this.playerType = playerType;
        initialize(uriString);
    }

    /**
     * Initialisierung des Fensters und der Fensterkomponenten
     * @throws IOException
     */
    private void initialize(String uriString) throws IOException {
        MpjLookAndFeel.initialize();

        // TODO: Icons nur 1x laden und an verschiedenen Stellen verwenden, siehe auch MpjSystray
        final Image icon16  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-16.png")).getImage();
        final Image icon20  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-20.png")).getImage();
        final Image icon24  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-24.png")).getImage();
        final Image icon32  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-32.png")).getImage();
        final Image icon48  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-48.png")).getImage();
        final Image icon64  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-64.png")).getImage();
        final Image icon128 = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-128.png")).getImage();
        final Image icon256 = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-256.png")).getImage();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        System.out.println("*** Screen = " + screenSize.width + "x" + screenSize.height + ", " + dpi + " dpi");

        //--------------------
        // Actions
        quitAction = new QuitAction();
        chooseFileAndPlayAction = new ChooseFileAndPlayAction();
        //chooseFileAndPlayAction.setEnabled(false);
        toggleDarkThemeAction = new ToggleDarkThemeAction();
        toggleLookAndFeelAction = new ToggleLookAndFeelAction();

        // SystemTray
        final MpjSystray systray = new MpjSystray();
        if (systray.isSupported()) {
            final JPopupMenu trayPopup = systray.getPopup();
            trayPopup.add(new JMenuItem(quitAction));
            trayPopup.add(new JMenuItem(chooseFileAndPlayAction));
            trayPopup.add(new JMenuItem(toggleDarkThemeAction));
            trayPopup.add(new JMenuItem(toggleLookAndFeelAction));
        }

        frame = new JFrame();
        frame.setBounds(10, 10, 1260, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ArrayList<Image> iconImages = new ArrayList<Image>(4);
        iconImages.add(icon16 );
        iconImages.add(icon20 );
        iconImages.add(icon24 );
        iconImages.add(icon32 );
        iconImages.add(icon48 );
        iconImages.add(icon64 );
        iconImages.add(icon128);
        iconImages.add(icon256);
        frame.setIconImages(iconImages);

        String version = VersionInfo.getVersionInfo();
        version += " (" + SystemInfo.getUserName() + "@" + SystemInfo.getComputerName() + ")";
        frame.setTitle(version);

        // Folgender Code soll wohl bei OpenSuse den Applikationstitel setzen (nicht getestet).
        // Bei Windows XP und Osx gibt's eine Exception "NoSuchFieldException: awtAppClassName"
        // Bei Kubuntu keine Exception, aber auch keine sichtbare Wirkung (z.B. Prozessname)
        try {
            Toolkit xToolkit = Toolkit.getDefaultToolkit();
            java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(xToolkit, "Mpjoe-Appname");
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) { }

        //--------------------
        // Hauptmenü
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        mnFile.add(new JMenuItem(quitAction));

        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);
        mnView.add(new JMenuItem(toggleDarkThemeAction));
        mnView.add(new JMenuItem(toggleLookAndFeelAction));

        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);

        JMenuItem mntmHelp = new JMenuItem(Help.instance.helpAction);
        mnHelp.add(mntmHelp);

        //--------------------
        // Toolbar
        Toolbar toolbar = new Toolbar();
        toolbar.setBorderPainted(true);
        toolbar.setFloatable(true);
        toolbar.add(quitAction);
        toolbar.addSeparator();
        toolbar.add(chooseFileAndPlayAction);
        toolbar.addSeparator();
        toolbar.add(toggleDarkThemeAction);
        toolbar.addSeparator();
        toolbar.add(toggleLookAndFeelAction);
        toolbar.addSeparator();
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);

        //--------------------
        // Statusbar
        statusbar = new Statusbar();
        frame.getContentPane().add(statusbar, BorderLayout.SOUTH);
        statusbar.setStatusTextB(frame.getTitle());

        //--------------------
        // Restlicher Fensterbereich
        splitPaneMain = new JSplitPane();
        splitPaneMain.setContinuousLayout(true);
        //splitPaneMain.setDividerLocation(192); nicht setzen, weil ergibt sich durch die PreferredSize des leftPanel
        frame.getContentPane().add(splitPaneMain, BorderLayout.CENTER);
        splitPaneRight = new JSplitPane();
        splitPaneRight.setContinuousLayout(true);
        splitPaneMain.setRightComponent(splitPaneRight);

        //--------------------
        // Panels
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        splitPaneMain.setLeftComponent(leftPanel);
        splitPaneRight.setLeftComponent(middlePanel);
        splitPaneRight.setRightComponent(rightPanel);

        playlistPanel = new PlaylistPanel();
        middlePanel.add(playlistPanel, BorderLayout.CENTER);
        playlistPanel.addAcceptEventListner(new PlaylistPanel.AcceptEventListner() {
            @Override public void selectionAccepted(PlaylistPanel playlistPanel) {
                try {
                    mpjPlayer.setTrack(playlistPanel.getSelectedEntries()[0]);
                    mpjPlayer.playTrack();
                } catch (InterruptedException | MpjPlayerException e) {
                    // TODO: Hmm, ist das so gut hier?
                    e.printStackTrace();
                }
            }
        });

        filesystemPanel = new FilesystemPanel();
        if      (new File("/D/MP3").isDirectory()) filesystemPanel.setRootDirectory("/D/MP3");  // TODO: Testcode entfernen
        else if (new File("D:\\MP3").isDirectory()) filesystemPanel.setRootDirectory("D:\\MP3");
        else if (new File(System.getProperty("user.home")).isDirectory()) filesystemPanel.setRootDirectory(System.getProperty("user.home"));

        rightPanel.add(filesystemPanel, BorderLayout.CENTER);
        filesystemPanel.addAcceptEventListner(new FilesystemPanel.AcceptEventListner() {
            @Override public void selectionAccepted(FilesystemPanel filesystemPanel) {
                final File[] files = filesystemPanel.getSelectedFiles();
                for (File f : files)
                    playlistPanel.addTrack(new MpjTrack(f.toURI()));
            }
        });


        //--------------------
        //frame.pack(); // dieses pack() findet man in vielen Beispielsourcen, führt bei mir aber dazu, dass das Player Panel auf Breite 0 reduziert wird
        //frame.setVisible(true);

        if (playerType == null || playerType == PlayerType.AUTO) {
            if (SystemInfo.isWindows()) {
                // Unter Windows funktioniert Jmf noch nicht mit MP3
                // Vlc Embedded scheint hier eine gute Wahl zu sein
                playerType = PlayerType.VLC_EMBEDDED;
            } else if (SystemInfo.isOsx()) {
                // Auf Mac funktiniert Vlcj nur als Direct (Embedded ist nicht unterstützt)
                // Jmf ginge aber auch
                playerType = PlayerType.VLC_DIRECT;
            } else {
                // Unter Linux verwende ich per default erst mal Vlc
                playerType = PlayerType.VLC_EMBEDDED;
            }
        }
        if (playerType == PlayerType.VLC) {
            if (SystemInfo.isOsx()) {
                // Auf Mac funktiniert Vlcj nur als Direct
                playerType = PlayerType.VLC_DIRECT;
            } else {
                playerType = PlayerType.VLC_EMBEDDED;
            }
        }

        version += " (" + playerType + ")"; // TODO, HACK: Playertyp mit im der Fenstertitel anzeigen
        frame.setTitle(version);

        switch (playerType) {
            case SOUND       : mpjPlayer = new MpjPlayerSound("Player");      break;
            case JMF         : mpjPlayer = new MpjPlayerJmf("Player");        break;
            case VLC_EMBEDDED: mpjPlayer = new MpjPlayerVlc("Player", false); break;
            case VLC_DIRECT  : mpjPlayer = new MpjPlayerVlc("Player", true);  break;
            default: break;
        }
        /*Object guiComponent = mpjPlayer.getGuiComponent();
        if (guiComponent instanceof Component) {
            playerPanel = (Component)guiComponent;
            playerPanel.setPreferredSize(new Dimension(200, 200));
            playerPanel.setMinimumSize(new Dimension(0, 0));
            leftPanel.add(playerPanel, BorderLayout.CENTER);
        }*/
        MpjPlayerSwing mpjPlayerSwing = new MpjPlayerSwing("Player", mpjPlayer);
        leftPanel.add(mpjPlayerSwing, BorderLayout.CENTER);
        mpjPlayer = mpjPlayerSwing.getCorePlayer();
        mnFile.add(new JMenuItem(mpjPlayerSwing.getPauseAction()));
        toolbar.add(mpjPlayerSwing.getPauseAction());

        // Den jewels letzten State des MpjPlayerJmf auch in der Statusbar anzeigen
        mpjPlayer.addListener(new MpjPlayer.EventListner() {
            @Override public void playerEvent(final MpjPlayer player, PlayerEvent evt, PlayerState newState, PlayerState oldState) {
                //final String msg = evt.toString() + " / " + player.getPlayerStateString();
                final String msg = player.getPlayerStateString();
                System.out.println("Player event: " + evt + ", state = " + msg);
                systray.setIconAnimation(newState == PlayerState.PLAYING);
                EventQueue.invokeLater(new Runnable() { public void run() {
                    statusbar.setStatusTextC(msg);
                }});
            }
        });

        // Hmm, leider keine Wirkung beim Mac, dort steht der Fokus nach wie vor auf dem Filesystem Panel
        //playlistPanel.requestFocus();

        /*{
            String nam = uriString;
            URI uri = null;
            if (nam == null || nam.isEmpty()) {
                nam = "/D/MP3/OGG-WMA-RM-Test/Testfiles/America - The Last Unicorn.mp3";
                //nam = "/D/MP3/OGG-WMA-RM-Test/Testfiles/Safri Dou - Played-A-Live.avi";
                //nam = "/D/MP3/Carsten/The Boss Hoss/Stallion Battalion/12 High.mp3";
                //nam = "/D/MP3/Elisa iPod/Basta - Gimme Hope Joachim - der Jogi Löw a Cappella WM Song 2010.wav";
                //nam = "/D/MP3/Nora de Mar - For your Beauty and Soul/Ogg Vorbis/02 - Island of Hope.ogg";
                //nam = "/D/MP3/Nora de Mar - For your Beauty and Soul/Flac/02 - Island of Hope.flac";

                // Bei Youtube gibt's unter Linux leider noch Fehler:
                //   [0x6884b248] gnutls tls client error: unsupported GnuTLS version
                //   [0x6884b248] main tls client error: TLS client plugin not available
                //   [0x68857830] main stream error: cannot pre fill buffer
                // Mit dem richtigen Vlc Player geht es jedoch
                // nam = "https://www.youtube.com/watch?v=0w1mP3oFXRU";   // Furch Durango Bahn Gong
                // nam = "http://www.youtube.com/watch?v=0w1mP3oFXRU";    // Furch Durango Bahn Gong
                // nam = "http://youtu.be/0w1mP3oFXRU";                   // Furch Durango Bahn Gong
                // nam = "https://www.youtube.com/watch?v=5oGXmvy0--w";
                // nam = "https://www.youtube.com/watch?v=oJh-jusiDvU";

                // Bei MyVideo gibt's unter Linux 'nen Crash (Segfault), Unter Windows XP hatte ich keine Wiedergabe
                //nam = "http://www.myvideo.de/watch/7880291/Joe_AFF_Level_V_VII";
                //nam = "http://prerelease.myvideo.de/watch/6322550/Lindsay_Lohan_zeigt_ihren_Haengebusen_smash247_com";

                if (nam.startsWith("/D/")) { // TODO: Debug-Hack entfernen!
                    if (SystemInfo.isWindows()) nam = nam.replace("/D/", "D:/");
                    if (SystemInfo.isOsx()    ) nam = nam.replace("/D/MP3/", "/Users/joe.merten/Development/");
                }
            }

            System.out.println("Nam   = " + nam);
            if (nam.startsWith("http://") || nam.startsWith("https://")) {
                uri = URI.create(nam);
            } else {
                // URI.create(nam) geht nicht, weil da sind dann keine Leerzeichen in Dateinamen erlaubt
                File f = new File(nam);
                System.out.println("File     = " + f.toString());
                uri = f.toURI();
            }
            System.out.println("Uri      = " + uri.toString());
            System.out.println("Uri.Url  = " + uri.toURL().toString());
            System.out.println("Uri.Path = " + uri.getPath());

            if (true) {
                try {
                    mpjPlayer.setTrack(new MpjTrack(uri));
                } catch (InterruptedException | MpjPlayerException e) {
                    e.printStackTrace();
                }
            } else {
                // Hier Testcode zum 1:1 Durchreichen des Kommandozeilenparameter an Vlcj, um Verluste bei der URI Konvertierung zu vermeiden
                EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
                leftPanel.add(mediaPlayerComponent);
                //playerFrame.setContentPane(mediaPlayerComponent);
                mediaPlayerComponent.getMediaPlayer().playMedia(nam);
            }
        }*/


        // Farbwerte von Bildschirmpunkten anzeigen
        // Thread colorSpyThread = new Thread("ColorSpy") {
        //     @Override public void run() {
        //         for (;;) {
        //             try {
        //                 EventQueue.invokeLater(new Runnable() { public void run() {
        //                     try {
        //                         //PointerInfo pointer;
        //                         //pointer = MouseInfo.getPointerInfo();
        //                         //Point coord = pointer.getLocation();
        //                         Robot robot = new Robot();
        //                         Point coord = MouseInfo.getPointerInfo().getLocation();
        //                         int x = (int)coord.getX();
        //                         int y = (int)coord.getY();
        //                         Color color = robot.getPixelColor(x, y);
        //
        //                         String msg = "";
        //                         msg += "x=" + x + " y=" + y;
        //                         msg += " r=" + color.getRed() + " g=" + color.getGreen() + " b=" + color.getBlue();
        //
        //                         // Jetzt noch versuchen, die Farbe im Look & Feel zu finden
        //                         final ThemeColor[] themeMatches = MpjLookAndFeel.getMatchingThemeColors(color);
        //                         if (themeMatches.length == 1) {
        //                             msg += " name=\"" + themeMatches[0].name + "\"";
        //                         } else if (themeMatches.length > 0) {
        //                             msg += " " + themeMatches.length + " names: ";
        //                             int i = 0;
        //                             for (ThemeColor tm : themeMatches) {
        //                                 if (i > 0) msg += ",";
        //                                 i++;
        //                                 if (i >= 3) {
        //                                     msg += "…";
        //                                     break;
        //                                 }
        //                                 msg += " \"" + themeMatches[0].name + "\"";
        //                             }
        //                         }
        //
        //                         statusbar.setStatusTextC(msg);
        //                     } catch (AWTException e) {
        //                         e.printStackTrace();
        //                     }
        //                 }});
        //
        //                 sleep(500);
        //             } catch (InterruptedException e) { }
        //         }
        //     }
        // };
        // colorSpyThread.setDaemon(true);
        // colorSpyThread.start();


        // TODO: moniThread woanders einbauen?
        Jsi jsi = Jsi.instance;
        MoniStd.register(jsi);
        //ShutdownAction.registerAction(jsi);
//        ButtonHandler.registerJsiActions(jsi);
        Thread moniThread = new Thread(new Monitor(), "Monitor");
        moniThread.setDaemon(true);     // Der Monitor-Thread soll das Programm nicht am Beenden hindern, deshalb "Daemon"
        moniThread.start();
        if (!moniThread.isAlive())
            throw new IllegalStateException("just started thread is not alive");
    }

    public boolean chooseFileAndPlay() throws InterruptedException, MpjPlayerException {
        FileChooser fc = new FileChooser("Open MpjTrack for play", cwd);
        javax.swing.filechooser.FileFilter filterAudio = new javax.swing.filechooser.FileNameExtensionFilter("Audio Files (mp3 ogg flac wav wma)", "mp3", "ogg", "flac", "wav", "wma");
        javax.swing.filechooser.FileFilter filterVideo = new javax.swing.filechooser.FileNameExtensionFilter("Video Files (mpeg avi mov ogv wmv)", "mpeg", "mpg", "mpe", "mp4", "avi", "mov", "ogv", "wmv");
        javax.swing.filechooser.FileFilter filterSupported = new javax.swing.filechooser.FileNameExtensionFilter("Supported Files", "mp3", "ogg", "flac", "wav");
        fc.addChoosableFileFilter(filterSupported);
        fc.addChoosableFileFilter(filterAudio);
        fc.addChoosableFileFilter(filterVideo);
        int returnVal = fc.showOpenDialog(frame);
        boolean ret = false;
        if (returnVal == FileChooser.APPROVE_OPTION) {
            ret = true;
            File file = fc.getSelectedFile();
            URI uri = file.toURI();
            /*System.out.println("File = " + file.toString());
            System.out.println("  Name = " + file.getName());
            System.out.println("  Abs  = " + file.getAbsolutePath());
            try { System.out.println("  Cano = " + file.getCanonicalPath()); } catch (IOException e) { e.printStackTrace(); }
            System.out.println("Uri  = " + uri.toString());
            System.out.println("  Path = " + uri.getPath());
            try {
                URL furl = file.toURL();
                System.out.println("File.Url  = " + furl.toString());
                System.out.println("  Path = " + furl.getPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                URL url = uri.toURL();
                System.out.println("Uri.Url  = " + url.toString());
                System.out.println("  Path = " + url.getPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }*/

            mpjPlayer.setTrack(new MpjTrack(uri));
        }
        return ret;
    }

    public void toggleDarkTheme() {
        MpjLookAndFeel.setDarkTheme(!MpjLookAndFeel.isDarkTheme());
        toggleDarkThemeAction.setSelected(MpjLookAndFeel.isDarkTheme());
        // revalidate() hilft leider nicht
        // SplitPaneUI spui = splitPaneMain.getUI();
        // BasicSplitPaneUI bspui = (BasicSplitPaneUI)spui;
        // BasicSplitPaneDivider div = bspui.getDivider();
        // div.setBackground(new Color(255,0,0));
        // div.setForeground(new Color(0,255,0));
        // div.setDividerSize(100);
        // div.setEnabled(false);
        // div.invalidate();
        // div.validate();
        // div.repaint();
        // div.revalidate();
        // div.setEnabled(true);
        //Another idea would be to go directly over JSplitPane.getUI(), which returns u a SplitPaneUI, which you probably can cast to a BasicSplitPaneUI. There you will find a function getDivider() and this is a Component, where you can set any Color.
        //splitPaneMain.revalidate();
        //splitPaneRight.revalidate();
    }

    public void toggleLookAndFeel() {
        int count = MpjLookAndFeel.getLookAndFeelCount();
        int index = MpjLookAndFeel.getCurrentLookAndFeelIndex();
        index++;
        if (index >= count) index = 0;
        MpjLookAndFeel.setCurrentLookAndFeelIndex(index);
    }
}
