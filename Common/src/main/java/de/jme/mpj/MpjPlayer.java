package de.jme.mpj;

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
public interface MpjPlayer {

    @SuppressWarnings("serial")
    class MpjPlayerException extends Exception {
        public MpjPlayerException(String message) {
            super(message);
        }
    };

    public enum PlayerState {
        EJECT,         // no track loaded
        STOP,          // track loaded but not started yet
        PLAYING,       // track is playing
        PAUSE,         // track playback is paused
        END,           // track playback has reached the end
        FADING_IN,     // track is playing, volume is currently increasing
        FADING_OUT,    // track is playing, volume is currently decreasing
        ERROR;
        @Override public String toString() {
            switch(this) {
                case EJECT     : return "eject";
                case STOP      : return "stop";
                case PLAYING   : return "playing";
                case PAUSE     : return "pause";
                case END       : return "end";
                case FADING_IN : return "fading in";
                case FADING_OUT: return "fading out";
                case ERROR     : return "ERROR";
                default: throw new IllegalArgumentException();
            }
        }
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
        TRACK_EJECTED,
        ERROR;
        public String toString() {
            switch(this) {
                case NONE          : return "none";
                case STATE_CHANGED : return "state changed";
                case TRACK_START   : return "track start";
                case TRACK_END     : return "track end";
                case TRACK_STOP    : return "track stop";
                case TRACK_PAUSE   : return "track pause";
                case TRACK_RESUME  : return "track resume";
                case TRACK_PROGRESS: return "track progress";
                case TRACK_EJECTED : return "track ejected";
                case ERROR         : return "ERROR";
                default: throw new IllegalArgumentException();
            }
        }
    }

    public interface EventListner {
        void playerEvent(MpjPlayer player, PlayerEvent evt, PlayerState newState, PlayerState oldState);
    }

    public void addListener(EventListner listener);
    public void removeListener(EventListner listener);
    public String getErrorMessage();
    public PlayerState getPlayerState();
    public String getPlayerStateString();

    public String getName();
    public Object getGuiComponent();

    public void setTrack(MpjTrack newTrack, MpjPlaylistEntry newPle) throws InterruptedException, MpjPlayerException;
    public void setTrack(MpjTrack newTrack) throws InterruptedException, MpjPlayerException;
    public void setTrack(MpjPlaylistEntry ple) throws InterruptedException, MpjPlayerException;
    public MpjTrack getTrack();
    public MpjPlaylistEntry getPlaylistEntry();

    public void ejectTrack() throws InterruptedException, MpjPlayerException;
    public void stopTrack() throws InterruptedException, MpjPlayerException;
    public void playTrack() throws InterruptedException, MpjPlayerException;
    public void pauseTrack() throws InterruptedException, MpjPlayerException;
    public void resumeTrack() throws InterruptedException, MpjPlayerException;


    // Delegation helper class
    public class Delegate {
        private MpjPlayer          player;
        private String             name;
        private List<EventListner> listeners = new ArrayList<EventListner>();
        private PlayerState        playerState;
        private MpjTrack           track;
        private MpjPlaylistEntry   ple;
        private String             errorMessage;
        private Object             guiComponent;


        // =============================================================================================================
        // General
        public Delegate(MpjPlayer player, String name) {
            this.player = player;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setGuiComponent(Object guiComponent) {
            this.guiComponent = guiComponent;
        }
        public Object getGuiComponent() {
            return guiComponent;
        }

        // =============================================================================================================
        // Track loading
        public void impSetTrack(MpjTrack newTrack, MpjPlaylistEntry newPle) {
            track = newTrack;
            ple = newPle;
        }

        // TODO: newTrack=null muss zu Eject führen (inkl. State & Event - klären wer hier wen für was aufruft!
        //       Ausserdem brauche ich einen Event, sowas wie TRACK_LOADED (oder nur STATE_CHANGED?) der vom Status EJECTED nach STOP führt.
        public void setTrack(MpjTrack newTrack, MpjPlaylistEntry newPle) throws InterruptedException, MpjPlayerException {
            if (track != null) {
                player.stopTrack();
                ple = null;
                track = null;
            }
            if (newTrack != null) {
                track = newTrack;
                ple = newPle;
            }
        }

        public void setTrack(MpjTrack newTrack) throws InterruptedException, MpjPlayerException {
            setTrack(newTrack, null);
        }

        public void setTrack(MpjPlaylistEntry ple) throws InterruptedException, MpjPlayerException {
            if (ple == null) setTrack(null, null);
            else setTrack(ple.track, ple);
        }

        public MpjTrack getTrack() {
            return track;
        }

        public MpjPlaylistEntry getPlaylistEntry() {
            return ple;
        }


        // =============================================================================================================
        // Player Control
        // TODO: Fading etc.


        // =============================================================================================================
        // Event Notification
        public void addListener(EventListner listener) {
            listeners.add(listener);
        }

        public void removeListener(EventListner listener) {
            listeners.remove(listener);
        }

        public void setPlayerStateWithEvent(PlayerState newState, PlayerEvent evt) {
            PlayerState oldState = playerState;
            playerState = newState;
            sendPlayerEvent(evt, newState, oldState);
        }

        public void setPlayerState(PlayerState newState) {
            if (newState != playerState) {
                PlayerState oldState = playerState;
                playerState = newState;
                sendPlayerEvent(PlayerEvent.STATE_CHANGED, newState, oldState);
            }
        }

        public void sendPlayerEvent(PlayerEvent evt, PlayerState newState, PlayerState oldState) {
            for (EventListner l : listeners) {
                try {
                    l.playerEvent(player, evt, newState, oldState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void setError(String msg) {
            errorMessage = msg;
            setPlayerStateWithEvent(PlayerState.ERROR, PlayerEvent.ERROR);
        }

        public void setError(Throwable e) {
            e.printStackTrace();
            setError(e.toString());
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public PlayerState getPlayerState() {
            return playerState;
        }

        public String getPlayerStateString() {
            String msg = playerState.toString();
            switch (playerState) {
                case PLAYING:
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
                    msg += ": " + getErrorMessage();
                    break;
                default:
                    break;
            }
            return msg;
        }

        // =============================================================================================================
        // Command Queue for inter-thread communication

        public interface MpjRunnable {
            public void run(MpjAnswer answer) throws MpjPlayerException;
        }

        public class MpjAnswer {
            public MpjPlayerException exception;
        }

        private BlockingQueue<MpjRunnable> commandQueue = new ArrayBlockingQueue<MpjRunnable>(1);
        private BlockingQueue<MpjAnswer>   answerQueue  = new ArrayBlockingQueue<MpjAnswer>(1);

        public MpjAnswer invokeCommand(final MpjRunnable func) throws InterruptedException, MpjPlayerException {
            synchronized (this) {
                commandQueue.put(func);
                MpjAnswer answer = answerQueue.take();
                if (answer.exception != null) {
                    // TODO: Wie macht man das
                    //throw answer.exception;
                    throw new MpjPlayerException("[[" + answer.exception.toString() + "]]");
                }
                return answer;
            }
        }

        public void dispatchCommand(int timeout) throws InterruptedException {
            MpjRunnable func = null;
            if (timeout < 0)
                func = commandQueue.take();
            else
                func = commandQueue.poll(timeout, TimeUnit.MILLISECONDS);
            if (func != null) {
                MpjAnswer answer = new MpjAnswer();
                try {
                    func.run(answer);
                } catch (MpjPlayerException e) {
                    answer.exception = e;
                }
                answerQueue.put(answer);
            }
        }
    }

}
