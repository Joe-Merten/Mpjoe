package de.jme.mpj;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zur Repräsentation einer MpjPlaylist
 *
 * @author Joe Merten
 */
public class MpjPlaylist implements AutoCloseable {

    private List<MpjPlaylistEntry> playlist = new ArrayList<MpjPlaylistEntry>();

    public MpjPlaylist() {
    }

    public void clear() {
        // TODO: Ist das so korrekt?
        synchronized (playlist) {
            for (MpjPlaylistEntry ple : playlist)
                ple.close();
            playlist = new ArrayList<MpjPlaylistEntry>();
        }
    }

    public void add(MpjPlaylistEntry pe) {
        synchronized (playlist) {
            playlist.add(pe);
        }
    }

    public int size() {
        synchronized (playlist) {
            return playlist.size();
        }
    }

    public MpjPlaylistEntry get(int index) {
        synchronized (playlist) {
            return playlist.get(index);
        }
    }

    @Override public void close() {
        clear();
    }

}
