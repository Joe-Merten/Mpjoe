package de.jme.mpjoe.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import de.jme.mpjoe.swing.gen.GenPanel;
import de.jme.mpjoe.swing.help.Help;
import de.jme.mpjoe.swing.ui.DauWarning;
import de.jme.mpjoe.swing.ui.Statusbar;
import de.jme.mpjoe.swing.ui.Toolbar;
import de.jme.toolbox.SystemInfo;
import de.jme.toolbox.VersionInfo;

/**
 * Applikationsfenster des Ews Client
 * @author Joe Merten
 */
public class MainWin {

    private JFrame      frame;
    private Statusbar   statusbar;
    private JTabbedPane tabbedPane;
    private GenPanel    genPanel;

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
        // Den jewels letzten State des AudioPlayer auch in der Statusbar anzeigen
        //TtsAudioPlayer.instance.addListener(new PlayerEventListner() {
        //    @Override public void playerEvent(TtsAudioPlayer player, PlayerEvent evt) {
        //        statusbar.setStatusTextC(player.getPlayerStateString());
        //    }
        //});
    }
}
