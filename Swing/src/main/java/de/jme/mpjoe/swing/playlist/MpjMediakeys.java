package de.jme.mpjoe.swing.playlist;

import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jme.toolbox.SystemInfo;

// Klasse zum Umgang mit den Mediakeys einer Tastatur, also wenn die Tastatur z.B. Tasten hat wie "Play", "Stop", "Next Track" et cetera
// Hier (http://stackoverflow.com/a/6312830/2880699) sagt zwar jemand was von VK_MEDIA_PLAY_PAUSE, aber die gibt es definitiv nicht
// Bzgl. der KeyEvents kann man die offenbar nur am rawCode erkennen
// - Linux (Kubuntu) liefert andere rawCodes als WinXP
// - Osx liefert mir gar keine Events
// Getestet ist das vorerst nur mit:
// - Natural Keyboard Pro
// - Lautstärketasten meines Dell M4400 Notebook
// - Logitech Cordless Keyboard

public class MpjMediakeys {
    public static final int VK_MEDIA_NONE         = 0;
    public static final int VK_MEDIA_VOLUME_MUTE  = 100001;
    public static final int VK_MEDIA_VOLUME_DOWN  = 100002;
    public static final int VK_MEDIA_VOLUME_UP    = 100003;
    public static final int VK_MEDIA_PLAY_PAUSE   = 100004;
    public static final int VK_MEDIA_STOP         = 100005;
    public static final int VK_MEDIA_PREV_TRACK   = 100006;
    public static final int VK_MEDIA_NEXT_TRACK   = 100007;
    public static final int VK_MEDIA              = 100008;

    // Unterscheidbar sind die Media Keys offenbar nur am internen "rawCode", der jedoch leider nicht via getter auslesbar ist.
    // Einzig über KeyEvent.toString komme ich an diesen Wert dran ...
    public static int check(KeyEvent evt) {
        // Zur Sicherheit und zwecks Performance ... bei den Mediakeys ist der KeyCode offenbar immer 0
        if (evt.getKeyCode() != 0) return VK_MEDIA_NONE;
        String dump = evt.toString();
        // innerhalb des Strings suche ich nun nach "rawCode=nnn"
        Pattern p = Pattern.compile("rawCode=[0-9]+");
        Matcher m = p.matcher(dump);
        if (!m.find()) return VK_MEDIA_NONE; // leider kein Treffer
        String s = m.group();
        if (m.find()) return VK_MEDIA_NONE; // noch ein Match? das darf nicht sein
        //System.out.println(s);
        String[] t = s.split("=");
        if (t.length != 2) return VK_MEDIA_NONE; // sollte nicht sein

        try {
            int rawCode = Integer.parseInt(t[1]);
            // hier habe ich jetzt Werte wie z.B. Play/Pause=172, Stop=174
            // allerdings liefert Kubuntu andere Werte als WinXP
            System.out.println("rawCode = " + rawCode);
            int vk = VK_MEDIA_NONE;
            if (SystemInfo.isLinux()) {
                switch (rawCode) {
                    case 121: vk = VK_MEDIA_VOLUME_MUTE; break;
                    case 122: vk = VK_MEDIA_VOLUME_DOWN; break;
                    case 123: vk = VK_MEDIA_VOLUME_UP  ; break;
                    case 172: vk = VK_MEDIA_PLAY_PAUSE ; break;
                    case 174: vk = VK_MEDIA_STOP       ; break;
                    case 173: vk = VK_MEDIA_PREV_TRACK ; break;
                    case 171: vk = VK_MEDIA_NEXT_TRACK ; break;
                    case 179: vk = VK_MEDIA            ; break;
                }
            }
            else if (SystemInfo.isWindows()) {
                switch (rawCode) {
                    case 173: vk = VK_MEDIA_VOLUME_MUTE; break;
                    case 174: vk = VK_MEDIA_VOLUME_DOWN; break;
                    case 175: vk = VK_MEDIA_VOLUME_UP  ; break;
                    case 179: vk = VK_MEDIA_PLAY_PAUSE ; break;
                    case 178: vk = VK_MEDIA_STOP       ; break;
                    case 177: vk = VK_MEDIA_PREV_TRACK ; break;
                    case 176: vk = VK_MEDIA_NEXT_TRACK ; break;
                    case 181: vk = VK_MEDIA            ; break;
                }
            }
            else return VK_MEDIA_NONE;

            System.out.println("vk = " + vk + " = " + toString(vk));
            return vk;
        } catch(NumberFormatException e) {
            return VK_MEDIA_NONE;
        }
    }

    public static String toString(int vk) {
        switch (vk) {
            case VK_MEDIA_NONE       : return "none";
            case VK_MEDIA_VOLUME_MUTE: return "mute";
            case VK_MEDIA_VOLUME_DOWN: return "volume down";
            case VK_MEDIA_VOLUME_UP  : return "volume up";
            case VK_MEDIA_PLAY_PAUSE : return "play/pause";
            case VK_MEDIA_STOP       : return "stop";
            case VK_MEDIA_PREV_TRACK : return "prev track";
            case VK_MEDIA_NEXT_TRACK : return "next track";
            case VK_MEDIA            : return "media";
            default: return "Unknown (" + vk + ")";
        }
    }

}
