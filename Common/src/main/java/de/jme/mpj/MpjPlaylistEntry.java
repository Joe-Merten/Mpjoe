package de.jme.mpj;


/**
 * Klasse zur Repräsentation eines MpjPlaylist-Eintrags, also MpjTrack zzgl. MpjPlaylist-spezifischer Zusatzinformationen
 *
 * @author Joe Merten
 */
public class MpjPlaylistEntry implements AutoCloseable {

    // TODO: Den State evtl. zum Track oder zum Player verschieben?
    public enum State {
        NONE,
        PLAY,
        STOP,
        PAUSE,
        FADEIN,
        FADEOUT;
        @Override public String toString() {
            switch(this) {
                case NONE   : return "none";
                case PLAY   : return "play";
                case STOP   : return "stop";
                case PAUSE  : return "pause";
                case FADEIN : return "fade in";
                case FADEOUT: return "fade out";
                default: throw new IllegalArgumentException();
            }
        }
    };

    MpjTrack   track;
    State      state;
    int        votes;

    public MpjPlaylistEntry(MpjTrack track) {
        this.track = track;
        state = State.NONE;
    }

    public MpjTrack getTrack() {
        return track;
    }

    public void setState(State newState) {
        state = newState;
    }

    public State getState() {
        return state;
    }

    public void setVotes(int n) {
        votes = n;
    }

    public int getVotes() {
        return votes;
    }

    @Override public void close() {
        if (track != null)
            track.close();
    }

}
