package jme.mpjoe.swing.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Erweiterter JFileChooser, z.B. mit Warnung "FileExist" im Falle eines Save-Dialogs
 */
public class FileChooser extends JFileChooser {
    private static final long serialVersionUID = -4411635257342736085L;

    private CwdSaver cwd;

    public FileChooser() {
    }

    public FileChooser(String title) {
        setDialogTitle(title);
    }

    public FileChooser(String title, CwdSaver cwd) {
        setDialogTitle(title);
        this.cwd = cwd;
        cwd.setTo(this);
    }


    // Abgeguckt aus http://stackoverflow.com/questions/3651494/jfilechooser-with-confirmation-dialog
    @Override public void approveSelection() {
        File f = getSelectedFile();
        if (f.exists() && getDialogType() == SAVE_DIALOG) {
            int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
            switch(result) {
                case JOptionPane.YES_OPTION:
                    if (cwd != null) cwd.getFrom(this);
                    super.approveSelection();
                    return;
                case JOptionPane.NO_OPTION:
                    return;
                case JOptionPane.CLOSED_OPTION:
                    return;
                case JOptionPane.CANCEL_OPTION:
                    cancelSelection();
                    return;
            }
        }

        if (cwd != null) cwd.getFrom(this);
        super.approveSelection();
    }

/*
    super.approveSelection();
    public int showOpenDialog(Component parent) {
        int ret = super.showOpenDialog(parent);
        if (ret == APPROVE_OPTION)
            cwd.getFrom(this);
        return ret;
    }

    public int showSaveDialog(Component parent) {
        int ret = 0;

        ret = super.showSaveDialog(parent);
        if (ret == APPROVE_OPTION) {
            if (getSelectedFile().exists())

        }

        if (ret == APPROVE_OPTION)
            cwd.getFrom(this);
        return ret;
    }*/

}
