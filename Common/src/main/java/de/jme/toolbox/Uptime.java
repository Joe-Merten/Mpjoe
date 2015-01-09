package de.jme.toolbox;

/**
 * Uptime des Systems und der Applikation
 *
 * @author Joe Merten
 */
public class Uptime {
    static private Timeter appUptime = new Timeter();
    static private Timeter sysUptime = new Timeter(0); // Timeter mit Startwert (TODO: Sicherstellen, dass das immer so ist. Ansonsten auf z.B. /proc/uptime ausweichen)

    static public Timeter getAppUptime() {
        return appUptime;
    }

    static public Timeter getSysUptime() {
        return sysUptime;

        /*  try (FileInputStream source = new FileInputStream("/proc/uptime");
        Scanner scanner = new Scanner(source); )
   {
       String s = scanner.next();              // Uptime in s mit Nachkommastellen
       float f = Float.parseFloat(s) * 1000;   // Uptime in ms
       systemUpTime = DatatypeFactory.newInstance().newDurationDayTime((long)f);
   }
*/
    }

}
