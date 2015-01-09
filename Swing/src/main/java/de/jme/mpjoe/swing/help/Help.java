package de.jme.mpjoe.swing.help;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * Aufruf der Html Hilfe im Webbrowser
 * @author Joe Merten
 */
public class Help {
    public static Help instance = new Help();

    private class HelpAction extends AbstractAction {
        private static final long serialVersionUID = -6397360823234984424L;
        public HelpAction() {
            super("Help", null);
            putValue(SHORT_DESCRIPTION, "Get Help");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                URI uri = null;

                // Versuchen, das Html File aus der Resouce (z.B. aus dem jar) zu laden (also nach /tmp kopieren)
                URL url = getClass().getResource("/de/jme/mpjoe/swing/help/index.html");
                //URL url = ClassLoader.getSystemResource("/de/jme/mpjoe/swing/help/index.html"); // Das ist wohl eine Alternative

                if (url != null) {
                    File tempFile = File.createTempFile("MpjoeHelp", ".html");
                    tempFile.deleteOnExit();
                    //System.out.println("TMP=" + tempFile.toString());
                    InputStream is = getClass().getResourceAsStream("/de/jme/mpjoe/swing/help/index.html");
                    Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    uri = tempFile.toURI();
                }

                Desktop.getDesktop().browse(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public HelpAction helpAction;

    public Help() {
       helpAction = new HelpAction();
    }

}
