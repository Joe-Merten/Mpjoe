package de.jme.toolbox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for handling the system time
 *
 * @author Joe Merten
 */
public class SysTime {

    static Logger logger = LoggerFactory.getLogger(SysTime.class);

    /**
     * Setzen der Systemzeit.
     * Auf Zielsystem PC wird bei fehlenden Rechten nur eine Meldung ausgegeben.
     * Bei anderen Zielsystemen hingegen wird bei fehlenden Rechten eine SecurityException geworfen.
     * Nach dem Setzen der Systemzeit wird diese auch zur HW-RTC synchronisiert, sodass die neue gesetzte Zeit
     * auch nach Abschalten der Betriebsspannung erhalten bleibt.
     * Diese Methode ist derzeit nur für Linux Targets implementiert.
     * @param  dateTime  Zu setzendes Datum / Uhrzeit als String (Utc)
     *
     * TODO JM: Testcode mit Zeitzonen und LocalTime versus UTC.
     */
    public static void setSystemTimeUtc(String dateTime) throws IOException, InterruptedException {
        // execute #date -u  -s "2013-11-01 19:51:56"
        String[] arguments = { "date", "-us", dateTime };
        Process p = Runtime.getRuntime().exec(arguments);
        p.waitFor();
        int exitCode = p.exitValue();
//        logger.trace(s + " returned " + exitCode);
        if (exitCode != 0) {
            /*if (SystemInfo.getMachineType() == MachineType.PC) {
                logger.warn("Can not set date and time on PC, " + Arrays.asList(arguments) + " failed, ignoring");
                return;
            } else*/ {
                throw new SecurityException("Setting date and time using " + Arrays.asList(arguments) + " failed");
            }
        }
        String[] arguments2 = { "hwclock", "-wu" };
        p = Runtime.getRuntime().exec(arguments2);
        p.waitFor();
        exitCode = p.exitValue();
        if (exitCode != 0) {
            /*if (SystemInfo.getMachineType() == MachineType.PC) {
                logger.warn("Can not set hwclock on PC, " + Arrays.asList(arguments2) + " failed, ignoring");
                return;
            } else*/ {
                throw new SecurityException("Setting hwclock using " + Arrays.asList(arguments2) + " failed");
            }
        }
    }

    public static void setSystemTime(Calendar dateTime) throws IOException, InterruptedException {
        /*{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            formatter.setTimeZone(dateTime.getTimeZone());
            logger.trace("setSystemTime(\"" + formatter.format(dateTime.getTime()) + "\")");
        }*/

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        setSystemTimeUtc(formatter.format(dateTime.getTime()));
    }

}
