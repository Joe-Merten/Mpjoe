package de.jme.jsi;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Debugmonitor
 *
 * @author Joe Merten
 */
public class Monitor implements Runnable {

    static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    private Jsi         jsi;
    private InputStream is;
    private Console     con;
    private PrintStream os;
    private BufferedReader reader;

    /**
     * Defaultkonstruktor, verwendet die globale Jsi-Instanz sowie System.in und System.out
     *
     * @throws IOException
     */
    public Monitor() throws IOException {
        this.jsi = Jsi.instance;
        impInit();
    }

    public Monitor(Jsi jsi) throws IOException {
        this.jsi = jsi;
        impInit();
    }

    private void impInit() throws IOException {
        /*con = System.console();
        if (con != null) {
            logger.debug("Monitor uses System.console()");
            //is = con.reader().
        } else*/ {
            is = System.in;
            if (is == null)
                throw new IOException("No input stream available");
            logger.debug("Monitor uses System.in");
            reader = new BufferedReader(new InputStreamReader(System.in));
        }

        os = System.out;
        if (os == null)
            throw new IOException("No output stream available");
    }

    @Override public void run() {
        try {
            impRun();
        } catch (InterruptedException e) {
            // Dies ist ein normaler Weg das run() zu verlassen
        } catch (IOException e) {
            logger.error("run() with", e);
        }
        os.println("Debuginterface terminated");
    }

    private void impRun() throws IOException, InterruptedException {
        os.println("Debuginterface started");
        Jsi.SessionData session = new Jsi.SessionData(is, os);
        for (;;) {
            String cmdline = readLine();  // Achtung, der kann wohl auch null returnieren (evtl. bei Eof?)
            // os.println("Commandline = \"" + cmdline + "\"");
            try {
                jsi.executeAction(cmdline, session);
            } catch (Jsi.JsiException e) {
                os.println(e);
            } catch (Throwable e) {
                logger.warn("impRun() with", e);
                // Falls der Debugmonitor auf einem von System.out abweichendne Stream läuft (z.B. via Tcp-Server, momentan noch nicht implementiert), dann auch auf dem OutStream des Monitors ausgeben.
                // Falls jetzt der Monitor auf System.out läuft und der Logger auch nach System.out ausgibt - naja, dann kommt der Stacktrace derzeit doppelt.
                e.printStackTrace(os);  // Stacktrace nicht nach System.err sondern auf unseren Output-Stream!
            }
            Thread.sleep(100);
        }
    }

    private String readLine() throws IOException {
        os.print("-> ");
        if (con != null) {
            return con.readLine();
        } else {
            return reader.readLine();
        }
    }

}
