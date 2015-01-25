package de.jme.mpjoe.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JPanel;

import de.jme.mpj.MpjPlayer;
import de.jme.mpj.MpjPlayer.MpjPlayerException;
import de.jme.mpj.MpjPlayer.PlayerEvent;
import de.jme.mpj.MpjPlayer.PlayerState;

// Evtl. interessant: http://www.jug-muenster.de/steelseries-java-swing-component-library-715/

/***********************************************************************************************************************
  Status und Funktionen der Buttons siehe MpjPlayer.java
***********************************************************************************************************************/

public class MpjPlayerSwing extends JPanel {
    private static final long serialVersionUID = 1L;

    private MpjPlayer mpjPlayer;
    private JPanel    buttonPanel;
    private MpjButton btnEject;
    private MpjButton btnStop;
    private MpjButton btnPlay;
    private MpjButton btnPause;
    private MpjButton btnPlayPause;
    private MpjButton btnMute;
    private MpjButton btnFade;
    private MpjButton btnHeadphone;
    private MpjButton btnPrevTrack;
    private MpjButton btnNextTrack;

    private class EjectAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public EjectAction() {
            super("Eject");
            setIconFromResource        ("/de/jme/mpj/Player/Eject-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/Eject-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                mpjPlayer.ejectTrack();
            } catch (InterruptedException | MpjPlayerException e) {
                // TODO: Exceptions ins Log und auf UI wenigstens ein bisschen signalisieren
                e.printStackTrace();
            }
        }
    }
    private EjectAction ejectAction = new EjectAction();
    public MpjAction getEjectAction() { return ejectAction; }

    private class StopAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public StopAction() {
            super("Stop");
            setIconFromResource        ("/de/jme/mpj/Player/Stop-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/Stop-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                mpjPlayer.stopTrack();
            } catch (InterruptedException | MpjPlayerException e) {
                e.printStackTrace();
            }
        }
    }
    private StopAction stopAction = new StopAction();
    public MpjAction getStopAction() { return stopAction; }

    private class PlayAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public PlayAction() {
            super("Play");
            setIconFromResource        ("/de/jme/mpj/Player/Play-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/Play-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                mpjPlayer.playTrack();
            } catch (InterruptedException | MpjPlayerException e) {
                e.printStackTrace();
            }
        }
    }
    private PlayAction playAction = new PlayAction();
    public MpjAction getPlayAction() { return playAction; }

    private class PauseAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public PauseAction() {
            super("Pause");
            setIconFromResource        ("/de/jme/mpj/Player/Pause-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/Pause-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                mpjPlayer.pauseTrack();
            } catch (InterruptedException | MpjPlayerException e) {
                e.printStackTrace();
            }
        }
    }
    private PauseAction pauseAction = new PauseAction();
    public MpjAction getPauseAction() { return pauseAction; }

    private class PlayPauseAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public PlayPauseAction() {
            super("Play / Pause");
            setIconFromResource          ("/de/jme/mpj/Player/Play-Pause-16.png");
            setSelectedIconFromResource  ("/de/jme/mpj/Player/Play-Pause-16-LightPlay.png");
            setThirdstateIconFromResource("/de/jme/mpj/Player/Play-Pause-16-LightPause.png");
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                mpjPlayer.playTrack();
            } catch (InterruptedException | MpjPlayerException e) {
                e.printStackTrace();
            }
        }
    }
    private PlayPauseAction playPauseAction = new PlayPauseAction();
    public MpjAction getPlayPauseAction() { return playPauseAction; }

    private class MuteAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public MuteAction() {
            super("Mute");
            setIconFromResource        ("/de/jme/mpj/Player/Mute-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/Mute-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            //try {
            //    mpjPlayer.toggleMute();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
        }
    }
    private MuteAction muteAction = new MuteAction();
    public MpjAction getMuteAction() { return muteAction; }

    private class FadeAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public FadeAction() {
            super("Fade");
            setIconFromResource        ("/de/jme/mpj/Player/FadeOut-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/FadeOut-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            //try {
            //    mpjPlayer.toggleFadeing();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
        }
    }
    private FadeAction fadeAction = new FadeAction();
    public MpjAction getFadeAction() { return fadeAction; }

    private class HeadphoneAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public HeadphoneAction() {
            super("Headphone");
            setIconFromResource        ("/de/jme/mpj/Player/Headphone-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/Headphone-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            //try {
            //    mpjPlayer.toggleHeadphone();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
        }
    }
    private HeadphoneAction headphoneAction = new HeadphoneAction();
    public MpjAction getHeadphoneAction() { return headphoneAction; }

    private class PrevTrackAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public PrevTrackAction() {
            super("PrevTrack");
            setIconFromResource        ("/de/jme/mpj/Player/PrevTrack-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/PrevTrack-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            //try {
            //    mpjPlayer.prevTrackTrack();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
        }
    }
    private PrevTrackAction prevTrackAction = new PrevTrackAction();
    public MpjAction getPrevTrackAction() { return prevTrackAction; }

    private class NextTrackAction extends MpjAction {
        private static final long serialVersionUID = 1L;
        public NextTrackAction() {
            super("NextTrack");
            setIconFromResource        ("/de/jme/mpj/Player/NextTrack-16.png");
            setSelectedIconFromResource("/de/jme/mpj/Player/NextTrack-16-Light.png");
        }
        public void actionPerformed(ActionEvent ae) {
            //try {
            //    mpjPlayer.nextTrackTrack();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
        }
    }
    private NextTrackAction nextTrackAction = new NextTrackAction();
    public MpjAction getNextTrackAction() { return nextTrackAction; }


    public MpjPlayerSwing(String name, MpjPlayer mpjPlayer) throws IOException {
        this.mpjPlayer = mpjPlayer;

        setLayout(new BorderLayout());
        // preferredSize bei z.B. 10 Butons
        // - für Linux und Osx sind 210 Pixel ausreichend
        // - Windows braucht hingegen 220 Pixel
        setPreferredSize(new Dimension(220, 200));
        setMinimumSize(new Dimension(0, 0));

        Object guiComponent = mpjPlayer.getGuiComponent();
        if (guiComponent instanceof Component)
            add((Component)guiComponent, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        // buttonPanel.setBackground(Color.GREEN); // nur Testweise um die Layoutgrenzen kenntlich zu machen

        btnEject = new MpjButton(ejectAction);
        buttonPanel.add(btnEject);

        btnStop = new MpjButton(stopAction);
        buttonPanel.add(btnStop);

        btnPlay = new MpjButton(playAction);
        buttonPanel.add(btnPlay);

        btnPause = new MpjButton(pauseAction);
        buttonPanel.add(btnPause);

        btnPlayPause = new MpjButton(playPauseAction);
        buttonPanel.add(btnPlayPause);

        btnMute = new MpjButton(muteAction);
        buttonPanel.add(btnMute);

        btnFade = new MpjButton(fadeAction);
        buttonPanel.add(btnFade);

        btnHeadphone = new MpjButton(headphoneAction);
        buttonPanel.add(btnHeadphone);

        btnPrevTrack = new MpjButton(prevTrackAction);
        buttonPanel.add(btnPrevTrack);

        btnNextTrack = new MpjButton(nextTrackAction);
        buttonPanel.add(btnNextTrack);

        add(buttonPanel, BorderLayout.SOUTH);

        updateActionStates();

        mpjPlayer.addListener(new MpjPlayer.EventListner() {
            @Override public void playerEvent(MpjPlayer player, PlayerEvent evt, PlayerState newState, PlayerState oldState) {
                updateActionStates();
            }
        });
    }

    private void updateActionStates() {
        PlayerState state = mpjPlayer.getPlayerState();
        boolean trackLoaded = mpjPlayer.getTrack() != null;
        ejectAction.setSelected(!trackLoaded);
        stopAction.setSelected(trackLoaded && (state == PlayerState.STOP || state == PlayerState.END));
        playAction.setSelected(trackLoaded && state == PlayerState.PLAYING);
        pauseAction.setSelected(trackLoaded && state == PlayerState.PAUSE);
        playPauseAction.setSelected(trackLoaded && state == PlayerState.PLAYING);
        muteAction.setSelected(false);
        fadeAction.setSelected(trackLoaded && (state == PlayerState.FADING_IN || state == PlayerState.FADING_OUT));
        headphoneAction.setSelected(false);
    }

    MpjPlayer getCorePlayer() {
        return mpjPlayer;
    }

}
