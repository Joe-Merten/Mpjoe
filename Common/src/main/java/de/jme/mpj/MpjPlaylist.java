package de.jme.mpj;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zur Repräsentation einer MpjPlaylist
 *
 * @author Joe Merten
 */
public class MpjPlaylist implements AutoCloseable {

    public enum PlaylistEvent {
        CHANGED;
        @Override public String toString() {
            switch(this) {
                case CHANGED: return "changed";
                default: throw new IllegalArgumentException();
            }
        }
    }

    public interface EventListner {
        void playerEvent(MpjPlaylist playlist, PlaylistEvent evt);
    }
    private List<EventListner> listeners = new ArrayList<EventListner>();

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
            sendEvent(PlaylistEvent.CHANGED);
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

    @Override public String toString() {
        return "Playlist with " + size() + " tracks";
    }

    public void addListener(EventListner listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListner listener) {
        listeners.remove(listener);
    }

    private void sendEvent(PlaylistEvent evt) {
        for (EventListner l : listeners) {
            try {
                l.playerEvent(this, evt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
