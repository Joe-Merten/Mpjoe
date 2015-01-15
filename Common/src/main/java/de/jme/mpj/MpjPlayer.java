package de.jme.mpj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
        TRACK_STOP,
        TRACK_PAUSE,
        TRACK_RESUME,
        TRACK_PROGRESS,
        ERROR
    }

    // Klassen, die über Änderungen des MpjPlayer-Zustands informiert werden wollen, müssen dieses Interface implementieren
    public interface EventListner {
        // Nachricht: Der MpjPlayer-Zustand hat sich geändert (kommt ggf. öfter als unbedingt nötig)
        void playerEvent(MpjPlayer player, PlayerEvent evt, PlayerState newState, PlayerState oldState);
    }
    private List<EventListner> listeners = new ArrayList<EventListner>();
    private PlayerState      playerState;
    private MpjTrack         track;
    private MpjPlaylistEntry ple;
    private String           errorMessage;
    private Object           guiParent;

    public MpjPlayer(String name) {
        setName(name);
    }

    public void setGuiParent(Object parent) {
        guiParent = parent;
    }

    public Object getGuiParent() {
        return guiParent;
    }

    public void setTrack(MpjTrack newTrack, MpjPlaylistEntry newPle) throws InterruptedException {
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

    public void setTrack(MpjTrack newTrack) throws InterruptedException {
        setTrack(newTrack, null);
    }

    public void setTrack(MpjPlaylistEntry ple) throws InterruptedException {
        if (ple == null) setTrack(null, null);
        else setTrack(ple.track, ple);
    }

    public MpjTrack getTrack() {
        return track;
    }

    public MpjPlaylistEntry getPlaylistEntry() {
        return ple;
    }

    public void ejectTrack() throws InterruptedException {
        setTrack(null, null);
    }

    public void stopTrack() throws InterruptedException {
    }

    public void playTrack() throws InterruptedException {
    }

    public void pauseTrack() throws InterruptedException {
    }

    public PlayerState getPlayerState() {
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

    public void addListener(EventListner listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListner listener) {
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
        for (EventListner l : listeners) {
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

    private BlockingQueue<Runnable> commandQueue = new ArrayBlockingQueue<Runnable>(100);

    protected void invokeCommand(final Runnable func) throws InterruptedException {
        synchronized (this) {
            commandQueue.put(func);
        }
    }

    protected void dispatchCommand(int timeout) throws InterruptedException {
        Runnable func = null;
        if (timeout < 0)
            func = commandQueue.take();
        else
            func = commandQueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (func != null)
            func.run();
    }

}
