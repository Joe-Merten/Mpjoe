package de.jme.util.log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Configure log4j2 without a configuration file
 * @author Joe Merten
 * Based on the idea of Alvaro de Lucas found at stackoverflow.com/questions/28235021/log4j2-on-android/28453604
 */
public class Log4jConfigure {

    private static final Logger logger = StatusLogger.getLogger();

    final static String simpleXmlConfig =
            "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Configuration status='INFO'>\n" +
            "  <Appenders>\n" +
            "    <Console name='Stdout' target='SYSTEM_OUT'>\n" +
            "      <PatternLayout pattern='%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n'/>\n" +
            "    </Console>\n" +
            "  </Appenders>\n" +
            "  <Loggers>\n" +
            "    <Root level='debug'>\n" +
            "      <AppenderRef ref='Stdout'/>\n" +
            "    </Root>\n" +
            "  </Loggers>\n" +
            "</Configuration>\n";

    final static String rollingFileXmlConfig =
            "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Configuration>\n" +
            "  <Properties>\n" +
            "    <Property name='projectPrefix'>Mpj</Property>\n" +
            "    <Property name='rawPattern'>%d %-5p [%t] %C{2} (%F:%L) - %m%n</Property>\n" +
            "    <Property name='coloredFullPattern'>%d %highlight{%-5p}{FATAL=bright red, ERROR=red, WARN=yellow, INFO=cyan, DEBUG=green, TRACE=bright blue} %style{[%t] %C{2} (%F:%L) -}{bright,black} %m%n</Property>\n" +
            "    <Property name='coloredMediumPattern'>%d %highlight{%-5p}{FATAL=bright red, ERROR=red, WARN=yellow, INFO=cyan, DEBUG=green, TRACE=bright blue} %style{[%t] (%F:%L) -}{bright,black} %m%n</Property>\n" +
            "    <Property name='coloredShortPattern'>%d %highlight{%-5p}{FATAL=bright red, ERROR=red, WARN=yellow, INFO=cyan, DEBUG=green, TRACE=bright blue} %style{[%t] -}{bright,black} %m%n</Property>\n" +
            "    <Property name='fileName'>Log/${projectPrefix}.log</Property>\n" +
            "    <Property name='filePattern'>Log/${projectPrefix}-%i.log</Property>\n" +
            "  </Properties>\n" +
            "  <Appenders>\n" +
            "    <Console name='Stdout' target='SYSTEM_OUT'>\n" +
          //"      <PatternLayout pattern='${coloredFullPattern}'/>" +         // incl. Class & Filename
          //"      <PatternLayout pattern='${coloredMediumPattern}'/>" +       // no Class but incl. Filename
            "      <PatternLayout pattern='${coloredShortPattern}'/>" +        // no Class & Filename
          //"      <PatternLayout pattern='${rawPattern}'/>" +                 // incl. Class & Filename but no colors
            "    </Console>\n" +
            "    <RollingFile name='Logfile' fileName='${fileName}' filePattern='${filePattern}'>\n" +
            "      <PatternLayout pattern='${rawPattern}'/>\n" +
            "      <Policies>\n" +
            "        <SizeBasedTriggeringPolicy size='16 MB'/>\n" +
            "      </Policies>\n" +
            "      <DefaultRolloverStrategy fileIndex='min' max='16'/>\n" +
            "    </RollingFile>\n" +
            "  </Appenders>\n" +
            "  <Loggers>\n" +
            "    <Logger name='de.jme.mpjoe.swing.MpjPlayerVlc$1MyMediaPlayerEventListener' level='debug'/>\n" +
            "    <Root level='trace'>\n" +
            "      <AppenderRef ref='Stdout'/>\n" +
            "      <AppenderRef ref='Logfile'/>\n" +
            "    </Root>\n" +
            "  </Loggers>\n" +
            "</Configuration>\n";


    /**
     * Configure log4j2 without a configuration file
     * see also https://issues.apache.org/jira/browse/LOG4J2-952
     * @param   xmlConfig Configuration
     * @return  LoggerContext (may not be needed by the caller)
     */
    public static LoggerContext configureFromXmlString(String xmlConfig) {
        LoggerContext ctx = null;
        try {
            InputStream is = new ByteArrayInputStream(xmlConfig.getBytes());
            ConfigurationSource source = new ConfigurationSource(is);
            ctx = Configurator.initialize(null, source);
        } catch (IOException e) {
            logger.error("Log4j configuration failed", e);
        }
        return ctx;
    }


    /**
     * Configure log4j2 for simple console output
     * @return  LoggerContext (may not be needed by the caller)
     */
    public static LoggerContext configureSimpleConsole() {
        return configureFromXmlString(simpleXmlConfig);
    }


    /**
     * Configure log4j2 for using colored console output and a rolling file appender
     * @return  LoggerContext (may not be needed by the caller)
     */
    public static LoggerContext configureRollingFile() {
        return configureFromXmlString(rollingFileXmlConfig);
    }

}
