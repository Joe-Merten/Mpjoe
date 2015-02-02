package de.jme.toolbox;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

/**
 * Test der Klasse Timeter.
 *
 * @author Joe Merten
 */

public class TimeterTest {

    /**
     * Basistests.
     */
    @Test public void testBase() throws InterruptedException {
        final long sleepTime   =  100;
        final long expectedMin =  100;
        final long expectedMax = 1000;

        Timeter t = new Timeter();
        Thread.sleep(sleepTime);
        t.stop();

        long elapsedMs = t.getElapsedMs();
        assertThat(elapsedMs, allOf(greaterThanOrEqualTo(expectedMin), lessThan(expectedMax)));

        long elapsedUs = t.getElapsedUs();
        assertThat(elapsedUs, allOf(greaterThanOrEqualTo(expectedMin*1000), lessThan(expectedMax*1000)));

        long elapsedNs = t.getElapsedNs();
        assertThat(elapsedNs, allOf(greaterThanOrEqualTo(expectedMin*1000000), lessThan(expectedMax*1000000)));
    }

    /**
     * Monotonie der Counter testen.
     * Ein Folgeaufruf darf nie einen kleineren Wert liefern (als der vorherige Aufruf).
     * Hier testen wir nur unserer Timeter.getMilli() sowie das Java-seitige System.nanoTime().
     */
    @Test public void testMonotonic() throws InterruptedException {
        final int  innerLoop   = 1000;  // Bei innerLoog=1000 und outerLoop=100 auf minem alten Notebook ca. 1,6s
        final int  outerLoop   = 100;   // mindestens 100 Schleifendurchläufe
        final long minDuration = 500;   // Mindestdauer des Tests in ms
        final long maxDuration = 30000; // Test nach dieser Zeit abbrechen (Timeout)
        long cnt = 0;
        Timeter t = new Timeter();
        long ms0 = Timeter.getMilli();
        long ns0 = System.nanoTime();
        for (;;) {
            for (int i = 0; i < innerLoop; i++) {
                long ms1 = Timeter.getMilli();
                assertThat(ms1, greaterThanOrEqualTo(ms0));
                ms0 = ms1;
            }

            for (int i = 0; i < innerLoop; i++) {
                long ns1 = System.nanoTime();
                assertThat(ns1, greaterThanOrEqualTo(ns0));
                ns0 = ns1;
            }

            cnt++;
            long elapsed = t.getElapsedMs();
            if (elapsed >= minDuration && cnt >= outerLoop) break;
            if (elapsed >= maxDuration)
                // Timeout!
                fail("Testtime exceeded the expected maximum of " + maxDuration + "ms (LoopCount=" + cnt + "/" + minDuration + ")");
            Thread.sleep(10);
        }
    }

    @Test public void testToString() {
        assertThat(Timeter.toStringWithDots(        0), is(          "0"));
        assertThat(Timeter.toStringWithDots(        1), is(          "1"));
        assertThat(Timeter.toStringWithDots(       12), is(         "12"));
        assertThat(Timeter.toStringWithDots(      123), is(        "123a"));
        assertThat(Timeter.toStringWithDots(     1234), is(      "1.234"));
        assertThat(Timeter.toStringWithDots(    12345), is(     "12.345"));
        assertThat(Timeter.toStringWithDots(   123456), is(    "123.456"));
        assertThat(Timeter.toStringWithDots(  1234567), is(  "1.234.567"));
        assertThat(Timeter.toStringWithDots( 12345678), is( "12.345.678"));
        assertThat(Timeter.toStringWithDots(       -1), is(         "-1"));
        assertThat(Timeter.toStringWithDots(      -12), is(        "-12"));
        assertThat(Timeter.toStringWithDots(     -123), is(       "-123"));
        assertThat(Timeter.toStringWithDots(    -1234), is(     "-1.234"));
        assertThat(Timeter.toStringWithDots(   -12345), is(    "-12.345"));
        assertThat(Timeter.toStringWithDots(  -123456), is(   "-123.456"));
        assertThat(Timeter.toStringWithDots( -1234567), is( "-1.234.567"));
        assertThat(Timeter.toStringWithDots(-12345678), is("-12.345.678"));

        assertThat(Timeter.toStringWithDotsAndComma(        0, 3), is(      "0,000"));
        assertThat(Timeter.toStringWithDotsAndComma(        1, 3), is(      "0,001"));
        assertThat(Timeter.toStringWithDotsAndComma(       12, 3), is(      "0,012"));
        assertThat(Timeter.toStringWithDotsAndComma(      123, 3), is(      "0,123"));
        assertThat(Timeter.toStringWithDotsAndComma(     1234, 3), is(      "1,234"));
        assertThat(Timeter.toStringWithDotsAndComma(    12345, 3), is(     "12,345"));
        assertThat(Timeter.toStringWithDotsAndComma(   123456, 3), is(    "123,456"));
        assertThat(Timeter.toStringWithDotsAndComma(  1234567, 3), is(  "1.234,567"));
        assertThat(Timeter.toStringWithDotsAndComma( 12345678, 3), is( "12.345,678"));
        assertThat(Timeter.toStringWithDotsAndComma(       -1, 3), is(     "-0,001"));
        assertThat(Timeter.toStringWithDotsAndComma(      -12, 3), is(     "-0,012"));
        assertThat(Timeter.toStringWithDotsAndComma(     -123, 3), is(     "-0,123"));
        assertThat(Timeter.toStringWithDotsAndComma(    -1234, 3), is(     "-1,234"));
        assertThat(Timeter.toStringWithDotsAndComma(   -12345, 3), is(    "-12,345"));
        assertThat(Timeter.toStringWithDotsAndComma(  -123456, 3), is(   "-123,456"));
        assertThat(Timeter.toStringWithDotsAndComma( -1234567, 3), is( "-1.234,567"));
        assertThat(Timeter.toStringWithDotsAndComma(-12345678, 3), is("-12.345,678"));
    }

    @Test public void testHumanReadable() {
        assertThat(Timeter.nsToHumanReadable(                          0 ), is(             "0ns"));
        assertThat(Timeter.nsToHumanReadable(                          1 ), is(             "1ns"));
        assertThat(Timeter.nsToHumanReadable(                        999 ), is(           "999ns"));
        assertThat(Timeter.nsToHumanReadable(                       1000 ), is(         "1,000µs"));
        assertThat(Timeter.nsToHumanReadable(                     999999 ), is(       "999,999µs"));
        assertThat(Timeter.nsToHumanReadable(                    1000000 ), is(         "1,000ms"));
        assertThat(Timeter.nsToHumanReadable(                  999999999 ), is(       "999,999ms"));
        assertThat(Timeter.nsToHumanReadable(                 1000000000 ), is(         "1,000s" ));
        assertThat(Timeter.nsToHumanReadable(               999999999999L), is(       "999,999s" ));
        assertThat(Timeter.nsToHumanReadable(              1000000000000L), is(        "00:16:40"));
        assertThat(Timeter.nsToHumanReadable(  -1L+100L*3600L*1000000000L), is(        "99:59:59"));
        assertThat(Timeter.nsToHumanReadable(      100L*3600L*1000000000L), is(     "4d 04:00:00")); // ab 100h
        assertThat(Timeter.nsToHumanReadable( 1000L*24L*3600L*1000000000L), is( "1.000d 00:00:00")); // Test für 1.000 Tage

        assertThat(Timeter.usToHumanReadable(                          0 ), is(             "0µs"));
        assertThat(Timeter.usToHumanReadable(                          1 ), is(             "1µs"));
        assertThat(Timeter.usToHumanReadable(                        999 ), is(           "999µs"));
        assertThat(Timeter.usToHumanReadable(                       1000 ), is(         "1,000ms"));
        assertThat(Timeter.usToHumanReadable(                     999999 ), is(       "999,999ms"));
        assertThat(Timeter.usToHumanReadable(                    1000000 ), is(         "1,000s" ));
        assertThat(Timeter.usToHumanReadable(                  999999999L), is(       "999,999s" ));
        assertThat(Timeter.usToHumanReadable(                 1000000000L), is(        "00:16:40"));
        assertThat(Timeter.usToHumanReadable(     -1L+100L*3600L*1000000L), is(        "99:59:59"));
        assertThat(Timeter.usToHumanReadable(         100L*3600L*1000000L), is(     "4d 04:00:00")); // ab 100h
        assertThat(Timeter.usToHumanReadable(    1000L*24L*3600L*1000000L), is( "1.000d 00:00:00")); // Test für 1.000 Tage

        assertThat(Timeter.msToHumanReadable(                          0 ), is(             "0ms"));
        assertThat(Timeter.msToHumanReadable(                          1 ), is(             "1ms"));
        assertThat(Timeter.msToHumanReadable(                        999 ), is(           "999ms"));
        assertThat(Timeter.msToHumanReadable(                       1000 ), is(         "1,000s" ));
        assertThat(Timeter.msToHumanReadable(                     999999L), is(       "999,999s" ));
        assertThat(Timeter.msToHumanReadable(                    1000000L), is(        "00:16:40"));
        assertThat(Timeter.msToHumanReadable(        -1L+100L*3600L*1000L), is(        "99:59:59"));
        assertThat(Timeter.msToHumanReadable(            100L*3600L*1000L), is(     "4d 04:00:00")); // ab 100h
        assertThat(Timeter.msToHumanReadable(       1000L*24L*3600L*1000L), is( "1.000d 00:00:00")); // Test für 1.000 Tage

        assertThat(Timeter.nsToHumanReadable(                         -1 ), is(            "-1ns"));
        assertThat(Timeter.nsToHumanReadable(                       -999 ), is(          "-999ns"));
        assertThat(Timeter.nsToHumanReadable(                      -1000 ), is(        "-1,000µs"));
        assertThat(Timeter.nsToHumanReadable(                    -999999 ), is(      "-999,999µs"));
        assertThat(Timeter.nsToHumanReadable(                   -1000000 ), is(        "-1,000ms"));
        assertThat(Timeter.nsToHumanReadable(                 -999999999 ), is(      "-999,999ms"));
        assertThat(Timeter.nsToHumanReadable(                -1000000000 ), is(        "-1,000s" ));
        assertThat(Timeter.nsToHumanReadable(              -999999999999L), is(      "-999,999s" ));
        assertThat(Timeter.nsToHumanReadable(             -1000000000000L), is(       "-00:16:40"));
        assertThat(Timeter.nsToHumanReadable(   1L-100L*3600L*1000000000L), is(       "-99:59:59"));
        assertThat(Timeter.nsToHumanReadable(     -100L*3600L*1000000000L), is(    "-4d 04:00:00")); // ab 100h
        assertThat(Timeter.nsToHumanReadable(-1000L*24L*3600L*1000000000L), is("-1.000d 00:00:00")); // Test für 1.000 Tage

        assertThat(Timeter.usToHumanReadable(                         -1 ), is(            "-1µs"));
        assertThat(Timeter.usToHumanReadable(                       -999 ), is(          "-999µs"));
        assertThat(Timeter.usToHumanReadable(                      -1000 ), is(        "-1,000ms"));
        assertThat(Timeter.usToHumanReadable(                    -999999 ), is(      "-999,999ms"));
        assertThat(Timeter.usToHumanReadable(                   -1000000 ), is(        "-1,000s" ));
        assertThat(Timeter.usToHumanReadable(                 -999999999L), is(      "-999,999s" ));
        assertThat(Timeter.usToHumanReadable(                -1000000000L), is(       "-00:16:40"));
        assertThat(Timeter.usToHumanReadable(      1L-100L*3600L*1000000L), is(       "-99:59:59"));
        assertThat(Timeter.usToHumanReadable(        -100L*3600L*1000000L), is(    "-4d 04:00:00")); // ab 100h
        assertThat(Timeter.usToHumanReadable(   -1000L*24L*3600L*1000000L), is("-1.000d 00:00:00")); // Test für 1.000 Tage

        assertThat(Timeter.msToHumanReadable(                         -1 ), is(            "-1ms"));
        assertThat(Timeter.msToHumanReadable(                       -999 ), is(          "-999ms"));
        assertThat(Timeter.msToHumanReadable(                      -1000 ), is(        "-1,000s" ));
        assertThat(Timeter.msToHumanReadable(                    -999999L), is(      "-999,999s" ));
        assertThat(Timeter.msToHumanReadable(                   -1000000L), is(       "-00:16:40"));
        assertThat(Timeter.msToHumanReadable(         1L-100L*3600L*1000L), is(       "-99:59:59"));
        assertThat(Timeter.msToHumanReadable(           -100L*3600L*1000L), is(    "-4d 04:00:00")); // ab 100h
        assertThat(Timeter.msToHumanReadable(      -1000L*24L*3600L*1000L), is("-1.000d 00:00:00")); // Test für 1.000 Tage
    }

}
