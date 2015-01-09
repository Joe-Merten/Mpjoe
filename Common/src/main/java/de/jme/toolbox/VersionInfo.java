package de.jme.toolbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Versionsinformationen der Applikation.
 *
 * Die Versionsinformationen werden biem Kompilieren vom Shellskript »InvokeVersion.sh« generiert
 * und über das Maven Buildskript im Propertyfile »<MainClassName>.buildinfo.properties« abgelegt.
 * Hier lesen wir das mittels getResourceAsStream() aus.
 *
 * Die Verwendung unterschiedlicher Namen für die Property-Files ist wichtig, weil durch die
 * Jar-Abhängigkeiten (z.B. Dpc verwendet Tts) Uneindeutigkeiten entstehen.
 *
 * Achtung: Wenn irgendwann mal per Maven kompiliert wurde, und anschliessend wieder mit dem
 * Eclipse-internen Java Compiler, dann sind die angezeigten Versionsinformationen (mitunter ziemlich
 * stark) veraltet, da dass File »...buildinfo.properties« in diesem Fall auf einem alten Stand stehen bleibt.
 * Hmm, da jetzt auch in Eclipse mit Maven übersetzt wird, sollte das Problem nicht länger bestehen - hab's aber nicht getestet.
 *
 * @author Joe Merten
 */
public class VersionInfo {

    static final Logger logger = LogManager.getLogger(VersionInfo.class);

    static private String vInfo;

    /**
     * Ermittelt den Versionsstring der Applikation
     */
    static public String getVersionInfo() {
        if (vInfo == null) {
            //printSystemProperties();
            //printSystemEnvironment();
            //printAllStackTraces();
            try {
                //if (vInfo == null) vInfo = getFromManifest();
                if (vInfo == null) vInfo = getFromProperties();
                if (vInfo == null)
                    // Hier kommen wir z.B. an, wenn das Projekt nicht per Maven gebaut wurde, sondern mit dem Eclipse-eigenen Java Compiler.
                    vInfo = "No version info available!";
            } catch (Exception e) {
                logger.error("getVersionInfo()) with", e);
                vInfo = "Error while determining the version info!";
            }
        }
        return vInfo;
    }

    /**
     * Versucht den Versionsstring aus dem Properties-File zu laden
     * @return  Versionsstring. null = nicht gefunden
     */
    static private String getFromProperties() throws IOException {
        String mainClassName = getMainClassName();
        //System.out.println("MainClass = " + mainClassName);

        if (mainClassName == null || mainClassName.isEmpty()) return null;
        Properties buildProps  = new Properties();
        InputStream stream = new VersionInfo().getClass().getResourceAsStream("/" + mainClassName + ".buildinfo.properties");
        if (stream == null) return null;
        buildProps.load(stream);
        return buildProps.getProperty("build.version");
    }

    /**
     * Versucht den Versionsstring aus dem Manifest zu laden
     * @return  Versionsstring. null = nicht gefunden
     *
     * Lösungsansatz vorerst verworfen, da es sich als recht schwierig erwiesen hat, das richtige Manifest zu lesen
     * Siehe auch: http://stackoverflow.com/questions/84486/how-do-you-create-a-manifest-mf-thats-available-when-youre-testing-and-running
     */
    @Deprecated // Deprecated markiert, weil es nicht (zuverlässig) funktioniert
    @SuppressWarnings("unused")
    static private String getFromManifest() throws IOException {
        // Ginge evtl. auch einfacher mittels System.getProperty("Implementation-Version") - aber noch nicht getestet
        // class.getResource() findet nicht immer das richtige Manifest, siehe: http://stackoverflow.com/questions/84486/how-do-you-create-a-manifest-mf-thats-available-when-youre-testing-and-running
        InputStream stream = new VersionInfo().getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
        if (stream == null) return null;
        Manifest mf = new Manifest(stream);
        printManifestEntries(mf);
        Attributes attr = mf.getMainAttributes();
        if (attr == null) return null;
        return attr.getValue("Implementation-Version");
    }

    /**
     * Ermitteln des Namen der Main-Class
     *
     * Siehe auch http://stackoverflow.com/questions/939932/how-to-determine-main-class-at-runtime-in-threaded-java-application
     * Allerdings konnte ich hier nichts mit »JAVA_MAIN_CLASS« finden.
     */
    //static public String getMainClassName() {
    //    for (final Map.Entry<String, String> entry : System.getenv().entrySet())
    //        if (entry.getKey().startsWith("JAVA_MAIN_CLASS"))
    //            return entry.getValue();
    //    throw new IllegalStateException("Cannot determine main class.");
    //}

    /**
     * Ermitteln des Namen der Main-Class
     *
     * @return  Name der Main-Class, z.B. "jme.mpjoe.swing.MainWin"
     * @throws  IllegalStateException, falls die Main-Class nicht ermittelt werden kann
     *
     * Siehe auch: http://stackoverflow.com/questions/939932/how-to-determine-main-class-at-runtime-in-threaded-java-application
     * Funktioniert nur, wenn der Mainthread »main« heisst.
     * Der Name der Main-Class steht zwar auch im Manifest, aber es hat sich aus recht schwierig erwiesen, das richtige Manifest zu lesen.
     * Siehe auch: http://stackoverflow.com/questions/84486/how-do-you-create-a-manifest-mf-thats-available-when-youre-testing-and-running
     */
    static public String getMainClassName() {
        Map<Thread,StackTraceElement[]> stackTraceMap = Thread.getAllStackTraces();
        for (Thread t : stackTraceMap.keySet()) { // TODO: Statt Schleife eher ein get("main")?
            if (t.getName().equals("main")) {
                StackTraceElement[] st = stackTraceMap.get(t);
                // Was hier ankommt, sieht ungefähr so aus:
                //   java.lang.Thread.dumpThreads(Native Method)
                //   java.lang.Thread.getMainClassName(Thread.java:1619)
                //   jme.toolbox.VersionInfo.printAllStackTraces(VersionInfo.java:129)
                //   jme.toolbox.VersionInfo.getVersionInfo(VersionInfo.java:49)
                //   jme.ews.tts.Tts.main(Tts.java:306)
                int idx = st.length - 1;
                if (idx < 0)
                    throw new IllegalStateException("Cannot determine main class (Main-Thread has no StackTrace-Elements).");
                StackTraceElement element = st[idx];
                String methodName = element.getMethodName();
                // Sicherheitsprüfung: Die Methode muss "main" heissen. Ok, bei static-Krams (Dinge die vor main() ausgeführt werden) heisst die Methode wohl "<clinit>" - ist zuweilen beim Dpc so vorzufinden.
                if (!methodName.equals("main") && !methodName.equals("<clinit>"))
                    throw new IllegalStateException("Cannot determine main class (StackTrace-Element Method-Name is \"" + methodName + "\" but either \"main\" or \"<clinit>\" was expected).");

                return element.getClassName();
            }
        }
        throw new IllegalStateException("Cannot determine main class (unable to locate Main-Thread.");
    }

    /**
     * Debugausgabe, Auflistung aller momentanen Stacktraces
     */
    static public void printAllStackTraces() {
        Map<Thread,StackTraceElement[]> stackTraceMap = Thread.getAllStackTraces();
        System.out.println("AllStackTraces:");
        for (Thread t : stackTraceMap.keySet()) {
            System.out.println("  Name = " + t.getName());
            StackTraceElement[] st = stackTraceMap.get(t);
            for (StackTraceElement element : st)
                System.out.println("    " + element);
        }
    }

    /**
     * Debugausgabe, Auflistung aller System-Properties
     */
    static public void printSystemProperties() {
        Properties props = System.getProperties();
        System.out.println("System.Properties:");
        for (String key : props.stringPropertyNames())
            System.out.println("  " + key + " = " + props .getProperty(key));
    }

    /**
     * Debugausgabe, Auflistung aller Einträge aus dem System-Enfironment (System.getenv())
     */
    static public void printSystemEnvironment() {
        System.out.println("System.Environment:");
        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            //System.out.println("  Entry = " + entry);
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }
    }

    /**
     * Debugausgabe, Auflistung aller Entries eines Manifestes
     */
    static public void printManifestEntries(Manifest manifest) {
        System.out.println("Manifest Entries of (" + manifest + "):");
        Map<String, Attributes> entries = manifest.getEntries();
        for (String entryName : entries.keySet()) {
            Attributes attrs = entries.get(entryName);
            for (Object attr : attrs.keySet()) {
                Attributes.Name attrName = (Attributes.Name)attr;
                String attrValue = attrs.getValue(attrName);
                System.out.println("  Attr = " + attr);
                System.out.println("  " + attrName + " =" + attrValue);
            }
        }
    }
}
