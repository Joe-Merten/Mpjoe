package jme.mpjoe.swing.ui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * UI-Komponente zur Anzeige von Fehlermeldungen im Ews Client
 * @author Joe Merten
 */
public class ErrorMessage extends JOptionPane {
    private static final long serialVersionUID = -6715895428242764567L;

    @Override public int getMaxCharactersPerLineCount() {
        return 140;
    }

    public static void showError(Component parentComponent, Exception e) {
        e.printStackTrace();
        JOptionPane optionPane = new ErrorMessage();
        optionPane.setMessage(e.toString());
        optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog(parentComponent, "Exception " + e.getClass().getName());
        dialog.setVisible(true);
    }
}
