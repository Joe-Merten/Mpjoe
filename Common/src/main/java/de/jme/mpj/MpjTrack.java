package de.jme.mpj;

import java.io.File;
import java.net.URI;

/**
 * Klasse zur Repräsentation eines MpjTrack, also z.B. einer Audio- oder Videodatei
 *
 * @author Joe Merten
 */
public class MpjTrack implements AutoCloseable {

    String  name;            // Name des MpjTrack
    URI     uri;             // Dateiname oder so  TODO: Besser File, URI, URL oder String?
                             // - javax.sound.sampled.AudioSystem.getAudioInputStream() will eine URL, aber die bekomme ich ja mit uri.toURL()
                             // - javax.media.ManagerManager.createRealizedPlayer() will auch eine URL
                             // - JFileChooser.getSelectedFile() liefert ein File, und dann File.toURI()
                             //   es gibt dann auch File.toURL(), aber das ist deprecated
                             // - TODO: Prüfen ob/wie ich ein File innerhalb des .jar abspielen kann
    boolean deleteOnClose;   // true, falls die Datei nach dem Abspielen gelöscht werden soll
    byte[]  audioData;       // Alternativ zu file können die Audiodaten auch im Speicher liegen
    //Clip    clip;            // Clip, über den die Datei abgespielt wird/wurde
    long    trackLength;     // Cliplänge in µs, wird erst beim Beginn des abspielens gesetzt
    long    trackPos;        // Aktuelle Abspielposition in µs

    public MpjTrack(URI uri) {
        try {
            // etwas umständlich um die %20 etc. wieder in Leerzeichen umzuwandeln (und hier auch gleich das Verzeichnis zu entfernen)
            this.name = new File(uri).getName();
        } catch (IllegalArgumentException e) {
            // Ok, die Uri ist offenbar kein File, sondern vielleicht sowas wie "http://youtu.be/0w1mP3oFXRU"
            this.name = uri.toString();
        }

        this.uri  = uri;
        this.deleteOnClose = false;
    }

    public MpjTrack(String name, URI uri) {
        this.name = name;
        this.uri  = uri;
        this.deleteOnClose = false;
    }

    public MpjTrack(String name, URI uri, boolean deleteFile) {
        this.name = name;
        this.uri  = uri;
        this.deleteOnClose = deleteFile;
    }

    public MpjTrack(String name, final byte[] data) {
        this.name = name;
        this.audioData = data;
    }

    @Override public void close() {
        //if (clip != null)
        //    clip.close();  TODO bei Bedarf
        //if (file != null && deleteOnClose) TODO bei Bedarf
        //    file.delete();
    }

    public String getName() {
        return name;
    }

    public URI getUri() {
        return uri;
    }

    public byte[] getAudioData() {
        return audioData;
    }

}
