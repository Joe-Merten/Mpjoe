package jme.mpjoe.swing.ui;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;

/**
 * Speicherung von Arbeitsverzeichnissen über mehrere Aufrufe von Dateiauswahldialogen,
 * damit man nicht bei jedem Aufruf des FileChooser immer wieder im Homeverzeichnis steht.
 * Diese Klasse ist nicht selbst eine UI-Komponente, vielmehr dient sie der Unterstützung anderer UI-Komponenten.
 * @author Joe Merten
 */
public class CwdSaver {
    private File            cwd;
    private ArrayList<File> defaultDirectories;

    public File getCwd()        { return cwd; }
    public void setCwd(File f)  { cwd = f;    }

    public CwdSaver() {
    }

    public CwdSaver(String defaultDir) {
        defaultDirectories = new ArrayList<File>();
        defaultDirectories.add(new File(defaultDir));
    }

    public CwdSaver(File defaultDir) {
        defaultDirectories = new ArrayList<File>();
        defaultDirectories.add(defaultDir);
    }

    public CwdSaver(String[] defaultDirs) {
        defaultDirectories = new ArrayList<File>();
        for (String dir : defaultDirs) {
            defaultDirectories.add(new File(dir));
        }
    }

    public CwdSaver(File[] defaultDirs) {
        defaultDirectories = new ArrayList<File>();
        for (File dir : defaultDirs) {
            defaultDirectories.add(dir);
        }
    }


    /**
     * Diese Methode ruft man vor fc.showLoad/SaveDialog() auf
     * um das zuletzt vom Benutzer gewählte Arbeitsverzeichnis am FileChooser voreinzustellen
     */
    public void setTo(JFileChooser fc) {
        File f = null;
        if (cwd == null) {
            // Erster Aufruf, also verschiedene Datenverzeichnisse probieren.
            // Sofern die Datenverzeichnisse relativ angegeben sind, prüft f.exists() relativ zum aktuellen CWD des Systems.
            // - Unter Linux ist das dann z.B. das Verzeichnis, in dem man beim Start der Applikation gerade steht.
            // - Beim Start aus Eclipse heraus, ist das im Regelfall das Project Root Verzeichnis
            // Diese Strategie ist vielleicht noch nicht so ganz optimal - werde ich später vielleicht nochmal ändern.
            // Falls nix passt, habe ich folgende Optionen:
            // - null setzen → dann stellt sich der FileChooser Dialog in das Homeverzeichnis des Benutzers
            // - "" setzen & f.getAbsoluteFile() → dann stehen wir im aktuellen CWD des Systems
            if (defaultDirectories != null) {
                for (File dir : defaultDirectories) {
                    if (dir.exists()) {
                        f = dir;
                        break;
                    }
                }

                // Durch getAbsoluteFile() legen wir jetzt erst mal fest, dass ggf. relative Verzeichnisangaben (aus defaultDirectories)
                // sich auf das aktuelle CWD unseres Environments beziehen (und nicht auf das Homeverzeichnis des Benutzers).
                if (f != null) f = f.getAbsoluteFile();
            }
        } else {
            // Folgeaufruf, also das zuletzt ausgewählte Verzeichnis voreinstellen
            f = cwd;
        }

        fc.setCurrentDirectory(f);

        //System.out.println("setTo(): cwd=" + cwd + ", f=" + f);
    }

    /**
     * Diese Methode ruft man nach fc.showLoad/SaveDialog() auf (sofern dieses mit APPROVE_OPTION returnierte)
     * um das vom Benutzer (ggf. neu) gewählte Arbeitsverzeichnis für Folgeaufrufe zu speichern.
     */
    public void getFrom(JFileChooser fc) {
        cwd = fc.getCurrentDirectory();
        //System.out.println("getFrom(): cwd=" + cwd);
    }

}
