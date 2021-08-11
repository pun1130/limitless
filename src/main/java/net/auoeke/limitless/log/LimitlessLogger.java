package net.auoeke.limitless.log;

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import net.auoeke.limitless.Limitless;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// log4j does not display logger names in production.
public class LimitlessLogger {
    private static final boolean development = FabricLoader.getInstance().isDevelopmentEnvironment();
    private static final Logger logger = development ? LogManager.getLogger("limitless") : null;

    public static void info(String message, Object... arguments) {
        print(System.out, Level.INFO, message, arguments);
    }

    public static void warn(String message, Object... arguments) {
        print(System.out, Level.WARN, message, arguments);
    }

    public static void error(String message, Object... arguments) {
        print(System.err, Level.ERROR, message, arguments);
    }

    private static void print(PrintStream stream, Level level, String message, Object... arguments) {
        if (development) {
            logger.printf(level, message, arguments);
        } else {
            stream.printf("[%s] [%s/%s] (%s) %s%n", DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT), Thread.currentThread().getName(), level, Limitless.ID, message.formatted(arguments));
        }
    }
}
