package de.jme.toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 * Ermittlung diverser Systeminformationen
 *
 * @author Joe Merten
 */
public class SystemInfo {

    static String userName;
    static String computerName;

    // Hmm, ändern auf "System.getProperty("user.name")" und "InetAddress.getLocalHost().getHostName()"?

    static public String getUserName() {
        if (userName == null) {
            // Unter Linux und Osx steht der Name des angemeldeten Benutzers in der Environmentvariablen "USER", unter Windows in "USERNAME"
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
            /*try {
                System.out.println("InetAddress.getLocalHost().getHostName() = " + java.net.InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }/**/
            // Unter Windows steht das in der Environmentvariablen "COMPUTERNAME" (evtl. auch in "USERDOMAIN" und "LOGONSERVER" (dort mit vorangestelltem \\)).
            // -> Vorerst nur für Windows XP geprüft
            String s = System.getenv("COMPUTERNAME");
            if (s == null || s.isEmpty()) {
                // Unter Linux ist dies mehr Aufwand
                // lt. StackOverflow macht man das ja üblicherweise mittels "InetAddress.getLocalHost().getHostName()", aber das geht z.B. bei Sven nicht
                // Also versuche ich es hier mit "uname -n"
                // Auf Osx bekomme ich hier sowas wie "N4s-MacBook-Pro.fritz.box"
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
            try {
                if (getMachineType() == MachineType.PcOsx) {
                    // ggf. das ".fritz.box" am Ende von "N4s-MacBook-Pro.fritz.box" entfernen
                    int dot = s.indexOf('.', 1);
                    if (dot > 0) s = s.substring(0, dot);
                }
            } catch (IOException e) { }
            computerName = s;
            //System.out.println("Computername = " + computerName);
        }
        return computerName;
    }

    private static MachineType machineType = null;

    public enum MachineType {
        PcLinux, PcWindows, PcOsx, Android;
        @Override public String toString() {
            switch(this) {
                case PcLinux  : return "PcLinux";
                case PcWindows: return "PcWindows";
                case PcOsx    : return "PcOsx";
                case Android  : return "Android";
                default: throw new IllegalArgumentException();
            }
        }
    };

    public static MachineType getMachineType() throws IOException {
        if (machineType == null) {
            // Linux + Osx:      Linux       Osx
            //   uname -s  ->   "Linux"     "Darwin"
            //   uname -m  ->  "i686" oder "x86_64"

            // Aber in Stackoverflow wird meistens System.getProperty("os.name") empfohlen:
            //   http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
            //   http://stackoverflow.com/questions/14288185/detecting-windows-or-linux
            //   https://java.net/projects/swingx/sources/svn/content/tags/swingx-project-1.6.4/swingx-common/src/main/java/org/jdesktop/swingx/util/OS.java?rev=4240

            // Linux (Kubuntu 14.04) = "Linux"
            // Windows (XP 32 Bit)   = "Windows XP"
            // Macbook               = "Mac OS X"

            String osName = System.getProperty("os.name");
            if (osName == null) throw new IOException("Can not determine Machine Type");
            osName = osName.trim().toLowerCase();

            if      (osName.equals("mac os x")      ) machineType = MachineType.PcOsx;
            else if (osName.indexOf("linux"  ) != -1) machineType = MachineType.PcLinux;
            else if (osName.indexOf("windows") != -1) machineType = MachineType.PcWindows;

            if (machineType == null)
                throw new IOException("Can not determine Machine Type (os.name = " + osName + ")");
            System.out.println("MachineType = " + machineType);
        }
        return machineType;
    }

}
