package de.jme.mpjoe.swing;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.jme.mpj.MpjPlayer;
import de.jme.mpj.MpjTrack;

/**
 * Klasse zur Wiedergabe von Audiodateien im Mpjoe Java Swing Client mittels javax.sound
 * Funktioniert wohl nur mit Wav Dateien.
 *
 * @author Joe Merten
 */
public class MpjPlayerSound extends MpjPlayerVlc  { public MpjPlayerSound(String name) throws IOException { super(name); } }

/*
public class MpjPlayerSound extends MpjPlayer implements AutoCloseable {

    public MpjPlayerSound(String name) {
        super(name);
        start();
    }

    @Override public void run() {
        try {
            while (true) {
                MpjTrack track = getTrack();
                if (track != null) {
                    try {
                        // Das Abspielen einer Audiodatei ist offenbar kompliziert.
                        AudioInputStream stream = null;
                        if (track.getAudioData() != null)
                            stream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(track.getAudioData()));
                        else
                            stream = AudioSystem.getAudioInputStream(track.getUri().toURL());
                        AudioFormat format = stream.getFormat();
                        Info info = new DataLine.Info(Clip.class, format);
                        Clip clip = (Clip)AudioSystem.getLine(info);
                        clip.open(stream);
                        long trackLength = clip.getMicrosecondLength();
                        long trackPos = 0;
                        //pe.clip = clip;
                        clip.start();
                        //currentTrack = pe;
                        setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_START);
                        //checkCleanup();
                        while (true) { // Warten bis der Clip gestartet ist
                            if (clip.isRunning()) break;
                            trackPos = clip.getMicrosecondPosition();
                            if (trackPos > 0 || trackLength <= 0) break;
                            Thread.sleep(10);
                        }
                        long t0 = System.nanoTime();
                        while (clip.isRunning()) { // Warten bis der Clip zu Ende ist
                            trackPos = clip.getMicrosecondPosition();
                            long t1 = System.nanoTime();
                            if (t1 - t0 >= 1000 * 1000000) { // Jede Sekunde ein Progress-Event
                                t0 = t1;
                                setPlayerStateWithEvent(PlayerState.PLAYING, PlayerEvent.TRACK_PROGRESS);
                            }
                            //if (pe.trackPos >= pe.trackLength - 200 * 1000) break; // Auf diese Weise liesse sich noch ein "Überblenden" erzielen und die von Acapela eingefügte Stille (ca. 400ms) noch etwas unterbinden.
                            //System.out.println("Len=" + pe.trackLength + " pos=" + pe.trackPos);
                            Thread.sleep(20);
                        }
                        // close() rufen wir später, damit uns das hier keine Latenz zwischen den Tracks bringt.
                        setPlayerStateWithEvent(PlayerState.IDLE, PlayerEvent.TRACK_END);
                        ejectTrack(); // TODO: Hack erst mal
                        //currentTrack = null;
                        //toClose.add(pe);
                    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                        ejectTrack(); // TODO: Hack erst mal
                        setError(e);
                    }
                } else {
                    //currentTrack = null;
                    //checkCleanup();
                    setPlayerState(PlayerState.IDLE);
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            // Das ist eine normale Thread-Beendigung
        }
    }

    @Override public void close() throws IOException {
        // TODO ...
    }

}
*/