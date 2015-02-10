package de.jme.toolbox.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class LogLevelHelper {
    static final Logger logger = LogManager.getLogger(LogLevelHelper.class);

    // Ändern des Loglevel eines spezifischen Loggers geht nur, wenn dieser Logger in der log4j2.xml einen eigenen Eintrag hat.
    // Falls nicht, dann wird das Loglevel des Root Logger geändert!
    // TODO: Mal mit "config.addLogger()" versuchen, siehe hier: https://issues.apache.org/jira/browse/LOG4J2-468
    //       evtl. auch interessant: https://issues.apache.org/jira/browse/LOG4J2-544
    public static void setLoggerLevel(String loggerName, Level level) {
        LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public static void setLoggerLevel(Logger logger, Level level) {
        setLoggerLevel(logger.getName(), level);
    }

    public static Level getLoggerLevel(String loggerName) {
        LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
        return loggerConfig.getLevel();
    }

    public static Level getLoggerLevel(Logger logger) {
        return getLoggerLevel(logger.getName());
    }

    public static void setLoggerRootLevel(Level level) {
        LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        //LoggerConfig loggerConfig = getLoggerConfig(LogManager.getRootLogger());
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public static Level getLoggerRootLevel() {
        LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        //LoggerConfig loggerConfig = getLoggerConfig(LogManager.getRootLogger());
        return loggerConfig.getLevel();
    }

}
