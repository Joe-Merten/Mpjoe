package de.jme.mpj;

import java.io.IOException;

/**
 * Klasse zur Repräsentation eines MpjPlaylist-Eintrags, also MpjTrack zzgl. MpjPlaylist-spezifischer Zusatzinformationen
 *
 * @author Joe Merten
 */
public class MpjPlaylistEntry implements AutoCloseable {

    MpjTrack   track;

    public MpjPlaylistEntry(MpjTrack track) {
        this.track = track;
    }

    @Override public void close() {
        if (track != null)
            track.close();
    }

}
