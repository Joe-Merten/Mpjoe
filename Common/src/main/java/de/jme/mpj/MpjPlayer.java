package de.jme.mpj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Basisklasse zur Wiedergabe von Audio- und Videodateien
 *
 * @author Joe Merten
 */
public class MpjPlayer extends Thread implements AutoCloseable {

    public enum PlayerState {
        IDLE,
        PLAYING,
        ERROR
    }

    public enum PlayerEvent {
        NONE,
        STATE_CHANGED,
        TRACK_START,
        TRACK_END,
        TRACK_PROGRESS,
        ERROR
    }

    // Klassen, die über Änderungen des MpjPlayer-Zustands informiert werden wollen, müssen dieses Interface implementieren
    public interface PlayerEventListner {
        // Nachricht: Der MpjPlayer-Zustand hat sich geändert (kommt ggf. öfter als unbedingt nötig)
        void playerEvent(MpjPlayer player, PlayerEvent evt, PlayerState newState, PlayerState oldState);
    }
    private List<PlayerEventListner> listeners = new ArrayList<PlayerEventListner>();
    private PlayerState   playerState;
    private MpjTrack         track;
    private MpjPlaylistEntry ple;
    private String           errorMessage;

    public MpjPlayer(String name) {
        setName(name);
    }

    public void setTrack(MpjTrack newTrack, MpjPlaylistEntry newPle) {
        if (track != null) {
            stopTrack();
            ple = null;
            track = null;
        }
        if (newTrack != null) {
            track = newTrack;
            ple = newPle;
        }
    }

    public void setTrack(MpjTrack newTrack) {
        setTrack(newTrack, null);
    }

    public void setTrack(MpjPlaylistEntry ple) {
        if (ple == null) setTrack(null, null);
        else setTrack(ple.track, ple);
    }

    public MpjTrack getTrack() {
        return track;
    }

    public MpjPlaylistEntry getPlaylistEntry() {
        return ple;
    }

    public void ejectTrack() {
        setTrack(null, null);
    }

    public void stopTrack() {
        // TODO: Spezieller Player muss hier auch wirklich die Wiedergabe stoppen
        setPlayerState(PlayerState.IDLE);
    }

    public void playTrack() {
    }

    public void pauseTrack() {
    }

    public PlayerState getPlayerState()  {
        return playerState;
    }

    public String getPlayerStateString() {
        String msg = "";
        switch (playerState) {
            case IDLE:
                msg = "Idle";
                break;
            case PLAYING:
                msg = "Playing";
                if (getTrack() != null) {
                    // Name des MpjTrack, sofern vorhanden
                    String name = getTrack().getName();
                    if (name != null) name = name.trim();
                    if (name != null && !name.isEmpty()) {
                        if (name.length() > 60)
                            name = name.substring(0, 60) + "…";
                        msg += " \"" + name + "\"";
                    }
                    // Restlaufzeit des MpjTrack, sofern > 0
                    //long timeElapsed = currentTrack.trackPos;
                    //long timeLeft = currentTrack.trackLength - timeElapsed;
                    //if (timeLeft < 0) timeLeft = 0;
                    //msg += " " + timeLeft / 1000000 + "s"; // Restspielzeit des MpjTrack in Sekunden (ohne Nachkommastellen)
                    //
                    //int tracksLeft = 0;
                    //synchronized (playlist) {
                    //    tracksLeft = playlist.size();
                    //}
                    //if (tracksLeft > 0)
                    //    msg += ", " + tracksLeft + " more track" + (tracksLeft > 1 ? "s":"") + " left";
                }
                break;
            case ERROR:
                msg = "Error: " + getErrorMessage();
                break;
            default:
                msg = "Unknown state";
                break;
        }
        return msg;
    }

    @Override public String toString() {
        return getPlayerStateString();
    }

    public void addListener(PlayerEventListner listener) {
        listeners.add(listener);
    }

    public void removeListener(PlayerEventListner listener) {
        listeners.remove(listener);
    }

    protected void setPlayerStateWithEvent(PlayerState newState, PlayerEvent evt) {
        PlayerState oldState = playerState;
        playerState = newState;
        sendPlayerEvent(evt, newState, oldState);
    }

    protected void setPlayerState(PlayerState newState) {
        if (newState != playerState) {
            PlayerState oldState = playerState;
            playerState = newState;
            sendPlayerEvent(PlayerEvent.STATE_CHANGED, newState, oldState);
        }
    }

    //protected void sendPlayerEvent(PlayerEvent evt) {
    //    sendPlayerEvent(evt, playerState, playerState);
    //}

    private void sendPlayerEvent(PlayerEvent evt, PlayerState newState, PlayerState oldState) {
        for (PlayerEventListner l : listeners) {
            try {
                l.playerEvent(this, evt, newState, oldState);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void setError(String msg) {
        errorMessage = msg;
        setPlayerStateWithEvent(PlayerState.ERROR, PlayerEvent.ERROR);
    }

    protected void setError(Throwable e) {
        e.printStackTrace();
        setError(e.toString());
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override public void close() throws IOException {
        // TODO Auto-generated method stub
    }

}
