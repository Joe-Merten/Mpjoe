package jme.toolbox;

/**
 * Funktionen zur Zeitmessung
 *
 * Im Gegensatz zur vielfach im Web empfohlenen Vorgehensweise per System.currentTimeMillis() (was fehlerträchtig ist!)
 * verwende ich hier System.nanoTime().
 *
 * Bzgl. der Genauigkeit und Granularität unterliegen wir denselben Bedingungen wie System.nanoTime(), was plattformspezifisch
 * unterschiedliche Qualität haben kann. Zugesichert wird jedoch eine zu System.currentTimeMillis() mindestens gleiche Qualität.
 *
 * Der zugrunde liegende Datentyp (long) hat 64 Bit. Somit hat der Timer einen Messbereich (max. Zeit zwischen start() und stop()
 * bzw. auslesen) von 2^63ns = ca. 292 Jahre.
 *
 * Der Klassenname ist eine Wortverkürzung aus »Timer« und »Metering«. Ok, ich hätte es auch »Timekeeping« oder »Timing« nennen können.
 *
 * Verwendung z.B.:
 *     Timeter t = new Timeter("Codelaufzeit");
 *     ... diverser Code ...
 *     logger.trace(t);
 *
 * @author Joe Merten
 */
public class Timeter {
    private String  name;      // Optinaler Name des Timers
    private long    startVal;  // Start-Zeitpunkt
    private long    stopVal;   // Stop-Zeitpunkt (nur wenn stopped=true)
    private boolean running;   // Flag, ob der Timer läuft

    /**
     * Konstruktor, Timer wird implizit gestartet.
     */
    public Timeter() {
        start();
    }

    /**
     * Konstruktor mit vorgegebenem Startwert
     *
     * @param  startVal  Startwert, muss kompatibel sein zu System.nanoTime()
     */

    public Timeter(long startVal) {
        this.startVal = startVal;
        running = true;
    }

    /**
     * Konstruktor, Timer wird implizit gestartet.
     * @param  timerName  Optionaler Name des Timers
     */
    public Timeter(String timerName) {
        name = timerName;
        start();
    }
    /**
     * @return  Name des Timers (kann null sein).
     */
    public String getName() {
        return name;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Starten (bzw. Neustarten) des Timers.
     */
    public void start() {
        startVal = System.nanoTime();
        running = true;
    }

    /**
     * Stoppen des Timers.
     */
    public void stop() {
        if (running) {
            stopVal = System.nanoTime();
            running = false;
        }
    }

    /**
     * Stoppen des Timers revidieren.
     */
    public void resume() {
        running = true;
    }

    /**
     * Ermitteln der verstrichenen Zeit zwischen start() und stop() in Sekunden (abgerundet).
     */
    public long getElapsedS() {
        return getElapsedNs() / 1000000000;
    }

    /**
     * Ermitteln der verstrichenen Zeit zwischen start() und stop() in Millisekunden (abgerundet).
     */
    public long getElapsedMs() {
        return getElapsedNs() / 1000000;
    }

    /**
     * Ermitteln der verstrichenen Zeit zwischen start() und stop() in Mikrosekunden (abgerundet).
     */
    public long getElapsedUs() {
        return getElapsedNs() / 1000;
    }

    /**
     * Ermitteln der verstrichenen Zeit zwischen start() und stop() in Nanosekunden.
     *
     * Wurde seit dem letzten start() noch kein stop() gerufen, dann wird als stop()-Zeitpunkt temrorär »jetzt« impliziert.
     */
    public long getElapsedNs() {
        long ret = 0;
        if (running)
            ret = System.nanoTime();
        else
            ret = stopVal;
        ret -= startVal;
        if (ret < 0)
          throw new TimeterException("Overflow (elapsed = " + ret + "ns)");
        return ret;
    }

    @Override public String toString() {
        long val = getElapsedNs();
        String n = "";
        if (name != null && !name.isEmpty())
            n = name + "=";
        return n + nsToHumanReadable(val);
    }

    static public String nsToString(long ns, int decimals) {
        if (decimals == 0)
            return toStringWithDots(ns) + "ns";
        if (decimals == 3)
            return toStringWithDotsAndComma(ns, 3) + "µs";
        if (decimals == 6)
            return toStringWithDotsAndComma(ns, 6) + "ms";
        if (decimals == 9)
            return toStringWithDotsAndComma(ns, 9) + "s";
        else
            throw new IllegalArgumentException("Unsupported count of decimals");
    }

    static public String nsToHumanReadable(long ns) {
        long a = ns;
        if (a < 0) a = -a;
        if (a < 1000)                         // 0ns bis 1.000ns
            return nsToString(ns, 0);
        else if (a < 1000000)                 // 1,000µs bis 1.000,000µs
            return nsToString(ns, 3);
        else if (a < 1000000000)              // 1,000ms bis 1.000,000ms
            return usToString(ns / 1000, 3);
        else if (a < 1000000000000L)          // 1,000s  bis 1.000,000s
            return msToString(ns / 1000000, 3);
        else if (a < 100L*3600L*1000000000L)  // 00:16:40 bis 100:00:00  (HH:MM:SS)
            return secsToHhmmssString(ns / 1000000000, false);
        else                                  // 4d 04:00:00 bis ...
            return secsToHhmmssString(ns / 1000000000, true);
    }

    static public String usToString(long us, int decimals) {
        if (decimals == 0)
            return toStringWithDots(us) + "µs";
        if (decimals == 3)
            return toStringWithDotsAndComma(us, 3) + "ms";
        if (decimals == 6)
            return toStringWithDotsAndComma(us, 6) + "s";
        else
            throw new IllegalArgumentException("Unsupported count of decimals");
    }

    static public String usToHumanReadable(long us) {
        long a = us;
        if (a < 0) a = -a;
        if (a < 1000)                     // 0µs bis 1.000µs
            return usToString(us, 0);
        else if (a < 1000000)             // 1,000ms bis 1.000,000ms
            return usToString(us, 3);
        else if (a < 1000000000)          // 1,000s  bis 1.000,000s
            return msToString(us / 1000, 3);
        else if (a < 100L*3600L*1000000L) // 00:16:40 bis 100:00:00  (HH:MM:SS)
            return secsToHhmmssString(us / 1000000, false);
        else                              // 4d 04:00:00 bis ...
            return secsToHhmmssString(us / 1000000, true);
    }

    static public String msToString(long ms, int decimals) {
        if (decimals == 0)
            return toStringWithDots(ms) + "ms";
        if (decimals == 3)
            return toStringWithDotsAndComma(ms, 3) + "s";
        else
            throw new IllegalArgumentException("Unsupported count of decimals");
    }

    static public String msToHumanReadable(long ms) {
        long a = ms;
        if (a < 0) a = -a;
        if (a < 1000)                     // 0ms bis 1.000ms
            return msToString(ms, 0);
        else if (a < 1000000)             // 1,000s  bis 1.000,000s
            return msToString(ms, 3);
        else if (a < 100L*3600L*1000L)    // 00:16:40 bis 100:00:00  (HH:MM:SS)
            return secsToHhmmssString(ms / 1000, false);
        else                              // 4d 04:00:00 bis ...
            return secsToHhmmssString(ms / 1000, true);
    }

    static public String secsToHhmmssString(long secs, boolean seperateDays) {
        boolean neg = secs < 0;
        if (neg) secs = -secs;

        StringBuilder b = new StringBuilder(20);  // "12.3454d 04:00:00" = 17 Zeichen

        if (seperateDays) {
            long days = secs / (24 * 3600);
            secs -= days * 24 * 3600;
            b.append(toStringWithDotsAndComma(days, 0));
            b.append("d ");
        }

        long hours = secs / 3600;
        secs -= hours * 3600;
        long mins = secs / 60;
        secs -= mins * 60;
        b.append(String.format("%02d:%02d:%02d", hours, mins, secs));

        if (neg)
            b.insert(0, "-");
        return b.toString();
    }

    /**
     * Wandelt einen numerischen Wert in einen String und fügt dabei Tausender-Separatoren ein.
     *
     * @param  val       Zu konvertierender numerischer Wert
     * @param  decimals  Anzahl der impliziten Dezimalstellen
     */
    static public String toStringWithDotsAndComma(long val, int decimals) {
        boolean neg = val < 0;
        if (neg) val = -val;

        StringBuilder b = new StringBuilder(Long.toString(val));

        int vkEnd = b.length(); // Index auf das nächste Zeichen nach dem Vorkomma-Anteil.

        if (decimals > 0) {
            while (b.length() <= decimals)
                b.insert(0, "0");
            vkEnd = b.length() - decimals;
            b.insert(vkEnd, ",");
        }

        int i = vkEnd - 3;
        while (i >= 1) {
            b.insert(i, ".");
            i -= 3;
        }

        if (neg)
            b.insert(0, "-");
        return b.toString();
    }

    static public String toStringWithDots(long val) {
        return toStringWithDotsAndComma(val, 0);
    }


//    /*******************************************************************************************************************
//     * Interface für »Software-Timer«, also irgendwann »Starten« und später die verstrichene Zeit auslesen.
//     *
//     * Verwendung z.B.:
//     *     long t = Timeter.start();
//     *     ... diverser Code ...
//     *     long elapsed = Timeter.getElapsedMilli(t);
//     *     loggeer.trace("Es sind " + elapsed + "ms vergangen");
//     *
//     * … … … hab' ich mir nun doch anders überlegt … … …
//     ******************************************************************************************************************/
//
//    /**
//     * Starten einer Zeitmessung.
//     * @return  Der Rückgabewert dieser Funktion ist dazu gedacht, ihn einer der Funktionen getElapsed...() zu übergeben.
//     */
//    static public long start() {
//        return impGetNano();
//    }
//
//    /**
//     * Auslesen einer Zeitmessung in Sekunden.
//     * @param  start  Der beim Aufruf von start() erhaltene Wert.
//     * @return        Vergangene Zeit seit start() in Sekunden.
//     */
//    static public long getElapsedSeconds(long start) {
//        return getElapsedNano(start) / 1000000000;
//    }
//
//    /**
//     * Auslesen einer Zeitmessung in Millisekunden.
//     * @param  start  Der beim Aufruf von start() erhaltene Wert.
//     * @return        Vergangene Zeit seit start() in Millisekunden.
//     */
//    static public long getElapsedMilli(long start) {
//        return getElapsedNano(start) / 1000000;
//    }
//
//    /**
//     * Auslesen einer Zeitmessung in Mikrosekunden.
//     * @param  start  Der beim Aufruf von start() erhaltene Wert.
//     * @return        Vergangene Zeit seit start() in Mikrosekunden.
//     */
//    static public long getElapsedMicro(long start) {
//        return getElapsedNano(start) / 1000;
//    }
//
//    /**
//     * Auslesen einer Zeitmessung in Nanosekunden.
//     * @param  start  Der beim Aufruf von start() erhaltene Wert.
//     * @return        Vergangene Zeit seit start() in Nanosekunden.
//     */
//    static public long getElapsedNano(long start) {
//        long ret = impGetNano() - start;
//        if (ret < 0)
//            throw new TimeterException("Timer overflow (elapsed = " + ret + "ns)");
//        return ret;
//    }


    /*******************************************************************************************************************
     * Weitere Methoden.
     ******************************************************************************************************************/

    /**
     * Zählerstand des Monotonic Counter in Millisekunden.
     *
     * Diese Funktion ist für Zeitmessungen gedacht als adäquater Ersatz für das fehlerträchtige System.currentTimeMillis()
     *
     * Wozu ein Ersatz für System.currentTimeMillis()?
     * Wenn man System.currentTimeMillis() zur Zeitmessung verwendet, unterliegt man einer nicht unerheblichen Gefahr von Messfehlern,
     * die zugegeben meist eher selten Auftreten, aber nicht vernachlässigt werden sollten.
     * System.currentTimeMillis() liefert die aktuelle UTC Systemzeit in Form von »Anzahl der Millisekunden seit dem 1.1.1970«.
     * - Wird also während einer Zeitmessung (also zwischen den Aufrufen von currentTimeMillis()) die Systemzeit verstellt,
     *   so misst man Unsinn. Verwendet man dies z.B. zur Realisierung von Timeouts, so werden diese u.U. viel zu lang oder kurz.
     * - Auch bei der Nachsynchronisation der Systemzeit (z.B. mittels NTP) kann es zu Messfehlern kommen, da viele Betriebssysteme
     *   unter bestimmten Umständen dann die Systemzeit nicht schlagartig umstellen, sondern den »Uhren-Takt« für eine bestimmte
     *   Zeit nur etwas beschleunigen oder drosseln.
     *
     * Einschränkung.
     * Die momentane Implementation funktioniert nur solange System.nanoTime() positive Werte liefert.
     * Bei den bisher von mir untersuchten Systemen stellt dies aber nicht wirklich ein Problem dar.
     * Getestet auf PC Linux (Kubuntu 12.04, 32 Bit), Windows XP (32 Bit), Karo TX6Q (Buildroot Linux).
     * Bei allen Systemen läuft der »Monotonic Counter« beim booten mit 0 los und wir haben einen Wertebereich von 63 Bit zur Verfügung.
     * Somit haben wir also 2^63ns = ca. 106.751 Tage = ca. 292 Jahre Zeit bis zum Überlauf.
     * -> Im Falle eines solchen (also unwahrscheinlichen) Überlaufs, lösen wir hier eine RuntimeException aus.
     */
    static public long getMilli() {
        long ret = System.nanoTime();
        if (ret < 0)
            throw new TimeterException("System.nanoTime() returns unexpexted value (" + ret + ")");
        return ret / 1000000;
    }

    /**
     * Exception für ungewöhnliche Umstände.
     */
    public static class TimeterException extends RuntimeException {
        private static final long serialVersionUID = 5454989696420010718L;
        public TimeterException(String desc) { super(desc); }
    }

}
