package de.jme.mpjoe.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

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
    public static void main(String[] args) {
        boolean justPrintVersion = false;
        for (String arg : args) {
            if (arg.equals("--version")) justPrintVersion = true;
            else {
                System.err.println("Unreconized parameter: \"" + arg + "\"");
                System.exit(1);
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
                    MainWin window = new MainWin();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Konstruktur
     */
    public MainWin() {
        initialize();
    }

    /**
     * Initialisierung des Fensters und der Fensterkomponenten
     */
    private void initialize() {
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
        mpjPlayer = new MpjPlayerJmf("Player");

        // Den jewels letzten State des MpjPlayerJmf auch in der Statusbar anzeigen
        mpjPlayer.addListener(new MpjPlayer.PlayerEventListner() {
            @Override public void playerEvent(MpjPlayer player, PlayerEvent evt, PlayerState newState, PlayerState oldState) {
                statusbar.setStatusTextC(player.getPlayerStateString());
            }
        });
    }

    public boolean chooseFileAndPlay() {

        // Vielleicht doch mal javazoom angucken:
        //   http://stackoverflow.com/a/22305518/2880699
        //   http://introcs.cs.princeton.edu/java/faq/mp3/MP3.java.html
        // http://www.onjava.com/pub/a/onjava/2004/08/11/javasound-mp3.html
        // Hier noch Sourcen mit fade-in/out
        // -> http://stackoverflow.com/questions/14959566/java-error-when-trying-to-use-mp3plugin-for-playing-an-mp3-file/14959818#14959818

        /*{
            String bip = "___/D/MP3/Carsten/The Boss Hoss/Stallion Battalion/12 High.mp3";
            //String bip = "/D/MP3/Elisa iPod/Basta - Gimme Hope Joachim - der Jogi Löw a Cappella WM Song 2010.wav";
            //String bip = "file:///D/MP3/Nora de Mar - For our Beaut and Soul/Ogg Vorbis/02 - Island of Hope.ogg";

            File f = new File(bip);
            mpjPlayer.setTrack(new MpjTrack(f.toURI()));
        }/**/

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
