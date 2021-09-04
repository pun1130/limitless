package net.auoeke.limitless.log

import net.auoeke.limitless.Limitless
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import java.io.PrintStream
import java.time.LocalTime

// log4j does not display logger names in production.
object LimitlessLogger {
    private val logger = when (FabricLoader.getInstance().isDevelopmentEnvironment) {
        true -> LogManager.getLogger("limitless")
        false -> null
    }

    fun info(message: String) {
        this.print(System.out, Level.INFO, message)
    }

    fun warn(message: String) {
        this.print(System.out, Level.WARN, message)
    }

    fun error(message: String) {
        this.print(System.err, Level.ERROR, message)
    }

    private fun print(stream: PrintStream, level: Level, message: String) {
        if (this.logger === null) {
            stream.printf("[${LocalTime.now().withNano(0)}] [${Thread.currentThread().name}/$level] (${Limitless.ID}) $message%n")
        } else {
            this.logger.printf(level, message)
        }
    }
}
