package de.jme.toolbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Versionsinformationen der Applikation.
 *
 * Die Versionsinformationen werden biem Kompilieren vom Shellskript »InvokeVersion.sh« generiert
 * und über das Maven Buildskript im Propertyfile »de.jme.application.buildinfo.properties« abgelegt.
 * Hier lesen wir das mittels getResourceAsStream() aus.
 *
 * TODO: Abgleich / Synchronisation zwischen den verschiedenen Versionsnummern:
 * - Meine Versionsnummer aus InvokeVersion.sh -> "1", aber zzgl. git rev, Timestamp et cetera
 * - pom.xml (0.0.1-SNAPSHOT)
 * - evtl. bei .jar im MANIFEST.MF
 * - bei Android im AndroidManifest.xml -> android:versionCode="1" und android:versionName="0.0.1"
 * - bei Distribution als Debian Package via InvokeVersion.sh
 *
 * @author Joe Merten
 */
public class VersionInfo {

    static final Logger logger = LoggerFactory.getLogger(VersionInfo.class);

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
        //String mainClassName = getMainClassName();
        //System.out.println("MainClass = " + mainClassName);
        //logger.trace("getFromManifest = \"" + getFromManifest() + "\"");
        //logger.trace("mainClassName = \"" + mainClassName + "\"");
        //if (mainClassName == null || mainClassName.isEmpty()) return null;
        // String propertiesPath = "/" + mainClassName + ".buildinfo.properties"
        String propertiesPath = "/de.jme.application.buildinfo.properties";
        InputStream stream = new VersionInfo().getClass().getResourceAsStream(propertiesPath);
        if (stream == null) return null;
        Properties buildProps  = new Properties();
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
     *
     * Auf 'nem PC sieht der Stacktrace in etwa so aus:
     *     java.lang.Thread.dumpThreads(Native Method)
     *     java.lang.Thread.getAllStackTraces(Thread.java:1640)
     *     de.jme.toolbox.VersionInfo.printAllStackTraces(VersionInfo.java:152)
     *     de.jme.toolbox.VersionInfo.getMainClassName(VersionInfo.java:119)
     *     de.jme.toolbox.VersionInfo.getFromProperties(VersionInfo.java:58)
     *     de.jme.toolbox.VersionInfo.getVersionInfo(VersionInfo.java:41)
     *     de.jme.mpjoe.swing.MainWin.main(MainWin.java:187)
     *     de.jme.mpjoe.swing.Mpjoe.main(Mpjoe.java:27)
     *
     * Auf Android hingegen aber eher so:
     *     dalvik.system.VMStack.getThreadStackTrace(Native Method)
     *     java.lang.Thread.getStackTrace(Thread.java:579)
     *     java.lang.Thread.getAllStackTraces(Thread.java:521)
     *     de.jme.toolbox.VersionInfo.printAllStackTraces(VersionInfo.java:152)
     *     de.jme.toolbox.VersionInfo.getMainClassName(VersionInfo.java:119)
     *     de.jme.toolbox.VersionInfo.getFromProperties(VersionInfo.java:58)
     *     de.jme.toolbox.VersionInfo.getVersionInfo(VersionInfo.java:41)
     *     de.jme.mpjoe.android.Mpjoe.onCreate(Mpjoe.java:33)                         <<<<<<<<<<<<<<<<<< hier ein Treffer, aber unsicher
     *     android.app.Activity.performCreate(Activity.java:5473)
     *     android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1093)
     *     android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2292)
     *     android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2386)
     *     android.app.ActivityThread.access$900(ActivityThread.java:169)
     *     android.app.ActivityThread$H.handleMessage(ActivityThread.java:1277)
     *     android.os.Handler.dispatchMessage(Handler.java:102)
     *     android.os.Looper.loop(Looper.java:136)
     *     android.app.ActivityThread.main(ActivityThread.java:5476)
     *     java.lang.reflect.Method.invokeNative(Native Method)
     *     java.lang.reflect.Method.invoke(Method.java:515)
     *     com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1268)
     *     com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1084)
     *     dalvik.system.NativeStart.main(Native Method)
     * Auch Android: System.property java.io.tmpdir = "/data/data/de.jme.mpjoe.android/cache", aber das schein mir zu unsicher
     */
    static public String getMainClassName() {
        Map<Thread,StackTraceElement[]> stackTraceMap = Thread.getAllStackTraces();
        for (Thread t : stackTraceMap.keySet()) { // TODO: Statt Schleife eher ein get("main")?
            if (t.getName().equals("main")) {
                StackTraceElement[] st = stackTraceMap.get(t);
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
