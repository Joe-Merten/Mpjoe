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

    static final String    buttonStyle = "Mpj-16";
    //static final String    buttonStyle = "Vlcj-32";
    //static final String    buttonStyle = "Kde-16";
    //static final String    buttonStyle = "Kde-22";
    //static final String    buttonStyle = "Kde-32";
    //static final String    buttonStyle = "Kde-48";
    static final boolean   mpj1buttons = buttonStyle.equals("Mpj-16");
    static final String    buttonPath = "/de/jme/mpj/Player/Buttons-" + buttonStyle + "/";

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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "Eject.png");
                setSelectedIconFromResource(buttonPath + "Eject-Light.png");
            } else {
                setIconFromResource        (buttonPath + "Eject.png");
            }
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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "Stop.png");
                setSelectedIconFromResource(buttonPath + "Stop-Light.png");
            } else {
                setIconFromResource        (buttonPath + "Stop.png");
            }
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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "Play.png");
                setSelectedIconFromResource(buttonPath + "Play-Light.png");
            } else {
                setIconFromResource        (buttonPath + "Play.png");
            }
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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "Pause.png");
                setSelectedIconFromResource(buttonPath + "Pause-Light.png");
            } else {
                setIconFromResource        (buttonPath + "Pause.png");
            }
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                mpjPlayer.togglePauseTrack();
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
            if (mpj1buttons) {
                setIconFromResource          (buttonPath + "Play-Pause.png");
                setSelectedIconFromResource  (buttonPath + "Play-Pause-LightPlay.png");
                setThirdstateIconFromResource(buttonPath + "Play-Pause-LightPause.png");
            } else {
                setIconFromResource          (buttonPath + "Play.png");
                setThirdstateIconFromResource(buttonPath + "Pause.png");
            }
        }
        public void actionPerformed(ActionEvent ae) {
            try {
                mpjPlayer.doPlayPauseTrack();
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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "Nomute.png");
                setSelectedIconFromResource(buttonPath + "Mute.png");
            } else {
                setIconFromResource        (buttonPath + "Nomute.png");
                setSelectedIconFromResource(buttonPath + "Mute.png");
            }
        }
        public void actionPerformed(ActionEvent ae) {
            // TODO
            setSelected(!isSelected());
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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "FadeOut.png");
                setSelectedIconFromResource(buttonPath + "FadeOut-Light.png");
            } else {
                //setIconFromResource        (buttonPath + "....png");
            }
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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "Headphone.png");
                setSelectedIconFromResource(buttonPath + "Headphone-Light.png");
            } else {
                //setIconFromResource        (buttonPath + "....png");
            }
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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "PrevTrack.png");
                setSelectedIconFromResource(buttonPath + "PrevTrack-Light.png");
            } else {
                setIconFromResource        (buttonPath + "PrevTrack.png");
            }
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
            if (mpj1buttons) {
                setIconFromResource        (buttonPath + "NextTrack.png");
                setSelectedIconFromResource(buttonPath + "NextTrack-Light.png");
            } else {
                setIconFromResource        (buttonPath + "NextTrack.png");
            }
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
        //int prefWidth = btnEject
        //setPreferredSize(new Dimension(220, 200));
        //setMinimumSize(new Dimension(0, 0));

        Object guiComponent = mpjPlayer.getGuiComponent();
        if (guiComponent instanceof Component)
            add((Component)guiComponent, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        // buttonPanel.setBackground(Color.GREEN); // nur Testweise um die Layoutgrenzen kenntlich zu machen


        int imageSize = ejectAction.getIcon().getIconWidth();
        int btn = imageSize + 6;
        if (btn < 24) btn = 24;
        Dimension buttonSize = new Dimension(btn, btn);

        btnEject = new MpjButton(ejectAction, buttonSize);
        buttonPanel.add(btnEject);

        btnStop = new MpjButton(stopAction, buttonSize);
        buttonPanel.add(btnStop);

        btnPlay = new MpjButton(playAction, buttonSize);
        buttonPanel.add(btnPlay);

        btnPause = new MpjButton(pauseAction, buttonSize);
        buttonPanel.add(btnPause);

        btnPlayPause = new MpjButton(playPauseAction, buttonSize);
        buttonPanel.add(btnPlayPause);

        btnMute = new MpjButton(muteAction, buttonSize);
        buttonPanel.add(btnMute);

        btnFade = new MpjButton(fadeAction, buttonSize);
        buttonPanel.add(btnFade);

        btnHeadphone = new MpjButton(headphoneAction, buttonSize);
        buttonPanel.add(btnHeadphone);

        btnPrevTrack = new MpjButton(prevTrackAction, buttonSize);
        buttonPanel.add(btnPrevTrack);

        btnNextTrack = new MpjButton(nextTrackAction, buttonSize);
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
        playPauseAction.setThirdstate(trackLoaded && state == PlayerState.PAUSE);
        playPauseAction.setBlinking(trackLoaded && state == PlayerState.PAUSE);
        muteAction.setSelected(false);
        fadeAction.setSelected(trackLoaded && (state == PlayerState.FADING_IN || state == PlayerState.FADING_OUT));
        headphoneAction.setSelected(false);
    }

    MpjPlayer getCorePlayer() {
        return mpjPlayer;
    }

}
