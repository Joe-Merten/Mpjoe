package de.jme.toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
            if (isAndroid()) {
                // TODO: Für Android implementieren
                userName = "";
            } else {
                // Unter Linux und Osx steht der Name des angemeldeten Benutzers in der Environmentvariablen "USER", unter Windows in "USERNAME"
                String s = System.getenv("USER");
                if (s == null || s.isEmpty()) s = System.getenv("USERNAME"); // für Windows
                if (s == null) s = "";  // Leerstring, falls nichts zu finden ist
                userName = s;
            }
            //System.out.println("Username = " + userName);
        }
        return userName;
    }

    static public String getComputerName() {
        if (computerName == null) {
            /*try {
                // Unter Android: benötigt android.permission.INTERNET, muss im Background Thread ausgeführt werden und liefert dann aber nur "localhost"
                System.out.println("InetAddress.getLocalHost().getHostName() = " + java.net.InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }/**/
            if (isAndroid()) {
                // TODO: Für Android implementieren
                computerName = "";
            } else {
                // Unter Windows steht das in der Environmentvariablen "COMPUTERNAME" (evtl. auch in "USERDOMAIN" und "LOGONSERVER" (dort mit vorangestelltem \\)).
                // -> Vorerst nur für Windows XP geprüft
                String s = System.getenv("COMPUTERNAME");
                //System.out.println("COMPUTERNAME = \"" + s + "\"");
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

                if (getMachineType() == MachineType.PcOsx) {
                    // ggf. das ".fritz.box" am Ende von "N4s-MacBook-Pro.fritz.box" entfernen
                    int dot = s.indexOf('.', 1);
                    if (dot > 0) s = s.substring(0, dot);
                }
                computerName = s;
            }
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

    public static MachineType getMachineType() /*throws IOException*/ {
        if (machineType == null) {
            //listAllSystemProperties();
            //listAllEnvironmentVariables();
            try {
                // Linux + Osx:      Linux       Osx
                //   uname -s  ->   "Linux"     "Darwin"
                //   uname -m  -> "i686" oder "x86_64"

                // Aber in Stackoverflow wird meistens System.getProperty("os.name") empfohlen:
                //   http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
                //   http://stackoverflow.com/questions/14288185/detecting-windows-or-linux
                //   https://java.net/projects/swingx/sources/svn/content/tags/swingx-project-1.6.4/swingx-common/src/main/java/org/jdesktop/swingx/util/OS.java?rev=4240

                // Linux (Kubuntu 14.04) = "Linux"
                // Android               = "Linux"
                // Macbook               = "Mac OS X"
                // Windows (XP 32 Bit)   = "Windows XP"

                String osName = System.getProperty("os.name");
                //System.out.println("osName = " + osName);
                if (osName == null) throw new IOException("Can not determine Machine Type");
                osName = osName.trim().toLowerCase();

                if (osName.equals("mac os x")) {
                    machineType = MachineType.PcOsx;
                }
                else if (osName.indexOf("linux") != -1) {
                    // Siehe auch: http://stackoverflow.com/a/4520732/2880699
                    //                     Linux                           Android
                    // java.vendor.url     "http://java.oracle.com/"       "http://www.android.com/"
                    // java.vm.name        "OpenJDK Server VM"             "Dalvik"
                    // java.vm.vendor      "Oracle Corporation"            "The Android Project"
                    // java.vendor         "Oracle Corporation"            "The Android Project"
                    // java.runtime.name   "OpenJDK Runtime Environment"   "Android Runtime"

                    // System.out.println("java.vendor.url   = \"" + System.getProperty("java.vendor.url"  ) + "\"");
                    // System.out.println("java.vm.name      = \"" + System.getProperty("java.vm.name"     ) + "\"");
                    // System.out.println("java.vm.vendor    = \"" + System.getProperty("java.vm.vendor"   ) + "\"");
                    // System.out.println("java.vendor       = \"" + System.getProperty("java.vendor"      ) + "\"");
                    // System.out.println("java.runtime.name = \"" + System.getProperty("java.runtime.name") + "\"");

                    if (System.getProperty("java.runtime.name").toLowerCase().equals("android runtime"))
                        machineType = MachineType.Android;
                    else
                        machineType = MachineType.PcLinux;
                }
                else if (osName.indexOf("windows") != -1) {
                    machineType = MachineType.PcWindows;
                }

                if (machineType == null)
                    throw new IOException("Can not determine Machine Type (os.name = " + osName + ")");
                System.out.println("MachineType = " + machineType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return machineType;
    }

    public static boolean isLinux() {
        return getMachineType() == MachineType.PcLinux;
    }

    public static boolean isWindows() {
        return getMachineType() == MachineType.PcWindows;
    }

    public static boolean isOsx() {
        return getMachineType() == MachineType.PcOsx;
    }

    public static boolean isAndroid() {
        return getMachineType() == MachineType.Android;
    }


    /**
     * Just debugoutput for testing purpose
     */
    public static void listAllSystemProperties() {
        System.out.println("===== System Properties =====");
        final Properties properties = System.getProperties();
        final Set<?> keys = properties.keySet();
        List<String> newKeys = new ArrayList<String>(keys.size());
        for (Object key : keys)
           newKeys.add(key.toString());
        Collections.sort(newKeys);
        for (String key : newKeys)
            System.out.println("    " + key + " = \"" + properties.get(key) + "\"");
    }

    /**
     * Just debugoutput for testing purpose
     */
    public static void listAllEnvironmentVariables() {
        System.out.println("===== Environment Variables =====");
        final Map<String, String> env = System.getenv();
        List<String> keys = new ArrayList<String>(env.keySet());
        Collections.sort(keys);
        for (String key : keys)
            System.out.println("    " + key + " = \"" + env.get(key) + "\"");
    }
}
