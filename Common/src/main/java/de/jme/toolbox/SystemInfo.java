package de.jme.toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Ermittlung diverser Systeminformationen
 *
 * @author Joe Merten
 *
 * TODO Joe + SH: SystemInfo (Client) und Sysinfo (Tts) sollte man m.E. zusammenführen
 */
public class SystemInfo {

    static String userName;
    static String computerName;

    static public String getUserName() {
        if (userName == null) {
            // Unter Linux steht der Name des angemeldeten Benutzers in der Environmentvariablen "USER", unter Windows in "USERNAME"
            String s = System.getenv("USER");
            if (s == null || s.isEmpty()) s = System.getenv("USERNAME"); // für Windows
            if (s == null) s = "";  // Leerstring, falls nichts zu finden ist
            userName = s;
            //System.out.println("Username = " + userName);
        }
        return userName;
    }

    static public String getComputerName() {
        if (computerName == null) {
            // Unter Windows steht das in der Environmentvariablen "COMPUTERNAME" (evtl. auch in "USERDOMAIN" und "LOGONSERVER" (dort mit vorangestelltem \\)).
            // -> Vorerst nur für Windows XP geprüft
            String s = System.getenv("COMPUTERNAME");
            if (s == null || s.isEmpty()) {
                // Unter Linux ist dies mehr Aufwand
                // lt. StackOverflow macht man das ja üblicherweise mittels "InetAddress.getLocalHost().getHostName()", aber das geht z.B. bei Sven nicht
                // Also versuche ich es hier mit "uname -n"
                Process p;
                try {
                    p = Runtime.getRuntime().exec("uname -n");
                    BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    s = bri.readLine();
                    bri.close();
                    p.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (s == null) s = "";  // Leerstring, falls nichts zu finden ist
            computerName = s;
            //System.out.println("Computername = " + computerName);
        }
        return computerName;
    }

}
