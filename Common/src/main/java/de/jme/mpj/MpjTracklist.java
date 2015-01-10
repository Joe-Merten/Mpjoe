package de.jme.mpj;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zur Repräsentation einer Trackliste
 *
 * @author Joe Merten
 */
public class MpjTracklist implements AutoCloseable {

    private List<MpjTrack> tracklist = new ArrayList<MpjTrack>();

    public MpjTracklist() {
    }

    public void clear() {
        // TODO: Ist das so korrekt?
        synchronized (tracklist) {
            for (MpjTrack t : tracklist)
                t.close();
            tracklist = new ArrayList<MpjTrack>();
        }
    }

    public void add(MpjTrack track) {
        synchronized (tracklist) {
            tracklist.add(track);
        }
    }

    @Override public void close() {
        clear();
    }

}
