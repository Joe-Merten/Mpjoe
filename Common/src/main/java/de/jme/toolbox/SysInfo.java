package de.jme.toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Tool-Klasse (nur statische Methoden) zur Abfrage von Systemspezifika.
 * Zur Zeit kann nur abgefragt werden, ob wir auf einem Ews-Board oder aus einem PC laufen.
 *
 * @author sven
 */
public class SysInfo {

    static final Logger logger = LogManager.getLogger(SysInfo.class);

    private static MachineType machType = null;

    public enum MachineType { PC, Android };

    public static MachineType getMachineType() throws IOException {
        if (machType == null) {
            try {
                Process p = Runtime.getRuntime().exec("uname -m");
                BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String mt = bri.readLine();  // We expect only 1 line, so no loop necessary
                bri.close();
                p.waitFor();
                // TODO for Android
                /*if (mt.equals("armv7l"))
                    machType = MachineType...;
                else*/ if (mt.equals("x86_64") || mt.equals("i686"))
                    machType = MachineType.PC;
                else
                    throw new IOException("Can not determine Machine Type " + mt);
            } catch (IOException | InterruptedException e) {
                logger.error("getMachineType() with", e);
                throw new IOException("Can not determine Machine Type");
            }
        }
        return machType;
    }


}
