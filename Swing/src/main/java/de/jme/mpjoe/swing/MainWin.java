package de.jme.mpjoe.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import de.jme.mpj.MpjPlayer;
import de.jme.mpj.MpjPlayer.PlayerEvent;
import de.jme.mpj.MpjPlayer.PlayerState;
import de.jme.mpj.MpjTrack;
import de.jme.mpjoe.swing.gen.GenPanel;
import de.jme.mpjoe.swing.help.Help;
import de.jme.mpjoe.swing.ui.CwdSaver;
import de.jme.mpjoe.swing.ui.DauWarning;
import de.jme.mpjoe.swing.ui.FileChooser;
import de.jme.mpjoe.swing.ui.Statusbar;
import de.jme.mpjoe.swing.ui.Toolbar;
import de.jme.toolbox.SystemInfo;
import de.jme.toolbox.SystemInfo.MachineType;
import de.jme.toolbox.VersionInfo;

/**
 * Applikationsfenster des Mpjoe Java Swing Client
 * @author Joe Merten
 */
public class MainWin {

    private JFrame      frame;
    private Statusbar   statusbar;
    private JTabbedPane tabbedPane;
    private GenPanel    genPanel;
    private MpjPlayer   mpjPlayer;
    private JPanel      playerPanel;

    private static CwdSaver cwd = new CwdSaver(new String[]{"/bbb-d/MP3/OGG-WMA-RM-Test/Testfiles/", "/D/MP3/", "D:/MP3/OGG-WMA-RM-Test/Testfiles/", "D:/MP3/"});  // TODO: Sinnvolle Defaultverzeichnisse

    private class QuitAction extends AbstractAction {
        private static final long serialVersionUID = 4852834538692097749L;
        public QuitAction() {
            super("Quit", null);
            putValue(SHORT_DESCRIPTION, "Quit the application");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
            //toolTipText = "Quit the application"; // Warum ist das Interface so komisch; warum putValue() statt setAccelerator()?
            //putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));  // Todo: Was ist das und warum "new Integer?" Das Beispiel ist aus http://docs.oracle.com/javase/tutorial/uiswing/misc/action.html
        }
        public void actionPerformed(ActionEvent ae) {
            //System.out.println("QuitAction: " + ae.toString());
            frame.dispose();
        }
    }
    private QuitAction quitAction;

    private class ChooseFileAndPlayAction extends AbstractAction {
        private static final long serialVersionUID = 4852834538692097750L;
        public ChooseFileAndPlayAction() {
            super("Play...", null);
            putValue(SHORT_DESCRIPTION, "Choose file and play");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        }
        public void actionPerformed(ActionEvent ae) {
            chooseFileAndPlay();
        }
    }
    private ChooseFileAndPlayAction chooseFileAndPlayAction;


    /**
     * Start der Applikation
     */
    public static void main(final String[] args) {
        boolean justPrintVersion = false;
        for (String arg : args) {
            if (arg.equals("--version")) justPrintVersion = true;
            else {
                //System.err.println("Unreconized parameter: \"" + arg + "\"");
                //System.exit(1);
            }
        }
        String version = VersionInfo.getVersionInfo();
        if (justPrintVersion) {
            // Ausgabe ohne Logger, damit z.B. vom Shellskript vernünftig auswertbar
            System.out.println(version);
            System.exit(0);
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (DauWarning.isWarningNeeded()) {
                    DauWarning warn = new DauWarning();
                    warn.setVisible(true);
                }
                try {
                    MainWin window = new MainWin(args);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Konstruktur
     * @throws IOException
     */
    public MainWin(String[] args) throws IOException {
        initialize(args);
    }

    /**
     * Initialisierung des Fensters und der Fensterkomponenten
     * @throws IOException
     */
    private void initialize(String[] args) throws IOException {
        if (SystemInfo.getMachineType() == MachineType.PcOsx) {
            // Unter Osx bekomme ich folgenden Fehler:
            //   JavaVM WARNING: JAWT_GetAWT must be called after loading a JVM
            //   java.lang.UnsatisfiedLinkError: Can't load JAWT at com.sun.jna.Native.getWindowHandle0(Native Method) ...
            // Manuelles laden der Lib hat aber auch nicht geholfen:
            //   System.loadLibrary("jawt");
        }

        frame = new JFrame();
        frame.setBounds(10, 10, 1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String version = VersionInfo.getVersionInfo();
        version += " (" + SystemInfo.getUserName() + "@" + SystemInfo.getComputerName() + ")";
        frame.setTitle(version);


        //--------------------
        // Actions
        quitAction = new QuitAction();
        chooseFileAndPlayAction = new ChooseFileAndPlayAction();

        //--------------------
        // Hauptmenü
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmQuit = new JMenuItem(quitAction);
        mnFile.add(mntmQuit);

        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);

        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);

        JMenuItem mntmHelp = new JMenuItem(Help.instance.helpAction);
        mnHelp.add(mntmHelp);

        //--------------------
        // Toolbar
        Toolbar toolbar = new Toolbar();
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);
        toolbar.add(quitAction);
        toolbar.add(chooseFileAndPlayAction);

        //--------------------
        // Statusbar
        statusbar = new Statusbar();
        frame.getContentPane().add(statusbar, BorderLayout.SOUTH);
        statusbar.setStatusTextB(frame.getTitle());

        //--------------------
        // Restlicher Fensterbereich
        JSplitPane splitPaneMain = new JSplitPane();
        splitPaneMain.setContinuousLayout(true);
        splitPaneMain.setDividerLocation(192);
        frame.getContentPane().add(splitPaneMain, BorderLayout.CENTER);

        //--------------------
        // Tts etc. Panels
        tabbedPane = new JTabbedPane();
        splitPaneMain.setRightComponent(tabbedPane);

        genPanel = new GenPanel(); tabbedPane.addTab("General", genPanel);

        //--------------------
        frame.setVisible(true);
        playerPanel = new JPanel();
        splitPaneMain.setLeftComponent(playerPanel);
        playerPanel.setBounds(0, 0, 200, 200);
        playerPanel.setBackground(Color.CYAN);
        playerPanel.setVisible(true);

        //mpjPlayer = new MpjPlayerSound("Player");
        //mpjPlayer = new MpjPlayerJmf("Player");
        //mpjPlayer = new MpjPlayerVlc("Player");

        if (mpjPlayer == null) {
            if (SystemInfo.getMachineType() == MachineType.PcWindows) {
                // Unter Windows funktioniert Jmf noch nicht mit MP3, deshalb hier erst mal Vlc verwenden
                mpjPlayer = new MpjPlayerVlc("Player");
            } else if (SystemInfo.getMachineType() == MachineType.PcOsx) {
                // Auf Mac frunzt Vlcj noch nicht, deshalb vorerst Jmf verwenden
                mpjPlayer = new MpjPlayerJmf("Player");
                //mpjPlayer = new MpjPlayerVlc("Player");
            }
        }

        if (mpjPlayer == null) {
            // Unter Linux defaulten wir erst mal auf Vlc
            //mpjPlayer = new MpjPlayerSound("Player");
            //mpjPlayer = new MpjPlayerJmf("Player");
            mpjPlayer = new MpjPlayerVlc("Player");
        }

        mpjPlayer.setGuiParent(playerPanel);

        // Den jewels letzten State des MpjPlayerJmf auch in der Statusbar anzeigen
        mpjPlayer.addListener(new MpjPlayer.PlayerEventListner() {
            @Override public void playerEvent(MpjPlayer player, PlayerEvent evt, PlayerState newState, PlayerState oldState) {
                //statusbar.setStatusTextC(player.getPlayerStateString());
                System.out.println(player.getPlayerStateString());
            }
        });

        {
            String nam = "";
            URI uri = null;
            if (args.length == 0) {
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
                    if (SystemInfo.getMachineType() == MachineType.PcWindows) nam = nam.replace("/D/", "D:/");
                    if (SystemInfo.getMachineType() == MachineType.PcOsx) nam = nam.replace("/D/MP3/", "/Users/joe.merten/Development/");
                }
            } else {
                nam = args[0];
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
                mpjPlayer.setTrack(new MpjTrack(uri));
            } else {
                // Hier Testcode zum 1:1 Durchreichen des Kommandozeilenparameter an Vlcj, um Verluste bei der URI Konvertierung zu vermeiden
                EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
                playerPanel.add(mediaPlayerComponent);
                //playerFrame.setContentPane(mediaPlayerComponent);
                mediaPlayerComponent.getMediaPlayer().playMedia(nam);
            }
        }
    }

    public boolean chooseFileAndPlay() {
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
}
