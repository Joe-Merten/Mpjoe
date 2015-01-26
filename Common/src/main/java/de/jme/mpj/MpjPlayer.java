package de.jme.mpj;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/***********************************************************************************************************************
  Empfehlung für Status und Funktionen für Buttons von Player Gui's
  - Eigentlich reden wir hier gar nicht von Buttons, sondern von den Actions die mit den Buttons assoziiert sind
  - Blinkend bedeutet: Wechsel zwischen normal und highlight
  - Drag & Drop:
    - Akzeptiert werden:
      - Tracks aus der Lib
      - Playlist Entries
      - Track / Playlist Entry aus einem anderen Player
      - Files (Drag & Drop vom Filemanager)
      - Url's z.B. vom Weebbrowser
    - während Dragover soll der Button mit der betreffenden Zielaktion z.B. highlight oder blinkend dargestellt werden
    - wenn Drop nicht über einem Button sondern irgendwo anders im Panel des Player, dann wird eine Defaultaktion ausgeführt (z.B. Play)
      Dragover soll dann diese Zielaktion entsprechend visualisieren
  - Bei Klick muss für jeden aktuellen State überleg werden, welche Aktion ausgelöst werden soll:
    - EJECT
    - LOADING
    - STOP
    - PLAYING
    - PAUSE
    - END
    - FADING_IN
    - FADING_OUT
------------------------------------------------------------------------------------------------------------------------
  Eject
  - Highlight: Playerstate = EJECT
  - Blinkend: Playerstate = LOADING
  - Klick wirft den Track aus dem Player, die Wiedergabe wird natürlich vorher ggf. noch gestoppt
------------------------------------------------------------------------------------------------------------------------
  Stop
  - Highlight: Playerstate = STOP, also Track geladen und Trackposition auf 0
    - Highlight aber auch bei Playerstate = END
  - Klick stoppt die aktuelle Wiedergabe und setzt die Trackposition auf 0
    - TODO bzgl. LOADING, siehe "Play"
  - Drag & Drop:
    - Highlight bei Dragover eines Track
    - Drop eines Track: Track laden aber noch keine Wiedergabe starten
------------------------------------------------------------------------------------------------------------------------
  Play
  - Highlight: Playerstate = PLAY
  - Klick startet die Wiedergabe, macht aber nichts wenn bereits Playerstate = PLAY (also kein Neustart des aktuellen Track)
    - STOP, END -> Wiedergabe ab Anfang starten
    - PLAYING   -> nichts tun
    - PAUSE, FADING_IN, FADING_OUT -> PLAY
    - evtl. bei EJECT
      - Überlegen, ob man hier einen Dialog zur Dateiauswahl und / oder Eingabe einer Url anbietet
    - TODO bzgl. LOADING
      - dieser Zustand ist tempörär (anders tempörär als z.B. PLAYING oder FADING_IN / FADING_OUT)
      - es wird hier immer einen Zielzustand geben, nämlich z.B. STOP, PLAYING, FADING_IN
      - nach setTrack() sind wir zwar erst mal auf STOP, aber die initiierende Aktion (z.B. via Gui, Remote oder Playlist Scheduling)
        möchte danach möglicherweise auf PLAYING, FADING_IN (evtl. auch mit vorherigem setPosition) machen
       -> TODO: Überlegen was ich in diesem Fall mit Aktionen wie STOP, PLAY, PAUSE, EJECT etc. mache.
                Am sinnvolsten wäre vermutlich, ich würde die einfach via CommandQueue serialisieren
------------------------------------------------------------------------------------------------------------------------
  Pause
  - Blinkend: Playerstate = PAUSE
  - Klick toggelt zwischen Pause an/aus:
    - PAUSE -> PLAY
    - PLAY  -> PAUSE (Resume)
    - FADING_IN, FADING_OUT -> PAUSE
    - TODO bzgl. LOADING, siehe "Play"
------------------------------------------------------------------------------------------------------------------------
  PlayPause
  - Dieser Button ist eine Kombination aus Play und Pause (platzsparend, statt der 2 Buttons wird also nur einer benötigt)
  - Highlight: Playerstate = PLAY
  - Blinkendes Pause: Playerstate = PAUSE
  - Klick:
    - STOP, END  -> Wiedergabe ab Anfang starten
    - PLAYING    -> PAUSE
    - PAUSE      -> PLAY (Resume)
    - FADING_IN  -> PLAY (also zurück auf Normallautstärke)
    - FADING_OUT -> PLAY (also zurück auf Normallautstärke)
------------------------------------------------------------------------------------------------------------------------
  Mute
  - "Highlight": Mute aktiviert
    TODO: Highlight ist hier eher der falsche Betriff, da eher ein durchgestrichenen Lautsprecher dargestellt wird
  - Klick: Toggelt zwischen Mute ein/aus
------------------------------------------------------------------------------------------------------------------------
  Fade
  - Blinkend: Playerstate = FADING_IN oder FADING_OUT
  - Klick:
    - PAUSE      -> FADING_IN
    - PLAY       -> FADING_OUT
    - FADING_OUT -> FADING_IN
    - FADING_IN  -> FADING_OUT
    - TODO bzgl. LOADING, siehe "Play"
  - Drag & Drop:
    - Highlight bei Dragover eines Track
    - Drop eines Track: Wiedergabe auf mit FADING_IN
------------------------------------------------------------------------------------------------------------------------
  Headphone
  - Highlight: Wiedergabe auf Headphone
  - Klick = Umschaltung Master / Headphone output
  - Drag & Drop:
    - Highlight bei Dragover eines Track
    - Drop eines Track: Wiedergabe auf Headphone
------------------------------------------------------------------------------------------------------------------------
  PrevTrack
  NextTrack
  - Blinkend: Übergang zum vorherigen / nächsten Track (Fade blinkt mit)
    Blinken aber nur bei manueller Auslösung, also nicht bei normalem Playlist Scheduling
  - Klick = Fade to prev/next Track
  - TODO: Evtl. Klick mit Modifier ohne Fade?
  - Mehrere Klicks = entsprechend mehrere Tracks der Playlist überspringen (wie bei Mpjoe V1)
  Gesteuert wird dies (also PrevTrack / NextTrack) allerdings nicht hier vom Player, sondern von aussen (Listener)
***********************************************************************************************************************/

/**
 * Basisklasse zur Wiedergabe von Audio- und Videodateien
 *
 * @author Joe Merten
 */
public interface MpjPlayer {

    static final Logger logger = LogManager.getLogger(MpjPlayer.class);

    @SuppressWarnings("serial")
    class MpjPlayerException extends Exception {
        public MpjPlayerException(String message) {
            super(message);
        }
    };

    public enum PlayerState {
        EJECT,         // no track loaded
        LOADING,       // track is currently opening (could need some time e.g. for network streams)
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
                case LOADING   : return "loading";
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

    // Die Aufrufe setTrack(), ejectTrack(), stopTrack(), playTrack() et cetera sind (u.U.) asynchron implementiert.
    // D.h. der Call returniert bevor die Aktion tatsächlich ausgeführt wurde
    // - entsprechend ist der PlayerState noch nicht auf dem erwarteten Zustand
    // - im Fehlerfall sieht man auch keine Exception
    // Wenn der Aufrufer dan Call Synchron (also blockierend) haben möchte, dann schreibt er ein waitfor() dahinter.
    // Beispielaufrufe:
    //   setTrack(track, PlayerState.FADING_IN).waitfor();
    //   playTrack().waitfor()

    public MpjAnswerHandle setTrack(MpjTrack newTrack, MpjPlaylistEntry newPle) throws InterruptedException, MpjPlayerException;
    public MpjAnswerHandle setTrack(MpjTrack newTrack) throws InterruptedException, MpjPlayerException;
    public MpjAnswerHandle setTrack(MpjPlaylistEntry ple) throws InterruptedException, MpjPlayerException;
    public MpjTrack getTrack();
    public MpjPlaylistEntry getPlaylistEntry();

    public MpjAnswerHandle ejectTrack() throws InterruptedException, MpjPlayerException;
    public MpjAnswerHandle stopTrack() throws InterruptedException, MpjPlayerException;
    public MpjAnswerHandle playTrack() throws InterruptedException, MpjPlayerException;
    public MpjAnswerHandle pauseTrack() throws InterruptedException, MpjPlayerException;
    public MpjAnswerHandle resumeTrack() throws InterruptedException, MpjPlayerException;

    static abstract public class MpjRunnable {
        public BlockingQueue<MpjAnswer> answerQueue = null;
        abstract public void run(MpjAnswer answer) throws MpjPlayerException;
    }

    static public class MpjAnswerHandle {
        final private BlockingQueue<MpjAnswer> answerQueue = new ArrayBlockingQueue<MpjAnswer>(1);
        final private MpjPlayer mpjPlayer;

        public MpjAnswerHandle(MpjPlayer mpjPlayer) {
            this.mpjPlayer = mpjPlayer;
        }

        public MpjAnswer waitfor() throws InterruptedException, MpjPlayerException {
            logger.trace("Player(" + mpjPlayer.getName() + ") waiting for answer");
            MpjAnswer answer = answerQueue.take();
            if (answer.exception != null) {
                logger.error("Player(" + mpjPlayer.getName() + ") got answer with exception", answer.exception);
                // TODO: Wie macht man das
                //throw answer.exception;
                throw new MpjPlayerException("[[" + answer.exception.toString() + "]]");
            } else {
                logger.trace("Player(" + mpjPlayer.getName() + ") got answer");
            }
            return answer;
        }

        // Nur mal testweise
        public interface MpjFinishListener {
            public void finish(MpjAnswer answer);
        }

        // Nur mal testweise, vermutlich keine gute Idee, hierfür immer einen Thread zu instanziieren
        public void onFinish(final MpjFinishListener func) {
            Thread thread = new Thread(mpjPlayer.getName()+ " waitfor answer") {
                @Override public void run() {
                    logger.trace("Player(" + mpjPlayer.getName() + ") waiting for answer");
                    try {
                        MpjAnswer answer = answerQueue.take();
                        if (answer.exception != null)
                            logger.error("Player(" + mpjPlayer.getName() + ") got answer with exception", answer.exception);
                        else
                            logger.trace("Player(" + mpjPlayer.getName() + ") got answer");
                        func.finish(answer);
                    } catch (InterruptedException e) {
                        // Ok, normale Threadbeendigung
                    }
                }
            };
            thread.start();
        }
    }

    public class MpjAnswer {
        public MpjPlayerException exception;
    }

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
            logger.trace("Player.Delegate created, name = " + name);
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
        public MpjAnswerHandle setTrack(MpjTrack newTrack, MpjPlaylistEntry newPle) throws InterruptedException, MpjPlayerException {
            if (track != null) {
                player.stopTrack();
                ple = null;
                track = null;
            }
            if (newTrack != null) {
                track = newTrack;
                ple = newPle;
            }
            return null; // TODO...
        }

        public MpjAnswerHandle setTrack(MpjTrack newTrack) throws InterruptedException, MpjPlayerException {
            return setTrack(newTrack, null);
        }

        public MpjAnswerHandle setTrack(MpjPlaylistEntry ple) throws InterruptedException, MpjPlayerException {
            if (ple == null) return setTrack(null, null);
            else return setTrack(ple.track, ple);
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
                    logger.error("sendPlayerEvent (" + name + ")", e);
                    e.printStackTrace();
                }
            }
        }

        public void setError(String msg) {
            logger.error("Player (" + name + "): " + msg);
            errorMessage = msg;
            setPlayerStateWithEvent(PlayerState.ERROR, PlayerEvent.ERROR);
        }

        public void setError(Throwable e) {
            logger.error("Player (" + name + ")", e);
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

        private BlockingQueue<MpjRunnable> commandQueue = new ArrayBlockingQueue<MpjRunnable>(10);

        public MpjAnswerHandle invokeCommand(final MpjRunnable func) throws InterruptedException, MpjPlayerException {
            synchronized (this) {
                logger.trace("Player(" + name + ") invokeCommand");
                MpjAnswerHandle answerHandle = new MpjAnswerHandle(player);
                func.answerQueue = answerHandle.answerQueue;
                commandQueue.put(func);
                return answerHandle;
            }
        }

        public void dispatchCommand(int timeout) throws InterruptedException {
            MpjRunnable func = null;
            if (timeout < 0) {
                logger.trace("Player(" + name + ") waiting for command to dispatch");
                func = commandQueue.take();
            } else
                func = commandQueue.poll(timeout, TimeUnit.MILLISECONDS);
            if (func != null) {
                logger.trace("Player(" + name + ") got command and execute em");
                MpjAnswer answer = new MpjAnswer();
                try {
                    func.run(answer);
                    logger.trace("Player(" + name + ") command execution done");
                } catch (MpjPlayerException e) {
                    answer.exception = e;
                    logger.error("Player(" + name + ") command execution exit with exception", e);
                }
                func.answerQueue.put(answer);
            }
        }
    }

}
